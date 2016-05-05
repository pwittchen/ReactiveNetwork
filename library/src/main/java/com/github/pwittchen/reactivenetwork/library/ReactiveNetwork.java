/*
 * Copyright (C) 2015 Piotr Wittchen
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Looper;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

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
  private ConnectivityStatus status = ConnectivityStatus.UNKNOWN;

  /**
   * Observes ConnectivityStatus,
   * which can be WIFI_CONNECTED, MOBILE_CONNECTED or OFFLINE
   *
   * @param context Context of the activity or an application
   * @return RxJava Observable with ConnectivityStatus
   */
  public Observable<ConnectivityStatus> observeNetworkConnectivity(final Context context) {
    final IntentFilter filter = new IntentFilter();
    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

    return Observable.create(new Observable.OnSubscribe<ConnectivityStatus>() {
      @Override public void call(final Subscriber<? super ConnectivityStatus> subscriber) {
        final BroadcastReceiver receiver = new BroadcastReceiver() {
          @Override public void onReceive(Context context, Intent intent) {
            final ConnectivityStatus newStatus = getConnectivityStatus(context);

            // we need to perform check below,
            // because after going off-line, onReceive() is called twice
            if (newStatus != status) {
              status = newStatus;
              subscriber.onNext(newStatus);
            }
          }
        };

        context.registerReceiver(receiver, filter);

        subscriber.add(unsubscribeInUiThread(new Action0() {
          @Override public void call() {
            context.unregisterReceiver(receiver);
          }
        }));
      }
    }).defaultIfEmpty(ConnectivityStatus.OFFLINE);
  }

  /**
   * Gets current network connectivity status
   *
   * @param context Application Context is recommended here
   * @return ConnectivityStatus, which can be WIFI_CONNECTED, MOBILE_CONNECTED or OFFLINE
   */
  public ConnectivityStatus getConnectivityStatus(final Context context) {
    final String service = Context.CONNECTIVITY_SERVICE;
    final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(service);
    final NetworkInfo networkInfo = manager.getActiveNetworkInfo();

    if (networkInfo == null) {
      return ConnectivityStatus.OFFLINE;
    }

    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
      return ConnectivityStatus.WIFI_CONNECTED;
    } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
      return ConnectivityStatus.MOBILE_CONNECTED;
    }

    return ConnectivityStatus.OFFLINE;
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

  /**
   * Observes WiFi Access Points.
   * Returns fresh list of Access Points
   * whenever WiFi signal strength changes.
   *
   * @param context Context of the activity or an application
   * @return RxJava Observable with list of WiFi scan results
   */
  public Observable<List<ScanResult>> observeWifiAccessPoints(final Context context) {
    final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    wifiManager.startScan(); // without starting scan, we may never receive any scan results

    final IntentFilter filter = new IntentFilter();
    filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
    filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

    return Observable.create(new Observable.OnSubscribe<List<ScanResult>>() {
      @Override public void call(final Subscriber<? super List<ScanResult>> subscriber) {
        final BroadcastReceiver receiver = new BroadcastReceiver() {
          @Override public void onReceive(Context context, Intent intent) {
            wifiManager.startScan(); // we need to start scan again to get fresh results ASAP
            subscriber.onNext(wifiManager.getScanResults());
          }
        };

        context.registerReceiver(receiver, filter);

        subscriber.add(unsubscribeInUiThread(new Action0() {
          @Override public void call() {
            context.unregisterReceiver(receiver);
          }
        }));
      }
    });
  }

  /**
   * Observes WiFi signal level with predefined max num levels.
   * Returns WiFi signal level as enum with information about current level
   *
   * @param context Context of the activity or an application
   * @return WifiSignalLevel as an enum
   */
  public Observable<WifiSignalLevel> observeWifiSignalLevel(final Context context) {
    return observeWifiSignalLevel(context, WifiSignalLevel.getMaxLevel()).map(
        new Func1<Integer, WifiSignalLevel>() {
          @Override public WifiSignalLevel call(Integer level) {
            return WifiSignalLevel.fromLevel(level);
          }
        });
  }

  /**
   * Observes WiFi signal level.
   * Returns WiFi signal level as an integer
   *
   * @param context Context of the activity or an application
   * @param numLevels The number of levels to consider in the calculated level as Integer
   * @return RxJava Observable with WiFi signal level
   */
  public Observable<Integer> observeWifiSignalLevel(final Context context, final int numLevels) {
    final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    final IntentFilter filter = new IntentFilter();
    filter.addAction(WifiManager.RSSI_CHANGED_ACTION);

    return Observable.create(new Observable.OnSubscribe<Integer>() {
      @Override public void call(final Subscriber<? super Integer> subscriber) {
        final BroadcastReceiver receiver = new BroadcastReceiver() {
          @Override public void onReceive(Context context, Intent intent) {
            final int rssi = wifiManager.getConnectionInfo().getRssi();
            final int level = WifiManager.calculateSignalLevel(rssi, numLevels);
            subscriber.onNext(level);
          }
        };

        context.registerReceiver(receiver, filter);

        subscriber.add(unsubscribeInUiThread(new Action0() {
          @Override public void call() {
            context.unregisterReceiver(receiver);
          }
        }));
      }
    }).defaultIfEmpty(0);
  }

  private Subscription unsubscribeInUiThread(final Action0 unsubscribe) {
    return Subscriptions.create(new Action0() {

      @Override public void call() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
          unsubscribe.call();
        } else {
          final Scheduler.Worker inner = AndroidSchedulers.mainThread().createWorker();
          inner.schedule(new Action0() {
            @Override public void call() {
              unsubscribe.call();
              inner.unsubscribe();
            }
          });
        }
      }
    });
  }
}
