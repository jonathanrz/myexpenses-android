#!/bin/bash
set -ev

./gradlew build
./gradlew jacocoTestReport
./gradlew testDebugUnitTest
./gradlew checkStyle
./gradlew findBugs
./gradlew assembleDebugAndroidTest
./gradlew installDebugAndroidTest
./gradlew connectedDebugAndroidTest -PdisablePreDex

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew testfairyJonathan
	./gradlew testfairyThainara
fi