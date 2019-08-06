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

import android.os.Build;

public final class Preconditions {
  /**
   * Validation method, which checks if an object is null
   *
   * @param object to verify
   * @param message to be thrown in exception
   */
  public static void checkNotNull(Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Validation method, which checks if a string is null or empty
   *
   * @param string to verify
   * @param message to be thrown in exception
   */
  public static void checkNotNullOrEmpty(String string, String message) {
    if (string == null || string.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Validation method, which checks is an integer number is positive
   *
   * @param number integer to verify
   * @param message to be thrown in exception
   */
  public static void checkGreaterOrEqualToZero(int number, String message) {
    if (number < 0) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Validation method, which checks is an integer number is non-zero or positive
   *
   * @param number integer to verify
   * @param message to be thrown in exception
   */
  public static void checkGreaterThanZero(int number, String message) {
    if (number <= 0) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Validation method, which checks if current Android version is at least Lollipop (API 21) or
   * higher
   *
   * @return boolean true if current Android version is Lollipop or higher
   */
  public static boolean isAtLeastAndroidLollipop() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
  }

  /**
   * Validation method, which checks if current Android version is at least Marshmallow (API 23) or
   * higher
   *
   * @return boolean true if current Android version is Marshmallow or higher
   */
  public static boolean isAtLeastAndroidMarshmallow() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
  }
}


