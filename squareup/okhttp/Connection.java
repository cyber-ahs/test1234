package com.squareup.okhttp;

import java.net.Socket;
/* loaded from: classes.dex */
public interface Connection {
    Handshake getHandshake();

    Protocol getProtocol();

    Route getRoute();

    Socket getSocket();
}
