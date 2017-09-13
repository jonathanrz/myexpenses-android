runTests:
	./gradlew uninstallDebug
	./gradlew assembleDebug
	./gradlew testDebugUnitTest
	./gradlew connectedCheck

runTestsWithCoverage: runTests
	./gradlew jacocoTestReport
	./app/codecov.sh

saveScreenshots:
	./gradlew executeScreenshotTests -Precord

validateScreenshots:
	./gradlew executeScreenshotTests