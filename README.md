# ReactiveNetwork

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ReactiveNetwork-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/2290)
[![Build Status](https://travis-ci.org/pwittchen/ReactiveNetwork.svg?branch=master)](https://travis-ci.org/pwittchen/ReactiveNetwork)
[![codecov](https://codecov.io/gh/pwittchen/ReactiveNetwork/branch/master/graph/badge.svg)](https://codecov.io/gh/pwittchen/ReactiveNetwork)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.pwittchen/reactivenetwork.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.pwittchen/reactivenetwork)

ReactiveNetwork is an Android library listening **network connection state** and **Internet connectivity** with RxJava Observables. It's a successor of [Network Events](https://github.com/pwittchen/NetworkEvents) library rewritten with Reactive Programming approach.

Library is compatible with RxJava 1.+ and RxAndroid 1.+ and uses them under the hood.

Library supports both new and legacy network monitoring strategies.

min sdk version = 9

JavaDoc is available at: http://pwittchen.github.io/ReactiveNetwork/

**Important note**:exclamation:: Since version **0.4.0**, functionality releated to **observing WiFi Access Points** and **WiFi singal strength (level)** is removed in favor of [ReactiveWiFi](https://github.com/pwittchen/ReactiveWiFi) library.
If you want to use this functionality, check [**ReactiveWiFi**](https://github.com/pwittchen/ReactiveWiFi) project.

If you want to see all changes in the public API, check [release notes](https://github.com/pwittchen/ReactiveNetwork/releases) and [JavaDoc](http://pwittchen.github.io/ReactiveNetwork/).

Contents
--------

- [Usage](#usage)
  - [Observing network connectivity](#observing-network-connectivity)
    - [Connectivity class](#connectivity-class)
  - [Observing Internet connectivity](#observing-internet-connectivity)
    - [Customization of observing Internet connectivity](#customization-of-observing-internet-connectivity)
  - [ProGuard configuration](#proguard-configuration)
- [Examples](#examples)
- [Download](#download)
- [Tests](#tests)
- [Code style](#code-style)
- [Static code analysis](#static-code-analysis)
- [Who is using this library?](#who-is-using-this-library)
- [Getting help](#getting-help)
- [Changelog](#changelog)
- [Releasing](#releasing)
- [License](#license)

Usage
-----

**Please note**: Due to memory leak in `WifiManager` reported
in [issue 43945](https://code.google.com/p/android/issues/detail?id=43945) in Android issue tracker
it's recommended to use Application Context instead of Activity Context.

### Observing network connectivity

We can observe `Connectivity` with `observeNetworkConnectivity(context)` method in the following way:

```java
ReactiveNetwork.observeNetworkConnectivity(context)
    .subscribeOn(Schedulers.io())
    ... // anything else what you can do with RxJava
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<Connectivity>() {
      @Override public void call(Connectivity connectivity) {
        // do something with connectivity
        // you can call connectivity.getState();
        // connectivity.getType(); or connectivity.toString();
      }
    });
```

When `Connectivity` changes, subscriber will be notified. `Connectivity` can change its state or type.

We can react on a concrete state, states, type or types changes with the `filter(...)` method from RxJava, `hasState(NetworkInfo.State... states)` and `hasType(int... types)` methods located in `ConnectivityPredicate` class.

```java
ReactiveNetwork.observeNetworkConnectivity(context)
    .subscribeOn(Schedulers.io())
    .filter(ConnectivityPredicate.hasState(NetworkInfo.State.CONNECTED))
    .filter(ConnectivityPredicate.hasType(ConnectivityManager.TYPE_WIFI))
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<Connectivity>() {
      @Override public void call(Connectivity connectivity) {
        // do something
      }
    });
```

`observeNetworkConnectivity(context)` checks only connectivity with the network (not Internet) as it's based on `BroadcastReceiver` for API 20 and lower and uses `NetworkCallback` for API 21 and higher.
 Concrete WiFi or mobile network may be connected to the Internet (and usually is), but it doesn't have to.

You can also use method:

```java
Observable<Connectivity> observeNetworkConnectivity(Context context, NetworkObservingStrategy strategy)
```

This method allows you to apply your own network observing strategy and is used by the library under the hood to determine appropriate strategy depending on the version of Android system.

#### Connectivity class

`Connectivity` class is used by `observeNetworkConnectivity(context)` and `observeNetworkConnectivity(context, networkObservingStrategy)` methods. It has the following API:

```java
Connectivity create()
Connectivity create(Context context)

NetworkInfo.State getState()
NetworkInfo.DetailedState getDetailedState()
int getType()
int getSubType()
boolean isAvailable()
boolean isFailover()
boolean isRoaming()
String getTypeName()
String getSubTypeName()
String getReason()
String getExtraInfo()

class Builder
```

### Observing Internet connectivity

We can observe connectivity with the Internet in the following way:

```java
ReactiveNetwork.observeInternetConnectivity()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Boolean>() {
          @Override public void call(Boolean isConnectedToInternet) {
            // do something with isConnectedToInternet value
          }
        });
```

An `Observable` will return `true` to the subscription if device is connected to the Internet and `false` if not.

Internet connectivity will be checked _as soon as possible_.

**Please note**: This method is less efficient than `observeNetworkConnectivity(context)` method, because it opens socket connection with remote host (default is www.google.com) every two seconds with two seconds of timeout and consumes data transfer. Use this method if you really need it. Optionally, you can unsubscribe subcription right after you get notification that Internet is available and do the work you want in order to decrease network calls.

#### Customization of observing Internet connectivity

Methods in this section should be used if they are really needed due to specific use cases.

If you want to specify your own custom details for checking Internet connectivity, you can use the following method:

```java
Observable<Boolean> observeInternetConnectivity(int interval, String host, int port, int timeout)
```

It allows you to specify custom interval of checking connectivity in milliseconds, host, port and connection timeout in milliseconds.

You can also use the following method:

```java
Observable<Boolean> observeInternetConnectivity(int initialIntervalInMs, int intervalInMs, String host, int port, int timeout)
```

It does the same thing as method above, but allows to define initial delay of the first Internet connectivity check. Default is equal to zero.

You can use method:

```java
Observable<Boolean> observeInternetConnectivity(final int initialIntervalInMs, final int intervalInMs, final String host, final int port, final int timeoutInMs, final ErrorHandler errorHandler)
```

which allows you to define `ErrorHandler` implementation, which handle any errors which can occur during checking connectivity.
By default library uses `DefaultErrorHandler`.

You can also use method:

```java
Observable<Boolean> observeInternetConnectivity(final InternetObservingStrategy strategy, final int initialIntervalInMs, final int intervalInMs, final String host, final int port, final int timeoutInMs, final ErrorHandler errorHandler)
```

which allows you to implement `ErrorHandler` and `InternetObservingStrategy` in case you want to have your own strategy for monitoring connectivity with the Internet.

You can use method:

```java
Observable<Boolean> observeInternetConnectivity(final InternetObservingStrategy strategy)
```

which allows you to implement custom `InternetObservingStrategy` in case you want to have your own strategy. Remaining settings will be default.

These methods are created to allow the users to fully customize the library and give them more control.

For more details check JavaDoc at: http://pwittchen.github.io/ReactiveNetwork/

### ProGuard configuration

```
-dontwarn com.github.pwittchen.reactivenetwork.library.ReactiveNetwork
-dontwarn io.reactivex.functions.Function
-dontwarn rx.internal.util.**
-dontwarn sun.misc.Unsafe
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
    <version>0.8.0</version>
</dependency>
```

or through Gradle:

```groovy
dependencies {
  compile 'com.github.pwittchen:reactivenetwork:0.8.0'
}
```

Tests
-----

Tests are available in `library/src/test/java/` directory and can be executed on JVM without any emulator or Android device from Android Studio or CLI with the following command:

```
./gradlew test
```

To generate test coverage report, run the following command:

```
./gradlew test jacocoTestReport
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

Who is using this library?
--------------------------
- [PAT Track - realtime Tracker for the public transit in Pittsburgh, PA](https://play.google.com/store/apps/details?id=rectangledbmi.com.pittsburghrealtimetracker)
- [Eero - Home WiFi System](https://play.google.com/store/apps/details?id=com.eero.android)
- [ACN Android Framework](https://github.com/ugurcany/ACN-Android-Framework)
- [Spatial Connect Android SDK](https://github.com/boundlessgeo/spatialconnect-android-sdk)
- [Qiscus SDK for Android](https://github.com/qiscus/qiscus-sdk-android)
- [Internet Radio](https://play.google.com/store/apps/details?id=com.stc.radio.player)
- [Tachiyomi](https://github.com/inorichi/tachiyomi)
- and more...

Are you using this library in your app and want to be listed here? Send me a Pull Request or an e-mail to piotr@wittchen.biz.pl

Getting help
------------

Do you need help related to using or configuring this library? 

You can do the following things:
- [Ask the question on StackOverflow](http://stackoverflow.com/questions/ask?tags=reactivenetwork)
- [Create new GitHub issue](https://github.com/pwittchen/ReactiveNetwork/issues/new)

Don't worry. Someone should help you with solving your problems.

Changelog
---------

See [CHANGELOG.md](https://github.com/pwittchen/ReactiveNetwork/blob/master/CHANGELOG.md) file.

Releasing
---------

See [RELEASING.md](https://github.com/pwittchen/ReactiveNetwork/blob/master/RELEASING.md) file.

License
-------

    Copyright 2016 Piotr Wittchen

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
