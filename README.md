# ReactiveNetwork

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ReactiveNetwork-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/2290)
[![Build Status](https://travis-ci.org/pwittchen/ReactiveNetwork.svg?branch=master)](https://travis-ci.org/pwittchen/ReactiveNetwork)
![Maven Central](https://img.shields.io/maven-central/v/com.github.pwittchen/reactivenetwork.svg?style=flat)

ReactiveNetwork is an Android library listening **network connection state** and **Internet connectivity** with RxJava Observables. It's a successor of [Network Events](https://github.com/pwittchen/NetworkEvents) library rewritten with Reactive Programming approach.

Library is compatible with RxJava 1.+ and RxAndroid 1.+ and uses them under the hood.

min sdk version = 9

JavaDoc is available at: http://pwittchen.github.io/ReactiveNetwork/

**Important note**:exclamation:: Since version **0.4.0**, functionality releated to **observing WiFi Access Points** and **WiFi singal strength (level)** is removed in favor of [ReactiveWiFi](https://github.com/pwittchen/ReactiveWiFi) library.
If you want to use this functionality, check [**ReactiveWiFi**](https://github.com/pwittchen/ReactiveWiFi) project.

If you want to see all changes in the public API, check [release notes](https://github.com/pwittchen/ReactiveNetwork/releases) and [JavaDoc](http://pwittchen.github.io/ReactiveNetwork/).

Contents
--------

- [Usage](#usage)
  - [Observing network connectivity](#observing-network-connectivity)
  - [Observing Internet connectivity](#observing-internet-connectivity)
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
Observable<Connectivity> observeNetworkConnectivity(final Context context)
Observable<Boolean> observeInternetConnectivity()
Observable<Boolean> observeInternetConnectivity(final int interval, final String host, final int port, final int timeout)
```

**Please note**: Due to memory leak in `WifiManager` reported
in [issue 43945](https://code.google.com/p/android/issues/detail?id=43945) in Android issue tracker
it's recommended to use Application Context instead of Activity Context.

### Observing network connectivity

`Connectivity` class has the following methods:

```java
// factory methods responsible for creating Connectivity object
Connectivity create()
Connectivity create(Context context)
Connectivity create(NetworkInfo.State state, int type, String name)

// methods returning information about connectivity
NetworkInfo.State getState()
int getType()
String getName()
boolean isDefault()
String toString()

// helper methods for filter(...) method from RxJava
Func1<Connectivity, Boolean> hasState(final NetworkInfo.State... states)
Func1<Connectivity, Boolean> hasType(final int... types)
```

We can observe `Connectivity` with `observeNetworkConnectivity(context)` method in the following way:

```java
new ReactiveNetwork().observeNetworkConnectivity(context)
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

We can react on a concrete state, states, type or types changes with the `filter(...)` method from RxJava, `hasState(final NetworkInfo.State... states)` and `hasType(final int... types)` methods located in `Connectivity` class.

```java
new ReactiveNetwork().observeNetworkConnectivity(context)
    .subscribeOn(Schedulers.io())
    .filter(Connectivity.hasState(NetworkInfo.State.CONNECTED))
    .filter(Connectivity.hasType(ConnectivityManager.TYPE_WIFI))
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<Connectivity>() {
      @Override public void call(Connectivity connectivity) {
        // do something
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

An `Observable` will return `true` to the subscription if device is connected to the Internet and `false` if not.

**Please note**: This method is less efficient than `observeNetworkConnectivity(context)` method, because it opens socket connection with remote host (default is www.google.com) every two seconds with two seconds of timeout and consumes data transfer. Use this method if you really need it.

If you want to specify your own custom details for checking Internet connectivity, you can use the following method:

```java
Observable<Boolean> observeInternetConnectivity(final int interval, final String host, final int port, final int timeout)
```

It allows you to specify custom interval of checking connectivity in milliseconds, host, port and connection timeout in milliseconds.

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
    <version>0.4.0</version>
</dependency>
```

or through Gradle:

```groovy
dependencies {
  compile 'com.github.pwittchen:reactivenetwork:0.4.0'
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
