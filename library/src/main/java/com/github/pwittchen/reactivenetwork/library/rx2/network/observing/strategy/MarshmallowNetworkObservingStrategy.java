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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.PowerManager;
import android.util.Log;
import androidx.annotation.NonNull;
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import com.github.pwittchen.reactivenetwork.library.rx2.info.NetworkState;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.NetworkObservingStrategy;
import com.jakewharton.nopen.annotation.Open;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.reactivestreams.Publisher;

import static com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork.LOG_TAG;

/**
 * Network observing strategy for devices with Android Marshmallow (API 23) or higher.
 * Uses Network Callback API and handles Doze mode.
 */
@Open
@TargetApi(23)
public class MarshmallowNetworkObservingStrategy implements NetworkObservingStrategy {
  protected static final String ERROR_MSG_NETWORK_CALLBACK =
      "could not unregister network callback";
  protected static final String ERROR_MSG_RECEIVER = "could not unregister receiver";
  @SuppressWarnings("NullAway") // it has to be initialized in the Observable due to Context
  private ConnectivityManager.NetworkCallback networkCallback;
  private final Subject<Connectivity> connectivitySubject;
  private final BroadcastReceiver idleReceiver;
  private Connectivity lastConnectivity = Connectivity.create();

  @SuppressWarnings("FieldMayBeFinal") private NetworkState networkState = new NetworkState();

  @SuppressWarnings("NullAway") // networkCallback cannot be initialized here
  public MarshmallowNetworkObservingStrategy() {
    this.idleReceiver = createIdleBroadcastReceiver();
    this.connectivitySubject = PublishSubject.<Connectivity>create().toSerialized();
  }

  @Override public Observable<Connectivity> observeNetworkConnectivity(final Context context) {
    final String service = Context.CONNECTIVITY_SERVICE;
    final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(service);
    networkCallback = createNetworkCallback(context);

    registerIdleReceiver(context);

    final NetworkRequest request =
        new NetworkRequest
            .Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .build();

    manager.registerNetworkCallback(request, networkCallback);

    //todo: fix logic of the stream below for the network state

    return connectivitySubject.toFlowable(BackpressureStrategy.LATEST).doOnCancel(new Action() {
      @Override public void run() {
        tryToUnregisterCallback(manager);
        tryToUnregisterReceiver(context);
      }
    }).doAfterNext(new Consumer<Connectivity>() {
      @Override
      public void accept(final Connectivity connectivity) {
        lastConnectivity = connectivity;
      }
    }).flatMap(new Function<Connectivity, Publisher<Connectivity>>() {
      @Override
      public Publisher<Connectivity> apply(final Connectivity connectivity) {
        return propagateAnyConnectedState(lastConnectivity, connectivity);
      }
    }).startWith(Connectivity.create(context)).distinctUntilChanged().toObservable();
  }

  //todo: fix logic of this method for the network state
  @SuppressWarnings("NullAway")
  protected Publisher<Connectivity> propagateAnyConnectedState(
      final Connectivity last,
      final Connectivity current
  ) {
    final boolean hasNetworkState
        = last.networkState() != null
        && current.networkState() != null;

    final boolean typeChanged = last.type() != current.type();

    boolean wasConnected;
    boolean isDisconnected;
    boolean isNotIdle;

    if (hasNetworkState) {
      // handling new NetworkState API
      wasConnected = last.networkState().isConnected();
      isDisconnected = !current.networkState().isConnected();
      isNotIdle = true;
    } else {
      // handling legacy, deprecated NetworkInfo API
      wasConnected = last.state() == NetworkInfo.State.CONNECTED;
      isDisconnected = current.state() == NetworkInfo.State.DISCONNECTED;
      isNotIdle = current.detailedState() != NetworkInfo.DetailedState.IDLE;
    }

    if (typeChanged && wasConnected && isDisconnected && isNotIdle) {
      return Flowable.fromArray(current, last);
    } else {
      return Flowable.fromArray(current);
    }
  }

  protected void registerIdleReceiver(final Context context) {
    final IntentFilter filter = new IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);
    context.registerReceiver(idleReceiver, filter);
  }

  @NonNull protected BroadcastReceiver createIdleBroadcastReceiver() {
    return new BroadcastReceiver() {
      @Override public void onReceive(final Context context, final Intent intent) {
        if (isIdleMode(context)) {
          onNext(Connectivity.create());
        } else {
          onNext(Connectivity.create(context));
        }
      }
    };
  }

  protected boolean isIdleMode(final Context context) {
    final String packageName = context.getPackageName();
    final PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    boolean isIgnoringOptimizations = manager.isIgnoringBatteryOptimizations(packageName);
    return manager.isDeviceIdleMode() && !isIgnoringOptimizations;
  }

  protected void tryToUnregisterCallback(final ConnectivityManager manager) {
    try {
      manager.unregisterNetworkCallback(networkCallback);
    } catch (Exception exception) {
      onError(ERROR_MSG_NETWORK_CALLBACK, exception);
    }
  }

  protected void tryToUnregisterReceiver(Context context) {
    try {
      context.unregisterReceiver(idleReceiver);
    } catch (Exception exception) {
      onError(ERROR_MSG_RECEIVER, exception);
    }
  }

  @Override public void onError(final String message, final Exception exception) {
    Log.e(LOG_TAG, message, exception);
  }

  protected ConnectivityManager.NetworkCallback createNetworkCallback(final Context context) {
    return new ConnectivityManager.NetworkCallback() {
      @Override
      public void onCapabilitiesChanged(
          @NonNull Network network,
          @NonNull NetworkCapabilities networkCapabilities
      ) {
        networkState.setNetwork(network);
        networkState.setNetworkCapabilities(networkCapabilities);
        onNext(Connectivity.create(context, networkState));
      }

      @Override
      public void onLinkPropertiesChanged(
          @NonNull Network network,
          @NonNull LinkProperties linkProperties
      ) {
        networkState.setNetwork(network);
        networkState.setLinkProperties(linkProperties);
        onNext(Connectivity.create(context, networkState));
      }

      @Override public void onAvailable(@NonNull Network network) {
        networkState.setNetwork(network);
        networkState.setConnected(true);
        onNext(Connectivity.create(context, networkState));
      }

      @Override public void onLost(@NonNull Network network) {
        networkState.setNetwork(network);
        networkState.setConnected(false);
        onNext(Connectivity.create(context, networkState));
      }

      @Override public void onUnavailable() {
        super.onUnavailable();
        networkState.setConnected(false);
        onNext(Connectivity.create(context, networkState));
      }
    };
  }

  protected void onNext(Connectivity connectivity) {
    connectivitySubject.onNext(connectivity);
  }
}
