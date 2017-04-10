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
package com.github.pwittchen.reactivenetwork.library.rx2.network.observing;

import android.content.Context;
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import io.reactivex.Observable;

/**
 * Network observing strategy allows to implement different strategies for monitoring network
 * connectivity change. Network monitoring API may differ depending of specific Android version.
 */
public interface NetworkObservingStrategy {
  /**
   * Observes network connectivity
   *
   * @param context of the Activity or an Application
   * @return Observable representing stream of the network connectivity
   */
  Observable<Connectivity> observeNetworkConnectivity(final Context context);

  /**
   * Handles errors, which occurred during observing network connectivity
   *
   * @param message to be processed
   * @param exception which was thrown
   */
  void onError(final String message, final Exception exception);
}
