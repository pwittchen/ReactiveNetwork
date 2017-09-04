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
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;
import com.github.pwittchen.reactivenetwork.library.rx2.BuildConfig;
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class MarshmallowNetworkObservingStrategyTest {

  @Rule public MockitoRule rule = MockitoJUnit.rule();
  @Spy private MarshmallowNetworkObservingStrategy strategy =
      new MarshmallowNetworkObservingStrategy();

  @Mock private PowerManager powerManager;
  @Mock private Context contextMock;
  @Spy private Context context;

  @Before public void setUp() {
    context = RuntimeEnvironment.application.getApplicationContext();
  }

  @Test public void shouldObserveConnectivity() {
    // when
    strategy.observeNetworkConnectivity(RuntimeEnvironment.application)
        .subscribe(new Consumer<Connectivity>() {
          @Override public void accept(Connectivity connectivity) {

            // then
            assertThat(connectivity.getState()).isEqualTo(NetworkInfo.State.CONNECTED);
          }
        });
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

    // when
    final Disposable disposable = observable.subscribe();
    disposable.dispose();

    // then
    verify(strategy).tryToUnregisterCallback(any(ConnectivityManager.class));
  }

  @Test public void shouldTryToUnregisterReceiverOnDispose() {
    // given
    final Observable<Connectivity> observable = strategy.observeNetworkConnectivity(context);

    // when
    final Disposable disposable = observable.subscribe();
    disposable.dispose();

    // then
    verify(strategy).tryToUnregisterReceiver(context);
  }

  @Test public void shouldNotBeInIdleModeWhenDeviceIsIdleAndIsIgnoringBatteryOptimizations() {
    // given
    preparePowerManagerMocks(Boolean.FALSE, Boolean.FALSE);

    // when
    final boolean isIdleMode = strategy.isIdleMode(contextMock);

    // then
    assertThat(isIdleMode).isFalse();
  }

  @Test public void shouldBeInIdleModeWhenDeviceIsIgnoringBatteryOptimizations() {
    // given
    preparePowerManagerMocks(Boolean.TRUE, Boolean.FALSE);

    // when
    final boolean isIdleMode = strategy.isIdleMode(contextMock);

    // then
    assertThat(isIdleMode).isTrue();
  }

  @Test public void shouldNotBeInIdleModeWhenDeviceIsInIdleMode() {
    // given
    preparePowerManagerMocks(Boolean.FALSE, Boolean.TRUE);

    // when
    final boolean isIdleMode = strategy.isIdleMode(contextMock);

    // then
    assertThat(isIdleMode).isFalse();
  }

  @TargetApi(Build.VERSION_CODES.M)
  private void preparePowerManagerMocks(final Boolean isDeviceInIdleMode,
      final Boolean isIgnoringBatteryOptimizations) {
    final String packageName = "com.github.pwittchen.test";
    when(contextMock.getPackageName()).thenReturn(packageName);
    when(contextMock.getSystemService(Context.POWER_SERVICE)).thenReturn(powerManager);
    when(powerManager.isDeviceIdleMode()).thenReturn(isDeviceInIdleMode);
    when(powerManager.isIgnoringBatteryOptimizations(packageName)).thenReturn(
        isIgnoringBatteryOptimizations);
  }
}
