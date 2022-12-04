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
