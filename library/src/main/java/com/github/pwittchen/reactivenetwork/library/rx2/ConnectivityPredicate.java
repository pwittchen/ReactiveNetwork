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
public final class ConnectivityPredicate {

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
      @Override public boolean test(@NonNull Connectivity connectivity) throws Exception {
        for (NetworkInfo.State state : states) {
          if (connectivity.state() == state) {
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
    final int[] extendedTypes = appendUnknownNetworkTypeToTypes(types);
    return new Predicate<Connectivity>() {
      @Override public boolean test(@NonNull Connectivity connectivity) throws Exception {
        for (int type : extendedTypes) {
          if (connectivity.type() == type) {
            return true;
          }
        }
        return false;
      }
    };
  }

  /**
   * Returns network types from the input with additional unknown type,
   * what helps during connections filtering when device
   * is being disconnected from a specific network
   *
   * @param types of the network as an array of ints
   * @return types of the network with unknown type as an array of ints
   */
  protected static int[] appendUnknownNetworkTypeToTypes(int[] types) {
    int i = 0;
    final int[] extendedTypes = new int[types.length + 1];
    for (int type : types) {
      extendedTypes[i] = type;
      i++;
    }
    extendedTypes[i] = Connectivity.UNKNOWN_TYPE;
    return extendedTypes;
  }
}
