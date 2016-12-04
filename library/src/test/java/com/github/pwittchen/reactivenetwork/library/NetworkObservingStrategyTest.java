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

import android.net.NetworkInfo;
import com.github.pwittchen.reactivenetwork.library.network.observing.NetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.network.observing.strategy.LollipopNetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.network.observing.strategy.PreLollipopNetworkObservingStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import rx.functions.Action1;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class NetworkObservingStrategyTest {

  @Test public void lollipopObserveNetworkConnectivityShouldBeConnectedWhenNetworkIsAvailable() {
    // given
    final NetworkObservingStrategy strategy = new LollipopNetworkObservingStrategy();

    // when
    assertThatIsConnected(strategy);
  }

  @Test public void preLollipopObserveNetworkConnectivityShouldBeConnectedWhenNetworkIsAvailable() {
    // given
    final NetworkObservingStrategy strategy = new PreLollipopNetworkObservingStrategy();

    // when
    assertThatIsConnected(strategy);
  }

  private void assertThatIsConnected(NetworkObservingStrategy strategy) {
    strategy.observeNetworkConnectivity(RuntimeEnvironment.application)
        .subscribe(new Action1<Connectivity>() {
          @Override public void call(Connectivity connectivity) {
            // then
            assertThat(connectivity.getState()).isEqualTo(NetworkInfo.State.CONNECTED);
          }
        });
  }
}
