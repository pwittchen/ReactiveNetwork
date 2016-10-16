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
package com.github.pwittchen.reactivenetwork.library.internet.socket;

import android.util.Log;

import static com.github.pwittchen.reactivenetwork.library.ReactiveNetwork.LOG_TAG;

public class DefaultSocketErrorHandler implements SocketErrorHandler {
  public static final String ON_ERROR_MSG = "Could not close the socket";

  @Override public void handleErrorDuringClosingSocket(Exception exception) {
    Log.e(LOG_TAG, ON_ERROR_MSG, exception);
  }
}
