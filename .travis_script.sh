#!/bin/bash
set -ev

./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew checkStyle
./gradlew findBugs
./gradlew pmd

git fetch --unshallow
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
    ./gradlew testfairyJonathan
fi