package com.github.pwittchen.reactivenetwork.library.internet.observing.strategy;

import com.github.pwittchen.reactivenetwork.library.Preconditions;
import com.github.pwittchen.reactivenetwork.library.internet.observing.InternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.internet.observing.error.ErrorHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WalledGardenInternetObservingStrategy implements InternetObservingStrategy {

  private static final String DEFAULT_HOST = "http://clients3.google.com/generate_204";

  @Override public Observable<Boolean> observeInternetConnectivity(final int initialIntervalInMs,
      final int intervalInMs, final String host, final int port, final int timeoutInMs,
      final ErrorHandler errorHandler) {

    checkPreconditions(initialIntervalInMs, intervalInMs, host, port, timeoutInMs, errorHandler);

    return Observable.interval(initialIntervalInMs, intervalInMs, TimeUnit.MILLISECONDS,
        Schedulers.io()).map(new Func1<Long, Boolean>() {
      @Override public Boolean call(Long aLong) {
        return isConnected(host, port, timeoutInMs, errorHandler);
      }
    }).distinctUntilChanged();
  }

  @Override public String getDefaultPingHost() {
    return DEFAULT_HOST;
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

  public Boolean isConnected(final String host, final int port, final int timeoutInMs,
      final ErrorHandler errorHandler) {
    HttpURLConnection urlConnection = null;
    try {
      urlConnection = createHttpUrlConnection(host, port, timeoutInMs);
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

  public HttpURLConnection createHttpUrlConnection(final String host, final int port,
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
}
