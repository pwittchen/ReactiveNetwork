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
import android.net.NetworkInfo;
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.LollipopNetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.PreLollipopNetworkObservingStrategy;
import io.reactivex.functions.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("NullAway") public class NetworkObservingStrategyTest {

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

  @SuppressWarnings("CheckReturnValue")
  private void assertThatIsConnected(NetworkObservingStrategy strategy) {
    // given
    final Context context = RuntimeEnvironment.application.getApplicationContext();

    //when
    strategy.observeNetworkConnectivity(context).subscribe(new Consumer<Connectivity>() {
      @Override public void accept(Connectivity connectivity) throws Exception {
        // then
        assertThat(connectivity.state()).isEqualTo(NetworkInfo.State.CONNECTED);
      }
    });
  }
}
