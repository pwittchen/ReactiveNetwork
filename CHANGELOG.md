CHANGELOG
=========

v. 0.12.1
--------
*02 Sep 2017*

Fixed memory leak in `PreLollipopNetworkObservingStrategy` during disposing of an `Observable` - issue #219.

v. 0.12.0
--------
*30 Aug 2017*

- Fixed NPE occuring when `ConnectivityManager` is `null` in `ReactiveNetwork.observeNetworkConnectivity()` method - issue #209
- Added new methods to the API for checking Internet connectivity - issue #205 
  - `Observable<Boolean> observeInternetConnectivity(strategy, host)`
  - `Single<Boolean> checkInternetConnectivity(strategy, host)`
- Added to documentation comment about monitoring Internet connectivity with custom host - issue #204 
- Classes which implement InternetObservingStrategy handle custom hosts with and without `http://` or `https://` prefix gracefully - issue #206
- organized packages with unit tests 
- made the library more hermetic
- changed visibility of `SocketInternetObservingStrategy#isConnected(String host, int port, int timeoutInMs, ErrorHandler handler)` method from `public` to `protected`
- changed visibility of `SocketInternetObservingStrategy#isConnected(Socket socket, String host, int port, int timeoutInMs, ErrorHandler errorHandler)` method from `public` to `protected`
- changed visibility of `Connectivity#create(Context, ConnectivityManager)` method from `public` to `protected`
- changed visibility of `WalledGardenInternetObservingStrategy#isConnected(String host, int port, int timeoutInMs, ErrorHandler errorHandler)` method from `public` to `protected`
- changed visibility of `WalledGardenInternetObservingStrategy#createHttpUrlConnection(String host, int port, int timeoutInMs)` method from `public` to `protected`

v. 0.11.0
--------
*05 Aug 2017*

- added `WalledGardenInternetObservingStrategy` - fixes #116
- made `WalledGardenInternetObservingStrategy` a default strategy for checking Internet connectivity
- added documentation for NetworkObservingStrategy - solves #197 
- added documentation for InternetObservingStrategy - solves #198
- fixed package name in `AndroidManifest.xml` file - solves #195 
- bumped RxJava2 version to 2.1.2
- bumped Kotlin version to 1.1.3-2
- bumped Gradle Android Tools version to 2.3.3
- bumped Retrolambda to 3.7.0
- increased code coverage with unit tests

v. 0.10.0
--------
*18 Jul 2017*

- bumped RxJava2 version to 2.1.1
- bumped test dependencies
- created Code of Conduct
- updated unit tests
- updated Kotlin version in sample apps
- added retrolambda to the sample Java app - issue #163 
- fixed behavior of network observing in disconnected state - issue #159 
- added the following methods to `ReactiveNetwork` class:
  - `Single<Boolean> checkInternetConnectivity()`
  - `Single<Boolean> checkInternetConnectivity(InternetObservingStrategy strategy)`
  - `Single<Boolean> checkInternetConnectivity(String host, int port, int timeoutInMs)`
  - `Single<Boolean> checkInternetConnectivity(String host, int port, int timeoutInMs, ErrorHandler errorHandler)`
  - `Single<Boolean> checkInternetConnectivity(InternetObservingStrategy strategy, String host, int port, int timeoutInMs, ErrorHandler errorHandler)`


v. 0.9.1
--------
*30 Apr 2017*

- updated `ConnectivityPredicate` and replaced `io.reactivex.functions.Function` with `io.reactivex.functions.Predicate` to make it compatible with RxJava2 filtering methods #168 
- bumped RxJava2.x version to 2.1.0

v. 0.9.0
--------
*11 Apr 2017*

- **migrated library to RxJava2.x** on RxJava2.x branch and released it as `reactivenetwork-rx2` artifact
- updated dependencies
- updated documentation
- updated sample apps

v. 0.8.0
--------
*12 Feb 2017*

- renamed `DefaultInternetObservingStrategy` to `SocketInternetObservingStrategy` class
- added `observeInternetConnectivity(InternetObservingStrategy)` method to `ReactiveNetwork` class
- removed `DefaultInternetObservingStrategy#ON_CLOSE_SOCKET_ERROR_MSG` static field
- added permission annotations
- updated `Connectivity` class. Now it contains the following fields with getters: `state, detailedState, type, subType, available, failover, roaming, typeName, subTypeName, reason, extraInfo` (it's wrapped data of `NetworkInfo` class from Android SDK)
- added `Builder` to the `Connectivity` class
- created `ConnectivityPredicate` class
- methods `Func1<Connectivity, Boolean> hasState(final NetworkInfo.State... states)` and `Func1<Connectivity, Boolean> hasType(final int... types)` were moved from `Connectivity` class to `ConnectivityPredicate` class
- updated Gradle and Travis configuration
- updated project dependencies

v. 0.7.0
--------
*11 Dec 2016*

- added `isConnected(final String host, final int port, final int timeoutInMs,
    ErrorHandler errorHandler)` method to `DefaultInternetObservingStrategy` class
- added `isConnected(final Socket socket, final String host, final int port, final int timeoutInMs,
    final ErrorHandler errorHandler)` method to `DefaultInternetObservingStrategy` class
- renamed `SocketErrorHandler` to `ErrorHandler` and updated its API
- renamed `DefaultSocketErrorHandler` to `DefaultErrorHandler`
- updated API of the `InternetObservingStrategy`
- updated packages organization
- migrated unit tests to Robolectric (now tests can be executed without an emulator or a device)
- enabled test coverage reports with codecov.io and Jacoco
- test coverage was increased from 54% to 74%
- unit tests are now executed on Travis CI
- test coverage report is generated by Travis CI with codecov.io
- added `MarshmallowNetworkObservingStrategy` and handling Doze mode
- bumped RxJava to v. 1.2.3
- updated build tools to v. 2.0.3
- updated Gradle configuration
- updated Travis CI configurati

v. 0.6.0
--------
*20 Oct 2016*

- fixed bug with the crash during unregister receiver for Pre-Lollipop devices #87 
- extended `NetworkObservingStrategy` with `void onError(String message, Exception exception)` method, which allows handling errors in network observing strategies
- closed the socket in the Internet connection check #91 
- added `SocketErrorHandler` with a default implementation in `DefaultSocketErrorHandler` class, which allows handling errors during closing socket connection
- delegated observing Internet connectivity functionality to separate class hidden behind `InternetObservingStrategy` interface
- added an `InternetObservingStrategy` interface with the default implementation in `DefaultInternetObservingStrategy` class, which allows to customize Internet observing strategy
- added `Observable<Boolean> observeInternetConnectivity(int initialIntervalInMs, int intervalInMs, String host, int port, int timeout)` method to `ReactiveNetwork` class
- added `Observable<Boolean> observeInternetConnectivity(final int initialIntervalInMs, final int intervalInMs, final String host, final int port, final int timeoutInMs, final SocketErrorHandler socketErrorHandler)` method to `ReactiveNetwork` class
- added `Observable<Boolean> observeInternetConnectivity(final InternetObservingStrategy strategy, final int initialIntervalInMs, final int intervalInMs, final String host, final int port, final int timeoutInMs, final SocketErrorHandler socketErrorHandler)` method to `ReactiveNetwork` class
- bumped RxJava version to 1.2.1

v. 0.5.2
--------
*03 Sep 2016*

- bumped RxJava to 1.1.9
- bumped Gradle Build Tools to 2.1.3

v. 0.5.1
--------
*30 Jul 2016*

- bumped RxJava to v. 1.1.8

v. 0.5.0
--------
*24 Jul 2016*

- handled all connection types (including Ethernet) (issue #71)
- removed `ConnectivityStatus` enum and replaced it with `Connectivity` class.
- replaced `Observable<ConnectivityStatus> observeNetworkConnectivity(final Context context)` method with `Observable<Connectivity> observeNetworkConnectivity(final Context context)`
- introduced a new way of network monitoring with [NetworkCallback](https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback.html) available from Android N (API 21) (issue #62)
- added `NetworkObservingStrategy`, which allows applying different network monitoring strategies
- added `PreLollipopNetworkObservingStrategy` with old network monitoring implementation
- added `LollipopNetworkObservingStrategy` with new network monitoring implementation
- added `Observable<Connectivity> observeNetworkConnectivity(final Context context, final NetworkObservingStrategy strategy)` method to ReactiveNetwork class
- made method for creating Observables static like in original RxJava library
- added `create()` method to `ReactiveNetwork` class
- made constructor of `ReactiveNetwork` class protected
- added `Preconditions` class verifying correctness of the input parameters
- added more unit tests

v. 0.4.0
--------
*11 Jun 2016*

**Removed deprecated methods from the public API**:
- removed depreacted `Observable<ConnectivityStatus> observeConnectivity(final Context context)` method in favor of `Observable<ConnectivityStatus> observeNetworkConnectivity(final Context context)` method
- removed depreacted `Observable<List<ScanResult>> observeWifiAccessPoints(final Context context)` method in favor of [ReactiveWiFi](https://github.com/pwittchen/ReactiveWiFi) library
- removed depreacted `Observable<WifiSignalLevel> observeWifiSignalLevel(final Context context)` method in favor of [ReactiveWiFi](https://github.com/pwittchen/ReactiveWiFi) library
- removed depreacted `Observable<Integer> observeWifiSignalLevel(final Context context,final int numLevels)` method in favor of [ReactiveWiFi](https://github.com/pwittchen/ReactiveWiFi) library

**Removed permissions in AndroidManifest.xml**:
- removed `android.permission.ACCESS_WIFI_STATE` permission from `AndroidManifest.xml`
- removed `android.permission.CHANGE_WIFI_STATE` permission from `AndroidManifest.xml`
- removed `android.permission.ACCESS_COARSE_LOCATION` permission from `AndroidManifest.xml`

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
