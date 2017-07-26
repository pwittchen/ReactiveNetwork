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

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class PreconditionsTest {

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
}
