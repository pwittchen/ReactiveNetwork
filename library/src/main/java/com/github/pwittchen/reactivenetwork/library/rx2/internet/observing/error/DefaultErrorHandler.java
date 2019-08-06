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

import android.util.Log;
import com.jakewharton.nopen.annotation.Open;

import static com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork.LOG_TAG;

@Open public class DefaultErrorHandler implements ErrorHandler {
  @Override public void handleError(final Exception exception, final String message) {
    Log.e(LOG_TAG, message, exception);
  }
}
