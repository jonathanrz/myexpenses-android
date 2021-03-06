#!/bin/bash

# raise an error if any command fails!
set -e

# existance of this file indicates that all dependencies were previously installed, and any changes to this file will use a different filename.
INITIALIZATION_FILE="$ANDROID_HOME/.initialized-dependencies-$(git log -n 1 --format=%h -- $0)"

if [ ! -e ${INITIALIZATION_FILE} ]; then
  # fetch and initialize $ANDROID_HOME
  download-android
  # Use the latest android sdk tools
  echo y | android update sdk --no-ui --filter platform-tool > /dev/null
  echo y | android update sdk --no-ui --filter tool > /dev/null

  # The BuildTools version used by your project
  echo y | android update sdk --no-ui --filter build-tools-24.0.2 --all > /dev/null

  # The SDK version used to compile your project
  echo y | android update sdk --no-ui --filter android-24 > /dev/null

  # uncomment to install the Extra/Android Support Library
  echo y | android update sdk --no-ui --filter extra-android-support --all > /dev/null

  # uncomment these if you are using maven/gradle to build your android project
  echo y | android update sdk --no-ui --filter extra-google-m2repository --all > /dev/null
  echo y | android update sdk --no-ui --filter extra-android-m2repository --all > /dev/null

  # Specify at least one system image if you want to run emulator tests
#  echo y | android update sdk --no-ui --filter sys-img-armeabi-v7a-android-24 --all > /dev/null
  echo y | android update sdk --no-ui --filter sys-img-x86-android-24 --all > /dev/null

  export DEBIAN_FRONTEND=noninteractive

  sudo apt-get -y install libpulse-dev

  touch ${INITIALIZATION_FILE}
else
  echo "not found"
fi
