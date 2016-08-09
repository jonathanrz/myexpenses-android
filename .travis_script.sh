#!/bin/bash
set -ev
./gradlew assembleDebugAndroidTest
./gradlew connectedDebugAndroidTest
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew testfairyJonathan
fi
