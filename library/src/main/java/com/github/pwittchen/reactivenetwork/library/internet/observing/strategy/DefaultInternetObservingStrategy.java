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
package com.github.pwittchen.reactivenetwork.library.internet.observing.strategy;

import com.github.pwittchen.reactivenetwork.library.Preconditions;
import com.github.pwittchen.reactivenetwork.library.internet.observing.InternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.internet.observing.error.ErrorHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Default strategy for monitoring connectivity with the Internet
 */
public class DefaultInternetObservingStrategy implements InternetObservingStrategy {

  public static final String ON_CLOSE_SOCKET_ERROR_MSG = "Could not close the socket";

  /**
   * Observes connectivity with the Internet by opening socket connection with remote host
   *
   * @param initialIntervalInMs in milliseconds determining the delay of the first connectivity
   * check
   * @param intervalInMs in milliseconds determining how often we want to check connectivity
   * @param host for checking Internet connectivity
   * @param port for checking Internet connectivity
   * @param timeoutInMs for pinging remote host in milliseconds
   * @param errorHandler for handling errors while closing socket
   * @return RxJava Observable with Boolean - true, when we have connection with host and false if
   * not
   */
  @Override public Observable<Boolean> observeInternetConnectivity(final int initialIntervalInMs,
      final int intervalInMs, final String host, final int port, final int timeoutInMs,
      final ErrorHandler errorHandler) {
    Preconditions.checkGreaterOrEqualToZero(initialIntervalInMs,
        "initialIntervalInMs is not a positive number");
    Preconditions.checkGreaterThanZero(intervalInMs, "intervalInMs is not a positive number");
    Preconditions.checkNotNullOrEmpty(host, "host is null or empty");
    Preconditions.checkGreaterThanZero(port, "port is not a positive number");
    Preconditions.checkGreaterThanZero(timeoutInMs, "timeoutInMs is not a positive number");
    Preconditions.checkNotNull(errorHandler, "errorHandler is null");

    return Observable.interval(initialIntervalInMs, intervalInMs, TimeUnit.MILLISECONDS,
        Schedulers.io()).map(new Func1<Long, Boolean>() {
      @Override public Boolean call(Long tick) {
        return isConnected(host, port, timeoutInMs, errorHandler);
      }
    }).distinctUntilChanged();
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
        errorHandler.handleError(exception, ON_CLOSE_SOCKET_ERROR_MSG);
      }
    }
    return isConnected;
  }
}
