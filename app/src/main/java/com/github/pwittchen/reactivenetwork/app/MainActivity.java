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
package com.github.pwittchen.reactivenetwork.app;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.pwittchen.reactivenetwork.R;
import com.github.pwittchen.reactivenetwork.library.ConnectivityStatus;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;

import com.github.pwittchen.reactivenetwork.library.WifiSignalLevel;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

  private static final String TAG = "ReactiveNetwork";
  private static final String WIFI_SIGNAL_LEVEL_MESSAGE = "WiFi signal level: ";
  private TextView tvConnectivityStatus;
  private TextView tvWifiSignalLevel;
  private ListView lvAccessPoints;
  private ReactiveNetwork reactiveNetwork;
  private Subscription wifiSubscription;
  private Subscription connectivitySubscription;
  private Subscription signalLevelSubscription;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tvConnectivityStatus = (TextView) findViewById(R.id.connectivity_status);
    lvAccessPoints = (ListView) findViewById(R.id.access_points);
    tvWifiSignalLevel = (TextView) findViewById(R.id.wifi_signal_level);
  }

  @Override protected void onResume() {
    super.onResume();
    reactiveNetwork = new ReactiveNetwork();

    connectivitySubscription = reactiveNetwork.observeConnectivity(getApplicationContext())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<ConnectivityStatus>() {
          @Override public void call(ConnectivityStatus connectivityStatus) {
            Log.d(TAG, connectivityStatus.toString());
            tvConnectivityStatus.setText(connectivityStatus.toString());
          }
        });

    signalLevelSubscription = reactiveNetwork.observeWifiSignalLevel(getApplicationContext())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<WifiSignalLevel>() {
          @Override public void call(WifiSignalLevel wifiSignalLevel) {
            Log.d(TAG, wifiSignalLevel.toString());
            final String description = wifiSignalLevel.description;
            tvWifiSignalLevel.setText(WIFI_SIGNAL_LEVEL_MESSAGE.concat(description));
          }
        });

    wifiSubscription = reactiveNetwork.observeWifiAccessPoints(getApplicationContext())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<ScanResult>>() {
          @Override public void call(List<ScanResult> scanResults) {
            displayAccessPoints(scanResults);
          }
        });
  }

  private void displayAccessPoints(List<ScanResult> scanResults) {
    final List<String> ssids = new ArrayList<>();

    for (ScanResult scanResult : scanResults) {
      ssids.add(scanResult.SSID);
    }

    int itemLayoutId = android.R.layout.simple_list_item_1;
    lvAccessPoints.setAdapter(new ArrayAdapter<>(this, itemLayoutId, ssids));
  }

  @Override protected void onPause() {
    super.onPause();
    safelyUnsubscribe(connectivitySubscription);
    safelyUnsubscribe(wifiSubscription);
    safelyUnsubscribe(signalLevelSubscription);
  }

  private void safelyUnsubscribe(Subscription subscription) {
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }
}
