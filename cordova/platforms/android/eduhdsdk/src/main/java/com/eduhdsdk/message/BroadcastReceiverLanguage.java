package com.eduhdsdk.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eduhdsdk.room.RoomClient;
import com.talkcloud.room.TKRoomManager;

/**
 * Created by Administrator on 2017/9/13.
 */

public class BroadcastReceiverLanguage extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
            TKRoomManager.getInstance().leaveRoom();
           /* android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);*/
        }
    }
}