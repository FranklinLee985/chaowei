package com.eduhdsdk.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.manage.WhiteBoradConfig;
import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.tools.Tools;
import com.talkcloud.room.TKRoomManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fucc on 2018/11/21
 */

public class MediaExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    //记录当前正在播放的视频id
    private long localfileid = -1;
    private PopupWindow pop = null;
    //教室的视频文件
    private List<ShareDoc> classArrayList;
    //公共的视频文件
    private List<ShareDoc> adminArrayList;

    private Map<String, Object> shareMediaAttrs;

    public void setPop(PopupWindow pop) {
        this.pop = pop;
    }

    public void setLocalfileid(Object objfileid) {
        long fileid = -1;
        if (objfileid != null) {
            if (objfileid instanceof String) {
                fileid = Long.valueOf(objfileid.toString());
            } else if (objfileid instanceof Number) {
                fileid = ((Number) objfileid).longValue();
            }
        }
        this.localfileid = fileid;
    }

    public MediaExpandableListAdapter(Activity context) {
        this.context = context;
    }

    public void setShareMediaAttrs(Map<String, Object> shareMediaAttrs) {
        this.shareMediaAttrs = shareMediaAttrs;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        //看后台配置查看几个分组
        return RoomControler.isDocumentClassification() ? 2 : 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupPosition == 0 ? classArrayList.size() : adminArrayList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition == 0 ? context.getResources().getString(R.string.class_file_group):
                context.getResources().getString(R.string.admin_file_group);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupPosition == 0 ? classArrayList.get(childPosition) : adminArrayList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //当没对文件分类去掉groupitem
        if (!RoomControler.isDocumentClassification()) {
            if (convertView == null) {
                convertView = new View(context);
            }
            return convertView;
        }
        FileExpandableListAdapter.GroupViewHolder groupViewHolder = null;
        if (convertView == null) {
            groupViewHolder = new FileExpandableListAdapter.GroupViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.tk_layout_file_type_item, null);
            groupViewHolder.text_file_type = convertView.findViewById(R.id.txt_file_type_name);
            groupViewHolder.img_file_switch = convertView.findViewById(R.id.img_file_item_switch);
            ScreenScale.scaleView(convertView, "CoursePopupWindowUtils");
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (FileExpandableListAdapter.GroupViewHolder) convertView.getTag();
        }
        if (getGroupCount() < 2) {
            groupViewHolder.text_file_type.setText(context.getString(R.string.default_file_group));
        } else {
            if (groupPosition == 0) {
                groupViewHolder.text_file_type.setText(context.getString(R.string.class_file_group));
            } else {
                groupViewHolder.text_file_type.setText(context.getString(R.string.admin_file_group));
            }
        }
        if (isExpanded) {
            groupViewHolder.img_file_switch.setImageResource(R.drawable.tk_popup_file_item_close);
        } else {
            groupViewHolder.img_file_switch.setImageResource(R.drawable.tk_popup_file_item_open);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder holder;
        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.tk_layout_file_list_item, null);
            holder.img_media_type = (ImageView) convertView.findViewById(R.id.img_file_type);
            holder.txt_media_name = (TextView) convertView.findViewById(R.id.txt_file_name);
            holder.img_play = (ImageView) convertView.findViewById(R.id.img_eye);
            holder.img_delete = (ImageView) convertView.findViewById(R.id.img_delete);
            ScreenScale.scaleView(convertView, "MediaListAdapter");
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        final ShareDoc media;
        //先显示教室文件
        if (groupPosition == 0) {
            if (classArrayList.size() <= childPosition) {
                return convertView;
            }
            media = classArrayList.get(childPosition);
        } else {
            if (adminArrayList.size() <= childPosition) {
                return convertView;
            }
            media = adminArrayList.get(childPosition);
        }
        if (media != null) {
            if (groupPosition != 0) {
                holder.img_delete.setVisibility(View.INVISIBLE);
            }

            holder.img_media_type.setImageResource(getMediaIcon(media.getFilename()));
            holder.txt_media_name.setText(media.getFilename());
            holder.img_play.setImageResource(R.drawable.tk_icon_play);
            if (media.getFileid() == localfileid && shareMediaAttrs != null) {
                changePlayBtn((Boolean) shareMediaAttrs.get("pause") == null ?
                        false : (Boolean) shareMediaAttrs.get("pause"), holder);
            }

            if (TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_LASSPATROL) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (pop != null) {
                            pop.dismiss();
                        }

                        if (media.getFileid() == localfileid) {
                            if (shareMediaAttrs == null) {
                                return;
                            }

                            if (RoomSession.isPublishMp3) {
                                boolean isPlay = (Boolean) shareMediaAttrs.get("pause") == null ?
                                        false : (Boolean) shareMediaAttrs.get("pause");
                                TKRoomManager.getInstance().playMedia(isPlay);
                                changePlayBtn(!isPlay, holder);
                                return;
                            }
                        }

                        localfileid = media.getFileid();
                        WhiteBoradConfig.getsInstance().setCurrentMediaDoc(media);
                        TKRoomManager.getInstance().stopShareMedia();
                        String strSwfpath = media.getSwfpath();
                        if (strSwfpath != null && !strSwfpath.isEmpty()) {
                            int pos = strSwfpath.lastIndexOf('.');
                            if (pos != -1) {
                                strSwfpath = String.format("%s-%d%s", strSwfpath.substring(0, pos), 1, strSwfpath.substring(pos));
                                String url = "http://" + WhiteBoradConfig.getsInstance().getFileServierUrl() + ":"
                                        + WhiteBoradConfig.getsInstance().getFileServierPort() + strSwfpath;

                                HashMap<String, Object> attrMap = new HashMap<String, Object>();
                                attrMap.put("filename", media.getFilename());
                                attrMap.put("fileid", media.getFileid());

                                if (Tools.isMp4(media.getFiletype())) {
                                    attrMap.put("pauseWhenOver", RoomControler.isNotCloseVideoPlayer());
                                } else {
                                    attrMap.put("pauseWhenOver", false);
                                }

                                if (RoomSession.isClassBegin) {
                                    TKRoomManager.getInstance().startShareMedia(url, Tools.isMp4(media.getFiletype()),
                                            "__all", attrMap);
                                } else {
                                    TKRoomManager.getInstance().startShareMedia(url, Tools.isMp4(media.getFiletype()),
                                            TKRoomManager.getInstance().getMySelf().peerId, attrMap);
                                }
                                holder.img_play.setImageResource(R.drawable.tk_icon_pause);
                            }
                        }
                    }
                });

                holder.img_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除媒体
                        Tools.showDialog(context, R.string.remind, context.getString(R.string.sure_delect_file_media),
                                new Tools.OnDialogClick() {
                                    @Override
                                    public void dialog_ok(Dialog dialog) {
                                        if (media.getFileid() == localfileid) {
                                            TKRoomManager.getInstance().stopShareMedia();
                                        }
                                        WhiteBoradConfig.getsInstance().delRoomFile(RoomInfo.getInstance().getSerial(),
                                                media.getFileid(), media.isMedia(), RoomSession.isClassBegin);
                                    }
                                });
                    }
                });
            }

            if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                if (groupPosition == 0) {
                    holder.img_delete.setVisibility(View.VISIBLE);
                }
            } else if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT) {
                holder.img_delete.setVisibility(View.GONE);
            }
        }
        //如果身份为巡课，隐藏课件后的切换按钮、删除按钮影藏，媒体库中添加媒体影藏
        if (4==TKRoomManager.getInstance().getMySelf().role){
            holder.img_play.setVisibility(View.GONE);
            holder.img_delete.setVisibility(View.GONE);
        }else {
            holder.img_play.setVisibility(View.VISIBLE);
            holder.img_delete.setVisibility(View.VISIBLE);
        }
        //公有文件不可删除
        if (groupPosition == 1) {
            holder.img_delete.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private int getMediaIcon(String filename) {
        int icon = -1;
        if (filename.toLowerCase().endsWith("mp4") || filename.toLowerCase().endsWith("webm")) {
            icon = R.drawable.tk_icon_mp4;
        } else if (filename.toLowerCase().endsWith("mp3") || filename.toLowerCase().endsWith("wav")
                || filename.toLowerCase().endsWith("ogg")) {
            icon = R.drawable.tk_icon_mp3;
        }
        return icon;
    }

    static class ChildViewHolder {
        ImageView img_media_type;
        ImageView img_play;
        ImageView img_delete;
        TextView txt_media_name;
    }

    public List<ShareDoc> getClassArrayList() {
        return classArrayList;
    }


    public List<ShareDoc> getAdminArrayList() {
        return adminArrayList;
    }


    public void setArrayList(List<ShareDoc> classArrayList, List<ShareDoc> adminArrayList) {
        this.classArrayList = classArrayList;
        this.adminArrayList = adminArrayList;

        //当是纯音频教室时不显示MP4文件
        if (RoomSession.isOnliyAudioRoom) {
            for (int i = this.classArrayList.size() - 1; i >= 0; i--) {
                if ("mp4".equals(this.classArrayList.get(i).getFiletype())) {
                    this.classArrayList.remove(i);
                }
            }
            for (int i = this.adminArrayList.size() - 1; i >= 0; i--) {
                if ("mp4".equals(this.adminArrayList.get(i).getFiletype())) {
                    this.adminArrayList.remove(i);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void notifyDataChangeOnlyAudioRoom() {
        if (RoomControler.isDocumentClassification()) {
            this.classArrayList = WhiteBoradConfig.getsInstance().getClassMediaList();
            this.adminArrayList = WhiteBoradConfig.getsInstance().getAdminmMediaList();
        } else {
            this.classArrayList = WhiteBoradConfig.getsInstance().getMediaList();
        }

        //当是纯音频教室时不显示MP4文件
        if (RoomSession.isOnliyAudioRoom) {
            if (this.classArrayList != null && this.classArrayList.size() != 0) {
                for (int i = this.classArrayList.size() - 1; i >= 0; i--) {
                    if ("mp4".equals(this.classArrayList.get(i).getFiletype())) {
                        this.classArrayList.remove(i);
                    }
                }
            }
            if (this.adminArrayList != null && this.adminArrayList.size() != 0) {
                for (int i = this.adminArrayList.size() - 1; i >= 0; i--) {
                    if ("mp4".equals(this.adminArrayList.get(i).getFiletype())) {
                        this.adminArrayList.remove(i);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private void changePlayBtn(boolean isPlay, ChildViewHolder holder) {
        if (!isPlay) {
            holder.img_play.setImageResource(R.drawable.tk_icon_pause);
        } else {
            holder.img_play.setImageResource(R.drawable.tk_icon_play);
        }
    }
}
