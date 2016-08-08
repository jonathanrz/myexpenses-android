#!/bin/bash
set -ev
./gradlew -Pandroid.testInstrumentationRunnerArguments.class=br.com.jonathanzanella.myexpenses.AppNavigationTest connectedAndroidTest
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew testfairyJonathan
fi