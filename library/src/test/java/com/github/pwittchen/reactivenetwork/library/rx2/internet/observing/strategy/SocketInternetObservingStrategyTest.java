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
package com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy;

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@SuppressWarnings({ "PMD", "NullAway" }) public class SocketInternetObservingStrategyTest {

  private static final int INITIAL_INTERVAL_IN_MS = 0;
  private static final int INTERVAL_IN_MS = 2000;
  private static final int PORT = 80;
  private static final int TIMEOUT_IN_MS = 30;
  private static final int HTTP_RESPONSE = 204;
  private static final String HOST_WITH_HTTP = "http://www.website.com";
  private static final String HOST_WITHOUT_HTTP = "www.website.com";

  @Rule public MockitoRule rule = MockitoJUnit.rule();
  @Spy private SocketInternetObservingStrategy strategy;
  @Mock private ErrorHandler errorHandler;
  @Mock private Socket socket;

  private String getHost() {
    return strategy.getDefaultPingHost();
  }

  @Test public void shouldBeConnectedToTheInternet() {
    // given
    when(strategy.isConnected(getHost(), PORT, TIMEOUT_IN_MS, errorHandler)).thenReturn(true);

    // when
    final Observable<Boolean> observable =
        strategy.observeInternetConnectivity(INITIAL_INTERVAL_IN_MS, INTERVAL_IN_MS, getHost(),
            PORT, TIMEOUT_IN_MS, HTTP_RESPONSE, errorHandler);

    boolean isConnected = observable.blockingFirst();

    // then
    assertThat(isConnected).isTrue();
  }

  @Test public void shouldNotBeConnectedToTheInternet() {
    // given
    when(strategy.isConnected(getHost(), PORT, TIMEOUT_IN_MS, errorHandler)).thenReturn(false);

    // when
    final Observable<Boolean> observable =
        strategy.observeInternetConnectivity(INITIAL_INTERVAL_IN_MS, INTERVAL_IN_MS, getHost(),
            PORT, TIMEOUT_IN_MS, HTTP_RESPONSE, errorHandler);

    boolean isConnected = observable.blockingFirst();

    // then
    assertThat(isConnected).isFalse();
  }

  @Test public void shouldNotBeConnectedToTheInternetWhenSocketThrowsAnExceptionOnConnect()
      throws IOException {
    // given
    final InetSocketAddress address = new InetSocketAddress(getHost(), PORT);
    doThrow(new IOException()).when(socket).connect(address, TIMEOUT_IN_MS);

    // when
    final boolean isConnected =
        strategy.isConnected(socket, getHost(), PORT, TIMEOUT_IN_MS, errorHandler);

    // then
    assertThat(isConnected).isFalse();
  }

  @Test public void shouldHandleAnExceptionThrownDuringClosingTheSocket() throws IOException {
    // given
    final String errorMsg = "Could not close the socket";
    final IOException givenException = new IOException(errorMsg);
    doThrow(givenException).when(socket).close();

    // when
    strategy.isConnected(socket, getHost(), PORT, TIMEOUT_IN_MS, errorHandler);

    // then
    verify(errorHandler, times(1)).handleError(givenException, errorMsg);
  }

  @Test public void shouldBeConnectedToTheInternetViaSingle() {
    // given
    when(strategy.isConnected(getHost(), PORT, TIMEOUT_IN_MS, errorHandler)).thenReturn(true);

    // when
    final Single<Boolean> observable =
        strategy.checkInternetConnectivity(getHost(), PORT, TIMEOUT_IN_MS, HTTP_RESPONSE,
            errorHandler);

    boolean isConnected = observable.blockingGet();

    // then
    assertThat(isConnected).isTrue();
  }

  @Test public void shouldNotBeConnectedToTheInternetViaSingle() {
    // given
    when(strategy.isConnected(getHost(), PORT, TIMEOUT_IN_MS, errorHandler)).thenReturn(false);

    // when
    final Single<Boolean> observable =
        strategy.checkInternetConnectivity(getHost(), PORT, TIMEOUT_IN_MS, HTTP_RESPONSE,
            errorHandler);

    boolean isConnected = observable.blockingGet();

    // then
    assertThat(isConnected).isFalse();
  }

  @Test public void shouldNotTransformHost() {
    // when
    String transformedHost = strategy.adjustHost(HOST_WITHOUT_HTTP);

    // then
    assertThat(transformedHost).isEqualTo(HOST_WITHOUT_HTTP);
  }

  @Test public void shouldRemoveHttpProtocolFromHost() {
    // when
    String transformedHost = strategy.adjustHost(HOST_WITH_HTTP);

    // then
    assertThat(transformedHost).isEqualTo(HOST_WITHOUT_HTTP);
  }

  @Test public void shouldRemoveHttpsProtocolFromHost() {
    // when
    String transformedHost = strategy.adjustHost(HOST_WITH_HTTP);

    // then
    assertThat(transformedHost).isEqualTo(HOST_WITHOUT_HTTP);
  }

  @Test @SuppressWarnings("CheckReturnValue")
  public void shouldAdjustHostDuringCheckingConnectivity() {
    // given
    final String host = getHost();
    when(strategy.isConnected(host, PORT, TIMEOUT_IN_MS, errorHandler)).thenReturn(true);

    // when
    strategy.observeInternetConnectivity(INITIAL_INTERVAL_IN_MS, INTERVAL_IN_MS, host, PORT,
        TIMEOUT_IN_MS, HTTP_RESPONSE, errorHandler).blockingFirst();

    // then
    verify(strategy).adjustHost(host);
  }
}
