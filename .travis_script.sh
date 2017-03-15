#!/bin/bash
set -ev

./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.package=br.com.jonathanzanella.myexpenses.app, br.com.jonathanzanella.myexpenses.account, br.com.jonathanzanella.myexpenses.bill, br.com.jonathanzanella.myexpenses.card
./gradlew connectedAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.package=br.com.jonathanzanella.myexpenses.expense, br.com.jonathanzanella.myexpenses.receipt, br.com.jonathanzanella.myexpenses.resume, br.com.jonathanzanella.myexpenses.source

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew testfairyJonathan
	./gradlew testfairyThainara
fi