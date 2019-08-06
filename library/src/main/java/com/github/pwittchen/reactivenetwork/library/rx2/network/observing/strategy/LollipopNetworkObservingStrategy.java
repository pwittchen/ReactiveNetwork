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
package com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkRequest;
import android.util.Log;
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.NetworkObservingStrategy;
import com.jakewharton.nopen.annotation.Open;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;

import static com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork.LOG_TAG;

/**
 * Network observing strategy for devices with Android Lollipop (API 21) or higher.
 * Uses Network Callback API.
 */
@Open @TargetApi(21) public class LollipopNetworkObservingStrategy
    implements NetworkObservingStrategy {
  @SuppressWarnings("NullAway") // it has to be initialized in the Observable due to Context
  private NetworkCallback networkCallback;

  @Override public Observable<Connectivity> observeNetworkConnectivity(final Context context) {
    final String service = Context.CONNECTIVITY_SERVICE;
    final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(service);

    return Observable.create(new ObservableOnSubscribe<Connectivity>() {
      @Override public void subscribe(ObservableEmitter<Connectivity> subscriber) throws Exception {
        networkCallback = createNetworkCallback(subscriber, context);
        final NetworkRequest networkRequest = new NetworkRequest.Builder().build();
        manager.registerNetworkCallback(networkRequest, networkCallback);
      }
    }).doOnDispose(new Action() {
      @Override public void run() {
        tryToUnregisterCallback(manager);
      }
    }).startWith(Connectivity.create(context)).distinctUntilChanged();
  }

  private void tryToUnregisterCallback(final ConnectivityManager manager) {
    try {
      manager.unregisterNetworkCallback(networkCallback);
    } catch (Exception exception) {
      onError("could not unregister network callback", exception);
    }
  }

  @Override public void onError(final String message, final Exception exception) {
    Log.e(LOG_TAG, message, exception);
  }

  private NetworkCallback createNetworkCallback(final ObservableEmitter<Connectivity> subscriber,
      final Context context) {
    return new ConnectivityManager.NetworkCallback() {
      @Override public void onAvailable(Network network) {
        subscriber.onNext(Connectivity.create(context));
      }

      @Override public void onLost(Network network) {
        subscriber.onNext(Connectivity.create(context));
      }
    };
  }
}
