/*
 * Copyright (C) 2017 Piotr Wittchen
 * Copyright (C) 2019 Tim Kist
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
package com.github.pwittchen.reactivenetwork.library.internet.observing.strategy;

import com.github.pwittchen.reactivenetwork.library.BuildConfig;
import com.github.pwittchen.reactivenetwork.library.internet.observing.error.ErrorHandler;
import java.io.IOException;
import java.net.HttpURLConnection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import rx.Observable;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class WalledGardenInternetObservingStrategyTest {

  private static final int INITIAL_INTERVAL_IN_MS = 0;
  private static final int INTERVAL_IN_MS = 2000;
  private static final int PORT = 80;
  private static final int TIMEOUT_IN_MS = 30;
  private static final String HOST_WITH_HTTP = "http://www.website.com";
  private static final String HOST_WITH_HTTPS = "https://www.website.com";
  private static final String HOST_WITHOUT_HTTPS = "www.website.com";

  @Rule public MockitoRule rule = MockitoJUnit.rule();
  @Spy public WalledGardenInternetObservingStrategy strategy;
  @Spy public ErrorHandler errorHandler = new ErrorHandler() {
    @Override
    public void handleError(Exception exception, String message) {
      // do nothing
    }
  };

  @Before
  public void setUp() throws Exception {
    Mockito.doNothing().when(errorHandler).handleError(ArgumentMatchers.any(Exception.class), ArgumentMatchers.anyString());
  }

  private String getHost() {
    return strategy.getDefaultPingHost();
  }

  @Test public void shouldBeConnectedToTheInternet() {
    // given
    String host = getHost();
    Mockito.doReturn(true).when(strategy).isConnected(host, PORT, TIMEOUT_IN_MS, errorHandler);

    // when
    final Observable<Boolean> observable =
        strategy.observeInternetConnectivity(INITIAL_INTERVAL_IN_MS, INTERVAL_IN_MS, host,
            PORT, TIMEOUT_IN_MS, errorHandler);

    boolean isConnected = observable.toBlocking().first();

    // then
    assertThat(isConnected).isTrue();
  }

  @Test public void shouldNotBeConnectedToTheInternet() {
    // given
    String host = getHost();
    Mockito.doReturn(false).when(strategy).isConnected(host, PORT, TIMEOUT_IN_MS, errorHandler);

    // when
    final Observable<Boolean> observable =
        strategy.observeInternetConnectivity(INITIAL_INTERVAL_IN_MS, INTERVAL_IN_MS, host,
            PORT, TIMEOUT_IN_MS, errorHandler);

    boolean isConnected = observable.toBlocking().first();

    // then
    assertThat(isConnected).isFalse();
  }

  @Test public void shouldCreateHttpUrlConnection() throws IOException {
    // given
    final String parsedDefaultHost = "clients3.google.com";

    // when
    HttpURLConnection connection = strategy.createHttpUrlConnection(getHost(), PORT, TIMEOUT_IN_MS);

    // then
    assertThat(connection).isNotNull();
    assertThat(connection.getURL().getHost()).isEqualTo(parsedDefaultHost);
    assertThat(connection.getURL().getPort()).isEqualTo(PORT);
    assertThat(connection.getConnectTimeout()).isEqualTo(TIMEOUT_IN_MS);
    assertThat(connection.getReadTimeout()).isEqualTo(TIMEOUT_IN_MS);
    assertThat(connection.getInstanceFollowRedirects()).isFalse();
    assertThat(connection.getUseCaches()).isFalse();
  }

  @Test public void shouldHandleAnExceptionWhileCreatingHttpUrlConnection() throws IOException {
    // given
    final String errorMsg = "Could not establish connection with WalledGardenStrategy";
    final IOException givenException = new IOException(errorMsg);
    when(strategy.createHttpUrlConnection(HOST_WITH_HTTP, PORT, TIMEOUT_IN_MS)).thenThrow(
        givenException);

    // when
    strategy.isConnected(HOST_WITH_HTTP, PORT, TIMEOUT_IN_MS, errorHandler);

    // then
    verify(errorHandler).handleError(givenException, errorMsg);
  }

  @Test public void shouldCreateHttpsUrlConnection() throws IOException {
    // given
    final String parsedDefaultHost = "clients3.google.com";

    // when
    HttpURLConnection connection =
        strategy.createHttpsUrlConnection(getHost(), PORT, TIMEOUT_IN_MS);

    // then
    assertThat(connection).isNotNull();
    assertThat(connection.getURL().getHost()).isEqualTo(parsedDefaultHost);
    assertThat(connection.getURL().getPort()).isEqualTo(PORT);
    assertThat(connection.getConnectTimeout()).isEqualTo(TIMEOUT_IN_MS);
    assertThat(connection.getReadTimeout()).isEqualTo(TIMEOUT_IN_MS);
    assertThat(connection.getInstanceFollowRedirects()).isFalse();
    assertThat(connection.getUseCaches()).isFalse();
  }

  @Test public void shouldHandleAnExceptionWhileCreatingHttpsUrlConnection() throws IOException {
    // given
    final String errorMsg = "Could not establish connection with WalledGardenStrategy";
    final IOException givenException = new IOException(errorMsg);
    when(strategy.createHttpsUrlConnection(getHost(), PORT, TIMEOUT_IN_MS)).thenThrow(
        givenException);

    // when
    strategy.isConnected(getHost(), PORT, TIMEOUT_IN_MS, errorHandler);

    // then
    verify(errorHandler).handleError(givenException, errorMsg);
  }

  @Test public void shouldNotTransformHttpHost() {
    // when
    String transformedHost = strategy.adjustHost(HOST_WITH_HTTPS);

    // then
    assertThat(transformedHost).isEqualTo(HOST_WITH_HTTPS);
  }

  @Test public void shouldNotTransformHttpsHost() {
    // when
    String transformedHost = strategy.adjustHost(HOST_WITH_HTTPS);

    // then
    assertThat(transformedHost).isEqualTo(HOST_WITH_HTTPS);
  }

  @Test public void shouldAddHttpsProtocolToHost() {
    // when
    String transformedHost = strategy.adjustHost(HOST_WITHOUT_HTTPS);

    // then
    assertThat(transformedHost).isEqualTo(HOST_WITH_HTTPS);
  }

  @Test public void shouldAdjustHostWhileCheckingConnectivity() {
    // given
    final String host = getHost();
    Mockito.doReturn(true).when(strategy).isConnected(host, PORT, TIMEOUT_IN_MS, errorHandler);

    // when
    strategy.observeInternetConnectivity(INITIAL_INTERVAL_IN_MS, INTERVAL_IN_MS, host, PORT,
        TIMEOUT_IN_MS, errorHandler).toBlocking().first();

    // then
    verify(strategy).adjustHost(host);
  }
}
