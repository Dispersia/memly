{
  description = "Memly + Wear";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = {
    self,
    nixpkgs,
  }: let
    system = "x86_64-linux";
    pkgs = import nixpkgs {
      inherit system;

      config = {
        allowUnfree = true;
        android_sdk.accept_license = true;
      };
    };

    android = pkgs.androidenv.composeAndroidPackages {
      toolsVersion = "26.1.1";

      platformToolsVersion = "37.0.0";
      buildToolsVersions = ["36.0.0"];

      platformVersions = ["36" "36.1"];

      abiVersions = ["x86_64"];

      includeEmulator = true;
      includeSystemImages = true;

      systemImageTypes = ["android-wear" "google_apis_playstore"];
    };

    androidSdk = "${android.androidsdk}/libexec/android-sdk";
  in {
    devShells.${system}.default = pkgs.mkShell {
      packages = [
        android.androidsdk
        pkgs.jdk21
        pkgs.gradle
        pkgs.git
        pkgs.unzip
        pkgs.just

        pkgs.alejandra
      ];

      ANDROID_SDK_ROOT = androidSdk;
      ANDROID_HOME = androidSdk;
      JAVA_HOME = pkgs.jdk21;
      JAVA_TOOL_OPTIONS = "-Dcom.jetbrains.ls.imports.gradle.java.home=${pkgs.jdk21}";

      shellHook = ''
        export QT_QPA_PLATFORM=xcb

        if ! emulator -list-avds | grep -q wearos; then
          echo "Creating Wear OS AVD..."

          yes | avdmanager create avd \
            -n wearos \
            -k "system-images;android-36;android-wear-signed;x86_64" \
            --device "wearos_large_round"
        fi

        if ! emulator -list-avds | grep -q phone; then
          echo "Creating Phone AVD..."

          yes | avdmanager create avd \
            -n phone \
            -k "system-images;android-36;google_apis_playstore;x86_64" \
            --device "pixel_9_pro"
        fi
      '';
    };

    formatter.${system} = pkgs.alejandra;
  };
}
