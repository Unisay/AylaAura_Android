AylaSDK
=======
Accelerate Ayla Platform Development


Build using Android Studio 2.3 or higher
==============================
Clone the repository into your working directory. By default it checks out the master branch.

    # git clone https://github.com/AylaNetworks/Android_AylaSDK_Public

Upgrade your Android Studio to version 2.3 or higher.
Open Android Studio and select "Open existing Android Studio project"
Navigate to the Android_AylaSDK folder and select it to be opened

From the Android Studio "Build" menu, select "Make Project"

If the build succeeds, the generated .aar file can be found in the
library/build/outputs directory.

There are 2 kinds of tests on the source tree:
1: Instrumentation based connected android tests: library/src/androidTest. These tests run on a connected Android device or emulator. So it is slow but it covers real code path well.
2: Robolectric tests: library/src/test. These tests run on your local JVM with no need of Android emulator or device. So it is much faster than conected Android tests. However "patch_source.rb" script needs to be run before running these tests due to Robolectric's limitation.

In Android Studio, these tests may be run by right-clicking on the "androidTest" folder or "test" in the project
window and selecting "Run Tests...", or individual test files within that folder
may be run the same way. As mentioned above, before running individual Robolectric tests inside Android Studio, it is required to run this command once: "cd test_scripts; ./patch_source.rb". After you run or debug Robolectric tests, run "cd test_scripts; ./unpatch_source.rb" to undo the SDK source code changes due to Robolectric tests.

From command line, command "./gradlew connectedAndroidTest" can be used to run connected Android tests. To run Robolectric tests, "cd test_scripts" first and then run "./run_robolectric_tests.sh" command.
Note: inside the script, it runs "gradlew testDebugUnitTest". If you change it to"gradlew test", it will run the same Robolectric tests twice: once for debug build and once for release build.

If you are going to be running these tests, first, you will need to update the TestConstants.java
to use your own credentials and DSNs for the tests. Many tests will fail without updating the TestConstants.java. Secondly, you need at least one Ayla certificated device like Ayla Evaluatoin Board to make meaningful tests; Thirdly, you need to modify the test code to match your specific device and settings details. All these can be done when you are familar with the test code.

API Documentation
===================================================
JavaDoc is available and is preferable to the reference guide for specifics on individual method calls. To build the JavaDoc documentation:

$cd <lib_dir>
$./gradlew generateReleaseJavadoc

if you see message asking you to upgrade to Gradle 3.3, you can use Gradle 3.3 installed by Android Studio 2.3. In Android Studio 2.3, click on "Preferences" then "Build, Execution, Deployment" then "Build Tools" then "Gradle" to find its full path. Now you can set your environment variable PATH to use this Gradle 3.3.

There may be warning and errors building the documentation, but it will build successfully. The javaDoc will be available at .../Android_AylaSDK_Public/library/build/docs/javadoc/index.html.
use the following command on Mac to view documentation in a browser:
$open library/build/docs/javadoc/index.html

Generate Stand-alone .jar or .aar File
=================
To create a .jar and/or .aar file to include in a projec:
$ ./gradlew jar
$ ./gradlew aar

It will generate Android_AylaSDK.jar or Android_AylaSDK.aar file in the library/build/outputs folder.

Contribute your code
====================

If you would like to contribute your own code change to our project, please submit pull requests against the "incoming" branch on Github. We will review and approve your pull requests if appropriate.

Dependencies
============

Google Volley		http://developer.android.com/license.html  
Google Gson		http://developer.android.com/license.html  
NanoHttpd		https://github.com/NanoHttpd/nanohttpd/blob/master/LICENSE.md  
Autobahn Websockets	https://github.com/crossbario/autobahn-android/blob/master/LICENSE  
SpongyCastle		http://www.bouncycastle.org/licence.html  
Joda			http://www.joda.org/joda-time/license.html  
Robolectric 		https://github.com/robolectric/robolectric/blob/master/LICENSE.txt

Version of major build tools used
=================================
Android Studio: 2.3.2
Gradle: 4.0

Releases
========

v5.6.02       07/19/2017

- add a method to get user UUID

v5.6.01       06/19/2017

- connectToNewDevice() API WPA and WEP support

v5.6.00       06/14/2017

- Local Device
  - New local device OTA
  - New local device developer guide
- Mobile DSS (websocket)
  - Version 2 support includes datapoint, datapointAck, connectivity, and location subscription types
  - Requires cloud side enablement - contact Customer Success
- New OAuth  Wechat Support
  - Currently limited to one application per OEM
- Setup, registration, and LAN mode improvements
- New app notes for WeChat
- New SDK Architecture diagrams

v5.5.02       04/28/2017

- Fixes crash emailing log files

v5.5.01       04/11/2017

- Fix lifecycle bug
- Add minimum time interval for refreshing authorization

v5.5.00       03/28/2017

New and Improved
- Google OAuth2 AuthProvider Support
  - Replaces Webview OAuth which has been deprecated by Google
  - See app note in /doc folder
- Local Device alpha
- Support for the latest AMAP & Aura 5.5
- polling in LAN Mode fix
- Use com.android.volley:volley
- PII improvements and clean up
- Improved registration token handling
- On-boarding support for secure devices

Bug Fixes & Chores
- Google OAuth2 Support via SDK 5.5
  - Replaces Webview OAuth which has been deprecated by Google
- App Notes (in /doc folder)
  - Wi-Fi Setup
  - Registration
  - LAN Mode
  - Google OAuth2
- Call sessionManager.onPause() before notifying listeners
- Reduce permissions during WiFi Setup from FINE_LOCATION to COURSE_LOCATION
- Increased maximum time to wait for device connection to service confirmation from 10 to 30 seconds
- Miscellaneous bug fixes
- Includes all hot-fixes

v5.4.00       01/24/2017

- Remove duplicate call to notify device listeners of a property change
- AylaDatapointBlob now inherits from AylaDatapoint
- Remove duplicate call to notify device listeners of a property change
- Keep polling for property changes even if we get an error back from the service
- Add equals() for AylaGrant. Otherwise we get constant "change" notifications, even though the grant has not changed
- Improved scan receiver registration
- New AylaDataPointBlob file preview support
- Add datapoint.echo() support - parity with iSDK
- Remove country code requirement from AylaUser.prepareUpdateArguments()
- Documentation updates
- Use gateway Ip address from DHCP as wifi setup address
- Correct the url in AylaPropertyTrigger.updateApp()
- Change method return type to AylaAPIRequest in AylaDevice.updateLocation()
- Change mtime type to long
- Add leading slash to LAN mode reply URLs

v5.3.01       11/09/2016

- Nexus 5x/6p WiFi setup improvements
- Life-cycle handling bug fixes

v5.3.00       10/25/2016

New and Improved
- Support for AMAP & Aura 5.3.00
- New Android 7 support
- New 4.x to 5.x SDK transition guide (see the /docs folder in github)
- New API for Notification/Alert History
- Retrieve all notifications sent for a given device
- New API for SSO Sign-out support
- New API WiFi Setup state listener
- Refresh the auth token on session resume, or close the session if it fails
- Optionally continue polling when we receive an error
- Do not clear setupDevice untill exitSetup() is called

Chores and Bug Fixes
- More and improved unit tests
- Improved AylaSSO support through AuthProvider Class
- All 5.2.xx hot-fixes

Special Notes In version 5.3 of the Ayla Mobile SDK:

changes were made related to SSO sign-in that will require additions to classes that implement AylaAuthProvider.

Because SSO providers manage user profiles as well as authenticating users, the AylaAuthProvider interface has been augmented with interfaces to update or delete the SSO user. The Ayla SDK will call these methods on the AylaAuthProvider that was used to sign in when requested to update or delete the session's user account.

The Ayla implementations of AylaAuthProvider (e.g. UsernameAuthProvider, CachedAuthProvider) have been updated to implement these methods to change the profile information on the Ayla user service. External identity providers will need to implement these methods to update or delete the account information on the identity provider's service. The following methods will need to be implemented in the mobile application on any class that implements the AylaAuthProvider interface:

// Updates the user profile information with the contents of the AylaUser parameter AylaAPIRequest updateUserProfile(AylaUser user, Listener successListener,ErrorListener errorListener);

// Deletes the current user account AylaAPIRequest deleteUser(AylaSessionManagersessionManager, Listener successListener, ErrorListener errorListener);

v5.2.00       08/19/2016

New & Improved
- Support for AMAP & Aura 5.2.00
- Support for new LAN OTA platform feature
- Unit tests using Robolectric

Bug Fixes & Chores
- Make sure to notify if timestamps change as well as datapoint values
- mDSS Heartbeat Support
- improvements for Offline Mode support
- Extended default network timeout by 2 seconds
- Improved authentication and session manager support
- Improved datapoint ACK support
- Setup and registration improvements

Known Issues
- Wifi setup will not work with Android N Beta. Scanning for WLAN access points fails. See https://code.google.com/p/android/issues/detail?id=219258.

v5.1.00       06/22/2016

New Features:
- Offline (LAN-only) support using cached device information
- Blob (opaque file) datapoint support
- Generic Gateway and Node support
- Ayla Log Service support
- Baidu push notification support
- Update account email address / password support
- Setup improvements including a new API to shut down AP mode on the module 

Enhancements and Bug Fixes:
- Improved contact and notification integration
- Typed (Java Generic) datapoints and properties
- Fixes to property trigger management APIs
- Fixes to schedule management APIs
- Device sharing fixes
- OAuth WebView now forgets password on sign out
- Improved error messaging
- Target Android SDK version set to 23

v5.0.04     06/07/2016
- WiFi Setup Improvements
- Add LAN mode timestamps to datapoint if missing

v5.0.03     06/01/2016
- Device notifications bug fixes

v5.0.02     05/16/2016
- WiFi Setup fixes for Nexus 5x / 6p devices
- Setup now returns last wifi connect status from connectDeviceToService
- Fix AylaProperty.updateTrigger to use PUT instead of POST

v5.0.01     05/06/2016
-Ensure device gets set for notifications
-Wifi setup improvments
-Include iOS push notifications

v5.0.00			04/19/2016
Initial release



