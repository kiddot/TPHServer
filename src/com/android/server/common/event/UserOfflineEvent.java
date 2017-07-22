package com.android.server.common.event;

import com.android.server.core.connection.Connection;

/**
 * Created by root on 17-7-22.
 */
public final class UserOfflineEvent implements Event{

    private final Connection connection;
    private final String userId;

    public UserOfflineEvent(Connection connection, String userId) {
        this.connection = connection;
        this.userId = userId;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getUserId() {
        return userId;
    }
}