# ReactiveNetwork

[![Build Status](https://travis-ci.org/pwittchen/ReactiveNetwork.svg?branch=master)](https://travis-ci.org/pwittchen/ReactiveNetwork)

ReactiveNetwork is an Android library listening network connection state and change of the WiFi signal strength with RxJava Observables. It's a successor of [Network Events](https://github.com/pwittchen/NetworkEvents) library rewritten with Reactive Programming approach.

Library is compatible with RxJava 1.0.+ and RxAndroid 1.0.+ and uses them under the hood.

min sdk version = 9

JavaDoc is available at: http://pwittchen.github.io/ReactiveNetwork/

Contents
--------

- [Usage](#usage)
  - [Observing connectivity](#observing-connectivity)
  - [Observing WiFi Access Points](#observing-wifi-access-points)
- [Examples](#examples)
- [Download](#download)
- [Tests](#tests)
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
  WIFI_CONNECTED("connected to WiFi"),
  MOBILE_CONNECTED("connected to mobile network"),
  OFFLINE("offline");
  ...
}  
```

We can observe `ConnectivityStatus` with `observeConnectivity(context)` method in the following way:

```java
new ReactiveNetwork().observeConnectivity(context)
    .observeOn(AndroidSchedulers.mainThread())
    .subscribeOn(Schedulers.io())
    ... // anything else you can do with RxJava
    .subscribe(new Action1<ConnectivityStatus>() {
      @Override public void call(ConnectivityStatus connectivityStatus) {
        // do something with connectivityStatus
      }
    });
```

We can react on a concrete status or statuses with the `filter(...)` method from RxJava, `isEqualTo(final ConnectivityStatus... statuses)` and `isNotEqualTo(final ConnectivityStatus... statuses)` methods located in `ConnectivityStatus`.

```java
new ReactiveNetwork().observeConnectivity(context)
    .observeOn(AndroidSchedulers.mainThread())
    .subscribeOn(Schedulers.io())
    .filter(ConnectivityStatus.isEqualTo(ConnectivityStatus.WIFI_CONNECTED))
    .subscribe(new Action1<ConnectivityStatus>() {
      @Override public void call(ConnectivityStatus connectivityStatus) {
        // do something with connectivityStatus, which will be WIFI_CONNECTED
      }
    });
```

### Observing WiFi Access Points

We can observe WiFi Access Points with `observeWifiAccessPoints(context)` method. Subscriber will be called everytime, when strength of the WiFi Access Points signal changes. We can do it in the following way:

```java
new ReactiveNetwork().observeWifiAccessPoints(context)
    .observeOn(AndroidSchedulers.mainThread())
    .subscribeOn(Schedulers.io())
    ... // anything else you can do with RxJava
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

Library will be available in Maven Central Repository soon.

Tests
-----

Tests are available in `library/src/androidTest/java/` directory and can be executed on emulator or Android device from Android Studio or CLI with the following command:

```
./gradlew connectedCheck
```

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
