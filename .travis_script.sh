#!/bin/bash
set -ev
./gradlew assembleAndroidTest
./gradlew connectedAndroidTest
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew testfairyJonathan
fi
