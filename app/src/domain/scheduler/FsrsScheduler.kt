package io.dispersia.memly.domain.scheduler

import io.dispersia.memly.domain.card.models.Card
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

enum class FsrsRating(val value: Int) {
    Again(1),
    Good(3),
}

data class SchedulingResult(
    val stability: Double,
    val difficulty: Double,
    val interval: Int,
    val dueDate: Instant,
    val reps: Int,
    val lapses: Int,
)

class FsrsScheduler(
    private val requestRetention: Double = 0.9,
    private val params: List<Double> = DEFAULT_WEIGHTS,
    private val maxInterval: Int = 36500,
) {
    private val decay = -params[20]
    private val factor = requestRetention.pow(1.0 / decay) - 1

    fun schedule(card: Card, rating: FsrsRating, now: Instant = Instant.now()): SchedulingResult {
        val isNew = card.reps == 0
        val reps = card.reps + 1
        val lapses = card.lapses + if (rating == FsrsRating.Again && !isNew) 1 else 0

        val newDifficulty: Double
        val newStability: Double
        val interval: Int

        if (isNew) {
            newDifficulty = initDifficulty(rating)
            newStability = initStability(rating)
            interval = if (rating == FsrsRating.Good) {
                nextInterval(newStability)
            } else {
                0
            }
        } else {
            newDifficulty = nextDifficulty(card.difficulty, rating)

            val elapsedDays = ChronoUnit.DAYS.between(card.lastReview, now).toDouble().coerceAtLeast(0.0)
            val retrievability = forgettingCurve(elapsedDays, card.stability)

            newStability = if (rating == FsrsRating.Again) {
                nextForgetStability(card.difficulty, card.stability, retrievability)
            } else {
                nextRecallStability(card.difficulty, card.stability, retrievability)
            }

            interval = if (rating == FsrsRating.Again) 0 else nextInterval(newStability)
        }

        val dueDate = if (interval == 0) {
            now.plus(SHORT_TERM_DELAY_MINUTES, ChronoUnit.MINUTES)
        } else {
            now.plus(interval.toLong(), ChronoUnit.DAYS)
        }

        return SchedulingResult(
            stability = newStability,
            difficulty = newDifficulty,
            interval = interval,
            dueDate = dueDate,
            reps = reps,
            lapses = lapses,
        )
    }

    fun retrievability(card: Card, now: Instant = Instant.now()): Double {
        if (card.reps == 0 || card.stability <= 0.0) return 0.0
        val elapsed = ChronoUnit.DAYS.between(card.lastReview, now).toDouble().coerceAtLeast(0.0)
        return forgettingCurve(elapsed, card.stability)
    }

    private fun forgettingCurve(elapsedDays: Double, stability: Double): Double {
        if (stability <= 0.0) return 0.0
        return (1.0 + factor * elapsedDays / stability).pow(decay)
    }

    private fun initDifficulty(rating: FsrsRating): Double {
        val raw = params[4] - exp(params[5] * (rating.value - 1)) + 1
        return raw.coerceIn(1.0, 10.0)
    }

    private fun initStability(rating: FsrsRating): Double {
        return params[rating.value - 1].coerceAtLeast(0.1)
    }

    private fun nextDifficulty(currentD: Double, rating: FsrsRating): Double {
        val deltaD = -params[6] * (rating.value - 3)
        val damped = deltaD * (10.0 - currentD) / 9.0
        val nextD = currentD + damped
        val reverted = params[7] * initDifficulty(FsrsRating.Good) + (1 - params[7]) * nextD
        return reverted.coerceIn(1.0, 10.0)
    }

    private fun nextRecallStability(d: Double, s: Double, r: Double): Double {
        val multiplier = exp(params[8]) *
                (11.0 - d) *
                s.pow(-params[9]) *
                (exp((1.0 - r) * params[10]) - 1.0)

        return s * (1.0 + multiplier)
    }

    private fun nextForgetStability(d: Double, s: Double, r: Double): Double {
        val sMin = s / exp(params[17] * params[18])

        val result = params[11] *
                d.pow(-params[12]) *
                ((s + 1.0).pow(params[13]) - 1.0) *
                exp((1.0 - r) * params[14])

        return min(result, sMin).coerceAtLeast(0.1)
    }

    private fun nextInterval(stability: Double): Int {
        val rawInterval = stability / factor * (requestRetention.pow(1.0 / decay) - 1.0)
        return rawInterval.roundToInt().coerceIn(1, maxInterval)
    }

    companion object {
        private const val SHORT_TERM_DELAY_MINUTES = 10L

        val DEFAULT_WEIGHTS = listOf(
            0.212, 1.2931, 2.3065, 8.2956,
            6.4133, 0.8334, 3.0194, 0.001,
            1.8722, 0.1666, 0.796, 1.4835,
            0.0614, 0.2629, 1.6483, 0.6014,
            1.8729, 0.5425, 0.0912, 0.0658,
            0.1542,
        )
    }
}
