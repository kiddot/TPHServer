package com.android.server.core.push;

/**
 * Created by root on 17-7-18.
 */
public abstract class PushCallback {

    void onResult(PushResult result) {
        switch (result.resultCode) {
            case PushResult.CODE_SUCCESS:
                onSuccess(result.userId, result.location);
                break;
            case PushResult.CODE_FAILURE:
                onFailure(result.userId, result.location);
                break;
            case PushResult.CODE_OFFLINE:
                onOffline(result.userId, result.location);
                break;
            case PushResult.CODE_TIMEOUT:
                onTimeout(result.userId, result.location);
                break;
        }
    }
    //void onResult(PushResult result);

    /**
     * 推送成功, 指定用户推送时重写此方法
     *
     * @param userId   成功的用户, 如果是广播, 值为空
     * @param location 用户所在机器, 如果是广播, 值为空
     */
    abstract void onSuccess(String userId, ClientLocation location);

    /**
     * 推送失败
     *
     * @param userId   推送用户
     * @param location 用户所在机器
     */
    abstract void onFailure(String userId, ClientLocation location) ;

    /**
     * 推送用户不在线
     *
     * @param userId   推送用户
     * @param location 用户所在机器
     */
    abstract void onOffline(String userId, ClientLocation location) ;

    /**
     * 推送超时
     *
     * @param userId   推送用户
     * @param location 用户所在机器
     */
    abstract void onTimeout(String userId, ClientLocation location) ;
}
