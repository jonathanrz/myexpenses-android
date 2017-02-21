runTests:
	./gradlew uninstallDebug
	./gradlew assembleDebug
	./gradlew testDebugUnitTest
	./gradlew connectedDebugAndroidTest

runTestsWithCoverage: runTests
	./gradlew jacocoTestReport
	./gradlew connectedCheck
	./app/codecov.sh