#!/usr/bin/env bash

./gradlew testfairyJonathan;
./gradlew testfairyThainara;
./gradlew jacocoTestReport;
./app/codecov.sh