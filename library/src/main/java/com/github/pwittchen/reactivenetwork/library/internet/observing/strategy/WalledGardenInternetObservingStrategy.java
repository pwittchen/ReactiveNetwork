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

import com.github.pwittchen.reactivenetwork.library.Preconditions;
import com.github.pwittchen.reactivenetwork.library.internet.observing.InternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.internet.observing.error.ErrorHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HttpsURLConnection;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WalledGardenInternetObservingStrategy implements InternetObservingStrategy {

  private static final String DEFAULT_HOST = "https://clients3.google.com/generate_204";
  private static final String HTTP_PROTOCOL = "http://";
  private static final String HTTPS_PROTOCOL = "https://";

  @Override public Observable<Boolean> observeInternetConnectivity(final int initialIntervalInMs,
      final int intervalInMs, final String host, final int port, final int timeoutInMs,
      final ErrorHandler errorHandler) {

    checkPreconditions(initialIntervalInMs, intervalInMs, host, port, timeoutInMs, errorHandler);

    final String adjustedHost = adjustHost(host);

    return Observable.interval(initialIntervalInMs, intervalInMs, TimeUnit.MILLISECONDS,
        Schedulers.io()).map(new Func1<Long, Boolean>() {
      @Override public Boolean call(Long aLong) {
        return isConnected(adjustedHost, port, timeoutInMs, errorHandler);
      }
    }).distinctUntilChanged();
  }

  @Override public String getDefaultPingHost() {
    return DEFAULT_HOST;
  }

  protected String adjustHost(final String host) {
    if (!host.startsWith(HTTP_PROTOCOL) && !host.startsWith(HTTPS_PROTOCOL)) {
      return HTTPS_PROTOCOL.concat(host);
    }

    return host;
  }

  private void checkPreconditions(final int initialIntervalInMs, final int intervalInMs,
      final String host, final int port, final int timeoutInMs, final ErrorHandler errorHandler) {
    Preconditions.checkGreaterOrEqualToZero(initialIntervalInMs,
        "initialIntervalInMs is not a positive number");
    Preconditions.checkGreaterThanZero(intervalInMs, "intervalInMs is not a positive number");
    Preconditions.checkNotNullOrEmpty(host, "host is null or empty");
    Preconditions.checkGreaterThanZero(port, "port is not a positive number");
    Preconditions.checkGreaterThanZero(timeoutInMs, "timeoutInMs is not a positive number");
    Preconditions.checkNotNull(errorHandler, "errorHandler is null");
  }

  protected Boolean isConnected(final String host, final int port, final int timeoutInMs,
      final ErrorHandler errorHandler) {
    HttpURLConnection urlConnection = null;
    try {
      if (host.startsWith(HTTPS_PROTOCOL)) {
        urlConnection = createHttpsUrlConnection(host, port, timeoutInMs);
      } else {
        urlConnection = createHttpUrlConnection(host, port, timeoutInMs);
      }
      return urlConnection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT;
    } catch (IOException e) {
      errorHandler.handleError(e, "Could not establish connection with WalledGardenStrategy");
      return Boolean.FALSE;
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
    }
  }

  protected HttpURLConnection createHttpUrlConnection(final String host, final int port,
      final int timeoutInMs) throws IOException {
    URL initialUrl = new URL(host);
    URL url = new URL(initialUrl.getProtocol(), initialUrl.getHost(), port, initialUrl.getFile());
    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    urlConnection.setConnectTimeout(timeoutInMs);
    urlConnection.setReadTimeout(timeoutInMs);
    urlConnection.setInstanceFollowRedirects(false);
    urlConnection.setUseCaches(false);
    return urlConnection;
  }

  protected HttpsURLConnection createHttpsUrlConnection(final String host, final int port,
      final int timeoutInMs) throws IOException {
    URL initialUrl = new URL(host);
    URL url = new URL(initialUrl.getProtocol(), initialUrl.getHost(), port, initialUrl.getFile());
    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
    urlConnection.setConnectTimeout(timeoutInMs);
    urlConnection.setReadTimeout(timeoutInMs);
    urlConnection.setInstanceFollowRedirects(false);
    urlConnection.setUseCaches(false);
    return urlConnection;
  }
}
