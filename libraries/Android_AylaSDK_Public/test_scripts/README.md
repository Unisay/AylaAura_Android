This folder is only used by those who develop or run Android Robolectric tests. Please ignore this folder if you are not doing this.

To run Robolectric tests from project root folder:
1: run
    $ cd test_scripts
    $ ./patch_source.rb
    $ cd ..
2: run Robolectric tests such as:
    $ ./gradlew testDebugUnitTest
    (note: "gradlew test" run run tests twice: one for debug build and one for release build)
3: run
    $ cd test_scripts
    $ ./unpatch_source.rb

You can also run Robolectric tests from Android Studio:
1: run
    $ cd test_scripts
    $ ./patch_source.rb
    $ cd ..
2: in your Android Studio, right click on the test file or a specific test method inside a test file, then select "run ..." to run it.
3: after you finish running or debugging tests inside Android Studio, must run the following to undo the source code changes made for Robolecgtric tests.
    $ cd test_scripts
    $ ./unpatch_source.rb
