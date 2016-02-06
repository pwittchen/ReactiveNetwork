package com.github.pwittchen.reactivenetwork.kotlinapp

import android.app.Activity
import android.net.wifi.ScanResult
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork
import kotlinx.android.synthetic.activity_main.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers
import java.util.*

class MainActivity : Activity() {
  private var wifiSub: Subscription? = null
  private var connectivitySub: Subscription? = null
  private var signalLevelSub: Subscription? = null

  companion object {
    private val TAG = "ReactiveNetwork"
    private val WIFI_NUM_LEVELS = 4;
    private val WIFI_SIGNAL_LEVEL_MESSAGE = "wifi signal level: ";
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  override fun onResume() {
    super.onResume()
    val reactiveNetwork: ReactiveNetwork = ReactiveNetwork()

    connectivitySub = reactiveNetwork.observeConnectivity(applicationContext)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { connectivityStatus ->
          connectivity_status.text = connectivityStatus.toString();
          Log.d(TAG, connectivityStatus.toString())
        }

    signalLevelSub = reactiveNetwork.observeWifiSignalLevel(applicationContext, WIFI_NUM_LEVELS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { signalLevel ->
          val message = WIFI_SIGNAL_LEVEL_MESSAGE.concat(signalLevel.toString())
          wifi_signal_level.text = message
          Log.d(TAG, message);
        }

    wifiSub = reactiveNetwork.observeWifiAccessPoints(applicationContext)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { scanResults -> displayAccessPoints(scanResults) }
  }

  private fun displayAccessPoints(scanResults: List<ScanResult>) {
    val ssids = ArrayList<String>()

    for (scanResult in scanResults) {
      ssids.add(scanResult.SSID)
    }

    access_points.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ssids)
  }

  override fun onPause() {
    super.onPause()
    safelyUnsubscribe(connectivitySub)
    safelyUnsubscribe(signalLevelSub)
    safelyUnsubscribe(wifiSub)
  }

  private fun safelyUnsubscribe(subscription: Subscription?) {
    if (subscription != null && !subscription.isUnsubscribed) {
      subscription.unsubscribe()
    }
  }
}
