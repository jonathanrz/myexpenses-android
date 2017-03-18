#!/bin/bash
set -ev

./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew checkStyle
./gradlew findBugs
./gradlew assembleDebugAndroidTest
./gradlew installDebugAndroidTest
./gradlew connectedDebugAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.size=small
./gradlew connectedDebugAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.size=medium
./gradlew connectedDebugAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.size=large