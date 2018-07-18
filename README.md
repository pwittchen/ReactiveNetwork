# ReactiveNetwork

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ReactiveNetwork-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/2290) 

view website with documentation: [RxJava1.x](http://pwittchen.github.io/ReactiveNetwork/docs/RxJava1.x/), [**RxJava2.x**](http://pwittchen.github.io/ReactiveNetwork/docs/RxJava2.x/)

ReactiveNetwork is an Android library listening **network connection state** and **Internet connectivity** with RxJava Observables. It's a successor of [Network Events](https://github.com/pwittchen/NetworkEvents) library rewritten with Reactive Programming approach. Library supports both new and legacy network monitoring strategies. Min sdk version = 9.

| Current Branch | Branch  | Artifact Id | Build Status  | Coverage | Maven Central |
|:--------------:|:-------:|:-----------:|:-------------:|:--------:|:-------------:|
| | [`RxJava1.x`](https://github.com/pwittchen/ReactiveNetwork/tree/RxJava1.x) | `reactivenetwork` | [![Build Status for RxJava1.x](https://travis-ci.org/pwittchen/ReactiveNetwork.svg?branch=RxJava1.x)](https://travis-ci.org/pwittchen/ReactiveNetwork) | [![codecov](https://codecov.io/gh/pwittchen/ReactiveNetwork/branch/RxJava1.x/graph/badge.svg)](https://codecov.io/gh/pwittchen/ReactiveNetwork/branch/RxJava1.x) | ![Maven Central](https://img.shields.io/maven-central/v/com.github.pwittchen/reactivenetwork.svg?style=flat) |
| :ballot_box_with_check: | [`RxJava2.x`](https://github.com/pwittchen/ReactiveNetwork/tree/RxJava2.x) | `reactivenetwork-rx2` | [![Build Status for RxJava2.x](https://travis-ci.org/pwittchen/ReactiveNetwork.svg?branch=RxJava2.x)](https://travis-ci.org/pwittchen/ReactiveNetwork) | [![codecov](https://codecov.io/gh/pwittchen/ReactiveNetwork/branch/RxJava2.x/graph/badge.svg)](https://codecov.io/gh/pwittchen/ReactiveNetwork/branch/RxJava2.x) | ![Maven Central](https://img.shields.io/maven-central/v/com.github.pwittchen/reactivenetwork-rx2.svg?style=flat) |

Contents
--------

- [Usage](#usage)
  - [Observing network connectivity](#observing-network-connectivity)
    - [Connectivity class](#connectivity-class)
    - [Network Observing Strategies](#network-observing-strategies)
  - [Observing Internet connectivity](#observing-internet-connectivity)
    - [Observing Internet connectivity continuously](#observing-internet-connectivity-continuously)
    - [Checking Internet connectivity once](#checking-internet-connectivity-once)
    - [Internet Observing Strategies](#internet-observing-strategies)
    - [Custom host](#custom-host)
  - [Chaining network and Internet connectivity streams](#chaining-network-and-internet-connectivity-streams)
  - [Integration with other libraries](#integration-with-other-libraries)
    - [Integration with OkHttp](#integration-with-okhttp)
    - [Integration with Retrofit](#integration-with-retrofit)
  - [ProGuard configuration](#proguard-configuration)
- [Examples](#examples)
- [Download](#download)
- [Tests](#tests)
- [Code style](#code-style)
- [Static code analysis](#static-code-analysis)
- [Who is using this library?](#who-is-using-this-library)
- [Getting help](#getting-help)
  - [Tutorials](#tutorials)
- [Caveats](#caveats)
- [Changelog](#changelog)
- [JavaDoc](#javadoc)
- [Documentation](#documentation)
- [Releasing](#releasing)
- [Contributors](#contributors)
- [References](#references)
  - [Mentions](#mentions)
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
    .subscribe(new Consumer<Connectivity>() {
      @Override public void accept(final Connectivity connectivity) {
        // do something with connectivity
        // you can call connectivity.state();
        // connectivity.type(); or connectivity.toString();
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
    .subscribe(new Consumer<Connectivity>() {
      @Override public void accept(final Connectivity connectivity) {
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

NetworkInfo.State state()
NetworkInfo.DetailedState detailedState()
int type()
int subType()
boolean available()
boolean failover()
boolean roaming()
String typeName()
String subTypeName()
String reason()
String extraInfo()

// and respective setters

class Builder
```

#### Network Observing Strategies

Right now, we have the following strategies for different Android versions:
- `LollipopNetworkObservingStrategy`
- `MarshmallowNetworkObservingStrategy`
- `PreLollipopNetworkObservingStrategy`

All of them implements `NetworkObservingStrategy` interface.
Concrete strategy is chosen automatically depending on the Android version installed on the device.
With `observeNetworkConnectivity(context, strategy)` method we can use one of these strategies explicitly.

### Observing Internet connectivity

#### Observing Internet connectivity continuously

We can observe connectivity with the Internet continuously in the following way:

```java
ReactiveNetwork.observeInternetConnectivity()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Boolean>() {
          @Override public void accept(Boolean isConnectedToInternet) {
            // do something with isConnectedToInternet value
          }
        });
```

An `Observable` will return `true` to the subscription (disposable) if device is connected to the Internet and `false` if not.

Internet connectivity will be checked _as soon as possible_.

**Please note**: This method is less efficient than `observeNetworkConnectivity(context)` method, because in default observing strategy, it opens socket connection with remote host (default is www.google.com) every two seconds with two seconds of timeout and consumes data transfer. Use this method if you really need it. Optionally, you can dispose subscription (disposable) right after you get notification that Internet is available and do the work you want in order to decrease network calls.

Methods in this section should be used if they are really needed due to specific use cases.

If you want to customize observing of the Internet connectivity, you can use `InternetObservingSettings` class and its builder.
They allow to customize monitoring interval in milliseconds, host, port, timeout, initial monitoring interval, timeout, error handler or whole observing strategy.

```java
InternetObservingSettings settings = InternetObservingSettings
    .initialInterval(initialInterval)
    .interval(interval)
    .host(host)
    .port(port)
    .timeout(timeout)
    .errorHandler(testErrorHandler)
    .strategy(strategy)
    .build();

ReactiveNetwork.observeInternetConnectivity(settings)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Boolean>() {
          @Override public void accept(Boolean isConnectedToInternet) {
            // do something with isConnectedToInternet value
          }
        });
```

These methods are created to allow the users to fully customize the library and give them more control.

For more details check JavaDoc at: http://pwittchen.github.io/ReactiveNetwork/javadoc/RxJava2.x

#### Checking Internet Connectivity once

If we don't want to observe Internet connectivity in the interval with `Observable<Boolean> observeInternetConnectivity(...)` method,
we can use `Single<Boolean> checkInternetConnectivity()`, which does the same thing, but **only once**.
It may be helpful in the specific use cases.

```java
Single<Boolean> single = ReactiveNetwork.checkInternetConnectivity();

single
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Consumer<Boolean>() {
      @Override public void accept(@NonNull Boolean isConnectedToTheInternet) throws Exception {
        // do something with isConnectedToTheInternet
      }
    });
```

As in the previous case, you can customize this feature with the `InternetObservingSettings` class and its builder.

```java
InternetObservingSettings settings = InternetObservingSettings
    .initialInterval(initialInterval)
    .interval(interval)
    .host(host)
    .port(port)
    .timeout(timeout)
    .errorHandler(testErrorHandler)
    .strategy(strategy)
    .build();

Single<Boolean> single = ReactiveNetwork.checkInternetConnectivity(settings);

single
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Consumer<Boolean>() {
      @Override public void accept(@NonNull Boolean isConnectedToTheInternet) throws Exception {
        // do something with isConnectedToTheInternet
      }
    });
```

Basic idea is the same. With just have `Single<Boolean>` return type instead of `Observable<Boolean>`
and we don't have `int initialIntervalInMs` and `int intervalInMs` parameters.

As previously, these methods are created to allow the users to fully customize the library and give them more control.

For more details check JavaDoc at: http://pwittchen.github.io/ReactiveNetwork/javadoc/RxJava2.x

#### Internet Observing Strategies

Right now, we have the following strategies for observing Internet connectivity:
- `SocketInternetObservingStrategy` -  monitors Internet connectivity via opening socket connection with the remote host
- `WalledGardenInternetObservingStrategy` - opens connection with a remote host and respects countries in the Walled Garden (e.g. China)

All of these strategies implements `NetworkObservingStrategy` interface. Default strategy used right now is `WalledGardenInternetObservingStrategy`,
but with `checkInternetConnectivity(strategy)` and `observeInternetConnectivity(strategy)` method we can use one of these strategies explicitly.

#### Custom host

If you want to ping custom host during checking Internet connectivity, it's recommended to use `SocketInternetObservingStrategy`.
You can do it as follows:

```java
InternetObservingSettings settings = InternetObservingSettings
    .host("www.yourhost.com")
    .strategy(new SocketInternetObservingStrategy())
    .build();

ReactiveNetwork.observeInternetConnectivity(settings)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Consumer<Boolean>() {
      @Override public void accept(@NonNull Boolean isConnectedToHost) throws Exception {
        // do something with isConnectedToHost
      }
    });
```

The same operation can be done with `checkInternetConnectivity(strategy, host)` method, which returns `Single` instead of `Observable`.

### Chaining network and Internet connectivity streams

Let's say we want to react on each network connectivity change and if we get connected to the network, then we want to check if that network is connected to the Internet. We can do it in the following way:

```java
ReactiveNetwork
  .observeNetworkConnectivity(getApplicationContext())
  .flatMapSingle(connectivity -> ReactiveNetwork.checkInternetConnectivity())
  .subscribeOn(Schedulers.io())
  .observeOn(AndroidSchedulers.mainThread())
  .subscribe(isConnected -> {
    // isConnected can be true or false
});
```

### Integration with other libraries

We can integrate ReactiveNetwork with other libraries. Especially those, which support RxJava2. In this section, we can find examples showing how to integrate this library with the OkHttp and Retrofit.

#### Integration with OkHttp

In order to integrate library with OkHttp, we need to wrap HTTP request with reactive type (e.g. `Observable`)

```java
private Observable<Response> getResponse(String url) {
  OkHttpClient client = new OkHttpClient();
  Request request = new Request.Builder().url(url).build();

  return Observable.create(emitter -> {
    try {
        Response response = client.newCall(request).execute();
        emitter.onNext(response);
    } catch (IOException exception) {
        emitter.onError(exception);
    } finally {
        emitter.onComplete();
    }
  });
}
```

Next, we can chain two streams:

```java
ReactiveNetwork
   .observeNetworkConnectivity(getApplicationContext())
   .flatMap(connectivity -> {
     if (connectivity.state() == NetworkInfo.State.CONNECTED) {
       return getResponse("http://github.com");
     }
     return Observable.error(() -> new RuntimeException("not connected"));
   })
   .subscribeOn(Schedulers.io())
   .observeOn(AndroidSchedulers.mainThread())
   .subscribe(
       response  -> /* handle response here */,
       throwable -> /* handle error here */)
   );
```

In the example above, whenever we get connected to the network, then request will be performed.

For more details regarding OkHttp, please visit its official website: http://square.github.io/okhttp/.

#### Integration with Retrofit

We can integrate ReactiveNetwork with the Retrofit.

First, we need to configure Retrofit:

```java
Retrofit retrofit = new Retrofit.Builder()
   .baseUrl("https://api.github.com/")
   .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
   .addConverterFactory(GsonConverterFactory.create())
   .build();
```

As you see, we need `RxJava2CallAdapterFactory` here.

Next, we need to define appropriate interface with RxJava `Single` types:

```java
public interface GitHubService {
 @GET("users/{user}/repos")
 Single<List<Repo>> listRepos(@Path("user") String user);
}
```

and instantiate the service:

```java
GitHubService service = retrofit.create(GitHubService.class);
```

Next, we want to call endpoint defined with the Retrofit whenever we get connected to the network. We can do it as follows:

```java
ReactiveNetwork
   .observeNetworkConnectivity(getApplicationContext())
   .flatMapSingle(connectivity -> service.listRepos("pwittchen"))
   .subscribeOn(Schedulers.io())
   .observeOn(AndroidSchedulers.mainThread())
   .subscribe(
       repos     -> /* handle repos here */,
       throwable -> /* handle error here */
   );
```

For more details regarding Retrofit, please visit its official website: http://square.github.io/retrofit/

### ProGuard configuration

```
-dontwarn com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
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
    <artifactId>reactivenetwork-rx2</artifactId>
    <version>1.0.0</version>
</dependency>
```

or through Gradle:

```groovy
dependencies {
  implementation 'com.github.pwittchen:reactivenetwork-rx2:1.0.0'
}
```

**Note**: If you are using Gradle version lower than 3.0, replace `implementation` with `compile`

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

Static code analysis runs Checkstyle, FindBugs, PMD, Lint, ErrorProne and NullAway. It can be executed with command:

 ```
 ./gradlew check
 ```

Static code analysis for the sample Kotlin app with detekt can be executed as follows:

```
./gradlew detektCheck
```

Reports from analysis are generated in `library/build/reports/` directory.

Who is using this library?
--------------------------
- [SkyScanner Android app](https://play.google.com/store/apps/details?id=net.skyscanner.android.main)
- [Slack Android app](https://play.google.com/store/apps/details?id=com.Slack)
- [PAT Track - realtime Tracker for the public transit in Pittsburgh, PA](https://play.google.com/store/apps/details?id=rectangledbmi.com.pittsburghrealtimetracker)
- [Eero - Home WiFi System](https://play.google.com/store/apps/details?id=com.eero.android)
- [ACN Android Framework](https://github.com/ugurcany/ACN-Android-Framework)
- [Spatial Connect Android SDK](https://github.com/boundlessgeo/spatialconnect-android-sdk)
- [Qiscus SDK for Android](https://github.com/qiscus/qiscus-sdk-android)
- [Internet Radio](https://play.google.com/store/apps/details?id=com.stc.radio.player)
- [Tachiyomi](https://github.com/inorichi/tachiyomi)
- [Actinium - V2Ray client for Android](https://github.com/V2Ray-Android/Actinium)
- [Project Bass - Android app](http://projectbass.org/)
- and more...

Are you using this library in your app and want to be listed here? Send me a Pull Request or an e-mail to piotr@wittchen.io

Getting help
------------

Do you need help related to using or configuring this library? 

You can do the following things:
- [Ask the question on StackOverflow](http://stackoverflow.com/questions/ask?tags=reactivenetwork)
- [Create new GitHub issue](https://github.com/pwittchen/ReactiveNetwork/issues/new)

Don't worry. Someone should help you with solving your problems.

### Tutorials

If you speak Spanish (Español), check out this tutorial: [ReactiveNetwork - Como funciona y como se integra en una app](https://www.youtube.com/watch?v=H7xGmQaKPsI) made by [Video Tutorials Android](https://www.youtube.com/channel/UC2q5P9JVoA6N8mE622gRP7w).

Caveats
-------

Since version **0.4.0**, functionality releated to **observing WiFi Access Points** and **WiFi signal strength (level)** is removed in favor of [ReactiveWiFi](https://github.com/pwittchen/ReactiveWiFi) library.
If you want to use this functionality, check [**ReactiveWiFi**](https://github.com/pwittchen/ReactiveWiFi) project.

Changelog
---------

See [CHANGELOG.md](https://github.com/pwittchen/ReactiveNetwork/blob/RxJava2.x/CHANGELOG.md) file.

JavaDoc
-------

JavaDoc is available at: http://pwittchen.github.io/ReactiveNetwork/javadoc/RxJava2.x

It can be generated as follows:

```
./gradlew androidJavadocs
```

In order to update JavaDoc on GitHub pages, use the following bash script:

```
./update_javadocs.sh
```

Then commit and push your changes to `gh-pages` branch.

Documentation
--------------------------

view website with documentation: [RxJava1.x](http://pwittchen.github.io/ReactiveNetwork/docs/RxJava1.x/), [**RxJava2.x**](http://pwittchen.github.io/ReactiveNetwork/docs/RxJava2.x/)

It can be generated as follows:

Copy the latest `README.md` file from `RxJava1.x` or `RxJava2.x` branch. Then checkout to `gh-pages` branch and put it into appropriate directory inside `docs/` directory.

You can do it as follows via bash script:

```
./update_docs.sh
git push
```

Install docsify with the following command:

```
npm i docsify-cli -g
```

Go into appropriate directory and type:

```
docsify init .
```

Right now it's already generated, so we can just update the `README.md` file and adjust generated files manually.

Next, we can just save changes, commit and push them to remote repository.


Releasing
---------

See [RELEASING.md](https://github.com/pwittchen/ReactiveNetwork/blob/RxJava2.x/RELEASING.md) file.

Contributors
------------

- [Piotr Wittchen](https://github.com/pwittchen) - project lead
- [Tushar Acharya](https://github.com/tushar-acharya)
- [Timothy Kist](https://github.com/Kisty)
- [@dilongl](https://github.com/dilongl)
- [@llp](https://github.com/llp)
- [Adam Gabryś](https://github.com/agabrys)
- [@lion4ik](https://github.com/lion4ik)
- [@futtetennista](https://github.com/futtetennista)
- [Manu Sridharan](https://github.com/msridhar)
- [Alexander Perfilyev](https://github.com/aperfilyev)
- [Vishesh Vadhera](https://github.com/VisheshVadhera)

References
----------
- [Android Documentation - Detect network changes, then change app behavior](https://developer.android.com/develop/quality-guidelines/building-for-billions-connectivity.html#network-behavior)
- [Android Documentation - Provide onboarding experiences for users' network choices](https://developer.android.com/develop/quality-guidelines/building-for-billions-data-cost.html#configurablenetwork-onboarding)
- [Android Documentation - Managing Network Usage](https://developer.android.com/training/basics/network-ops/managing.html)
- [RxJava](https://github.com/ReactiveX/RxJava)
- [DroidCon Poland 2017 presentation slides - Is your app really connected?](https://speakerdeck.com/pwittchen/is-your-app-really-connected)

### Mentions
- [Android Weekly #166](http://androidweekly.net/issues/issue-166)
- [Android Weekly #289](http://androidweekly.net/issues/issue-289)
- [Android Weekly China #44](http://www.androidweekly.cn/android-dev-weekly-issue44/)
- [Android Arsenal #2290](https://android-arsenal.com/details/1/2290)
- [GitHub Trending, 14th of Aug 2015](https://twitter.com/TrendingGithub/status/632117206801891328)
- [TL DEV TECH - Best Android Libraries in 2017](http://www.tldevtech.com/best-android-libraries-2017/)

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
