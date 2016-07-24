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
package com.github.pwittchen.reactivenetwork.app;

import android.app.Activity;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.github.pwittchen.reactivenetwork.library.Connectivity;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {
  private static final String TAG = "ReactiveNetwork";
  private TextView tvConnectivityStatus;
  private TextView tvInternetStatus;
  private ReactiveNetwork reactiveNetwork;
  private Subscription networkConnectivitySubscription;
  private Subscription internetConnectivitySubscription;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tvConnectivityStatus = (TextView) findViewById(R.id.connectivity_status);
    tvInternetStatus = (TextView) findViewById(R.id.internet_status);
  }

  @Override protected void onResume() {
    super.onResume();
    reactiveNetwork = new ReactiveNetwork();

    networkConnectivitySubscription =
        reactiveNetwork.observeNetworkConnectivity(getApplicationContext())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Connectivity>() {
              @Override public void call(final Connectivity connectivity) {
                Log.d(TAG, connectivity.toString());
                final NetworkInfo.State state = connectivity.getState();
                final String name = connectivity.getName();
                tvConnectivityStatus.setText(String.format("state: %s, name: %s", state, name));
              }
            });

    internetConnectivitySubscription = reactiveNetwork.observeInternetConnectivity()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Boolean>() {
          @Override public void call(Boolean isConnectedToInternet) {
            tvInternetStatus.setText(isConnectedToInternet.toString());
          }
        });
  }

  @Override protected void onPause() {
    super.onPause();
    safelyUnsubscribe(networkConnectivitySubscription, internetConnectivitySubscription);
  }

  private void safelyUnsubscribe(Subscription... subscriptions) {
    for (Subscription subscription : subscriptions) {
      if (subscription != null && !subscription.isUnsubscribed()) {
        subscription.unsubscribe();
      }
    }
  }
}
