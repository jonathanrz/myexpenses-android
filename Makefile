runTests:
	./gradlew uninstallDebug assembleDebug testDebugUnitTest connectedDebugAndroidTest

runTestsWithCoverage: runTests
	./gradlew jacocoTestReport connectedCheck
	./codecov.sh