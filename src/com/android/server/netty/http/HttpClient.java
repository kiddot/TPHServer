package com.android.server.netty.http;

import com.android.server.common.Service;

/**
 * Created by root on 17-7-17.
 */
public interface HttpClient extends Service {
    void request(RequestContext context) throws Exception;
}
