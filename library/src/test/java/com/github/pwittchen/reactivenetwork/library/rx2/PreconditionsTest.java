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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("NullAway") public class PreconditionsTest {

  private static final String MSG_STRING_IS_NULL = "String is null";
  private static final String MSG_VALUE_IS_NOT_GREATER_THAN_ZERO = "value is not greater than zero";

  @Test @Config(sdk = 21) public void shouldBeAtLeastAndroidLollipop() {
    boolean isAtLeastAndroidLollipop = Preconditions.isAtLeastAndroidLollipop();
    assertThat(isAtLeastAndroidLollipop).isTrue();
  }

  @Test @Config(sdk = 22) public void shouldBeAtLeastAndroidLollipopForHigherApi() {
    boolean isAtLeastAndroidLollipop = Preconditions.isAtLeastAndroidLollipop();
    assertThat(isAtLeastAndroidLollipop).isTrue();
  }

  @Test @Config(sdk = 22) public void shouldNotBeAtLeastAndroidMarshmallowForLowerApi() {
    boolean isAtLeastAndroidMarshmallow = Preconditions.isAtLeastAndroidMarshmallow();
    assertThat(isAtLeastAndroidMarshmallow).isFalse();
  }

  @Test @Config(sdk = 23) public void shouldBeAtLeastAndroidMarshmallow() {
    boolean isAtLeastAndroidMarshmallow = Preconditions.isAtLeastAndroidMarshmallow();
    assertThat(isAtLeastAndroidMarshmallow).isTrue();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionWhenStringIsNull() {
    Preconditions.checkNotNullOrEmpty(null, MSG_STRING_IS_NULL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionWhenStringIsEmpty() {
    Preconditions.checkNotNullOrEmpty("", MSG_STRING_IS_NULL);
  }

  @Test
  public void shouldNotThrowAnythingWhenStringIsNotEmpty() {
    Preconditions.checkNotNullOrEmpty("notEmpty", MSG_STRING_IS_NULL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionWhenValueIsZero() {
    Preconditions.checkGreaterThanZero(0, MSG_VALUE_IS_NOT_GREATER_THAN_ZERO);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionWhenValueLowerThanZero() {
    Preconditions.checkGreaterThanZero(-1, MSG_VALUE_IS_NOT_GREATER_THAN_ZERO);
  }

  @Test
  public void shouldNotThrowAnythingWhenValueIsGreaterThanZero() {
    Preconditions.checkGreaterThanZero(1, MSG_VALUE_IS_NOT_GREATER_THAN_ZERO);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionWhenValueLowerThanZeroForGreaterOrEqualCheck() {
    Preconditions.checkGreaterOrEqualToZero(-1, MSG_VALUE_IS_NOT_GREATER_THAN_ZERO);
  }

  @Test
  public void shouldNotThrowAnythingWhenValueIsGreaterThanZeroForGreaterOrEqualCheck() {
    Preconditions.checkGreaterOrEqualToZero(1, MSG_VALUE_IS_NOT_GREATER_THAN_ZERO);
  }

  @Test
  public void shouldNotThrowAnythingWhenValueIsEqualToZero() {
    Preconditions.checkGreaterOrEqualToZero(0, MSG_VALUE_IS_NOT_GREATER_THAN_ZERO);
  }

}
