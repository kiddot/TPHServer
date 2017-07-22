package com.android.server.router;

/**
 * Created by root on 17-7-22.
 */
public interface Router<T> {

    T getRouteValue();

    RouterType getRouteType();

    enum RouterType {
        LOCAL, REMOTE
    }

}
