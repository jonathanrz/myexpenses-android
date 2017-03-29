#!/bin/bash
set -ev

./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew checkStyle
./gradlew findBugs
./gradlew pmd

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
    ./gradlew testfairyJonathan
    ./gradlew testfairyThainara
fi