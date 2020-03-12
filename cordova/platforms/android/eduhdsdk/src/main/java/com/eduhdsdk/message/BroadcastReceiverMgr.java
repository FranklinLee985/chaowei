package com.eduhdsdk.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import com.talkcloud.room.TKRoomManager;

/**
 * Created by Administrator on 2017/9/13.
 */

public class BroadcastReceiverMgr extends BroadcastReceiver {

    private final String TAG = "classroom";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        //呼入电话
        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            doReceivePhone(context, intent);
        }
    }

    /**
     * 处理电话广播.
     *
     * @param context
     * @param intent
     */
    public void doReceivePhone(Context context, Intent intent) {
        String phoneNumber = intent.getStringExtra(
                TelephonyManager.EXTRA_INCOMING_NUMBER);
        TelephonyManager telephony =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int state = telephony.getCallState();
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                TKRoomManager.getInstance().enableSendMyVoice(false);
                TKRoomManager.getInstance().enableOtherAudio(true);
                TKRoomManager.getInstance().setMuteAllStream(true);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                if (TKRoomManager.getInstance().getMySelf() != null ) {
                    if (TKRoomManager.getInstance().getMySelf().publishState == 1 || TKRoomManager.getInstance().getMySelf().publishState == 3) {
                        TKRoomManager.getInstance().enableSendMyVoice(true);
                    }
                }
                TKRoomManager.getInstance().enableOtherAudio(false);
                TKRoomManager.getInstance().setMuteAllStream(false);
                TKRoomManager.getInstance().useLoudSpeaker(true);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                TKRoomManager.getInstance().enableSendMyVoice(false);
                TKRoomManager.getInstance().enableOtherAudio(true);
                TKRoomManager.getInstance().setMuteAllStream(true);
                break;
        }
    }
}