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
package com.github.pwittchen.reactivenetwork.library.network.observing.strategy;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import com.github.pwittchen.reactivenetwork.library.Connectivity;
import com.github.pwittchen.reactivenetwork.library.network.observing.NetworkObservingStrategy;
import rx.Observable;
import rx.functions.Action0;
import rx.subjects.PublishSubject;

import static com.github.pwittchen.reactivenetwork.library.ReactiveNetwork.LOG_TAG;

/**
 * Network observing strategy for devices with Android Lollipop (API 21) or higher
 */
@TargetApi(21) public class LollipopNetworkObservingStrategy implements NetworkObservingStrategy {
  private static final String ON_ERROR_MSG = "could not unregister network callback";
  private NetworkCallback networkCallback;
  private PublishSubject<Connectivity> connectivitySubject = PublishSubject.create();
  private BroadcastReceiver idleReceiver;

  @Override public Observable<Connectivity> observeNetworkConnectivity(final Context context) {
    final String service = Context.CONNECTIVITY_SERVICE;
    final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(service);
    networkCallback = createNetworkCallback(context);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      registerIdleReceiver(context);
    }
    NetworkRequest request =
        new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .build();
    manager.registerNetworkCallback(request, networkCallback);
    return connectivitySubject.asObservable().onBackpressureLatest().doOnUnsubscribe(new Action0() {
      @Override public void call() {
        tryToUnregisterCallback(manager);
        tryToUnregisterReceiver(context);
      }
    }).startWith(Connectivity.create(context)).distinctUntilChanged();
  }

  @TargetApi(Build.VERSION_CODES.M) private void registerIdleReceiver(Context context) {
    IntentFilter intentFilter = new IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);
    idleReceiver = new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {
        if (isIdleMode(context)) {
          connectivitySubject.onNext(Connectivity.create());
        } else {
          connectivitySubject.onNext(Connectivity.create(context));
        }
      }
    };
    context.registerReceiver(idleReceiver, intentFilter);
  }

  @TargetApi(Build.VERSION_CODES.M) private boolean isIdleMode(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
      return powerManager.isDeviceIdleMode() && !powerManager.isIgnoringBatteryOptimizations(
          context.getPackageName());
    } else {
      return false;
    }
  }

  private void tryToUnregisterCallback(final ConnectivityManager manager) {
    try {
      manager.unregisterNetworkCallback(networkCallback);
    } catch (Exception exception) {
      onError(ON_ERROR_MSG, exception);
    }
  }

  @TargetApi(Build.VERSION_CODES.M) private void tryToUnregisterReceiver(Context context) {
    if (idleReceiver != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      try {
        context.unregisterReceiver(idleReceiver);
      } catch (Exception exception) {
        onError(ON_ERROR_MSG, exception);
      }
    }
  }

  @Override public void onError(final String message, final Exception exception) {
    Log.e(LOG_TAG, message, exception);
  }

  private NetworkCallback createNetworkCallback(final Context context) {
    return new ConnectivityManager.NetworkCallback() {
      @Override public void onAvailable(Network network) {
        connectivitySubject.onNext(Connectivity.create(context));
      }

      @Override public void onLost(Network network) {
        connectivitySubject.onNext(Connectivity.create(context));
      }
    };
  }
}
