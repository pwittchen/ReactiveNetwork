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
import android.content.Context;
import android.net.NetworkInfo;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.DefaultErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.NetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.LollipopNetworkObservingStrategy;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
@SuppressWarnings("NullAway") public class ReactiveNetworkTest {

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
    networkConnectivityObservableShouldNotBeNull();
  }

  @Test @Config(sdk = 23) public void observeNetworkConnectivityShouldNotBeNullForMarshmallow() {
    // given
    networkConnectivityObservableShouldNotBeNull();
  }

  @Test @Config(sdk = 21) public void observeNetworkConnectivityShouldNotBeNullForLollipop() {
    networkConnectivityObservableShouldNotBeNull();
  }

  private void networkConnectivityObservableShouldNotBeNull() {
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
    // when
    final Observable<Boolean> observable =
        ReactiveNetwork.observeInternetConnectivity(TEST_VALID_INTERVAL, TEST_VALID_HOST,
            TEST_VALID_PORT, TEST_VALID_TIMEOUT);

    // then
    assertThat(observable).isNotNull();
  }

  @Test public void observeInternetConnectivityWithFullConfigurationShouldNotBeNull() {
    // when
    final Observable<Boolean> observable =
        ReactiveNetwork.observeInternetConnectivity(TEST_VALID_INITIAL_INTERVAL,
            TEST_VALID_INTERVAL, TEST_VALID_HOST, TEST_VALID_PORT, TEST_VALID_TIMEOUT);

    // then
    assertThat(observable).isNotNull();
  }

  @Test public void observeNetworkConnectivityShouldBeConnectedOnStartWhenNetworkIsAvailable() {
    // given
    final Application context = RuntimeEnvironment.application;

    // when
    Connectivity connectivity = ReactiveNetwork.observeNetworkConnectivity(context).blockingFirst();

    // then
    assertThat(connectivity.getState()).isEqualTo(NetworkInfo.State.CONNECTED);
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeNetworkConnectivityShouldThrowAnExceptionForNullContext() {
    // given
    final Context context = null;
    final NetworkObservingStrategy strategy = new LollipopNetworkObservingStrategy();

    // when
    ReactiveNetwork.observeNetworkConnectivity(context, strategy);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeNetworkConnectivityShouldThrowAnExceptionForNullStrategy() {
    // given
    final Context context = RuntimeEnvironment.application;
    final NetworkObservingStrategy strategy = null;

    // when
    ReactiveNetwork.observeNetworkConnectivity(context, strategy);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNegativeInterval() {
    // given

    // when
    ReactiveNetwork.observeInternetConnectivity(-1, TEST_VALID_HOST, TEST_VALID_PORT,
        TEST_VALID_TIMEOUT);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForZeroInterval() {
    // when
    ReactiveNetwork.observeInternetConnectivity(0, TEST_VALID_HOST, TEST_VALID_PORT,
        TEST_VALID_TIMEOUT);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNullHost() {
    // when
    ReactiveNetwork.observeInternetConnectivity(TEST_VALID_INTERVAL, null, TEST_VALID_PORT,
        TEST_VALID_TIMEOUT);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForEmptyHost() {
    // given

    // when
    ReactiveNetwork.observeInternetConnectivity(TEST_VALID_INTERVAL, "", TEST_VALID_PORT,
        TEST_VALID_TIMEOUT);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNegativePort() {
    // given

    // when
    ReactiveNetwork.observeInternetConnectivity(TEST_VALID_INTERVAL, TEST_VALID_HOST, -1,
        TEST_VALID_TIMEOUT);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForZeroPort() {
    // given

    // when
    ReactiveNetwork.observeInternetConnectivity(TEST_VALID_INTERVAL, TEST_VALID_HOST, 0,
        TEST_VALID_TIMEOUT);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNegativeTimeout() {
    // when
    ReactiveNetwork.observeInternetConnectivity(TEST_VALID_INTERVAL, TEST_VALID_HOST,
        TEST_VALID_PORT, -1);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForZeroTimeout() {
    // when
    ReactiveNetwork.observeInternetConnectivity(TEST_VALID_INTERVAL, TEST_VALID_HOST,
        TEST_VALID_PORT, 0);

    // then an exception is thrown
  }

  @Test public void observeInternetConnectivityShouldNotThrowAnExceptionForZeroInitialInterval() {
    // when
    final Observable<Boolean> observable =
        ReactiveNetwork.observeInternetConnectivity(0, TEST_VALID_INTERVAL, TEST_VALID_HOST,
            TEST_VALID_PORT, TEST_VALID_TIMEOUT);

    // then
    assertThat(observable).isNotNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionForNegativeInitialInterval() {
    // when
    ReactiveNetwork.observeInternetConnectivity(-1, TEST_VALID_INTERVAL, TEST_VALID_HOST,
        TEST_VALID_PORT, TEST_VALID_TIMEOUT);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionWhenSocketErrorHandlerIsNull() {
    // when
    ReactiveNetwork.observeInternetConnectivity(TEST_VALID_INITIAL_INTERVAL, TEST_VALID_INTERVAL,
        TEST_VALID_HOST, TEST_VALID_PORT, TEST_VALID_TIMEOUT, null);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionWhenStrategyIsNull() {
    // given
    final InternetObservingStrategy strategy = null;
    final ErrorHandler errorHandler = new DefaultErrorHandler();

    // when
    ReactiveNetwork.observeInternetConnectivity(strategy, TEST_VALID_INITIAL_INTERVAL,
        TEST_VALID_INTERVAL, TEST_VALID_HOST, TEST_VALID_PORT, TEST_VALID_TIMEOUT, errorHandler);

    // then an exception is thrown
  }

  @Test public void observeInternetConnectivityShouldNotThrowAnExceptionWhenStrategyIsNotNull() {
    // given
    final InternetObservingStrategy strategy = new SocketInternetObservingStrategy();
    final ErrorHandler errorHandler = new DefaultErrorHandler();

    // when
    final Observable<Boolean> observable =
        ReactiveNetwork.observeInternetConnectivity(strategy, TEST_VALID_INITIAL_INTERVAL,
            TEST_VALID_INTERVAL, TEST_VALID_HOST, TEST_VALID_PORT, TEST_VALID_TIMEOUT,
            errorHandler);

    // then
    assertThat(observable).isNotNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeInternetConnectivityShouldThrowAnExceptionWhenJustStrategyIsNull() {
    // given
    final InternetObservingStrategy strategy = null;

    // when
    ReactiveNetwork.observeInternetConnectivity(strategy);

    // then an exception is thrown
  }

  @Test
  public void observeInternetConnectivityShouldNotThrowAnExceptionWhenJustStrategyIsNotNull() {
    // given
    final InternetObservingStrategy strategy = new SocketInternetObservingStrategy();

    // when
    final Observable<Boolean> observable = ReactiveNetwork.observeInternetConnectivity(strategy);

    // then
    assertThat(observable).isNotNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkInternetConnectivityShouldThrowAnExceptionForNullHost() {
    // when
    ReactiveNetwork.checkInternetConnectivity(null, TEST_VALID_PORT, TEST_VALID_TIMEOUT);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkInternetConnectivityShouldThrowAnExceptionForEmptyHost() {
    // when
    ReactiveNetwork.checkInternetConnectivity("", TEST_VALID_PORT, TEST_VALID_TIMEOUT);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkInternetConnectivityShouldThrowAnExceptionForNegativePort() {
    // when
    ReactiveNetwork.checkInternetConnectivity(TEST_VALID_HOST, -1, TEST_VALID_TIMEOUT);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkInternetConnectivityShouldThrowAnExceptionForZeroPort() {
    // when
    ReactiveNetwork.checkInternetConnectivity(TEST_VALID_HOST, 0, TEST_VALID_TIMEOUT);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkInternetConnectivityShouldThrowAnExceptionForNegativeTimeout() {
    // when
    ReactiveNetwork.checkInternetConnectivity(TEST_VALID_HOST, TEST_VALID_PORT, -1);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkInternetConnectivityShouldThrowAnExceptionForZeroTimeout() {
    // when
    ReactiveNetwork.checkInternetConnectivity(TEST_VALID_HOST, TEST_VALID_PORT, 0);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkInternetConnectivityShouldThrowAnExceptionWhenSocketErrorHandlerIsNull() {
    // when
    ReactiveNetwork.checkInternetConnectivity(TEST_VALID_HOST, TEST_VALID_PORT, TEST_VALID_TIMEOUT,
        null);

    // then an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkInternetConnectivityShouldThrowAnExceptionWhenStrategyIsNull() {
    // given
    final ErrorHandler errorHandler = new DefaultErrorHandler();

    // when
    ReactiveNetwork.checkInternetConnectivity(null, TEST_VALID_HOST, TEST_VALID_PORT,
        TEST_VALID_TIMEOUT, errorHandler);

    // then an exception is thrown
  }

  @Test public void checkInternetConnectivityShouldNotThrowAnExceptionWhenStrategyIsNotNull() {
    // given
    final InternetObservingStrategy strategy = new SocketInternetObservingStrategy();
    final ErrorHandler errorHandler = new DefaultErrorHandler();

    // when
    final Single<Boolean> single =
        ReactiveNetwork.checkInternetConnectivity(strategy, TEST_VALID_HOST, TEST_VALID_PORT,
            TEST_VALID_TIMEOUT, errorHandler);

    // then
    assertThat(single).isNotNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkInternetConnectivityShouldThrowAnExceptionWhenJustStrategyIsNull() {
    // given
    final InternetObservingStrategy strategy = null;

    // when
    ReactiveNetwork.checkInternetConnectivity(strategy);

    // then an exception is thrown
  }

  @Test public void checkInternetConnectivityShouldNotThrowAnExceptionWhenJustStrategyIsNotNull() {
    // given
    final InternetObservingStrategy strategy = new SocketInternetObservingStrategy();

    // when
    final Single<Boolean> single = ReactiveNetwork.checkInternetConnectivity(strategy);

    // then
    assertThat(single).isNotNull();
  }
}
