package com.eduhdsdk.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.interfaces.OnMultiClickListener;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.tools.Tools;
import com.eduhdsdk.viewutils.CommonUtil;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/27.
 */

public class MemberListAdapter extends BaseAdapter {

    private ArrayList<RoomUser> userList = new ArrayList<RoomUser>();
    private Context context;

    public void setUserList(ArrayList<RoomUser> userList) {
        this.userList = userList;
    }

    public MemberListAdapter(Context context, ArrayList<RoomUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (userList.size() > 0) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.tk_layout_member_list_item, null);
                holder.txt_user_name = (TextView) convertView.findViewById(R.id.txt_user_name);
                holder.img_hand_up = (ImageView) convertView.findViewById(R.id.img_hand_up);
                holder.img_tool = (ImageView) convertView.findViewById(R.id.img_draw);
                holder.img_up_sd = (ImageView) convertView.findViewById(R.id.img_up_sd);
                holder.img_audio = (ImageView) convertView.findViewById(R.id.img_audio);
                holder.img_video = (ImageView) convertView.findViewById(R.id.img_video);
                holder.im_type = (ImageView) convertView.findViewById(R.id.im_type);
                holder.iv_no_speak = (ImageView) convertView.findViewById(R.id.iv_no_speak);
                holder.iv_out_room = (ImageView) convertView.findViewById(R.id.iv_out_room);
                /* holder.txt_degree = (TextView) convertView.findViewById(R.id.txt_degree);*/
                ScreenScale.scaleView(convertView, "MemberListAdapter");
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (RoomControler.isHideKickPeople()) {
                holder.iv_out_room.setVisibility(View.INVISIBLE);
            } else {
                holder.iv_out_room.setVisibility(View.VISIBLE);
            }
            final RoomUser user = userList.get(position);
            if (user != null) {
                holder.txt_user_name.setText(user.nickName);
                if (user.properties.containsKey("raisehand")) {
                    boolean israisehand = Tools.isTure(user.properties.get("raisehand"));
                    if (israisehand) {
                        holder.img_hand_up.setVisibility(View.VISIBLE);//正在举手
                    } else {
                        holder.img_hand_up.setVisibility(View.INVISIBLE);//同意了，或者拒绝了
                    }
                } else {
                    holder.img_hand_up.setVisibility(View.INVISIBLE);//还没举手
                }

                /*if (user.role == 2) {
                    holder.txt_degree.setText("(" + context.getResources().getString(R.string.student) + ")");
                } else if (user.role == 4) {
                    holder.txt_degree.setText("(" + context.getResources().getString(R.string.lass_patrol) + ")");
                } else if (user.role == 1) {
                    holder.txt_degree.setText("(" + context.getResources().getString(R.string.assistant) + ")");
                }*/

                if (user.properties.containsKey("devicetype")) {
                    String type = (String) user.properties.get("devicetype");
                    int udpstate = 1;
                    switch (type) {
                        case "AndroidPad":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.tk_icon_androidpad_error);
                            } else {
                                holder.im_type.setImageResource(R.drawable.tk_icon_androidpad);
                            }
                            break;
                        case "iPad":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.tk_icon_ipad_error);
                            } else {
                                holder.im_type.setImageResource(R.drawable.tk_icon_ipad);
                            }
                            break;
                        case "AndroidPhone":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.tk_icon_androidphone_error);
                            } else {
                                holder.im_type.setImageResource(R.drawable.tk_icon_androidphone);
                            }
                            break;
                        case "iPhone":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.tk_icon_iphone_error);
                            } else {
                                holder.im_type.setImageResource(R.drawable.tk_icon_iphone);
                            }
                            break;
                        case "WindowClient":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.tk_icon_win_error);
                            } else {
                                holder.im_type.setImageResource(R.drawable.tk_icon_win);
                            }
                            break;
                        case "WindowPC":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.tk_icon_win_explorer_error);
                            } else {
                                holder.im_type.setImageResource(R.drawable.tk_icon_win_explorer);
                            }
                            break;
                        case "MacClient":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.tk_icon_imac_error);
                            } else {
                                holder.im_type.setImageResource(R.drawable.tk_icon_imac);
                            }
                            break;
                        case "MacPC":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.tk_icon_mac_explorer_error);
                            } else {
                                holder.im_type.setImageResource(R.drawable.tk_icon_mac_explorer);
                            }
                            break;
                        case "AndroidTV":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.tk_icon_tv_error);
                            } else {
                                holder.im_type.setImageResource(R.drawable.tk_icon_tv);
                            }
                            break;
                        default:
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.tk_icon_unknow_error);
                            } else {
                                holder.im_type.setImageResource(R.drawable.tk_icon_unknow);
                            }
                            break;
                    }
                }

                if (user.properties.containsKey("disablechat")) {
                    boolean temp = Tools.isTure(user.properties.get("disablechat"));
                    if (temp) {
                        holder.iv_no_speak.setImageResource(R.drawable.tk_button_close_speak);
                    } else {
                        holder.iv_no_speak.setImageResource(R.drawable.tk_button_open_speak);
                    }
                    if (user.role == 1) {
//                        holder.iv_no_speak.setVisibility(View.INVISIBLE);
//                        holder.iv_out_room.setVisibility(View.INVISIBLE);
                        holder.iv_no_speak.setImageResource(R.drawable.tk_speak_disable);
                        holder.iv_out_room.setImageResource(R.drawable.tk_remove_disable);
                        holder.iv_no_speak.setEnabled(false);
                        holder.iv_out_room.setEnabled(false);
                    } else {
                        holder.iv_no_speak.setEnabled(true);
                        holder.iv_out_room.setEnabled(true);
                    }
                }

                //当没上课   用户是助教就没开启画笔
                if (user.role == 1) {
                    holder.img_tool.setEnabled(false);
                    if (RoomSession.isClassBegin) {
                        holder.img_tool.setImageResource(R.drawable.tk_button_open_draw_disable);//可以画图
                    } else {
                        holder.img_tool.setImageResource(R.drawable.tk_button_close_draw_disable);//不可以画图
                    }

                } else {
                    if (RoomSession.isClassBegin) {
                        holder.img_tool.setEnabled(true);
                        if (user.properties.containsKey("candraw")) {
                            boolean candraw = Tools.isTure(user.properties.get("candraw"));
                            if (candraw) {
                                holder.img_tool.setImageResource(R.drawable.tk_button_open_draw);//可以画图
                            } else {
                                holder.img_tool.setImageResource(R.drawable.tk_button_close_draw);//不可以画图
                            }
                        } else {
                            holder.img_tool.setImageResource(R.drawable.tk_button_close_draw_disable);//没给过画图权限
                        }
                    } else {
                        holder.img_tool.setEnabled(false);
                        holder.img_tool.setImageResource(R.drawable.tk_button_close_draw_disable);//不可以画图
                    }
                }

                //当没上课   用户是助教且没开启企业配置
                if ((user.role == 1 && !RoomControler.isShowAssistantAV()) || !RoomSession.isClassBegin) {
                    holder.img_up_sd.setImageResource(R.drawable.tk_button_xiajiangtai_disable);
                    holder.img_up_sd.setEnabled(false);
                } else if (user.getPublishState() > 0) {//只要有流
                    holder.img_up_sd.setEnabled(true);
                    holder.img_up_sd.setImageResource(R.drawable.tk_button_shangjiangtai);
                } else {
                    holder.img_up_sd.setEnabled(true);
                    holder.img_up_sd.setImageResource(R.drawable.tk_button_xiajiangtai);
                }

                if (user.disableaudio || (user.role == 1 && !RoomControler.isShowAssistantAV()) || !RoomSession.isClassBegin) {
                    holder.img_audio.setImageResource(R.drawable.tk_button_close_audio_disable);
                    holder.img_audio.setEnabled(false);
                } else {
                    holder.img_audio.setEnabled(true);
                    if (user.getPublishState() == 1 || user.getPublishState() == 3) {//音频状态改的开启
                        holder.img_audio.setImageResource(R.drawable.tk_button_open_audio);
                    } else {
                        holder.img_audio.setImageResource(R.drawable.tk_button_close_audio);
                    }
                }

                if (user.disablevideo || (user.role == 1 && !RoomControler.isShowAssistantAV())
                        || !RoomSession.isClassBegin || RoomSession.isOnliyAudioRoom) {
                    holder.img_video.setEnabled(false);
                    holder.img_video.setImageResource(R.drawable.tk_button_close_video_disable);
                } else {
                    holder.img_video.setEnabled(true);
                    if (user.getPublishState() == 2 || user.getPublishState() == 3) {//视频状态改的开启
                        holder.img_video.setImageResource(R.drawable.tk_button_open_video);
                    } else {
                        holder.img_video.setImageResource(R.drawable.tk_button_close_video);
                    }
                }

                //巡课没有点击权限
                if (TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_LASSPATROL) {
                    //花名册上课后才能点击并且当用户是助教时候开启助教上下台企业配置时才有点击效果
                    if (RoomSession.isClassBegin && !(user.role == 1 && !RoomControler.isShowAssistantAV())) {
                        holder.img_tool.setOnClickListener(new OnMultiClickListener() {
                            @Override
                            public void onMultiClick(View v) {
                                //授权
                                if (user.properties.containsKey("candraw")) {
                                    boolean candraw = Tools.isTure(user.properties.get("candraw"));
                                    if (candraw) {
                                        //不可以画图
                                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", false);
                                    } else {

                                        RoomSession.getInstance().getUserPublishStateList();
                                        if (RoomSession.publishState.size() >= RoomInfo.getInstance().getMaxVideo() && user.getPublishState() <= 1) {
                                            Toast.makeText(context, R.string.member_overload, Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        if (user.getPublishState() == 0) {
                                            TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                                    "__all", "publishstate", 4);
                                            TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                                                    "raisehand", false);
                                        }
                                        //可以画图
                                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                                                "candraw", true);
                                    }
                                } else {
                                    RoomSession.getInstance().getUserPublishStateList();
                                    if (RoomSession.publishState.size() >= RoomInfo.getInstance().getMaxVideo()
                                            && user.getPublishState() <= 1) {
                                        Toast.makeText(context, R.string.member_overload, Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if (user.getPublishState() == 0) {
                                        TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                                "__all", "publishstate", 4);
                                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                                                "raisehand", false);
                                    }
                                    //可以画图
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                                            "candraw", true);
                                }
                            }
                        });


                        holder.img_up_sd.setOnClickListener(new OnMultiClickListener() {//上下台
                            @Override
                            public void onMultiClick(View v) {

                                if (!RoomSession.isClassBegin) {
                                    return;
                                }

                                RoomSession.getInstance().getUserPublishStateList();
                                if (RoomSession.publishState.size() >= RoomInfo.getInstance().getMaxVideo()
                                        && user.getPublishState() <= 1) {
                                    Toast.makeText(context, R.string.member_overload, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (user.role == 1 && !RoomControler.isShowAssistantAV()) {
                                    return;
                                }

                                if (user.getPublishState() == 0 && user.properties.containsKey("isInBackGround") &&
                                        Tools.isTure(user.properties.get("isInBackGround"))) {
                                    Toast.makeText(context, user.nickName + context.getResources().getString(R.string.select_back_hint),
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }

                                user.properties.put("passivityPublish", true);
                                if (user.getPublishState() >= 1) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                            "__all", "publishstate", 0);
                                    if (user.role == 2) {
                                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                                                "candraw", false);
                                    }
                                } else if (!user.disablevideo && !user.disableaudio) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                            "__all", "publishstate", RoomSession.isOnliyAudioRoom ? 1 : 3);
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                                            "raisehand", false);

                                } else if (!user.disableaudio) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                            "__all", "publishstate", 1);
                                    if (user.role == 2) {
                                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                                                "candraw", false);
                                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                                                "raisehand", false);
                                    }
                                } else if (!user.disablevideo) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                            "__all", "publishstate", 2);
                                } else if (user.disableaudio || user.disablevideo) {
                                    Toast.makeText(context, R.string.device_disable,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                        holder.img_audio.setOnClickListener(new OnMultiClickListener() {
                            @Override
                            public void onMultiClick(View v) {
                                RoomSession.getInstance().getUserPublishStateList();
                                if (RoomSession.publishState.size() >= RoomInfo.getInstance().getMaxVideo() && user.getPublishState() <= 1) {
                                    Toast.makeText(context, R.string.member_overload, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                //打开关闭语音
                                if (user.getPublishState() == 0 || user.getPublishState() == 4) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                            "__all", "publishstate", 1);
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                                            "raisehand", false);
                                }

                                if (user.getPublishState() == 1) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                            "__all", "publishstate", 4);
                                }

                                if (user.getPublishState() == 2) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                            "__all", "publishstate", 3);
                                }

                                if (user.getPublishState() == 3) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                            "__all", "publishstate", 2);
                                }
                            }
                        });

                        holder.img_video.setOnClickListener(new OnMultiClickListener() {
                            @Override
                            public void onMultiClick(View v) {
                                //当全音频时不开启视频
                                if (RoomSession.isOnliyAudioRoom) {
                                    return;
                                }
                                RoomSession.getInstance().getUserPublishStateList();
                                if (RoomSession.publishState.size() >=
                                        RoomInfo.getInstance().getMaxVideo() && user.getPublishState() <= 1) {
                                    Toast.makeText(context, R.string.member_overload, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                //打开关闭摄像头
                                if (user.getPublishState() == 0 || user.getPublishState() == 4) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                            "__all", "publishstate", 2);
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                                            "raisehand", false);
                                }

                                if (user.getPublishState() == 1) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                            "__all", "publishstate", 3);
                                }

                                if (user.getPublishState() == 2) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                            "__all", "publishstate", 4);
                                }

                                if (user.getPublishState() == 3) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                                            "__all", "publishstate", 1);
                                }
                            }
                        });

                    }

                    //禁言和踢人不管上下课都能
                    holder.iv_no_speak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            boolean b = false;
                            try {
                                if (user.properties.containsKey("disablechat")) {
                                    b = (boolean) user.properties.get("disablechat");
                                } else {
                                    b = false;
                                }
                                if (b) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                                            "disablechat", false);
                                } else {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                                            "disablechat", true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    holder.iv_out_room.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //踢出房间
                            Tools.showDialog(context, R.string.remind, context.getString(R.string.sure_get_out_the_people),
                                    new Tools.OnDialogClick() {
                                        @Override
                                        public void dialog_ok(Dialog dialog) {
                                            TKRoomManager.getInstance().evictUser(user.peerId, 1);
                                            dialog.dismiss();
                                        }
                                    });
                        }
                    });
                }
            }

            if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL) { //当前用户为巡课时 隐藏按钮。
                holder.img_up_sd.setVisibility(View.INVISIBLE);
                holder.img_video.setVisibility(View.INVISIBLE);
                holder.img_audio.setVisibility(View.INVISIBLE);
                holder.img_tool.setVisibility(View.INVISIBLE);
                holder.img_hand_up.setVisibility(View.INVISIBLE);
                holder.iv_no_speak.setVisibility(View.INVISIBLE);
                holder.iv_out_room.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public class ViewHolder {
        TextView txt_user_name/*, txt_degree*/;
        ImageView img_hand_up;
        ImageView img_tool;
        ImageView img_up_sd;
        ImageView img_audio;
        ImageView img_video;
        ImageView im_type;
        ImageView iv_no_speak;
        ImageView iv_out_room;
    }
}
