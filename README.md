# ReactiveNetwork

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ReactiveNetwork-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/2290)
[![Build Status](https://travis-ci.org/pwittchen/ReactiveNetwork.svg?branch=master)](https://travis-ci.org/pwittchen/ReactiveNetwork)
![Maven Central](https://img.shields.io/maven-central/v/com.github.pwittchen/reactivenetwork.svg?style=flat)

ReactiveNetwork is an Android library listening network connection state and change of the WiFi signal strength with RxJava Observables. It's a successor of [Network Events](https://github.com/pwittchen/NetworkEvents) library rewritten with Reactive Programming approach.

Library is compatible with RxJava 1.0.+ and RxAndroid 1.0.+ and uses them under the hood.

min sdk version = 9

JavaDoc is available at: http://pwittchen.github.io/ReactiveNetwork/

Contents
--------

- [Usage](#usage)
  - [Observing connectivity](#observing-connectivity)
  - [Enabling Internet check](#enabling-internet-check)
  - [Observing WiFi Access Points](#observing-wifi-access-points)
- [Examples](#examples)
- [Download](#download)
- [Tests](#tests)
- [Code style](#code-style)
- [Static code analysis](#static-code-analysis)
- [License](#license)

Usage
-----

Library has two RxJava Observables available in the public API:

```java
Observable<ConnectivityStatus> observeConnectivity(final Context context)
Observable<List<ScanResult>> observeWifiAccessPoints(final Context context)
```

### Observing connectivity

`ConnectivityStatus` can have one of the following values:

```java
public enum ConnectivityStatus {
  UNKNOWN("unknown"),
  WIFI_CONNECTED("connected to WiFi"),
  WIFI_CONNECTED_HAS_INTERNET("connected to WiFi (Internet available)"),
  WIFI_CONNECTED_HAS_NO_INTERNET("connected to WiFi (Internet not available)"),
  MOBILE_CONNECTED("connected to mobile network"),
  OFFLINE("offline");
  ...
}  
```

We can observe `ConnectivityStatus` with `observeConnectivity(context)` method in the following way:

```java
new ReactiveNetwork().observeConnectivity(context)
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
new ReactiveNetwork().observeConnectivity(context)
    .subscribeOn(Schedulers.io())
    .filter(ConnectivityStatus.isEqualTo(ConnectivityStatus.WIFI_CONNECTED))
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<ConnectivityStatus>() {
      @Override public void call(ConnectivityStatus connectivityStatus) {
        // do something with connectivityStatus, which will be WIFI_CONNECTED
      }
    });
```

### Enabling Internet check

This feature is available from 0.1.0 version.

Internet connection check is disabled by default. We can enable it in the following way:

```java
new ReactiveNetwork().enableInternetCheck()
  .observeConnectivity(context)
```

Please note, that after enabling it, we will receive only one of the following events after connecting to WiFi network:
- `WIFI_CONNECTED_HAS_INTERNET`
- `WIFI_CONNECTED_HAS_NO_INTERNET`

In such case, pure `WIFI_CONNECTED` status will never occur.

When internet connection check is disabled (by default), we will receive only `WIFI_CONNECTED` status after connecting to WiFi. In such case, other statuses with `WIFI_` prefix will never occur.

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

Examples
--------

Exemplary application is located in `app` directory of this repository.

Download
--------

You can depend on the library through Maven:

```xml
<dependency>
    <groupId>com.github.pwittchen</groupId>
    <artifactId>reactivenetwork</artifactId>
    <version>0.1.2</version>
</dependency>
```

or through Gradle:

```groovy
dependencies {
  compile 'com.github.pwittchen:reactivenetwork:0.1.2'
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
