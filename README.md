# ReactiveNetwork

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ReactiveNetwork-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/2290)
[![Build Status](https://travis-ci.org/pwittchen/ReactiveNetwork.svg?branch=master)](https://travis-ci.org/pwittchen/ReactiveNetwork)
![Maven Central](https://img.shields.io/maven-central/v/com.github.pwittchen/reactivenetwork.svg?style=flat)

ReactiveNetwork is an Android library listening network connection state and change of the WiFi signal strength with RxJava Observables. It's a successor of [Network Events](https://github.com/pwittchen/NetworkEvents) library rewritten with Reactive Programming approach.

Library is compatible with RxJava 1.+ and RxAndroid 1.+ and uses them under the hood.

min sdk version = 9

JavaDoc is available at: http://pwittchen.github.io/ReactiveNetwork/

Contents
--------

- [Usage](#usage)
  - [Observing network connectivity](#observing-network-connectivity)
  - [Observing Internet connectivity](#observing-internet-connectivity)
  - [Observing WiFi Access Points](#observing-wifi-access-points)
  - [Observing WiFi signal level](#observing-wifi-signal-level)
- [Examples](#examples)
- [Download](#download)
- [Tests](#tests)
- [Code style](#code-style)
- [Static code analysis](#static-code-analysis)
- [License](#license)

Usage
-----

Library has the following RxJava Observables available in the public API:

```java
Observable<ConnectivityStatus> observeNetworkConnectivity(final Context context)
Observable<Boolean> observeInternetConnectivity()
Observable<Boolean> observeInternetConnectivity(final int interval, final String host, final int port, final int timeout)
Observable<List<ScanResult>> observeWifiAccessPoints(final Context context)
Observable<Integer> observeWifiSignalLevel(final Context context, final int numLevels)
Observable<WifiSignalLevel> observeWifiSignalLevel(final Context context)
```

Moreover it has the following helper method for checking connectivity:

```java
ConnectivityStatus getConnectivityStatus(final Context context)
```

**Please note**: Due to memory leak in `WifiManager` reported
in [issue 43945](https://code.google.com/p/android/issues/detail?id=43945) in Android issue tracker
it's recommended to use Application Context instead of Activity Context.

### Observing network connectivity

`ConnectivityStatus` can have one of the following values:

```java
public enum ConnectivityStatus {
  UNKNOWN("unknown"),
  WIFI_CONNECTED("connected to WiFi"),
  MOBILE_CONNECTED("connected to mobile network"),
  OFFLINE("offline");
  ...
}  
```

We can observe `ConnectivityStatus` with `observeNetworkConnectivity(context)` method in the following way:

```java
new ReactiveNetwork().observeNetworkConnectivity(context)
    .subscribeOn(Schedulers.io())
    ... // anything else what you can do with RxJava
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<ConnectivityStatus>() {
      @Override public void call(ConnectivityStatus connectivityStatus) {
        // do something with connectivityStatus
      }
    });
```

When `ConnectivityStatus` changes, subscriber will be notified.

We can react on a concrete status or statuses with the `filter(...)` method from RxJava, `isEqualTo(final ConnectivityStatus... statuses)` and `isNotEqualTo(final ConnectivityStatus... statuses)` methods located in `ConnectivityStatus`.

```java
new ReactiveNetwork().observeNetworkConnectivity(context)
    .subscribeOn(Schedulers.io())
    .filter(ConnectivityStatus.isEqualTo(ConnectivityStatus.WIFI_CONNECTED))
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<ConnectivityStatus>() {
      @Override public void call(ConnectivityStatus connectivityStatus) {
        // do something with connectivityStatus, which will be WIFI_CONNECTED
      }
    });
```

`observeNetworkConnectivity(context)` checks only connectivity with the network (not Internet) as is based on BroadCastReceiver. Concrete WiFi or mobile network may be connected to the Internet (and usually is), but it doesn't have to.

### Observing Internet connectivity

We can observe connectivity with the Internet in the following way:

```java
new ReactiveNetwork().observeInternetConnectivity()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Boolean>() {
          @Override public void call(Boolean isConnectedToInternet) {
            // do something with isConnectedToInternet value
          }
        });
```

**Please note**: This method is less efficient than `observeNetworkConnectivity(context)` method, because it opens socket connection with remote host (default is www.google.com) and consumes data transfer. Use this method if you really need it.

If you want to specify your own custom details for checking Internet connectivity, you can use the following method:

```java
Observable<Boolean> observeInternetConnectivity(final int interval, final String host, final int port, final int timeout)
```

It allows you to specify custom interval of checking connectivity in milliseconds, host, port and connection timeout in milliseconds.

### Observing WiFi Access Points

We can observe WiFi Access Points with `observeWifiAccessPoints(context)` method. Subscriber will be called everytime, when strength of the WiFi Access Points signal changes (it usually happens when user is moving around with a mobile device). We can do it in the following way:

```java
new ReactiveNetwork().observeWifiAccessPoints(context)
    .subscribeOn(Schedulers.io())
    ... // anything else what you can do with RxJava
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<List<ScanResult>>() {
      @Override public void call(List<ScanResult> scanResults) {
        // do something with scanResults
      }
    });
```

**Hint**: If you want to operate on a single `ScanResult` instead of `List<ScanResult>` in a `subscribe(...)` method, consider using `flatMap(...)` and `Observable.from(...)` operators from RxJava for transforming the stream.

### Observing WiFi signal level

We can observe WiFi signal level with `observeWifiSignalLevel(context, numLevels)` method. Subscriber will be called everytime, when signal level of the connected WiFi  changes (it usually happens when user is moving around with a mobile device). We can do it in the following way:

```java
new ReactiveNetwork().observeWifiSignalLevel(context, numLevels)
    .subscribeOn(Schedulers.io())
    ... // anything else what you can do with RxJava
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<Integer>() {
      @Override public void call(Integer level) {
        // do something with level
      }
    });
```

We can also observe WiFi signal level with `observeWifiSignalLevel(final Context context)` method, which has predefined num levels value, which is equal to 4 and returns `Observable<WifiSignalLevel>`. `WifiSignalLevel` is an enum, which contains information about current signal level. We can do it as follows:

```java
new ReactiveNetwork().observeWifiSignalLevel(context)
    .subscribeOn(Schedulers.io())
    ... // anything else what you can do with RxJava
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<WifiSignalLevel>() {
      @Override public void call(WifiSignalLevel signalLevel) {
        // do something with signalLevel
      }
    });
```

`WifiSignalLevel` has the following values:

```java
public enum WifiSignalLevel {
  NO_SIGNAL(0, "no signal"),
  POOR(1, "poor"),
  FAIR(2, "fair"),
  GOOD(3, "good"),
  EXCELLENT(4, "excellent");
  ...
}
```

Examples
--------

Exemplary application is located in `app` directory of this repository.

If you want to know, how to use this library with Kotlin, check `app-kotlin` directory.

Download
--------

You can depend on the library through Maven:

```xml
<dependency>
    <groupId>com.github.pwittchen</groupId>
    <artifactId>reactivenetwork</artifactId>
    <version>0.2.0</version>
</dependency>
```

or through Gradle:

```groovy
dependencies {
  compile 'com.github.pwittchen:reactivenetwork:0.2.0'
}
```

Tests
-----

Tests are available in `library/src/androidTest/java/` directory and can be executed on emulator or Android device from Android Studio or CLI with the following command:

```
./gradlew connectedCheck
```

Code style
----------

Code style used in the project is called `SquareAndroid` from Java Code Styles repository by Square available at: https://github.com/square/java-code-styles.

Static code analysis
--------------------

Static code analysis runs Checkstyle, FindBugs, PMD and Lint. It can be executed with command:

 ```
 ./gradlew check
 ```

Reports from analysis are generated in `library/build/reports/` directory.

License
-------

    Copyright 2015 Piotr Wittchen

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
