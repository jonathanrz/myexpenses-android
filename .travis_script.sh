#!/bin/bash
set -ev

./gradlew assembleDebug
./gradlew connectedAndroidTest
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew testfairyJonathan
	./gradlew testfairyThainara
fi
