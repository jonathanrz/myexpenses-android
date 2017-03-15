#!/bin/bash
set -ev

./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.size=small
./gradlew connectedAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.size=medium
./gradlew connectedAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.size=large

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew testfairyJonathan
	./gradlew testfairyThainara
fi