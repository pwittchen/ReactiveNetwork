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

public enum WifiSignalLevel {
  NO_SIGNAL(0, "no signal"),
  POOR(1, "poor"),
  FAIR(2, "fair"),
  GOOD(3, "good"),
  EXCELLENT(4, "excellent");

  public final int level;
  public final String description;

  WifiSignalLevel(final int level, final String description) {
    this.level = level;
    this.description = description;
  }

  public static int getMaxLevel() {
    return EXCELLENT.level;
  }

  /**
   * Gets WifiSignalLevel enum basing on integer value
   * @param level as an integer
   * @return WifiSignalLevel enum
   */
  public static WifiSignalLevel fromLevel(final int level) {
    switch (level) {
      case 0:
        return NO_SIGNAL;
      case 1:
        return POOR;
      case 2:
        return FAIR;
      case 3:
        return GOOD;
      case 4:
        return EXCELLENT;
      default:
        return NO_SIGNAL;
    }
  }

  @Override public String toString() {
    return "WifiSignalLevel{" + "level=" + level + ", description='" + description + '\'' + '}';
  }
}
