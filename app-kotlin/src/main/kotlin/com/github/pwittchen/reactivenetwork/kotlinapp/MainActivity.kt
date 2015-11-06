package com.github.pwittchen.reactivenetwork.kotlinapp

import android.net.wifi.ScanResult
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork
import kotlinx.android.synthetic.activity_main.access_points
import kotlinx.android.synthetic.activity_main.connectivity_status
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class MainActivity : AppCompatActivity() {
    private var wifiSubscription: Subscription? = null
    private var connectivitySubscription: Subscription? = null

    companion object {
        private val TAG = "ReactiveNetwork"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        val reactiveNetwork: ReactiveNetwork = ReactiveNetwork()

        connectivitySubscription = reactiveNetwork.observeConnectivity(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { connectivityStatus ->
                    connectivity_status.text = connectivityStatus.toString();
                    Log.d(TAG, connectivityStatus.toString())
                }

        wifiSubscription = reactiveNetwork.observeWifiAccessPoints(this)
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
        safelyUnsubscribe(connectivitySubscription)
        safelyUnsubscribe(wifiSubscription)
    }

    private fun safelyUnsubscribe(subscription: Subscription?) {
        if (subscription != null && !subscription.isUnsubscribed) {
            subscription.unsubscribe()
        }
    }
}
