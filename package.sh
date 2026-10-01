#!/usr/bin/env bash
# ==============================================================================
# CipheRaz — Native Cross-Platform Packaging Script (jpackage)
# Developed by Abderrazak BENAROUS
# ==============================================================================
set -e

APP_NAME="CipheRaz"
APP_VERSION="1.0.0"
MAIN_CLASS="com.cryptolab.app.ApplicationLauncher"
JAR_NAME="cryptolab-1.0.0-SNAPSHOT.jar"

echo "Building standalone application package for: $APP_NAME v$APP_VERSION"
./mvnw clean package -DskipTests

# Detect Operating System
OS="$(uname -s)"
case "${OS}" in
    Darwin*)
        echo "Detected macOS. Generating macOS DMG / APP bundle..."
        jpackage \
            --input target/ \
            --name "$APP_NAME" \
            --main-jar "$JAR_NAME" \
            --main-class "$MAIN_CLASS" \
            --type dmg \
            --app-version "$APP_VERSION" \
            --vendor "Abderrazak BENAROUS" \
            --copyright "Copyright (c) 2026 Abderrazak BENAROUS. All rights reserved." \
            --description "CipheRaz Educational Cryptography and Steganography Laboratory" \
            --dest dist/
        echo "Package created in dist/"
        ;;
    Linux*)
        echo "Detected Linux. Generating DEB package..."
        jpackage \
            --input target/ \
            --name "cipheraz" \
            --main-jar "$JAR_NAME" \
            --main-class "$MAIN_CLASS" \
            --type deb \
            --app-version "$APP_VERSION" \
            --vendor "Abderrazak BENAROUS" \
            --description "CipheRaz Educational Cryptography and Steganography Laboratory" \
            --linux-shortcut \
            --dest dist/
        echo "Package created in dist/"
        ;;
    MINGW*|MSYS*|CYGWIN*)
        echo "Detected Windows. Generating MSI installer..."
        jpackage \
            --input target/ \
            --name "$APP_NAME" \
            --main-jar "$JAR_NAME" \
            --main-class "$MAIN_CLASS" \
            --type msi \
            --app-version "$APP_VERSION" \
            --vendor "Abderrazak BENAROUS" \
            --win-shortcut \
            --win-menu \
            --dest dist/
        echo "Package created in dist/"
        ;;
    *)
        echo "Unknown OS: ${OS}. Run with jpackage manually."
        ;;
esac
