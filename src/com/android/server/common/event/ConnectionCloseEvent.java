package com.android.server.common.event;

import com.android.server.core.connection.Connection;

/**
 * Created by root on 17-7-22.
 */
public final class ConnectionCloseEvent {
    public final Connection connection;


    public ConnectionCloseEvent(Connection connection) {
        this.connection = connection;
    }
}
