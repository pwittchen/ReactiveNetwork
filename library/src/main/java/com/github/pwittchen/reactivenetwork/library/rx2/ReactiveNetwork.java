/*
 * Copyright (C) 2016 Piotr Wittchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pwittchen.reactivenetwork.library.rx2;

import android.Manifest;
import android.content.Context;
import androidx.annotation.RequiresPermission;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.NetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.LollipopNetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.MarshmallowNetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.PreLollipopNetworkObservingStrategy;
import com.jakewharton.nopen.annotation.Open;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * ReactiveNetwork is an Android library
 * listening network connection state and change of the WiFi signal strength
 * with RxJava Observables. It can be easily used with RxAndroid.
 */
@Open public class ReactiveNetwork {
  public final static String LOG_TAG = "ReactiveNetwork";

  protected ReactiveNetwork() {
  }

  /**
   * Creates a new instance of the ReactiveNetwork class
   *
   * @return ReactiveNetwork object
   */
  public static ReactiveNetwork create() {
    return new ReactiveNetwork();
  }

  /**
   * Observes network connectivity. Information about network state, type and typeName are contained
   * in
   * observed Connectivity object.
   *
   * @param context Context of the activity or an application
   * @return RxJava Observable with Connectivity class containing information about network state,
   * type and typeName
   */
  @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
  public static Observable<Connectivity> observeNetworkConnectivity(final Context context) {
    final NetworkObservingStrategy strategy;

    if (Preconditions.isAtLeastAndroidMarshmallow()) {
      strategy = new MarshmallowNetworkObservingStrategy();
    } else if (Preconditions.isAtLeastAndroidLollipop()) {
      strategy = new LollipopNetworkObservingStrategy();
    } else {
      strategy = new PreLollipopNetworkObservingStrategy();
    }

    return observeNetworkConnectivity(context, strategy);
  }

  /**
   * Observes network connectivity. Information about network state, type and typeName are contained
   * in observed Connectivity object. Moreover, allows you to define NetworkObservingStrategy.
   *
   * @param context Context of the activity or an application
   * @param strategy NetworkObserving strategy to be applied - you can use one of the existing
   * strategies {@link PreLollipopNetworkObservingStrategy},
   * {@link LollipopNetworkObservingStrategy} or create your own custom strategy
   * @return RxJava Observable with Connectivity class containing information about network state,
   * type and typeName
   */
  @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
  public static Observable<Connectivity> observeNetworkConnectivity(final Context context,
      final NetworkObservingStrategy strategy) {
    Preconditions.checkNotNull(context, "context == null");
    Preconditions.checkNotNull(strategy, "strategy == null");
    return strategy.observeNetworkConnectivity(context);
  }

  /**
   * Observes connectivity with the Internet with default settings. It pings remote host
   * (www.google.com) at port 80 every 2 seconds with 2 seconds of timeout. This operation is used
   * for determining if device is connected to the Internet or not. Please note that this method is
   * less efficient than {@link #observeNetworkConnectivity(Context)} method and consumes data
   * transfer, but it gives you actual information if device is connected to the Internet or not.
   *
   * @return RxJava Observable with Boolean - true, when we have an access to the Internet
   * and false if not
   */
  @RequiresPermission(Manifest.permission.INTERNET)
  public static Observable<Boolean> observeInternetConnectivity() {
    InternetObservingSettings settings = InternetObservingSettings.create();
    return observeInternetConnectivity(settings.strategy(), settings.initialInterval(),
        settings.interval(), settings.host(), settings.port(),
        settings.timeout(), settings.httpResponse(), settings.errorHandler());
  }

  /**
   * Observes connectivity with the Internet in a given time interval.
   *
   * @param settings Internet Observing Settings created via Builder pattern
   * @return RxJava Observable with Boolean - true, when we have connection with host and false if
   * not
   */
  @RequiresPermission(Manifest.permission.INTERNET)
  public static Observable<Boolean> observeInternetConnectivity(
      InternetObservingSettings settings) {
    return observeInternetConnectivity(settings.strategy(), settings.initialInterval(),
        settings.interval(), settings.host(), settings.port(),
        settings.timeout(), settings.httpResponse(), settings.errorHandler());
  }

  /**
   * Observes connectivity with the Internet in a given time interval.
   *
   * @param strategy for observing Internet connectivity
   * @param initialIntervalInMs in milliseconds determining the delay of the first connectivity
   * check
   * @param intervalInMs in milliseconds determining how often we want to check connectivity
   * @param host for checking Internet connectivity
   * @param port for checking Internet connectivity
   * @param timeoutInMs for pinging remote host in milliseconds
   * @param httpResponse expected HTTP response code indicating that connection is established
   * @param errorHandler for handling errors during connectivity check
   * @return RxJava Observable with Boolean - true, when we have connection with host and false if
   * not
   */
  @RequiresPermission(Manifest.permission.INTERNET)
  protected static Observable<Boolean> observeInternetConnectivity(
      final InternetObservingStrategy strategy, final int initialIntervalInMs,
      final int intervalInMs, final String host, final int port, final int timeoutInMs,
      final int httpResponse, final ErrorHandler errorHandler) {
    checkStrategyIsNotNull(strategy);
    return strategy.observeInternetConnectivity(initialIntervalInMs, intervalInMs, host, port,
        timeoutInMs, httpResponse, errorHandler);
  }

  /**
   * Checks connectivity with the Internet. This operation is performed only once.
   *
   * @return RxJava Single with Boolean - true, when we have an access to the Internet
   * and false if not
   */
  @RequiresPermission(Manifest.permission.INTERNET)
  public static Single<Boolean> checkInternetConnectivity() {
    InternetObservingSettings settings = InternetObservingSettings.create();
    return checkInternetConnectivity(settings.strategy(), settings.host(), settings.port(),
        settings.timeout(), settings.httpResponse(), settings.errorHandler());
  }

  /**
   * Checks connectivity with the Internet. This operation is performed only once.
   *
   * @param settings Internet Observing Settings created via Builder pattern
   * @return RxJava Single with Boolean - true, when we have connection with host and false if
   * not
   */
  @RequiresPermission(Manifest.permission.INTERNET)
  public static Single<Boolean> checkInternetConnectivity(InternetObservingSettings settings) {
    return checkInternetConnectivity(settings.strategy(), settings.host(), settings.port(),
        settings.timeout(), settings.httpResponse(), settings.errorHandler());
  }

  /**
   * Checks connectivity with the Internet. This operation is performed only once.
   *
   * @param strategy for observing Internet connectivity
   * @param host for checking Internet connectivity
   * @param port for checking Internet connectivity
   * @param timeoutInMs for pinging remote host in milliseconds
   * @param httpResponse expected HTTP response code indicating that connection is established
   * @param errorHandler for handling errors during connectivity check
   * @return RxJava Single with Boolean - true, when we have connection with host and false if
   * not
   */
  @RequiresPermission(Manifest.permission.INTERNET)
  protected static Single<Boolean> checkInternetConnectivity(
      final InternetObservingStrategy strategy,
      final String host, final int port, final int timeoutInMs, final int httpResponse,
      final ErrorHandler errorHandler) {
    checkStrategyIsNotNull(strategy);
    return strategy.checkInternetConnectivity(host, port, timeoutInMs, httpResponse, errorHandler);
  }

  private static void checkStrategyIsNotNull(InternetObservingStrategy strategy) {
    Preconditions.checkNotNull(strategy, "strategy == null");
  }
}
