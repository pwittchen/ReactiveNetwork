CHANGELOG
=========

v. 0.1.3
--------
*06 Nov 2015*

- fixed bug with incorrect status after going back from background inside the sample app reported in issue #31
- fixed RxJava usage in sample app
- fixed RxJava usage in code snippets in `README.md`
- added static code analysis
- updated code formatting
- added sample sample app in Kotlin

v. 0.1.2
--------
*01 Oct 2015*

- now library is emitting `OFFLINE` status at subscription time, when device is not connected to any network
- bumped target SDK version to 23
- bumped buildToolsVersion to 23.0.1
- removed `CHANGE_NETWORK_STATE` and `INTERNET` permissions from `AndroidManifest.xml`, because they're no longer required

v. 0.1.1
--------
*27 Sep 2015*

- bumped RxJava to v. 1.0.14
- bumped Gradle Build Tools to v. 1.3.1

v. 0.1.0
--------
*13 Sep 2015*

- changed `UNDEFINED` status to `UNKNOWN`
- added `WIFI_CONNECTED_HAS_INTERNET` and `WIFI_CONNECTED_HAS_NO_INTERNET` statuses
- added `enableInternetCheck()` method to `ReactiveNetwork` object. When it's called, `WIFI_CONNECTED_HAS_INTERNET` and `WIFI_CONNECTED_HAS_NO_INTERNET` statuses can occur. Otherwise, only `WIFI_CONNECTED` can occur.

v. 0.0.4
--------
*02 Sep 2015*

- added `WifiManager.SCAN_RESULTS_AVAILABLE_ACTION` to BroadcastReceiver responsible for receiving WiFi access points scan results
- fixed bug connected with improper work of `observeWifiAccessPoints()` method reported in issue #8
- updated sample app

v. 0.0.3
--------
*01 Sep 2015*

- removed `WifiManager.WIFI_STATE_CHANGED_ACTION` filter from BroadcastReceiver for observing connectivity (now we're observing only situation when device connects to the network or disconnects from the network - not situation when user turns WiFi on or off)
- added `UNDEFINED` element for `ConnectivityStatus`
- fixed bug causing emission of the same `ConnectivityStatus` twice

v. 0.0.2
--------
*20 Aug 2015*

- improved WiFi Access Points scanning
- updated documentation

v. 0.0.1
--------
*10 Aug 2015*

First release of the library.
