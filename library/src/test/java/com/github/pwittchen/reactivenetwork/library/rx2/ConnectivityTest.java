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
import io.reactivex.functions.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("NullAway") public class ConnectivityTest {
  private static final String TYPE_NAME_WIFI = "WIFI";
  private static final String TYPE_NAME_MOBILE = "MOBILE";
  private static final String TYPE_NAME_NONE = "NONE";

  @Test public void shouldCreateConnectivity() {
    // when
    Connectivity connectivity = Connectivity.create();

    // then
    assertThat(connectivity).isNotNull();
    assertThat(connectivity.state()).isEqualTo(NetworkInfo.State.DISCONNECTED);
    assertThat(connectivity.detailedState()).isEqualTo(NetworkInfo.DetailedState.IDLE);
    assertThat(connectivity.type()).isEqualTo(Connectivity.UNKNOWN_TYPE);
    assertThat(connectivity.subType()).isEqualTo(Connectivity.UNKNOWN_SUB_TYPE);
    assertThat(connectivity.available()).isFalse();
    assertThat(connectivity.failover()).isFalse();
    assertThat(connectivity.roaming()).isFalse();
    assertThat(connectivity.typeName()).isEqualTo(TYPE_NAME_NONE);
    assertThat(connectivity.subTypeName()).isEqualTo(TYPE_NAME_NONE);
    assertThat(connectivity.reason()).isEmpty();
    assertThat(connectivity.extraInfo()).isEmpty();
  }

  @Test public void stateShouldBeEqualToGivenValue() throws Exception {
    // given
    final Connectivity connectivity = Connectivity.state(NetworkInfo.State.CONNECTED)
        .type(ConnectivityManager.TYPE_WIFI)
        .typeName(TYPE_NAME_WIFI)
        .build();

    // when
    final Predicate<Connectivity> equalTo = ConnectivityPredicate.hasState(connectivity.state());
    final Boolean shouldBeEqualToGivenStatus = equalTo.test(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isTrue();
  }

  @Test public void stateShouldBeEqualToOneOfGivenMultipleValues() throws Exception {
    // given
    final Connectivity connectivity = Connectivity.state(NetworkInfo.State.CONNECTING)
        .type(ConnectivityManager.TYPE_WIFI)
        .typeName(TYPE_NAME_WIFI)
        .build();

    final NetworkInfo.State states[] =
        { NetworkInfo.State.CONNECTED, NetworkInfo.State.CONNECTING };

    // when
    final Predicate<Connectivity> equalTo = ConnectivityPredicate.hasState(states);
    final Boolean shouldBeEqualToGivenStatus = equalTo.test(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isTrue();
  }

  @Test public void stateShouldNotBeEqualToGivenValue() throws Exception {
    // given
    final Connectivity connectivity =
        Connectivity.state(NetworkInfo.State.DISCONNECTED)
            .type(ConnectivityManager.TYPE_WIFI)
            .typeName(TYPE_NAME_WIFI)
            .build();

    // when
    final Predicate<Connectivity> equalTo =
        ConnectivityPredicate.hasState(NetworkInfo.State.CONNECTED);
    final Boolean shouldBeEqualToGivenStatus = equalTo.test(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isFalse();
  }

  @Test public void typeShouldBeEqualToGivenValue() throws Exception {
    // given
    final Connectivity connectivity = Connectivity.state(NetworkInfo.State.CONNECTED)
        .type(ConnectivityManager.TYPE_WIFI)
        .typeName(TYPE_NAME_WIFI)
        .build();

    // note that unknown type is added initially by the ConnectivityPredicate#hasType method
    final int givenTypes[] = { connectivity.type(), Connectivity.UNKNOWN_TYPE };

    // when
    final Predicate<Connectivity> equalTo = ConnectivityPredicate.hasType(givenTypes);
    final Boolean shouldBeEqualToGivenStatus = equalTo.test(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isTrue();
  }

  @Test public void typeShouldBeEqualToOneOfGivenMultipleValues() throws Exception {
    // given
    final Connectivity connectivity = Connectivity.state(NetworkInfo.State.CONNECTING)
        .type(ConnectivityManager.TYPE_MOBILE)
        .typeName(TYPE_NAME_MOBILE)
        .build();

    // note that unknown type is added initially by the ConnectivityPredicate#hasType method
    final int givenTypes[] = {
        ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_MOBILE, Connectivity.UNKNOWN_TYPE
    };

    // when
    final Predicate<Connectivity> equalTo = ConnectivityPredicate.hasType(givenTypes);
    final Boolean shouldBeEqualToGivenStatus = equalTo.test(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isTrue();
  }

  @Test public void typeShouldNotBeEqualToGivenValue() throws Exception {
    // given
    final Connectivity connectivity = Connectivity.state(NetworkInfo.State.CONNECTED)
        .type(ConnectivityManager.TYPE_WIFI)
        .typeName(TYPE_NAME_WIFI)
        .build();

    // note that unknown type is added initially by the ConnectivityPredicate#hasType method
    final int givenTypes[] = { ConnectivityManager.TYPE_MOBILE, Connectivity.UNKNOWN_TYPE };

    // when
    final Predicate<Connectivity> equalTo = ConnectivityPredicate.hasType(givenTypes);
    final Boolean shouldBeEqualToGivenStatus = equalTo.test(connectivity);

    // then
    assertThat(shouldBeEqualToGivenStatus).isFalse();
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

  @Test public void shouldAppendUnknownTypeWhileFilteringNetworkTypesInsidePredicate() {
    // given
    int[] types = { ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI };
    int[] expectedOutputTypes = {
        ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI, Connectivity.UNKNOWN_TYPE
    };

    // when
    int[] outputTypes = ConnectivityPredicate.appendUnknownNetworkTypeToTypes(types);

    // then
    assertThat(outputTypes).isEqualTo(expectedOutputTypes);
  }

  @Test
  public void shouldAppendUnknownTypeWhileFilteringNetworkTypesInsidePredicateForEmptyArray() {
    // given
    int[] types = {};
    int[] expectedOutputTypes = { Connectivity.UNKNOWN_TYPE };

    // when
    int[] outputTypes = ConnectivityPredicate.appendUnknownNetworkTypeToTypes(types);

    // then
    assertThat(outputTypes).isEqualTo(expectedOutputTypes);
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
    Connectivity connectivity = Connectivity.state(state)
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
    assertThat(connectivity.state()).isEqualTo(state);
    assertThat(connectivity.detailedState()).isEqualTo(detailedState);
    assertThat(connectivity.type()).isEqualTo(type);
    assertThat(connectivity.subType()).isEqualTo(subType);
    assertThat(connectivity.available()).isTrue();
    assertThat(connectivity.failover()).isFalse();
    assertThat(connectivity.roaming()).isTrue();
    assertThat(connectivity.typeName()).isEqualTo(typeName);
    assertThat(connectivity.subTypeName()).isEqualTo(subTypeName);
    assertThat(connectivity.reason()).isEqualTo(reason);
    assertThat(connectivity.extraInfo()).isEqualTo(extraInfo);
  }

  @Test public void connectivityShouldNotBeEqualToAnotherOne() {
    // given
    Connectivity connectivityOne = Connectivity.state(NetworkInfo.State.CONNECTED)
        .detailedState(NetworkInfo.DetailedState.CONNECTED)
        .type(ConnectivityManager.TYPE_WIFI)
        .subType(1)
        .available(true)
        .failover(true)
        .roaming(true)
        .typeName(TYPE_NAME_WIFI)
        .subTypeName("subtypeOne")
        .reason("reasonOne")
        .extraInfo("extraInfoOne")
        .build();

    Connectivity connectivityTwo = Connectivity.state(NetworkInfo.State.DISCONNECTED)
        .detailedState(NetworkInfo.DetailedState.DISCONNECTED)
        .type(ConnectivityManager.TYPE_MOBILE)
        .subType(2)
        .available(false)
        .failover(false)
        .roaming(false)
        .typeName(TYPE_NAME_MOBILE)
        .subTypeName("subtypeTwo")
        .reason("reasonTwo")
        .extraInfo("extraInfoTwo")
        .build();

    // when
    final boolean isAnotherConnectivityTheSame = connectivityOne.equals(connectivityTwo);

    // then
    assertThat(isAnotherConnectivityTheSame).isFalse();
  }

  @Test public void shouldCreateDefaultConnectivityWhenConnectivityManagerIsNull() {
    // given
    final Context context = RuntimeEnvironment.application.getApplicationContext();
    final ConnectivityManager connectivityManager = null;

    // when
    Connectivity connectivity = Connectivity.create(context, connectivityManager);

    // then
    assertThat(connectivity.type()).isEqualTo(Connectivity.UNKNOWN_TYPE);
    assertThat(connectivity.subType()).isEqualTo(Connectivity.UNKNOWN_SUB_TYPE);
    assertThat(connectivity.state()).isEqualTo(NetworkInfo.State.DISCONNECTED);
    assertThat(connectivity.detailedState()).isEqualTo(NetworkInfo.DetailedState.IDLE);
    assertThat(connectivity.available()).isFalse();
    assertThat(connectivity.failover()).isFalse();
    assertThat(connectivity.roaming()).isFalse();
    assertThat(connectivity.typeName()).isEqualTo(TYPE_NAME_NONE);
    assertThat(connectivity.subTypeName()).isEqualTo(TYPE_NAME_NONE);
    assertThat(connectivity.reason()).isEmpty();
    assertThat(connectivity.extraInfo()).isEmpty();
  }
}
