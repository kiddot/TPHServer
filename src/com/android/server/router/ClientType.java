package com.android.server.router;

import java.util.Arrays;

/**
 * Created by root on 17-7-22.
 */
public enum ClientType {
    MOBILE(1, "android", "ios"),
    PC(2, "windows", "mac", "linux"),
    WEB(3, "web", "h5"),
    UNKNOWN(-1);

    public final int type;
    public final String[] os;

    ClientType(int type, String... os) {
        this.type = type;
        this.os = os;
    }

    public boolean contains(String osName) {
        for (int i = 0; i < os.length; i++ ){
            if (os[i].contains(osName)){
                return true;
            }
        }
        return false;
    }

    public static ClientType find(String osName) {
        for (ClientType type : values()) {
            if (type.contains(osName.toLowerCase())) return type;
        }
        return UNKNOWN;
    }

    public static boolean isSameClient(String osNameA, String osNameB) {
        if (osNameA.equals(osNameB)) return true;
        return find(osNameA).contains(osNameB);
    }
}
