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
import java.util.List;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * ReactiveNetwork is an Android library
 * listening network connection state and change of the WiFi signal strength
 * with RxJava Observables. It can be easily used with RxAndroid.
 */
public final class ReactiveNetwork {

  /**
   * Observes ConnectivityStatus,
   * which can be WIFI_CONNECTED, MOBILE_CONNECTED or OFFLINE
   *
   * @param context Context of the activity or an application
   * @return RxJava Observable with ConnectivityStatus
   */
  public Observable<ConnectivityStatus> observeConnectivity(final Context context) {
    final IntentFilter filter = new IntentFilter();
    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

    return Observable.create(new Observable.OnSubscribe<ConnectivityStatus>() {

      @Override public void call(final Subscriber<? super ConnectivityStatus> subscriber) {
        final BroadcastReceiver receiver = new BroadcastReceiver() {

          @Override public void onReceive(Context context, Intent intent) {
            subscriber.onNext(getConnectivityStatus(context));
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

  private ConnectivityStatus getConnectivityStatus(Context context) {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

    if (networkInfo != null) {
      if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
        return ConnectivityStatus.WIFI_CONNECTED;
      } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
        return ConnectivityStatus.MOBILE_CONNECTED;
      }
    }

    return ConnectivityStatus.OFFLINE;
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

    final IntentFilter filter = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);

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
