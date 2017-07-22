package com.android.server.router;

import com.android.server.core.connection.Connection;

/**
 * Created by root on 17-7-22.
 */
public final class LocalRouter implements Router<Connection> {
    private final Connection connection;

    public LocalRouter(Connection connection) {
        this.connection = connection;
    }

    public int getClientType() {
        return connection.getSessionContext().getClientType();
    }

    @Override
    public Connection getRouteValue() {
        return connection;
    }

    @Override
    public RouterType getRouteType() {
        return RouterType.LOCAL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalRouter that = (LocalRouter) o;

        return getClientType() == that.getClientType();

    }

    @Override
    public int hashCode() {
        return getClientType();
    }

    @Override
    public String toString() {
        return "LocalRouter{" + connection + '}';
    }
}