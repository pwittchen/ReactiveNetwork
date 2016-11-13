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

import com.github.pwittchen.reactivenetwork.library.internet.socket.DefaultSocketErrorHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class DefaultSocketErrorHandlerTest {

  @Spy DefaultSocketErrorHandler handler;

  @Before public void setUp() {
    handler = new DefaultSocketErrorHandler();
    MockitoAnnotations.initMocks(this);
  }

  @Test public void shouldHandleErrorDuringClosingSocket() {
    // given
    Exception exception = new Exception("error during closing socket");

    // when
    handler.handleErrorDuringClosingSocket(exception);

    // then
    verify(handler, times(1)).handleErrorDuringClosingSocket(exception);
  }
}
