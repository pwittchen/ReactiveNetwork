CHANGELOG
=========

v. 0.4.0
--------
*11 Jun 2016*

**Removed deprecated methods from the public API**:
- removed depreacted `Observable<ConnectivityStatus> observeConnectivity(final Context context)` method in favor of `Observable<ConnectivityStatus> observeNetworkConnectivity(final Context context)` method
- removed depreacted `Observable<List<ScanResult>> observeWifiAccessPoints(final Context context)` method in favor of [ReactiveWiFi](https://github.com/pwittchen/ReactiveWiFi) library
- removed depreacted `Observable<WifiSignalLevel> observeWifiSignalLevel(final Context context)` method in favor of [ReactiveWiFi](https://github.com/pwittchen/ReactiveWiFi) library
- removed depreacted `Observable<Integer> observeWifiSignalLevel(final Context context,final int numLevels)` method in favor of [ReactiveWiFi](https://github.com/pwittchen/ReactiveWiFi) library

v. 0.3.0
--------
*07 Jun 2016*

- removed `enableInternetCheck()` method
- removed  `ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET` enum value
- removed `ConnectivityStatus.WIFI_CONNECTED_HAS_NO_INTERNET` enum value
- changed method name from `Observable<ConnectivityStatus> observeConnectivity(final Context context)` to `Observable<ConnectivityStatus> observeNetworkConnectivity(final Context context)`
- deprecated `Observable<ConnectivityStatus> observeConnectivity(final Context context)` method
- deprecated `Observable<List<ScanResult>> observeWifiAccessPoints(final Context context)` method
- depreceated `Observable<WifiSignalLevel> observeWifiSignalLevel(final Context context)` method
- deprecated `Observable<Integer> observeWifiSignalLevel(final Context context, final int numLevels)` method
- changed method signature from `ConnectivityStatus getConnectivityStatus(final Context context, final boolean checkInternet)` to `ConnectivityStatus getConnectivityStatus(final Context context)`
- updated `ConnectivityStatus getConnectivityStatus(final Context context)` method and added JavaDoc for it
- added `Observable<Boolean> observeInternetConnectivity(final int interval, final String host, final int port, final int timeout)` method
- added `Observable<Boolean> observeInternetConnectivity()` method
- added `android.permission.INTERNET` to the `AndroidManifest.xml`
- updated JavaDoc
- updated sample apps
- updated documentation in `README.md`
- bumped RxJava version to 1.1.5
- bumped RxAndroid version to 1.2.0
- bumped Google Truth version to 0.28 (test dependency)

v. 0.2.0
--------
*10 Feb 2016*

- added possibility to observe WiFi signal level with `observeWifiSignalLevel(context, numLevels)`  and `observeWifiSignalLevel(context)` method
- created `WifiSignalLevel` enum
- added internet check to parameters of `getConnectivityStatus(context, checkInternet)` method
- made `getConnectivityStatus(context, checkInternet)` method public
- changed String variable `status` in `ConnectivityStatus` enum to `description` and made it public
- changed output of the `toString()` method in `ConnectivityStatus` to keep consistency with another enum
- made `ReactiveNetwork` class non-final
- bumped Kotlin version in sample app to 1.0.0-rc-1036
- increased immutability of code of the library
- updated sample apps and documentation

v. 0.1.5
--------
*10 Jan 2016*

- Due to memory leak in WifiManager reported in issue [43945](https://code.google.com/p/android/issues/detail?id=43945) in Android issue tracker replaced Activity Context with Application Context in sample apps and added appropriate note in `README.md`
- added `ACCESS_COARSE_LOCATION` permission to `AndroidManifest.xml` to be able to scan WiFi access points on Android 6

v. 0.1.4
--------
*13 Dec 2015*

- bumped RxJava dependency to v. 1.1.0
- bumped RxAndroid dependency to v. 1.1.0
- bumped Google Truth test dependency to v. 0.27

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
