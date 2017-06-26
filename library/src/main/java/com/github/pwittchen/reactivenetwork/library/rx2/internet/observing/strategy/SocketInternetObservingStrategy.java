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

import com.github.pwittchen.reactivenetwork.library.rx2.Preconditions;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Default strategy for monitoring connectivity with the Internet
 */
public class SocketInternetObservingStrategy implements InternetObservingStrategy {

  @Override public Observable<Boolean> observeInternetConnectivity(final int initialIntervalInMs,
      final int intervalInMs, final String host, final int port, final int timeoutInMs,
      final ErrorHandler errorHandler) {
    Preconditions.checkGreaterOrEqualToZero(initialIntervalInMs,
        "initialIntervalInMs is not a positive number");
    Preconditions.checkGreaterThanZero(intervalInMs, "intervalInMs is not a positive number");
    checkGeneralPreconditions(host, port, timeoutInMs, errorHandler);

    return Observable.interval(initialIntervalInMs, intervalInMs, TimeUnit.MILLISECONDS,
        Schedulers.io()).map(new Function<Long, Boolean>() {
      @Override public Boolean apply(@NonNull Long tick) throws Exception {
        return isConnected(host, port, timeoutInMs, errorHandler);
      }
    }).distinctUntilChanged();
  }

  @Override public Single<Boolean> observeInternetConnectivity(final String host, final int port,
      final int timeoutInMs, final ErrorHandler errorHandler) {
    checkGeneralPreconditions(host, port, timeoutInMs, errorHandler);

    return Single.create(new SingleOnSubscribe<Boolean>() {
      @Override public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
        emitter.onSuccess(isConnected(host, port, timeoutInMs, errorHandler));
      }
    });
  }

  private void checkGeneralPreconditions(String host, int port, int timeoutInMs,
      ErrorHandler errorHandler) {
    Preconditions.checkNotNullOrEmpty(host, "host is null or empty");
    Preconditions.checkGreaterThanZero(port, "port is not a positive number");
    Preconditions.checkGreaterThanZero(timeoutInMs, "timeoutInMs is not a positive number");
    Preconditions.checkNotNull(errorHandler, "errorHandler is null");
  }

  /**
   * checks if device is connected to given host at given port
   *
   * @param host to connect
   * @param port to connect
   * @param timeoutInMs connection timeout
   * @param errorHandler error handler for socket connection
   * @return boolean true if connected and false if not
   */
  public boolean isConnected(final String host, final int port, final int timeoutInMs,
      final ErrorHandler errorHandler) {
    final Socket socket = new Socket();
    return isConnected(socket, host, port, timeoutInMs, errorHandler);
  }

  /**
   * checks if device is connected to given host at given port
   *
   * @param socket to connect
   * @param host to connect
   * @param port to connect
   * @param timeoutInMs connection timeout
   * @param errorHandler error handler for socket connection
   * @return boolean true if connected and false if not
   */
  public boolean isConnected(final Socket socket, final String host, final int port,
      final int timeoutInMs, final ErrorHandler errorHandler) {
    boolean isConnected;
    try {
      socket.connect(new InetSocketAddress(host, port), timeoutInMs);
      isConnected = socket.isConnected();
    } catch (IOException e) {
      isConnected = Boolean.FALSE;
    } finally {
      try {
        socket.close();
      } catch (IOException exception) {
        errorHandler.handleError(exception, "Could not close the socket");
      }
    }
    return isConnected;
  }
}
