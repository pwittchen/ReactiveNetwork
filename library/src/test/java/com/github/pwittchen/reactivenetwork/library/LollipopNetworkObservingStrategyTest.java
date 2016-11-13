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
package com.github.pwittchen.reactivenetwork.library;

import android.app.Application;
import android.net.NetworkInfo;
import com.github.pwittchen.reactivenetwork.library.network.observing.NetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.network.observing.strategy.LollipopNetworkObservingStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class LollipopNetworkObservingStrategyTest {

  @Spy private NetworkObservingStrategy strategy;

  @Before public void setUp() {
    strategy = new LollipopNetworkObservingStrategy();
    MockitoAnnotations.initMocks(this);
  }

  @Test public void shouldObserveConnectivity() {
    // given
    NetworkObservingStrategy strategy = new LollipopNetworkObservingStrategy();

    // when
    strategy.observeNetworkConnectivity(RuntimeEnvironment.application)
        .subscribe(new Action1<Connectivity>() {
          @Override public void call(Connectivity connectivity) {

            // then
            assertThat(connectivity.getState()).isEqualTo(NetworkInfo.State.CONNECTED);
          }
        });
  }

  @Test public void shouldStopObservingConnectivity() {
    // given
    NetworkObservingStrategy strategy = new LollipopNetworkObservingStrategy();
    Application context = RuntimeEnvironment.application;
    Observable<Connectivity> observable = strategy.observeNetworkConnectivity(context);

    // when
    Subscription subscription = observable.subscribe();
    subscription.unsubscribe();

    // then
    assertThat(subscription.isUnsubscribed()).isTrue();
  }

  @Test public void shouldCallOnError() {
    // given
    String message = "error message";
    Exception exception = new Exception();

    // when
    strategy.onError(message, exception);

    // then
    verify(strategy, times(1)).onError(message, exception);
  }
}
