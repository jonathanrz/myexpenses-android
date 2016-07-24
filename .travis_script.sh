#!/bin/bash
set -ev
./gradlew connectedAndroidTest
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew testfairyJonathan
	./gradlew testfairyThainara
fi