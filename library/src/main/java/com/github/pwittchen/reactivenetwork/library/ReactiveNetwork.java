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
package com.github.pwittchen.reactivenetwork.library;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import com.github.pwittchen.reactivenetwork.library.network.observing.NetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.network.observing.strategy.LollipopNetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.network.observing.strategy.PreLollipopNetworkObservingStrategy;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * ReactiveNetwork is an Android library
 * listening network connection state and change of the WiFi signal strength
 * with RxJava Observables. It can be easily used with RxAndroid.
 */
public class ReactiveNetwork {
  private static final String DEFAULT_PING_HOST = "www.google.com";
  private static final int DEFAULT_PING_PORT = 80;
  private static final int DEFAULT_PING_INTERVAL_IN_MS = 2000;
  private static final int DEFAULT_PING_TIMEOUT_IN_MS = 2000;

  /**
   * Observes network connectivity. Information about network state, type and name are contained in
   * observed Connectivity object.
   *
   * @param context Context of the activity or an application
   * @return RxJava Observable with Connectivity class containing information about network state,
   * type and name
   */
  public Observable<Connectivity> observeNetworkConnectivity(final Context context) {
    final NetworkObservingStrategy strategy;
    final boolean isAtLeastLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    if (isAtLeastLollipop) {
      strategy = new LollipopNetworkObservingStrategy();
    } else {
      strategy = new PreLollipopNetworkObservingStrategy();
    }

    return observeNetworkConnectivity(context, strategy);
  }

  /**
   * Observes network connectivity. Information about network state, type and name are contained in
   * observed Connectivity object. Moreover, allows you to define NetworkObservingStrategy.
   *
   * @param context Context of the activity or an application
   * @param strategy NetworkObserving strategy to be applied - you can use one of the existing
   * strategies {@link PreLollipopNetworkObservingStrategy},
   * {@link LollipopNetworkObservingStrategy} or create your own custom strategy
   * @return RxJava Observable with Connectivity class containing information about network state,
   * type and name
   */
  public Observable<Connectivity> observeNetworkConnectivity(final Context context,
      final NetworkObservingStrategy strategy) {
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
  public Observable<Boolean> observeInternetConnectivity() {
    return observeInternetConnectivity(DEFAULT_PING_INTERVAL_IN_MS, DEFAULT_PING_HOST,
        DEFAULT_PING_PORT, DEFAULT_PING_TIMEOUT_IN_MS);
  }

  /**
   * Observes connectivity with the Internet by opening socket connection with remote host
   *
   * @param interval in milliseconds determining how often we want to check connectivity
   * @param host for checking Internet connectivity
   * @param port for checking Internet connectivity
   * @param timeout for pinging remote host
   * @return RxJava Observable with Boolean - true, when we have connection with host and false if
   * not
   */
  public Observable<Boolean> observeInternetConnectivity(final int interval, final String host,
      final int port, final int timeout) {
    return Observable.interval(interval, TimeUnit.MILLISECONDS, Schedulers.io())
        .map(new Func1<Long, Boolean>() {
          @Override public Boolean call(Long tick) {
            try {
              Socket socket = new Socket();
              socket.connect(new InetSocketAddress(host, port), timeout);
              return socket.isConnected();
            } catch (IOException e) {
              return Boolean.FALSE;
            }
          }
        })
        .distinctUntilChanged();
  }
}
