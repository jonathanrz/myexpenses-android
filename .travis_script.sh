#!/bin/bash
set -ev
./gradlew assembleJonathan
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew testfairyJonathan
fi
