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

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.functions.Func1;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class) public class ConnectivityTest {

  @Test public void testStatusShouldBeEqualToGivenValue() {
    // given
    final NetworkInfo.State givenState = NetworkInfo.State.CONNECTED;
    final int givenType = ConnectivityManager.TYPE_WIFI;
    final String givenTypeName = "WIFI";
    final Connectivity connectivity = Connectivity.create(givenState, givenType, givenTypeName);

    // when
    Func1<Connectivity, Boolean> equalTo = Connectivity.hasState(connectivity.getState());
    Boolean shouldBeEqualToGivenStatus = equalTo.call(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isTrue();
  }

  @Test public void testStatusShouldBeEqualToOneOfGivenMultipleValues() {
    // given
    final NetworkInfo.State givenState = NetworkInfo.State.CONNECTING;
    final int givenType = ConnectivityManager.TYPE_WIFI;
    final String givenTypeName = "WIFI";
    final Connectivity connectivity = Connectivity.create(givenState, givenType, givenTypeName);

    NetworkInfo.State givenStates[] = { NetworkInfo.State.CONNECTED, NetworkInfo.State.CONNECTING };

    // when
    Func1<Connectivity, Boolean> equalTo = Connectivity.hasState(givenStates);
    Boolean shouldBeEqualToGivenStatus = equalTo.call(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isTrue();
  }

  @Test public void testTypeShouldBeEqualToGivenValue() {
    // given
    final NetworkInfo.State givenState = NetworkInfo.State.CONNECTED;
    final int givenType = ConnectivityManager.TYPE_WIFI;
    final String givenTypeName = "WIFI";
    final Connectivity connectivity = Connectivity.create(givenState, givenType, givenTypeName);

    // when
    Func1<Connectivity, Boolean> equalTo = Connectivity.hasType(connectivity.getType());
    Boolean shouldBeEqualToGivenStatus = equalTo.call(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isTrue();
  }

  @Test public void testTypeShouldBeEqualToOneOfGivenMultipleValues() {
    // given
    final NetworkInfo.State givenState = NetworkInfo.State.CONNECTING;
    final int givenType = ConnectivityManager.TYPE_MOBILE;
    final String givenTypeName = "MOBILE";
    final Connectivity connectivity = Connectivity.create(givenState, givenType, givenTypeName);

    int givenTypes[] = { ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_MOBILE };

    // when
    Func1<Connectivity, Boolean> equalTo = Connectivity.hasType(givenTypes);
    Boolean shouldBeEqualToGivenStatus = equalTo.call(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isTrue();
  }
}
