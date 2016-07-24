package com.github.pwittchen.reactivenetwork.library;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.github.pwittchen.reactivenetwork.library.network.observing.NetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.network.observing.strategy.LollipopNetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.network.observing.strategy.PreLollipopNetworkObservingStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.functions.Action1;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class) public class NetworkObservingStrategyTest {

  @Test public void lollipopObserveNetworkConnectivityShouldBeDefaultIfEmpty() {
    // given
    NetworkObservingStrategy strategy = new LollipopNetworkObservingStrategy();

    // when
    assertThatConnectivityIsDefault(strategy);
  }

  @Test public void preLollipopObserveNetworkConnectivityShouldBeDefaultIfEmpty() {
    // given
    NetworkObservingStrategy strategy = new PreLollipopNetworkObservingStrategy();

    // when
    assertThatConnectivityIsDefault(strategy);
  }

  private void assertThatConnectivityIsDefault(NetworkObservingStrategy strategy) {
    strategy.observeNetworkConnectivity(InstrumentationRegistry.getContext())
        .subscribe(new Action1<Connectivity>() {
          @Override public void call(Connectivity connectivity) {
            // then
            assertThat(connectivity.isDefault()).isTrue();
          }
        });
  }
}
