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
package com.github.pwittchen.reactivenetwork.kotlinapp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.connectivity_status
import kotlinx.android.synthetic.main.activity_main.internet_status

class MainActivity : Activity() {
  private var connectivityDisposable: Disposable? = null
  private var internetDisposable: Disposable? = null

  companion object {
    private val TAG = "ReactiveNetwork"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  override fun onResume() {
    super.onResume()

    connectivityDisposable = ReactiveNetwork.observeNetworkConnectivity(applicationContext)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe { connectivity ->
        Log.d(TAG, connectivity.toString())
        val state = connectivity.state()
        val name = connectivity.typeName()
        connectivity_status.text = String.format("state: %s, typeName: %s", state, name)
      }

    internetDisposable = ReactiveNetwork.observeInternetConnectivity()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe { isConnectedToInternet ->
        internet_status.text = isConnectedToInternet.toString()
      }
  }

  override fun onPause() {
    super.onPause()
    safelyDispose(connectivityDisposable)
    safelyDispose(internetDisposable)
  }

  private fun safelyDispose(disposable: Disposable?) {
    if (disposable != null && !disposable.isDisposed) {
      disposable.dispose()
    }
  }
}