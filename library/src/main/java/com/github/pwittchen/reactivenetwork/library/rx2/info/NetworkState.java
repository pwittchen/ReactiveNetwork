package com.github.pwittchen.reactivenetwork.library.rx2.info;

import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;

/**
 * NetworkState data object
 */
public class NetworkState {
    private boolean isConnected = false;
    private Network network = null;
    private NetworkCapabilities networkCapabilities = null;
    private LinkProperties linkProperties = null;

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public NetworkCapabilities getNetworkCapabilities() {
        return networkCapabilities;
    }

    public void setNetworkCapabilities(NetworkCapabilities networkCapabilities) {
        this.networkCapabilities = networkCapabilities;
    }

    public LinkProperties getLinkProperties() {
        return linkProperties;
    }

    public void setLinkProperties(LinkProperties linkProperties) {
        this.linkProperties = linkProperties;
    }
}
