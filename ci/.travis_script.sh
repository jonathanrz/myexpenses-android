#!/bin/bash
set -ev

./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew checkStyle
./gradlew findBugs
./gradlew pmd