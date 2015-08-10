package com.github.pwittchen.reactivenetwork.app;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.pwittchen.reactivenetwork.R;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.github.pwittchen.reactivenetwork.library.ConnectivityStatus;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  private TextView tvConnectivityStatus;
  private ListView lvAccessPoints;
  private ReactiveNetwork reactiveNetwork;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tvConnectivityStatus = (TextView) findViewById(R.id.connectivity_status);
    lvAccessPoints = (ListView) findViewById(R.id.access_points);
  }

  @Override protected void onResume() {
    super.onResume();
    reactiveNetwork = new ReactiveNetwork();

    reactiveNetwork.observeConnectivity(this)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<ConnectivityStatus>() {
          @Override public void call(ConnectivityStatus connectivityStatus) {
            tvConnectivityStatus.setText(connectivityStatus.toString());
          }
        });

    reactiveNetwork.observeWifiAccessPoints(this)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<List<ScanResult>>() {
          @Override public void call(List<ScanResult> scanResults) {
            displayAccessPoints(scanResults);
          }
        });
  }

  private void displayAccessPoints(List<ScanResult> scanResults) {
    List<String> wifiScanResults = new ArrayList<>();

    for (ScanResult scanResult : scanResults) {
      wifiScanResults.add(scanResult.SSID);
    }

    lvAccessPoints.setAdapter(
        new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,
            wifiScanResults));

    String message = getString(R.string.wifi_signal_strength_changed);
    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
  }
}
