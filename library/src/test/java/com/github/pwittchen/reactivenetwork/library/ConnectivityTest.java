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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import rx.functions.Func1;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class ConnectivityTest {

  @Test public void statusShouldBeEqualToGivenValue() {
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

  @Test public void statusShouldBeEqualToOneOfGivenMultipleValues() {
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

  @Test public void typeShouldBeEqualToGivenValue() {
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

  @Test public void typeShouldBeEqualToOneOfGivenMultipleValues() {
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

  @Test(expected = IllegalArgumentException.class)
  public void createShouldThrowAnExceptionWhenContextIsNull() {
    // given
    Context context = null;

    // when
    Connectivity.create(context);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void createShouldThrowAnExceptionWhenStateIsNull() {
    // given
    NetworkInfo.State state = null;
    int type = 0;
    String name = "name";

    // when
    Connectivity.create(state, type, name);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void createShouldThrowAnExceptionWhenNameIsNull() {
    // given
    NetworkInfo.State state = NetworkInfo.State.CONNECTED;
    int type = 0;
    String name = null;

    // when
    Connectivity.create(state, type, name);

    // then
    // an exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void createShouldThrowAnExceptionWhenNameIsEmpty() {
    // given
    NetworkInfo.State state = NetworkInfo.State.CONNECTED;
    int type = 0;
    String name = "";

    // when
    Connectivity.create(state, type, name);

    // then
    // an exception is thrown
  }

  @Test public void shouldReturnProperToStringValue() {
    // given
    NetworkInfo.State defaultState = NetworkInfo.State.DISCONNECTED;
    int defaultType = -1;
    String defaultName = "NONE";
    String expectedToString = "Connectivity{"
        + "state="
        + defaultState
        + ", type="
        + defaultType
        + ", name='"
        + defaultName
        + '\''
        + '}';

    // when
    Connectivity connectivity = Connectivity.create();

    // then
    assertThat(connectivity.toString()).isEqualTo(expectedToString);
  }

  @Test public void shouldCreateDefaultConnectivity() {
    // given
    Connectivity connectivity;

    // when
    connectivity = Connectivity.create();

    // then
    assertThat(connectivity.isDefault()).isTrue();
  }

  @Test public void theSameConnectivityObjectsShouldBeEqual() {
    // given
    Connectivity connectivityOne = Connectivity.create();
    Connectivity connectivityTwo = Connectivity.create();

    // when
    boolean objectsAreEqual = connectivityOne.equals(connectivityTwo);

    // then
    assertThat(objectsAreEqual).isTrue();
  }

  @Test public void twoDefaultObjectsShouldBeInTheSameBucket() {
    // given
    Connectivity connectivityOne = Connectivity.create();
    Connectivity connectivityTwo = Connectivity.create();

    // when
    boolean hashCodesAreEqual = connectivityOne.hashCode() == connectivityTwo.hashCode();

    // then
    assertThat(hashCodesAreEqual).isTrue();
  }
}
