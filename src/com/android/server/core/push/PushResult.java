package com.android.server.core.push;

import com.android.server.core.connection.Connection;
import com.sun.org.apache.regexp.internal.RE;

import java.util.Arrays;

/**
 * Created by root on 17-7-18.
 */
public class PushResult {
    public static final int CODE_SUCCESS = 1;
    public static final int CODE_FAILURE = 2;
    public static final int CODE_OFFLINE = 3;
    public static final int CODE_TIMEOUT = 4;
    public int resultCode;
    public String userId;
    public Object[] timeLine;
    public ClientLocation location;
    public Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public PushResult setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    public PushResult(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public PushResult setResultCode(int resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public PushResult setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Object[] getTimeLine() {
        return timeLine;
    }

    public PushResult setTimeLine(Object[] timeLine) {
        this.timeLine = timeLine;
        return this;
    }

    public ClientLocation getLocation() {
        return location;
    }

    public PushResult setLocation(ClientLocation location) {
        this.location = location;
        return this;
    }

    public String getResultDesc() {
        switch (resultCode) {
            case CODE_SUCCESS:
                return "success";
            case CODE_FAILURE:
                return "failure";
            case CODE_OFFLINE:
                return "offline";
            case CODE_TIMEOUT:
                return "timeout";
        }
        return Integer.toString(CODE_TIMEOUT);
    }

    @Override
    public String toString() {
        return "PushResult{" +
                "resultCode=" + getResultDesc() +
                ", userId='" + userId + '\'' +
                ", timeLine=" + Arrays.toString(timeLine) +
                ", " + location +
                '}';
    }
}
