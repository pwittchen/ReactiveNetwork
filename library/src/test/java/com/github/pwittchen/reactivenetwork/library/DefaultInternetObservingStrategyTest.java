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

import com.github.pwittchen.reactivenetwork.library.internet.observing.strategy.DefaultInternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.internet.socket.SocketErrorHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import rx.Observable;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
@SuppressWarnings("PMD") public class DefaultInternetObservingStrategyTest {

  private static final int INITIAL_INTERVAL_IN_MS = 0;
  private static final int INTERVAL_IN_MS = 2000;
  private static final String HOST = "www.google.com";
  private static final int PORT = 80;
  private static final int TIMEOUT_IN_MS = 30;
  private @Spy DefaultInternetObservingStrategy strategy;
  private @Mock SocketErrorHandler socketErrorHandler;
  private @Mock Socket socket;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test public void shouldBeConnectedToTheInternet() {
    // given
    when(strategy.isConnected(HOST, PORT, TIMEOUT_IN_MS, socketErrorHandler)).thenReturn(true);

    // when
    Observable<Boolean> observable =
        strategy.observeInternetConnectivity(INITIAL_INTERVAL_IN_MS, INTERVAL_IN_MS, HOST, PORT,
            TIMEOUT_IN_MS, socketErrorHandler);

    boolean isConnected = observable.toBlocking().first();

    // then
    assertThat(isConnected).isTrue();
  }

  @Test public void shouldNotBeConnectedToTheInternet() {
    // given
    when(strategy.isConnected(HOST, PORT, TIMEOUT_IN_MS, socketErrorHandler)).thenReturn(false);

    // when
    Observable<Boolean> observable =
        strategy.observeInternetConnectivity(INITIAL_INTERVAL_IN_MS, INTERVAL_IN_MS, HOST, PORT,
            TIMEOUT_IN_MS, socketErrorHandler);

    boolean isConnected = observable.toBlocking().first();

    // then
    assertThat(isConnected).isFalse();
  }

  @Test public void shouldNotBeConnectedToTheInternetWhenSocketThrowsAnExceptionOnConnect()
      throws IOException {
    // given
    final InetSocketAddress address = new InetSocketAddress(HOST, PORT);
    doThrow(new IOException()).when(socket).connect(address, TIMEOUT_IN_MS);

    // when
    final boolean isConnected =
        strategy.isConnected(socket, HOST, PORT, TIMEOUT_IN_MS, socketErrorHandler);

    // then
    assertThat(isConnected).isFalse();
  }

  @Test public void shouldHandleAnExceptionThrownDuringClosingTheSocket() throws IOException {
    // given
    IOException givenException = new IOException("error during closing socket");
    doThrow(givenException).when(socket).close();

    // when
    strategy.isConnected(socket, HOST, PORT, TIMEOUT_IN_MS, socketErrorHandler);

    // then
    verify(socketErrorHandler, times(1)).handleErrorDuringClosingSocket(givenException);
  }
}
