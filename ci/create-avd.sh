#!/bin/bash

set -e

# existance of this file indicates that all dependencies were previously installed, and any changes to this file will use a different filename.
INITIALIZATION_FILE="$ANDROID_HOME/.create_avd-$(git log -n 1 --format=%h -- $0)"

if [ ! -e ${INITIALIZATION_FILE} ]; then
  echo no | android create avd --force -n test -t android-24 --abi armeabi-v7a #creates device

  emulator -avd test -no-skin -no-audio -no-window & #starts emulator and put the process on background
else
  echo "not found"
fi