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
package com.github.pwittchen.reactivenetwork.library.rx2;

import android.app.Application;
import android.net.NetworkInfo;
import com.github.pwittchen.reactivenetwork.library.BuildConfig;
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.NetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.MarshmallowNetworkObservingStrategy;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class MarshmallowNetworkObservingStrategyTest {

  @Rule public MockitoRule rule = MockitoJUnit.rule();
  @Spy private NetworkObservingStrategy strategy = new MarshmallowNetworkObservingStrategy();

  @Test public void shouldObserveConnectivity() {
    // given
    final NetworkObservingStrategy strategy = new MarshmallowNetworkObservingStrategy();

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
    final NetworkObservingStrategy strategy = new MarshmallowNetworkObservingStrategy();
    final Application context = RuntimeEnvironment.application;
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
}
