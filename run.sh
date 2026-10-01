#!/usr/bin/env bash
# ==============================================================================
# CipheRaz — Application Launcher
# Developed by Abderrazak BENAROUS
# ==============================================================================
set -e

# Resolve JAVA_HOME if not already set or not pointing to a valid JDK
if [ -z "$JAVA_HOME" ] || [ ! -x "$JAVA_HOME/bin/java" ]; then
    if [ -d "/Library/Java/JavaVirtualMachines/temurin-24.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-24.jdk/Contents/Home"
    elif [ -x "/usr/libexec/java_home" ]; then
        export JAVA_HOME="$(/usr/libexec/java_home -v 21 2>/dev/null || /usr/libexec/java_home 2>/dev/null)"
    fi
fi

JAVA_BIN="${JAVA_HOME}/bin/java"
if [ ! -x "$JAVA_BIN" ]; then
    JAVA_BIN="java"
fi

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_FILE="$DIR/target/cryptolab-1.0.0-SNAPSHOT.jar"

# Build the JAR if it doesn't already exist
if [ ! -f "$JAR_FILE" ]; then
    echo "Executable JAR not found. Building project..."
    "$DIR/mvnw" clean package -DskipTests
fi

echo "Launching CipheRaz..."
echo "Developed by Abderrazak BENAROUS"
exec "$JAVA_BIN" -jar "$JAR_FILE" "$@"
