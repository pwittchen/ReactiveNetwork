/*
 * Copyright (C) 2022 Piotr Wittchen
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
package com.github.pwittchen.reactivenetwork.library.rx2.info;

import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import androidx.annotation.Nullable;
import com.jakewharton.nopen.annotation.Open;

@Open
public class NetworkState {
  @SuppressWarnings("PMD") private boolean isConnected = false;
  @Nullable private Network network = null;
  @Nullable private NetworkCapabilities networkCapabilities = null;
  @Nullable private LinkProperties linkProperties = null;

  @SuppressWarnings("PMD")
  public boolean isConnected() {
    return isConnected;
  }

  public void setConnected(boolean connected) {
    isConnected = connected;
  }

  @Nullable public Network getNetwork() {
    return network;
  }

  public void setNetwork(@Nullable Network network) {
    this.network = network;
  }

  @Nullable public NetworkCapabilities getNetworkCapabilities() {
    return networkCapabilities;
  }

  public void setNetworkCapabilities(@Nullable NetworkCapabilities networkCapabilities) {
    this.networkCapabilities = networkCapabilities;
  }

  @Nullable public LinkProperties getLinkProperties() {
    return linkProperties;
  }

  public void setLinkProperties(@Nullable LinkProperties linkProperties) {
    this.linkProperties = linkProperties;
  }
}
