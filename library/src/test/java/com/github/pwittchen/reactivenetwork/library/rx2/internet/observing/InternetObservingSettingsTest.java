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

import androidx.annotation.NonNull;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.DefaultErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.WalledGardenInternetObservingStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
public class InternetObservingSettingsTest {

  @Test
  public void shouldCreateSettings() {
    // when
    InternetObservingSettings settings = InternetObservingSettings.create();

    // then
    assertThat(settings).isNotNull();
  }

  @Test
  public void shouldBuildSettingsWithDefaultValues() {
    // when
    InternetObservingSettings settings = InternetObservingSettings.create();

    // then
    assertThat(settings.initialInterval()).isEqualTo(0);
    assertThat(settings.interval()).isEqualTo(2000);
    assertThat(settings.host()).isEqualTo("http://clients3.google.com/generate_204");
    assertThat(settings.port()).isEqualTo(80);
    assertThat(settings.timeout()).isEqualTo(2000);
    assertThat(settings.httpResponse()).isEqualTo(204);
    assertThat(settings.errorHandler()).isInstanceOf(DefaultErrorHandler.class);
    assertThat(settings.strategy()).isInstanceOf(WalledGardenInternetObservingStrategy.class);
  }

  @Test
  public void shouldBuildSettings() {
    // given
    final int initialInterval = 1;
    final int interval = 2;
    final String host = "www.test.com";
    int port = 90;
    int timeout = 3;
    int httpResponse = 200;
    ErrorHandler testErrorHandler = createTestErrorHandler();
    SocketInternetObservingStrategy strategy = new SocketInternetObservingStrategy();

    // when
    InternetObservingSettings settings = InternetObservingSettings.builder()
        .initialInterval(initialInterval)
        .interval(interval)
        .host(host)
        .port(port)
        .timeout(timeout)
        .httpResponse(httpResponse)
        .errorHandler(testErrorHandler)
        .strategy(strategy)
        .build();

    // then
    assertThat(settings.initialInterval()).isEqualTo(initialInterval);
    assertThat(settings.interval()).isEqualTo(interval);
    assertThat(settings.host()).isEqualTo(host);
    assertThat(settings.port()).isEqualTo(port);
    assertThat(settings.timeout()).isEqualTo(timeout);
    assertThat(settings.httpResponse()).isEqualTo(httpResponse);
    assertThat(settings.errorHandler()).isNotNull();
    assertThat(settings.errorHandler()).isNotInstanceOf(DefaultErrorHandler.class);
    assertThat(settings.strategy()).isInstanceOf(SocketInternetObservingStrategy.class);
  }

  @NonNull private ErrorHandler createTestErrorHandler() {
    return new ErrorHandler() {
      @Override public void handleError(Exception exception, String message) {
      }
    };
  }
}