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
package com.github.pwittchen.reactivenetwork.library.rx2.internet.observing;

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Internet observing strategy allows to implement different strategies for monitoring connectivity
 * with the Internet.
 */
public interface InternetObservingStrategy {
  /**
   * Observes connectivity with the Internet by opening socket connection with remote host in a
   * given interval infinitely
   *
   * @param initialIntervalInMs in milliseconds determining the delay of the first connectivity
   * check
   * @param intervalInMs in milliseconds determining how often we want to check connectivity
   * @param host for checking Internet connectivity
   * @param port for checking Internet connectivity
   * @param timeoutInMs for pinging remote host in milliseconds
   * @param errorHandler for handling errors while checking connectivity
   * @return RxJava Observable with Boolean - true, when we have connection with host and false if
   * not
   */
  Observable<Boolean> observeInternetConnectivity(final int initialIntervalInMs,
      final int intervalInMs, final String host, final int port, final int timeoutInMs,
      final int httpResponse, final ErrorHandler errorHandler);

  /**
   * Observes connectivity with the Internet by opening socket connection with remote host once
   *
   * @param host for checking Internet connectivity
   * @param port for checking Internet connectivity
   * @param timeoutInMs for pinging remote host in milliseconds
   * @param errorHandler for handling errors while checking connectivity
   * @return RxJava Single with Boolean - true, when we have connection with host and false if
   * not
   */
  Single<Boolean> checkInternetConnectivity(final String host, final int port,
      final int timeoutInMs, final int httpResponse, final ErrorHandler errorHandler);

  /**
   * Gets default remote ping host for a given Internet Observing Strategy
   *
   * @return String with a ping host used in the current strategy
   */
  String getDefaultPingHost();
}
