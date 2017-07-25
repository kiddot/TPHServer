package com.android.server.boot;

import com.android.server.core.push.*;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by kiddo on 17-7-15.
 */

public class Main {

    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        ServerLauncher launcher = new ServerLauncher();
        launcher.start();
//Test
//        PushSender sender = new PushServer();
//        PushCallback pushCallback = new PushCallback() {
//            @Override
//            public void onSuccess(String userId, ClientLocation location) {
//
//            }
//
//            @Override
//            public void onFailure(String userId, ClientLocation location) {
//
//            }
//
//            @Override
//            public void onOffline(String userId, ClientLocation location) {
//
//            }
//
//            @Override
//            public void onTimeout(String userId, ClientLocation location) {
//
//            }
//        };
//        sender.send("hello", "user-0", pushCallback);
    }
}
