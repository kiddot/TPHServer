package com.android.server.netty.http;

import io.netty.handler.codec.http.HttpResponse;

/**
 * Created by root on 17-7-17.
 */
public interface HttpCallback {

    void onResponse(HttpResponse response);

    void onFailure(int statusCode, String reasonPhrase);

    void onException(Throwable throwable);

    void onTimeout();

    boolean onRedirect(HttpResponse response);
}
