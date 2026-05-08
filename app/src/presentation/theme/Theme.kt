package io.dispersia.memly.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme

@Composable
fun MemlyTheme(
    content: @Composable () -> Unit
) {
    /**
     * Empty theme to customize for your app.
     * See: https://developer.android.com/jetpack/compose/designsystems/custom
     */
    MaterialTheme(
        content = content
    )
}
