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
import android.content.Context;
import android.net.NetworkInfo;
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.DefaultErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.NetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.LollipopNetworkObservingStrategy;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class ReactiveNetworkTest {

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

  @Test public void observeNetworkConnectivityShouldNotBeNull() {
    // given
    final Context context = RuntimeEnvironment.application;

    // when
    Observable<Connectivity> observable;
    observable = ReactiveNetwork.observeNetworkConnectivity(context);

    // then
    assertThat(observable).isNotNull();
  }

  @Test public void observeNetworkConnectivityWithStrategyShouldNotBeNull() {
    // given
    final Context context = RuntimeEnvironment.application;
    NetworkObservingStrategy strategy = new LollipopNetworkObservingStrategy();

    // when
    Observable<Connectivity> observable;
    observable = ReactiveNetwork.observeNetworkConnectivity(context, strategy);

    // then
    assertThat(observable).isNotNull();
  }

  @Test public void observeInternetConnectivityDefaultShouldNotBeNull() {
    // given
    Observable<Boolean> observable;

    // when
    observable = ReactiveNetwork.observeInternetConnectivity();

    // then
    assertThat(observable).isNotNull();
  }

  @Test public void observeInternetConnectivityWithConfigurationShouldNotBeNull() {
    // given
    Observable<Boolean> observable;
    final int interval = TEST_VALID_INTERVAL;
    final String host = TEST_VALID_HOST;
    final int port = TEST_VALID_PORT;
    final int timeout = TEST_VALID_TIMEOUT;

    // when
    observable = ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    assertThat(observable).isNotNull();
  }

  @Test public void observeInternetConnectivityWithFullConfigurationShouldNotBeNull() {
    // given
    Observable<Boolean> observable;
    final int initialInterval = TEST_VALID_INITIAL_INTERVAL;
    final int interval = TEST_VALID_INTERVAL;
    final String host = TEST_VALID_HOST;
    final int port = TEST_VALID_PORT;
    final int timeout = TEST_VALID_TIMEOUT;

    // when
    observable =
        ReactiveNetwork.observeInternetConnectivity(initialInterval, interval, host, port, timeout);

    // then
    assertThat(observable).isNotNull();
  }

  @Test public void observeNetworkConnectivityShouldBeConnectedOnStartWhenNetworkIsAvailable() {

    final Application context = RuntimeEnvironment.application;

    ReactiveNetwork.observeNetworkConnectivity(context).subscribe(new Consumer<Connectivity>() {
      @Override public void accept(Connectivity connectivity) {
            assertThat(connectivity.getState()).isEqualTo(NetworkInfo.State.CONNECTED);
          }
        });
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeNetworkConnectivityShouldThrowAnExceptionForNullContext() {
    // given
    final Context context = null;
    final NetworkObservingStrategy strategy = new LollipopNetworkObservingStrategy();

    // when
    ReactiveNetwork.observeNetworkConnectivity(context, strategy);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeNetworkConnectivityShouldThrowAnExceptionForNullStrategy() {
    // given
    final Context context = RuntimeEnvironment.application;
    final NetworkObservingStrategy strategy = null;

    // when
    ReactiveNetwork.observeNetworkConnectivity(context, strategy);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNegativeInterval() {
    // given
    final int interval = -1;
    final String host = TEST_VALID_HOST;
    final int port = TEST_VALID_PORT;
    final int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForZeroInterval() {
    // given
    final int interval = 0;
    final String host = TEST_VALID_HOST;
    final int port = TEST_VALID_PORT;
    final int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNullHost() {
    // given
    final int interval = TEST_VALID_INTERVAL;
    final String host = null;
    final int port = TEST_VALID_PORT;
    final int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForEmptyHost() {
    // given
    final int interval = TEST_VALID_INTERVAL;
    final String host = "";
    final int port = TEST_VALID_PORT;
    final int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNegativePort() {
    // given
    final int interval = TEST_VALID_INTERVAL;
    final String host = TEST_VALID_HOST;
    final int port = -1;
    final int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForZeroPort() {
    // given
    final int interval = TEST_VALID_INTERVAL;
    final String host = TEST_VALID_HOST;
    final int port = 0;
    final int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNegativeTimeout() {
    // given
    final int interval = TEST_VALID_INTERVAL;
    final String host = TEST_VALID_HOST;
    final int port = TEST_VALID_PORT;
    final int timeout = -1;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForZeroTimeout() {
    // given
    final int interval = TEST_VALID_INTERVAL;
    final String host = TEST_VALID_HOST;
    final int port = TEST_VALID_PORT;
    final int timeout = 0;

    // when
    ReactiveNetwork.observeInternetConnectivity(interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test public void observeInternetConnectivityShouldNotThrowAnExceptionForZeroInitialInterval() {
    // given
    Observable<Boolean> observable;
    final int initialInterval = 0;
    final int interval = TEST_VALID_INTERVAL;
    final String host = TEST_VALID_HOST;
    final int port = TEST_VALID_PORT;
    final int timeout = TEST_VALID_TIMEOUT;

    // when
    observable =
        ReactiveNetwork.observeInternetConnectivity(initialInterval, interval, host, port, timeout);

    // then
    assertThat(observable).isNotNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNegativeInitialInterval() {
    // given
    final int initialInterval = -1;
    final int interval = TEST_VALID_INTERVAL;
    final String host = TEST_VALID_HOST;
    final int port = TEST_VALID_PORT;
    final int timeout = TEST_VALID_TIMEOUT;

    // when
    ReactiveNetwork.observeInternetConnectivity(initialInterval, interval, host, port, timeout);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionWhenSocketErrorHandlerIsNull() {
    // given
    final int initialInterval = -1;
    final int interval = TEST_VALID_INTERVAL;
    final String host = TEST_VALID_HOST;
    final int port = TEST_VALID_PORT;
    final int timeout = TEST_VALID_TIMEOUT;
    final ErrorHandler errorHandler = null;

    // when
    ReactiveNetwork.observeInternetConnectivity(initialInterval, interval, host, port, timeout,
        errorHandler);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionWhenStrategyIsNull() {
    // given
    final InternetObservingStrategy strategy = null;
    final int initialInterval = -1;
    final int interval = TEST_VALID_INTERVAL;
    final String host = TEST_VALID_HOST;
    final int port = TEST_VALID_PORT;
    final int timeout = TEST_VALID_TIMEOUT;
    final ErrorHandler errorHandler = new DefaultErrorHandler();

    // when
    ReactiveNetwork.observeInternetConnectivity(strategy, initialInterval, interval, host, port,
        timeout, errorHandler);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldNotThrowAnExceptionWhenStrategyIsNotNull() {
    // given
    final InternetObservingStrategy strategy = new SocketInternetObservingStrategy();
    final int initialInterval = -1;
    final int interval = TEST_VALID_INTERVAL;
    final String host = TEST_VALID_HOST;
    final int port = TEST_VALID_PORT;
    final int timeout = TEST_VALID_TIMEOUT;
    final ErrorHandler errorHandler = new DefaultErrorHandler();

    // when
    Observable<Boolean> observable =
        ReactiveNetwork.observeInternetConnectivity(strategy, initialInterval, interval, host, port,
            timeout, errorHandler);

    // then
    assertThat(observable).isNotNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionWhenJustStrategyIsNull() {
    // given
    final InternetObservingStrategy strategy = null;

    // when
    ReactiveNetwork.observeInternetConnectivity(strategy);

    // then
    // an exception is thrown
  }

  @Test
  public void observeInternetConnectivityShouldNotThrowAnExceptionWhenJustStrategyIsNotNull() {
    // given
    Observable<Boolean> observable;
    final InternetObservingStrategy strategy = new SocketInternetObservingStrategy();

    // when
    observable =
        ReactiveNetwork.observeInternetConnectivity(strategy);

    // then
    assertThat(observable).isNotNull();
  }
}
