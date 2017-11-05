runTests:
	./gradlew uninstallDebug
	./gradlew assembleDebug
	./gradlew testDebugUnitTest
	./gradlew connectedCheck

runTestsWithCoverage: runTests
	./gradlew jacocoTestReport
	./app/codecov.sh
