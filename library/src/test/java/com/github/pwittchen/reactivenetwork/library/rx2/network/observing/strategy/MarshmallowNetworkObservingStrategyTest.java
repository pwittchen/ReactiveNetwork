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
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.reactivestreams.Publisher;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// we're suppressing PMD warnings because we want static imports in tests
@RunWith(RobolectricTestRunner.class)
@SuppressWarnings({ "PMD", "NullAway" }) public class MarshmallowNetworkObservingStrategyTest {

  @Rule public MockitoRule rule = MockitoJUnit.rule();
  @Spy private MarshmallowNetworkObservingStrategy strategy =
      new MarshmallowNetworkObservingStrategy();

  @Mock private PowerManager powerManager;
  @Mock private ConnectivityManager connectivityManager;
  @Mock private Context contextMock;
  @Mock private Intent intent;
  @Mock private Network network;
  @Spy private Context context;

  @Before public void setUp() {
    context = RuntimeEnvironment.application.getApplicationContext();
  }

  @Test public void shouldObserveConnectivity() {
    // given
    final Context context = RuntimeEnvironment.application.getApplicationContext();

    // when
    Connectivity connectivity = strategy.observeNetworkConnectivity(context).blockingFirst();

    // then
    assertThat(connectivity.state()).isEqualTo(NetworkInfo.State.CONNECTED);
  }

  @Test public void shouldStopObservingConnectivity() {
    // given
    final Observable<Connectivity> observable = strategy.observeNetworkConnectivity(context);

    // when
    final Disposable disposable = observable.subscribe();
    disposable.dispose();

    // then
    assertThat(disposable.isDisposed()).isTrue();
  }

  @Test public void shouldCallOnError() {
    // given
    final String message = "error message";
    final Exception exception = new Exception();

    // when
    strategy.onError(message, exception);

    // then
    verify(strategy, times(1)).onError(message, exception);
  }

  @Test public void shouldTryToUnregisterCallbackOnDispose() {
    // given
    final Observable<Connectivity> observable = strategy.observeNetworkConnectivity(context);
    final TestObserver<Connectivity> observer = new TestObserver<>();

    // when
    observable.subscribe(observer);
    observer.dispose();

    // then
    verify(strategy).tryToUnregisterCallback(any(ConnectivityManager.class));
  }

  @Test public void shouldTryToUnregisterReceiverOnDispose() {
    // given
    final Observable<Connectivity> observable = strategy.observeNetworkConnectivity(context);
    final TestObserver<Connectivity> observer = new TestObserver<>();

    // when
    observable.subscribe(observer);
    observer.dispose();

    // then
    verify(strategy).tryToUnregisterReceiver(context);
  }

  @Test
  public void shouldNotBeInIdleModeWhenDeviceIsNotInIdleAndIsNotIgnoringBatteryOptimizations() {
    // given
    preparePowerManagerMocks(Boolean.FALSE, Boolean.FALSE);

    // when
    final boolean isIdleMode = strategy.isIdleMode(contextMock);

    // then
    assertThat(isIdleMode).isFalse();
  }

  @Test public void shouldBeInIdleModeWhenDeviceIsNotIgnoringBatteryOptimizations() {
    // given
    preparePowerManagerMocks(Boolean.TRUE, Boolean.FALSE);

    // when
    final boolean isIdleMode = strategy.isIdleMode(contextMock);

    // then
    assertThat(isIdleMode).isTrue();
  }

  @Test public void shouldNotBeInIdleModeWhenDeviceIsInIdleModeAndIgnoringBatteryOptimizations() {
    // given
    preparePowerManagerMocks(Boolean.TRUE, Boolean.TRUE);

    // when
    final boolean isIdleMode = strategy.isIdleMode(contextMock);

    // then
    assertThat(isIdleMode).isFalse();
  }

  @Test public void shouldNotBeInIdleModeWhenDeviceIsNotInIdleMode() {
    // given
    preparePowerManagerMocks(Boolean.FALSE, Boolean.TRUE);

    // when
    final boolean isIdleMode = strategy.isIdleMode(contextMock);

    // then
    assertThat(isIdleMode).isFalse();
  }

  @Test public void shouldReceiveIntentInIdleMode() {
    // given
    preparePowerManagerMocks(Boolean.TRUE, Boolean.FALSE);
    BroadcastReceiver broadcastReceiver = strategy.createIdleBroadcastReceiver();

    // when
    broadcastReceiver.onReceive(contextMock, intent);

    // then
    verify(strategy).onNext(any(Connectivity.class));
  }

  @Test public void shouldReceiveIntentWhenIsNotInIdleMode() {
    // given
    preparePowerManagerMocks(Boolean.FALSE, Boolean.FALSE);
    BroadcastReceiver broadcastReceiver = strategy.createIdleBroadcastReceiver();

    // when
    broadcastReceiver.onReceive(contextMock, intent);

    // then
    verify(strategy).onNext(any(Connectivity.class));
  }

  @TargetApi(Build.VERSION_CODES.M)
  private void preparePowerManagerMocks(final Boolean idleMode, final Boolean ignoreOptimizations) {
    final String packageName = "com.github.pwittchen.test";
    when(contextMock.getPackageName()).thenReturn(packageName);
    when(contextMock.getSystemService(Context.POWER_SERVICE)).thenReturn(powerManager);
    when(powerManager.isDeviceIdleMode()).thenReturn(idleMode);
    when(powerManager.isIgnoringBatteryOptimizations(packageName)).thenReturn(ignoreOptimizations);
  }

  @Test public void shouldCreateNetworkCallbackOnSubscribe() {
    // given
    final Observable<Connectivity> observable = strategy.observeNetworkConnectivity(context);

    // when
    observable.subscribe();

    // then
    verify(strategy).createNetworkCallback(context);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Test
  public void shouldInvokeOnNextOnNetworkAvailable() {
    // given
    ConnectivityManager.NetworkCallback networkCallback = strategy.createNetworkCallback(context);

    // when
    networkCallback.onAvailable(network);

    // then
    verify(strategy).onNext(any(Connectivity.class));
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Test public void shouldInvokeOnNextOnNetworkLost() {
    // given
    ConnectivityManager.NetworkCallback networkCallback = strategy.createNetworkCallback(context);

    // when
    networkCallback.onLost(network);

    // then
    verify(strategy).onNext(any(Connectivity.class));
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Test
  public void shouldHandleErrorWhileTryingToUnregisterCallback() {
    // given
    strategy.observeNetworkConnectivity(context);
    final IllegalArgumentException exception = new IllegalArgumentException();
    doThrow(exception).when(connectivityManager)
        .unregisterNetworkCallback(any(ConnectivityManager.NetworkCallback.class));

    // when
    strategy.tryToUnregisterCallback(connectivityManager);

    // then
    verify(strategy).onError(MarshmallowNetworkObservingStrategy.ERROR_MSG_NETWORK_CALLBACK,
        exception);
  }

  @Test public void shouldHandleErrorWhileTryingToUnregisterReceiver() {
    // given
    strategy.observeNetworkConnectivity(context);
    final RuntimeException exception = new RuntimeException();
    doThrow(exception).when(contextMock).unregisterReceiver(any(BroadcastReceiver.class));

    // when
    strategy.tryToUnregisterReceiver(contextMock);

    // then
    verify(strategy).onError(MarshmallowNetworkObservingStrategy.ERROR_MSG_RECEIVER, exception);
  }

  @Test public void shouldPropagateCurrentAndLastConnectivityWhenSwitchingFromWifiToMobile() {
    final int lastType = ConnectivityManager.TYPE_WIFI;
    final int currentType = ConnectivityManager.TYPE_MOBILE;

    assertThatConnectivityIsPropagatedDuringChange(lastType, currentType);
  }

  @Test public void shouldPropagateCurrentAndLastConnectivityWhenSwitchingFromMobileToWifi() {
    final int lastType = ConnectivityManager.TYPE_MOBILE;
    final int currentType = ConnectivityManager.TYPE_WIFI;

    assertThatConnectivityIsPropagatedDuringChange(lastType, currentType);
  }

  private void assertThatConnectivityIsPropagatedDuringChange(
      final int lastType, final int currentType) {
    // given
    final Connectivity last = new Connectivity.Builder()
        .type(lastType)
        .state(NetworkInfo.State.CONNECTED)
        .build();

    final Connectivity current = new Connectivity.Builder()
        .type(currentType)
        .state(NetworkInfo.State.DISCONNECTED)
        .detailedState(NetworkInfo.DetailedState.CONNECTED)
        .build();

    // when
    final Publisher<Connectivity> publisher = strategy.propagateAnyConnectedState(last, current);

    // then
    final TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
    publisher.subscribe(testSubscriber);
    testSubscriber.assertValueCount(2);
    testSubscriber.assertValues(current, last);
    testSubscriber.assertValueAt(0, current);
    testSubscriber.assertValueAt(1, last);
  }

  @Test public void shouldNotPropagateLastConnectivityEventWhenTypeIsNotChanged() {
    // given
    final Connectivity last = new Connectivity.Builder()
        .type(ConnectivityManager.TYPE_WIFI)
        .state(NetworkInfo.State.CONNECTED)
        .build();

    final Connectivity current = new Connectivity.Builder()
        .type(ConnectivityManager.TYPE_WIFI)
        .state(NetworkInfo.State.DISCONNECTED)
        .detailedState(NetworkInfo.DetailedState.CONNECTED)
        .build();

    // when
    final Publisher<Connectivity> publisher = strategy.propagateAnyConnectedState(last, current);

    // then
    final TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
    publisher.subscribe(testSubscriber);
    testSubscriber.assertValueCount(1);
    testSubscriber.assertValues(current);
  }

  @Test public void shouldNotPropagateLastConnectivityWhenWasNotConnected() {
    // given
    final Connectivity last = new Connectivity.Builder()
        .type(ConnectivityManager.TYPE_WIFI)
        .state(NetworkInfo.State.DISCONNECTED)
        .build();

    final Connectivity current = new Connectivity.Builder()
        .type(ConnectivityManager.TYPE_MOBILE)
        .state(NetworkInfo.State.CONNECTED)
        .detailedState(NetworkInfo.DetailedState.CONNECTED)
        .build();

    // when
    final Publisher<Connectivity> publisher = strategy.propagateAnyConnectedState(last, current);

    // then
    final TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
    publisher.subscribe(testSubscriber);
    testSubscriber.assertValueCount(1);
    testSubscriber.assertValues(current);
  }

  @Test public void shouldNotPropagateLastConnectivityWhenIsConnected() {
    // given
    final Connectivity last = new Connectivity.Builder()
        .type(ConnectivityManager.TYPE_WIFI)
        .state(NetworkInfo.State.CONNECTED)
        .build();

    final Connectivity current = new Connectivity.Builder()
        .type(ConnectivityManager.TYPE_MOBILE)
        .state(NetworkInfo.State.CONNECTED)
        .detailedState(NetworkInfo.DetailedState.CONNECTED)
        .build();

    // when
    final Publisher<Connectivity> publisher = strategy.propagateAnyConnectedState(last, current);

    // then
    final TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
    publisher.subscribe(testSubscriber);
    testSubscriber.assertValueCount(1);
    testSubscriber.assertValues(current);
  }

  @Test public void shouldNotPropagateLastConnectivityWhenIsIdle() {
    // given
    final Connectivity last = new Connectivity.Builder()
        .type(ConnectivityManager.TYPE_WIFI)
        .state(NetworkInfo.State.CONNECTED)
        .build();

    final Connectivity current = new Connectivity.Builder()
        .type(ConnectivityManager.TYPE_MOBILE)
        .state(NetworkInfo.State.DISCONNECTED)
        .detailedState(NetworkInfo.DetailedState.IDLE)
        .build();

    // when
    final Publisher<Connectivity> publisher = strategy.propagateAnyConnectedState(last, current);

    // then
    final TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
    publisher.subscribe(testSubscriber);
    testSubscriber.assertValueCount(1);
    testSubscriber.assertValues(current);
  }
}
