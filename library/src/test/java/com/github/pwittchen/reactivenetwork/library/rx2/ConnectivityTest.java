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
package com.github.pwittchen.reactivenetwork.library.rx2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.github.pwittchen.reactivenetwork.library.BuildConfig;
import io.reactivex.functions.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class ConnectivityTest {
  private static final String TYPE_NAME_WIFI = "WIFI";
  private static final String TYPE_NAME_MOBILE = "MOBILE";

  @Test public void statusShouldBeEqualToGivenValue() throws Exception {
    // given
    final Connectivity connectivity = new Connectivity.Builder().state(NetworkInfo.State.CONNECTED)
        .type(ConnectivityManager.TYPE_WIFI)
        .typeName(TYPE_NAME_WIFI)
        .build();

    // when
    final Function<Connectivity, Boolean> equalTo =
        ConnectivityPredicate.hasState(connectivity.getState());
    final Boolean shouldBeEqualToGivenStatus = equalTo.apply(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isTrue();
  }

  @Test public void statusShouldBeEqualToOneOfGivenMultipleValues() throws Exception {
    // given
    final Connectivity connectivity = new Connectivity.Builder().state(NetworkInfo.State.CONNECTING)
        .type(ConnectivityManager.TYPE_WIFI)
        .typeName(TYPE_NAME_WIFI)
        .build();

    final NetworkInfo.State states[] =
        { NetworkInfo.State.CONNECTED, NetworkInfo.State.CONNECTING };

    // when
    final Function<Connectivity, Boolean> equalTo = ConnectivityPredicate.hasState(states);
    final Boolean shouldBeEqualToGivenStatus = equalTo.apply(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isTrue();
  }

  @Test public void typeShouldBeEqualToGivenValue() throws Exception {
    // given
    final Connectivity connectivity = new Connectivity.Builder().state(NetworkInfo.State.CONNECTED)
        .type(ConnectivityManager.TYPE_WIFI)
        .typeName(TYPE_NAME_WIFI)
        .build();

    // when
    final Function<Connectivity, Boolean> equalTo =
        ConnectivityPredicate.hasType(connectivity.getType());
    final Boolean shouldBeEqualToGivenStatus = equalTo.apply(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isTrue();
  }

  @Test public void typeShouldBeEqualToOneOfGivenMultipleValues() throws Exception {
    // given
    final Connectivity connectivity = new Connectivity.Builder().state(NetworkInfo.State.CONNECTING)
        .type(ConnectivityManager.TYPE_MOBILE)
        .typeName(TYPE_NAME_MOBILE)
        .build();

    final int givenTypes[] = { ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_MOBILE };

    // when
    final Function<Connectivity, Boolean> equalTo = ConnectivityPredicate.hasType(givenTypes);
    final Boolean shouldBeEqualToGivenStatus = equalTo.apply(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isTrue();
  }

  @Test(expected = IllegalArgumentException.class)
  public void createShouldThrowAnExceptionWhenContextIsNull() {
    // given
    final Context context = null;

    // when
    Connectivity.create(context);

    // then
    // an exception is thrown
  }

  @Test public void shouldReturnProperToStringValue() {
    // given
    final String expectedToString = "Connectivity{"
        + "state=DISCONNECTED, "
        + "detailedState=IDLE, "
        + "type=-1, "
        + "subType=-1, "
        + "available=false, "
        + "failover=false, "
        + "roaming=false, "
        + "typeName='NONE', "
        + "subTypeName='NONE', "
        + "reason='', "
        + "extraInfo=''}";

    // when
    Connectivity connectivity = Connectivity.create();

    // then
    assertThat(connectivity.toString()).isEqualTo(expectedToString);
  }

  @Test public void theSameConnectivityObjectsShouldBeEqual() {
    // given
    final Connectivity connectivityOne = Connectivity.create();
    final Connectivity connectivityTwo = Connectivity.create();

    // when
    boolean objectsAreEqual = connectivityOne.equals(connectivityTwo);

    // then
    assertThat(objectsAreEqual).isTrue();
  }

  @Test public void twoDefaultObjectsShouldBeInTheSameBucket() {
    // given
    final Connectivity connectivityOne = Connectivity.create();
    final Connectivity connectivityTwo = Connectivity.create();

    // when
    boolean hashCodesAreEqual = connectivityOne.hashCode() == connectivityTwo.hashCode();

    // then
    assertThat(hashCodesAreEqual).isTrue();
  }

  @Test public void shouldCreateConnectivityWithBuilder() {
    // given
    NetworkInfo.State state = NetworkInfo.State.CONNECTED;
    NetworkInfo.DetailedState detailedState = NetworkInfo.DetailedState.CONNECTED;
    int type = ConnectivityManager.TYPE_WIFI;
    int subType = ConnectivityManager.TYPE_WIMAX;
    String typeName = TYPE_NAME_WIFI;
    String subTypeName = "test subType";
    String reason = "no reason";
    String extraInfo = "extra info";

    // when
    Connectivity connectivity = new Connectivity.Builder().state(state)
        .detailedState(detailedState)
        .type(type)
        .subType(subType)
        .available(true)
        .failover(false)
        .roaming(true)
        .typeName(typeName)
        .subTypeName(subTypeName)
        .reason(reason)
        .extraInfo(extraInfo)
        .build();

    // then
    assertThat(connectivity.getState()).isEqualTo(state);
    assertThat(connectivity.getDetailedState()).isEqualTo(detailedState);
    assertThat(connectivity.getType()).isEqualTo(type);
    assertThat(connectivity.getSubType()).isEqualTo(subType);
    assertThat(connectivity.isAvailable()).isTrue();
    assertThat(connectivity.isFailover()).isFalse();
    assertThat(connectivity.isRoaming()).isTrue();
    assertThat(connectivity.getTypeName()).isEqualTo(typeName);
    assertThat(connectivity.getSubTypeName()).isEqualTo(subTypeName);
    assertThat(connectivity.getReason()).isEqualTo(reason);
    assertThat(connectivity.getExtraInfo()).isEqualTo(extraInfo);
  }
}
