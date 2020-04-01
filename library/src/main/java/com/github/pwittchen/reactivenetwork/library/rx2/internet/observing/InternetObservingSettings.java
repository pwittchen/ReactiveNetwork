/*
 * Copyright (C) 2018 Piotr Wittchen
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

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.DefaultErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.WalledGardenInternetObservingStrategy;
import java.net.HttpURLConnection;

/**
 * Contains state of internet connectivity settings.
 * We should use its Builder for creating new settings
 */
@SuppressWarnings("PMD") // I want to have the same method names as variable names on purpose
public final class InternetObservingSettings {
  private final int initialInterval;
  private final int interval;
  private final String host;
  private final int port;
  private final int timeout;
  private final int httpResponse;
  private final ErrorHandler errorHandler;
  private final InternetObservingStrategy strategy;

  private InternetObservingSettings(int initialInterval, int interval, String host, int port,
      int timeout, int httpResponse, ErrorHandler errorHandler,
      InternetObservingStrategy strategy) {
    this.initialInterval = initialInterval;
    this.interval = interval;
    this.host = host;
    this.port = port;
    this.timeout = timeout;
    this.httpResponse = httpResponse;
    this.errorHandler = errorHandler;
    this.strategy = strategy;
  }

  /**
   * @return settings with default parameters
   */
  public static InternetObservingSettings create() {
    return new Builder().build();
  }

  private InternetObservingSettings(Builder builder) {
    this(builder.initialInterval, builder.interval, builder.host, builder.port, builder.timeout,
        builder.httpResponse, builder.errorHandler, builder.strategy);
  }

  private InternetObservingSettings() {
    this(builder());
  }

  /**
   * Creates builder object
   * @return Builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * @return initial ping interval in milliseconds
   */
  public int initialInterval() {
    return initialInterval;
  }

  /**
   * @return ping interval in milliseconds
   */
  public int interval() {
    return interval;
  }

  /**
   * @return ping host
   */
  public String host() {
    return host;
  }

  /**
   * @return ping port
   */
  public int port() {
    return port;
  }

  /**
   * @return ping timeout in milliseconds
   */
  public int timeout() {
    return timeout;
  }

  public int httpResponse() {
    return httpResponse;
  }

  /**
   * @return error handler for pings and connections
   */
  public ErrorHandler errorHandler() {
    return errorHandler;
  }

  /**
   * @return internet observing strategy
   */
  public InternetObservingStrategy strategy() {
    return strategy;
  }

  /**
   * Settings builder, which contains default parameters
   */
  public final static class Builder {
    private int initialInterval = 0;
    private int interval = 2000;
    private String host = "http://clients3.google.com/generate_204";
    private int port = 80;
    private int timeout = 2000;
    private int httpResponse = HttpURLConnection.HTTP_NO_CONTENT;
    private ErrorHandler errorHandler = new DefaultErrorHandler();
    private InternetObservingStrategy strategy = new WalledGardenInternetObservingStrategy();

    private Builder() {
    }

    /**
     * sets initial ping interval in milliseconds
     *
     * @param initialInterval in milliseconds
     * @return Builder
     */
    public Builder initialInterval(int initialInterval) {
      this.initialInterval = initialInterval;
      return this;
    }

    /**
     * sets ping interval in milliseconds
     *
     * @param interval in milliseconds
     * @return Builder
     */
    public Builder interval(int interval) {
      this.interval = interval;
      return this;
    }

    /**
     * sets ping host
     *
     * @return Builder
     */
    public Builder host(String host) {
      this.host = host;
      return this;
    }

    /**
     * sets ping port
     *
     * @return Builder
     */
    public Builder port(int port) {
      this.port = port;
      return this;
    }

    /**
     * sets ping timeout in milliseconds
     *
     * @param timeout in milliseconds
     * @return Builder
     */
    public Builder timeout(int timeout) {
      this.timeout = timeout;
      return this;
    }

    /**
     * sets HTTP response code indicating that connection is established
     *
     * @param httpResponse as integer
     * @return Builder
     */
    public Builder httpResponse(final int httpResponse) {
      this.httpResponse = httpResponse;
      return this;
    }

    /**
     * sets error handler for pings and connections
     *
     * @return Builder
     */
    public Builder errorHandler(ErrorHandler errorHandler) {
      this.errorHandler = errorHandler;
      return this;
    }

    /**
     * sets internet observing strategy
     *
     * @param strategy for observing and internet connection
     * @return Builder
     */
    public Builder strategy(InternetObservingStrategy strategy) {
      this.strategy = strategy;
      return this;
    }

    public InternetObservingSettings build() {
      return new InternetObservingSettings(this);
    }
  }
}
