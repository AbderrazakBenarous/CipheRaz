#!/usr/bin/env bash
set -e

# Resolve JAVA_HOME if not already set or not pointing to a valid JDK
if [ -z "$JAVA_HOME" ] || [ ! -x "$JAVA_HOME/bin/java" ]; then
    if [ -d "/Library/Java/JavaVirtualMachines/temurin-24.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-24.jdk/Contents/Home"
    elif [ -x "/usr/libexec/java_home" ]; then
        export JAVA_HOME="$(/usr/libexec/java_home -v 21 2>/dev/null || /usr/libexec/java_home 2>/dev/null)"
    fi
fi

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -x "$DIR/.tools/maven/bin/mvn" ]; then
    MVN_CMD="$DIR/.tools/maven/bin/mvn"
else
    MVN_CMD="mvn"
fi
exec "$MVN_CMD" "$@"
