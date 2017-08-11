#!/bin/sh
# simple scriopt to show how to run Robolectric tests
# Note: this scripot is supposed to run from this directory

./patch_source.rb
cd ..
./gradlew testDebugUnitTest
cd test_scripts
./unpatch_source.rb

