#!/bin/bash
set -ev

./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest -PdisablePreDex

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew testfairyJonathan
	./gradlew testfairyThainara
fi