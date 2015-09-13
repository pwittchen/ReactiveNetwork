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

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.github.pwittchen.reactivenetwork.R;
import com.github.pwittchen.reactivenetwork.library.ConnectivityStatus;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import java.util.ArrayList;
import java.util.List;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "ReactiveNetwork";
  private TextView tvConnectivityStatus;
  private ListView lvAccessPoints;
  private ReactiveNetwork reactiveNetwork;
  private Subscription wifiSubscription;
  private Subscription connectivitySubscription;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tvConnectivityStatus = (TextView) findViewById(R.id.connectivity_status);
    lvAccessPoints = (ListView) findViewById(R.id.access_points);
  }

  @Override protected void onResume() {
    super.onResume();
    reactiveNetwork = new ReactiveNetwork();

    connectivitySubscription = reactiveNetwork.observeConnectivity(this)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<ConnectivityStatus>() {
          @Override public void call(ConnectivityStatus connectivityStatus) {
            Log.d(TAG, connectivityStatus.toString());
            tvConnectivityStatus.setText(connectivityStatus.toString());
          }
        });

    wifiSubscription = reactiveNetwork.observeWifiAccessPoints(this)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<List<ScanResult>>() {
          @Override public void call(List<ScanResult> scanResults) {
            displayAccessPoints(scanResults);
          }
        });
  }

  private void displayAccessPoints(List<ScanResult> scanResults) {
    List<String> ssids = new ArrayList<>();

    for (ScanResult scanResult : scanResults) {
      ssids.add(scanResult.SSID);
    }

    int itemLayoutId = android.R.layout.simple_list_item_1;
    lvAccessPoints.setAdapter(new ArrayAdapter<>(this, itemLayoutId, ssids));
  }

  @Override protected void onPause() {
    super.onPause();
    connectivitySubscription.unsubscribe();
    wifiSubscription.unsubscribe();
  }
}
