/*
 * Copyright (C) 2017 Piotr Wittchen
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
import com.jakewharton.nopen.annotation.Open;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HttpsURLConnection;

/**
 * Walled Garden Strategy for monitoring connectivity with the Internet.
 * This strategy handle use case of the countries behind Great Firewall (e.g. China),
 * which does not has access to several websites like Google. It such case, different HTTP responses
 * are generated. Instead HTTP 200 (OK), we got HTTP 204 (NO CONTENT), but it still can tell us
 * if a device is connected to the Internet or not.
 */
@Open public class WalledGardenInternetObservingStrategy implements InternetObservingStrategy {
  private static final String DEFAULT_HOST = "https://clients3.google.com/generate_204";
  private static final String HTTP_PROTOCOL = "http://";
  private static final String HTTPS_PROTOCOL = "https://";

  @Override public String getDefaultPingHost() {
    return DEFAULT_HOST;
  }

  @Override public Observable<Boolean> observeInternetConnectivity(final int initialIntervalInMs,
      final int intervalInMs, final String host, final int port, final int timeoutInMs,
      final int httpResponse,
      final ErrorHandler errorHandler) {

    Preconditions.checkGreaterOrEqualToZero(initialIntervalInMs,
        "initialIntervalInMs is not a positive number");
    Preconditions.checkGreaterThanZero(intervalInMs, "intervalInMs is not a positive number");
    checkGeneralPreconditions(host, port, timeoutInMs, httpResponse, errorHandler);

    final String adjustedHost = adjustHost(host);

    return Observable.interval(initialIntervalInMs, intervalInMs, TimeUnit.MILLISECONDS,
        Schedulers.io()).map(new Function<Long, Boolean>() {
      @Override public Boolean apply(@NonNull Long tick) {
        return isConnected(adjustedHost, port, timeoutInMs, httpResponse, errorHandler);
      }
    }).distinctUntilChanged();
  }

  @Override public Single<Boolean> checkInternetConnectivity(final String host, final int port,
      final int timeoutInMs, final int httpResponse, final ErrorHandler errorHandler) {
    checkGeneralPreconditions(host, port, timeoutInMs, httpResponse, errorHandler);

    return Single.create(new SingleOnSubscribe<Boolean>() {
      @Override public void subscribe(@NonNull SingleEmitter<Boolean> emitter) {
        emitter.onSuccess(isConnected(host, port, timeoutInMs, httpResponse, errorHandler));
      }
    });
  }

  protected String adjustHost(final String host) {
    if (!host.startsWith(HTTP_PROTOCOL) && !host.startsWith(HTTPS_PROTOCOL)) {
      return HTTPS_PROTOCOL.concat(host);
    }

    return host;
  }

  private void checkGeneralPreconditions(final String host, final int port, final int timeoutInMs,
      final int httpResponse, final ErrorHandler errorHandler) {
    Preconditions.checkNotNullOrEmpty(host, "host is null or empty");
    Preconditions.checkGreaterThanZero(port, "port is not a positive number");
    Preconditions.checkGreaterThanZero(timeoutInMs, "timeoutInMs is not a positive number");
    Preconditions.checkNotNull(errorHandler, "errorHandler is null");
    Preconditions.checkNotNull(httpResponse, "httpResponse is null");
    Preconditions.checkGreaterThanZero(httpResponse, "httpResponse is not a positive number");
  }

  protected Boolean isConnected(final String host, final int port, final int timeoutInMs,
      final int httpResponse, final ErrorHandler errorHandler) {
    HttpURLConnection urlConnection = null;
    try {
      if (host.startsWith(HTTPS_PROTOCOL)) {
        urlConnection = createHttpsUrlConnection(host, port, timeoutInMs);
      } else {
        urlConnection = createHttpUrlConnection(host, port, timeoutInMs);
      }
      return urlConnection.getResponseCode() == httpResponse;
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
