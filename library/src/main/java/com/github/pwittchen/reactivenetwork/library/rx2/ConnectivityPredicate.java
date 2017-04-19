/*
 * Copyright (C) 2017 Piotr Wittchen
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

import android.net.NetworkInfo;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;

/**
 * ConnectivityPredicate is a class containing predefined methods, which can be used for filtering
 * reactive streams of network connectivity
 */
public class ConnectivityPredicate {

  private ConnectivityPredicate() {
  }

  /**
   * Filter, which returns true if at least one given state occurred
   *
   * @param states NetworkInfo.State, which can have one or more states
   * @return true if at least one given state occurred
   */
  public static Predicate<Connectivity> hasState(final NetworkInfo.State... states) {
    return new Predicate<Connectivity>() {

      @Override
      public boolean test(@NonNull Connectivity connectivity) throws Exception {
        for (NetworkInfo.State state : states) {
          if (connectivity.getState() == state) {
            return true;
          }
        }
        return false;
      }
    };
  }

  /**
   * Filter, which returns true if at least one given type occurred
   *
   * @param types int, which can have one or more types
   * @return true if at least one given type occurred
   */
  public static Predicate<Connectivity> hasType(final int... types) {
    return new Predicate<Connectivity>() {

      @Override
      public boolean test(@NonNull Connectivity connectivity) throws Exception {
        for (int type : types) {
          if (connectivity.getType() == type) {
            return true;
          }
        }
        return false;
      }
    };
  }
}
