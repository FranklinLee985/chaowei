package com.eduhdsdk.interfaces;

/**
 * Created by Administrator on 2017/3/3 0003.
 */

public interface MeetingNotify {
    void onKickOut(int res);
    void onWarning(int code);
    void onClassBegin();
    void onClassDismiss();
}
