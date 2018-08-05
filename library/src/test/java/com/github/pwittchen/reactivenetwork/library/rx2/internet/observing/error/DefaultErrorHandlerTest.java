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
package com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("NullAway") public class DefaultErrorHandlerTest {

  @Rule public MockitoRule rule = MockitoJUnit.rule();
  @Spy private DefaultErrorHandler handler = new DefaultErrorHandler();

  @Test public void shouldHandleErrorDuringClosingSocket() {
    // given
    final String errorMsg = "Could not close the socket";
    final Exception exception = new Exception(errorMsg);

    // when
    handler.handleError(exception, errorMsg);

    // then
    verify(handler, times(1)).handleError(exception, errorMsg);
  }
}
