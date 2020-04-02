package com.eduhdsdk.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.classroomsdk.utils.NotificationCenter;
import com.classroomsdk.manage.WBSession;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/11/19/019.
 */

public abstract class TKBaseFragment extends Fragment implements NotificationCenter.NotificationCenterDelegate{

    protected Activity mActivity;

    protected SharedPreferences spkv = null;
    protected SharedPreferences.Editor editor = null;

    protected abstract int setView();

    protected abstract void init(View view);

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (Activity) activity;
        spkv = mActivity.getSharedPreferences("dataphone", MODE_PRIVATE);
        editor = spkv.edit();

        NotificationCenter.getInstance().addObserver(this, WBSession.onRemoteMsg);
        NotificationCenter.getInstance().addObserver(this, WBSession.onRoomLeaved);
        NotificationCenter.getInstance().addObserver(this, WBSession.onPlayBackClearAll);
        NotificationCenter.getInstance().addObserver(this, WBSession.onRoomConnectFaild);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(setView(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        NotificationCenter.getInstance().removeObserver(this);
    }

    @Override
    public void didReceivedNotification(final int id, final Object... args) {
        if (args == null) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (id) {
                    case WBSession.onRemoteMsg:
                        if (args != null) {
                            boolean addRemoteMsg = (boolean) args[0];
                            String idRemoteMsg = (String) args[1];
                            String nameRemoteMsg = (String) args[2];
                            long tsRemoteMsg = (long) args[3];
                            Object dataRemoteMsg = (Object) args[4];
                            boolean inList = (boolean) args[5];
                            String fromIDRemoteMsg = (String) args[6];
                            String associatedMsgIDRemoteMsg = (String) args[7];
                            String associatedUserIDRemoteMsg = (String) args[8];
                            JSONObject jsonObjectRemoteMsg = (JSONObject) args[9];
                            onRemoteMsg(addRemoteMsg, idRemoteMsg, nameRemoteMsg, tsRemoteMsg, dataRemoteMsg, fromIDRemoteMsg,
                                    associatedMsgIDRemoteMsg, associatedUserIDRemoteMsg, jsonObjectRemoteMsg);
                        }
                        break;

                    case WBSession.onRoomLeaved:
                        onRoomLeaved();
                        break;

                    case WBSession.onPlayBackClearAll:
                        roomPlaybackClearAll();
                        break;

                    case WBSession.onRoomConnectFaild:
                        roomDisConnect();
                        break;
                }
            }
        });
    }

    protected abstract void onRemoteMsg(boolean addRemoteMsg, String idRemoteMsg, String nameRemoteMsg,
                                        long tsRemoteMsg, Object dataRemoteMsg, String fromIDRemoteMsg,
                                        String associatedMsgIDRemoteMsg, String associatedUserIDRemoteMsg,
                                        JSONObject jsonObjectRemoteMsg);

    protected abstract void roomDisConnect();

    protected abstract void roomPlaybackClearAll();

    protected abstract void onRoomLeaved();

}
