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

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.github.pwittchen.reactivenetwork.library.network.observing.NetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.network.observing.strategy.LollipopNetworkObservingStrategy;

import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Observable;
import rx.functions.Action1;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class) public class ReactiveNetworkTest {

  private static final String TEST_VALID_HOST = "www.test.com";
  private static final int TEST_VALID_PORT = 80;
  private static final int TEST_VALID_TIMEOUT = 1000;
  private static final int TEST_VALID_INTERVAL = 1000;
  private static final int TEST_VALID_INITIAL_INTERVAL = 1000;

  @Test public void testReactiveNetworkObjectShouldNotBeNull() {
    // given
    ReactiveNetwork reactiveNetwork;

    // when
    reactiveNetwork = ReactiveNetwork.create();

    // then
    assertThat(reactiveNetwork).isNotNull();
  }

  @Test
  public void observeNetworkConnectivityShouldNotBeNull() {
    // given
    Context context = InstrumentationRegistry.getTargetContext();

    // when
    Observable<Connectivity> observable;
    observable = ReactiveNetwork.observeNetworkConnectivity(context);

    // then
    assertThat(observable).isNotNull();
  }

  @Test
  public void observeNetworkConnectivityWithStrategyShouldNotBeNull() {
    // given
    Context context = InstrumentationRegistry.getTargetContext();
    NetworkObservingStrategy strategy = new LollipopNetworkObservingStrategy();

    // when
    Observable<Connectivity> observable;
    observable = ReactiveNetwork.observeNetworkConnectivity(context, strategy);

    // then
    assertThat(observable).isNotNull();
  }

  @Test
  public void observeInternetConnectivityDefaultShouldNotBeNull() {
    // given
    Observable<Boolean> observable;

    // when
    observable = ReactiveNetwork.observeInternetConnectivity();

    // then
    assertThat(observable).isNotNull();
  }

  @Test
  public void observeInternetConnectivityWithConfigurationShouldNotBeNull() {
    // given
    Observable<Boolean> observable;
    int interval = TEST_VALID_INTERVAL;
    String host = TEST_VALID_HOST;
    int port = TEST_VALID_PORT;
    int timeout = TEST_VALID_TIMEOUT;

    // when
    observable = ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    assertThat(observable).isNotNull();
  }

  @Test
  public void observeInternetConnectivityWithFullConfigurationShouldNotBeNull() {
    // given
    Observable<Boolean> observable;
    int initialInterval = TEST_VALID_INITIAL_INTERVAL;
    int interval = TEST_VALID_INTERVAL;
    String host = TEST_VALID_HOST;
    int port = TEST_VALID_PORT;
    int timeout = TEST_VALID_TIMEOUT;

    // when
    observable = ReactiveNetwork.observeInternetConnectivity(initialInterval, interval, host, port, timeout);

    // then
    assertThat(observable).isNotNull();
  }

  @Test public void observeNetworkConnectivityShouldBeDefaultIfEmpty() {
    ReactiveNetwork.observeNetworkConnectivity(InstrumentationRegistry.getContext())
        .subscribe(new Action1<Connectivity>() {
          @Override public void call(Connectivity connectivity) {
            assertThat(connectivity.isDefault()).isTrue();
          }
        });
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeNetworkConnectivityShouldThrowAnExceptionForNullContext() {
    // given
    Context context = null;
    NetworkObservingStrategy strategy = new LollipopNetworkObservingStrategy();

    // when
    ReactiveNetwork.observeNetworkConnectivity(context, strategy);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeNetworkConnectivityShouldThrowAnExceptionForNullStrategy() {
    // given
    Context context = InstrumentationRegistry.getContext();
    NetworkObservingStrategy strategy = null;

    // when
    ReactiveNetwork.observeNetworkConnectivity(context, strategy);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNegativeInterval() {
    // given
    int interval = -1;
    String host = TEST_VALID_HOST;
    int port = TEST_VALID_PORT;
    int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForZeroInterval() {
    // given
    int interval = 0;
    String host = TEST_VALID_HOST;
    int port = TEST_VALID_PORT;
    int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNullHost() {
    // given
    int interval = TEST_VALID_INTERVAL;
    String host = null;
    int port = TEST_VALID_PORT;
    int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForEmptyHost() {
    // given
    int interval = TEST_VALID_INTERVAL;
    String host = "";
    int port = TEST_VALID_PORT;
    int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNegativePort() {
    // given
    int interval = TEST_VALID_INTERVAL;
    String host = TEST_VALID_HOST;
    int port = -1;
    int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForZeroPort() {
    // given
    int interval = TEST_VALID_INTERVAL;
    String host = TEST_VALID_HOST;
    int port = 0;
    int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNegativeTimeout() {
    // given
    int interval = TEST_VALID_INTERVAL;
    String host = TEST_VALID_HOST;
    int port = TEST_VALID_PORT;
    int timeout = -1;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForZeroTimeout() {
    // given
    int interval = TEST_VALID_INTERVAL;
    String host = TEST_VALID_HOST;
    int port = TEST_VALID_PORT;
    int timeout = 0;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test
  public void observeInternetConnectivityShouldNotThrowAnExceptionForZeroInitialInterval() {
    // given
    Observable<Boolean> observable;
    int initialInterval = 0;
    int interval = TEST_VALID_INTERVAL;
    String host = TEST_VALID_HOST;
    int port = TEST_VALID_PORT;
    int timeout = TEST_VALID_TIMEOUT;

    // when
    observable = ReactiveNetwork.observeInternetConnectivity(initialInterval, interval, host, port, timeout);

    // then
    assertThat(observable).isNotNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNegativeInitialInterval() {
    // given
    int initialInterval = -1;
    int interval = TEST_VALID_INTERVAL;
    String host = TEST_VALID_HOST;
    int port = TEST_VALID_PORT;
    int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(initialInterval, interval, host, port, timeout);

    // then
    // an exception is thrown
  }
}
