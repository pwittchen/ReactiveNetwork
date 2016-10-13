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
import rx.functions.Func1;

/**
 * Connectivity class represents current connectivity, which consists of state, type and name
 */
public class Connectivity {
  private static final NetworkInfo.State DEFAULT_STATE = NetworkInfo.State.DISCONNECTED;
  private static final int DEFAULT_TYPE = -1;
  private static final String DEFAULT_NAME = "NONE";

  private NetworkInfo.State state;
  private int type;
  private String name;

  public static Connectivity create() {
    return new Connectivity();
  }

  public static Connectivity create(Context context) {
    Preconditions.checkNotNull(context, "context == null");
    return new Connectivity(context);
  }

  public static Connectivity create(NetworkInfo.State state, int type, String name) {
    Preconditions.checkNotNull(state, "state == null");
    Preconditions.checkNotNullOrEmpty(name, "name is null or empty");
    return new Connectivity(state, type, name);
  }

  private Connectivity() {
    this(DEFAULT_STATE, DEFAULT_TYPE, DEFAULT_NAME);
  }

  private Connectivity(Context context) {
    final NetworkInfo networkInfo = getNetworkInfo(context);
    if (networkInfo == null) {
      initAttributes(DEFAULT_STATE, DEFAULT_TYPE, DEFAULT_NAME);
    } else {
      initAttributes(networkInfo.getState(), networkInfo.getType(), networkInfo.getTypeName());
    }
  }

  private Connectivity(NetworkInfo.State state, int type, String name) {
    initAttributes(state, type, name);
  }

  private NetworkInfo getNetworkInfo(Context context) {
    final String service = Context.CONNECTIVITY_SERVICE;
    final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(service);
    return manager.getActiveNetworkInfo();
  }

  private void initAttributes(NetworkInfo.State state, int type, String name) {
    this.state = state;
    this.type = type;
    this.name = name;
  }

  public NetworkInfo.State getState() {
    return state;
  }

  public int getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  /**
   * Filter, which returns true if at least one given state occurred
   *
   * @param states NetworkInfo.State, which can have one or more states
   * @return true if at least one given state occurred
   */
  public static Func1<Connectivity, Boolean> hasState(final NetworkInfo.State... states) {
    return new Func1<Connectivity, Boolean>() {
      @Override public Boolean call(Connectivity connectivity) {
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
  public static Func1<Connectivity, Boolean> hasType(final int... types) {
    return new Func1<Connectivity, Boolean>() {
      @Override public Boolean call(Connectivity connectivity) {
        for (int type : types) {
          if (connectivity.getType() == type) {
            return true;
          }
        }
        return false;
      }
    };
  }

  /**
   * Checks if current connectivity is in default, disconnected state without information any
   * information about network type.
   *
   * @return boolean true if connectivity is default and false if not
   */
  public boolean isDefault() {
    return getState() == DEFAULT_STATE && getType() == DEFAULT_TYPE && getName().equals(DEFAULT_NAME);
  }

  @Override public String toString() {
    return "Connectivity{" + "state=" + state + ", type=" + type + ", name='" + name + '\'' + '}';
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Connectivity that = (Connectivity) o;

    if (type != that.type) {
      return false;
    }

    if (state != that.state) {
      return false;
    }
    return name.equals(that.name);
  }

  @Override public int hashCode() {
    int result = state.hashCode();
    result = 31 * result + type;
    result = 31 * result + name.hashCode();
    return result;
  }
}
