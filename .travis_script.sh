#!/bin/bash
set -ev

./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.package=br.com.jonathanzanella.myexpenses.app
./gradlew connectedAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.package=br.com.jonathanzanella.myexpenses.account
./gradlew connectedAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.package=br.com.jonathanzanella.myexpenses.bill
./gradlew connectedAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.package=br.com.jonathanzanella.myexpenses.card
./gradlew connectedAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.package=br.com.jonathanzanella.myexpenses.expense
./gradlew connectedAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.package=br.com.jonathanzanella.myexpenses.receipt
./gradlew connectedAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.package=br.com.jonathanzanella.myexpenses.resume
./gradlew connectedAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.package=br.com.jonathanzanella.myexpenses.source

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew testfairyJonathan
	./gradlew testfairyThainara
fi