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
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {
  private static final String TAG = "ReactiveNetwork";
  private TextView tvConnectivityStatus;
  private TextView tvInternetStatus;
  private Disposable networkDisposable;
  private Disposable internetDisposable;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tvConnectivityStatus = (TextView) findViewById(R.id.connectivity_status);
    tvInternetStatus = (TextView) findViewById(R.id.internet_status);
  }

  @Override protected void onResume() {
    super.onResume();

    networkDisposable = ReactiveNetwork.observeNetworkConnectivity(getApplicationContext())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(connectivity -> {
          Log.d(TAG, connectivity.toString());
          final NetworkInfo.State state = connectivity.state();
          final String name = connectivity.typeName();
          tvConnectivityStatus.setText(String.format("state: %s, typeName: %s", state, name));
        });

    internetDisposable = ReactiveNetwork.observeInternetConnectivity()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isConnected -> tvInternetStatus.setText(isConnected.toString()));
  }

  @Override protected void onPause() {
    super.onPause();
    safelyDispose(networkDisposable, internetDisposable);
  }

  private void safelyDispose(Disposable... disposables) {
    for (Disposable subscription : disposables) {
      if (subscription != null && !subscription.isDisposed()) {
        subscription.dispose();
      }
    }
  }
}
