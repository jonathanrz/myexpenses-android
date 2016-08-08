#!/bin/bash
set -ev
./gradlew connectedDebugAndroidTest
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew testfairyJonathan
fi