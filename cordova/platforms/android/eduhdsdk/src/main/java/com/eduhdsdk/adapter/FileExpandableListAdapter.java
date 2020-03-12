package com.eduhdsdk.adapter;

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
import com.classroomsdk.common.Packager;
import com.classroomsdk.manage.WhiteBoradConfig;
import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.tools.Tools;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by fucc on 2018/11/20.
 */

public class FileExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private PopupWindow pop = null;
    //教室文件集合
    private List<ShareDoc> classArrayList;
    //公共文件集合
    private List<ShareDoc> adminArrayList;

    public void setPop(PopupWindow pop) {
        this.pop = pop;
    }

    public FileExpandableListAdapter(Context context) {
        this.context = context;
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
        return groupPosition == 0 ? context.getResources().getString(R.string.class_file_group) :
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
        GroupViewHolder groupViewHolder = null;
        if (convertView == null) {
            groupViewHolder = new GroupViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.tk_layout_file_type_item, null);
            groupViewHolder.text_file_type = convertView.findViewById(R.id.txt_file_type_name);
            groupViewHolder.img_file_switch = convertView.findViewById(R.id.img_file_item_switch);
            ScreenScale.scaleView(convertView, "CoursePopupWindowUtils");
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
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
        ChildViewHolder holder = null;
        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.tk_layout_file_list_item, null);
            holder.img_file_type = (ImageView) convertView.findViewById(R.id.img_file_type);
            holder.txt_file_name = (TextView) convertView.findViewById(R.id.txt_file_name);
            holder.img_eye = (ImageView) convertView.findViewById(R.id.img_eye);
            holder.img_delete = (ImageView) convertView.findViewById(R.id.img_delete);
            ScreenScale.scaleView(convertView, "FilelistAdapter");
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        final ShareDoc fileDoc;
        if (groupPosition == 0) {
            fileDoc = classArrayList.get(childPosition);
        } else {
            fileDoc = adminArrayList.get(childPosition);
        }
        if (fileDoc != null) {

            if (groupPosition != 0) {
                holder.img_delete.setVisibility(View.INVISIBLE);
            }

            if (fileDoc != null) {
                if (fileDoc.getFileid() == 0) {
                    fileDoc.setFilename(context.getString(R.string.share_pad));
                    holder.img_delete.setVisibility(View.INVISIBLE);
                }

               /* int icon = getFileIcon(fileDoc.getFilename());
                holder.img_file_type.setImageResource(icon);*/
                setFileIcom(fileDoc.getFilename(), holder.img_file_type);
                holder.txt_file_name.setText(fileDoc.getFilename());

                if (fileDoc.getFileid() == WhiteBoradConfig.getsInstance().getCurrentFileDoc().getFileid()) {
                    holder.img_eye.setImageResource(R.drawable.tk_openeyes);
                } else {
                    holder.img_eye.setImageResource(R.drawable.tk_closeeyes);
                }
                if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER /*&& fileDoc.getFilecategory() == 0*/) {
                    if (fileDoc.getFileid() == 0) {
                        holder.img_delete.setVisibility(View.GONE);
                    } else {
                        if (groupPosition == 0) {
                            holder.img_delete.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT) {
                    holder.img_delete.setVisibility(View.GONE);
                } else if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL) {
                    holder.img_delete.setVisibility(View.VISIBLE);
                }

                if (TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_LASSPATROL) {
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (fileDoc.getFileid() == WhiteBoradConfig.getsInstance().getCurrentFileDoc().getFileid()) {
                                return;
                            }
                            if (RoomSession.isClassBegin) {
                                JSONObject data = new JSONObject();
                                data = Packager.pageSendData(fileDoc);
                                TKRoomManager.getInstance().pubMsg("ShowPage", "DocumentFilePage_ShowPage", "__all", data.toString(), true, null, null);
                            } else {
                                WhiteBoradConfig.getsInstance().localChangeDoc(fileDoc);
                            }
                            notifyDataSetChanged();
                        }
                    });

                    holder.img_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //删除课件
                            Tools.showDialog(context, R.string.remind, context.getString(R.string.sure_delect_file_media),
                                    new Tools.OnDialogClick() {
                                        @Override
                                        public void dialog_ok(Dialog dialog) {
                                            WhiteBoradConfig.getsInstance().delRoomFile(RoomInfo.getInstance().getSerial(),
                                                    fileDoc.getFileid(), fileDoc.isMedia(), RoomSession.isClassBegin);
                                        }
                                    });
                        }
                    });
                }
            }
            //如果身份为巡课，隐藏课件后的切换按钮、删除按钮影藏，媒体库中添加媒体影藏
            if (4 == TKRoomManager.getInstance().getMySelf().role) {
                holder.img_eye.setVisibility(View.GONE);
                holder.img_delete.setVisibility(View.GONE);
            } else {
                holder.img_eye.setVisibility(View.VISIBLE);
                //未上课，学生不能删除课件
                if (!RoomSession.isClassBegin && Constant.USERROLE_STUDENT == TKRoomManager.getInstance().getMySelf().role) {
                    holder.img_delete.setVisibility(View.INVISIBLE);
                } else {
                    holder.img_delete.setVisibility(fileDoc.getFileid() == 0 ? View.INVISIBLE : View.VISIBLE);
                }
            }
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


    public List<ShareDoc> getClassArrayList() {
        return classArrayList;
    }

    public List<ShareDoc> getAdminArrayList() {
        return adminArrayList;
    }

    public void setArrayList(List<ShareDoc> classArrayList, List<ShareDoc> adminArrayList) {
        this.classArrayList = classArrayList;
        this.adminArrayList = adminArrayList;
        notifyDataSetChanged();
    }

    private void setFileIcom(String filename, ImageView img_file_type) {

        if (filename == null && filename.isEmpty()) {
            img_file_type.setImageResource(R.drawable.tk_icon_whiteboard);
        }
        if (filename.toLowerCase().endsWith(".pptx") || filename.toLowerCase().endsWith(".ppt") || filename.toLowerCase().endsWith(".pps")) {
            img_file_type.setImageResource(R.drawable.tk_icon_ppt);
        } else if (filename.toLowerCase().endsWith(".docx") || filename.toLowerCase().endsWith(".doc")) {
            img_file_type.setImageResource(R.drawable.tk_icon_word);
        } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")
                || filename.toLowerCase().endsWith(".png") || filename.toLowerCase().endsWith(".gif")
                || filename.toLowerCase().endsWith(".bmp")) {
            img_file_type.setImageResource(R.drawable.tk_icon_images);
        } else if (filename.toLowerCase().endsWith(".xls") || filename.toLowerCase().endsWith(".xlsx")
                || filename.toLowerCase().endsWith(".xlt") || filename.toLowerCase().endsWith("xlsm")) {
            img_file_type.setImageResource(R.drawable.tk_icon_excel);
        } else if (filename.toLowerCase().endsWith(".pdf")) {
            img_file_type.setImageResource(R.drawable.tk_icon_pdf);
        } else if (filename.equals(context.getResources().getString(R.string.share_pad))) {
            img_file_type.setImageResource(R.drawable.tk_icon_empty);
        } else if (filename.toLowerCase().endsWith(".txt")) {
            img_file_type.setImageResource(R.drawable.tk_icon_text_pad);
        } else if (filename.toLowerCase().endsWith(".zip")) {
            img_file_type.setImageResource(R.drawable.tk_icon_h5);
        } else {
            img_file_type.setImageResource(R.drawable.tk_icon_weizhi);
        }
    }

    public void changDocData(ShareDoc doc) {
        if (classArrayList == null || adminArrayList == null) {
            return;
        }
        for (int i = 0; i < classArrayList.size(); i++) {
            if (classArrayList.get(i).getFileid() == doc.getFileid()) {
                classArrayList.remove(i);
                classArrayList.add(i, doc);
                break;
            }
        }
        for (int i = 0; i < adminArrayList.size(); i++) {
            if (adminArrayList.get(i).getFileid() == doc.getFileid()) {
                adminArrayList.remove(i);
                adminArrayList.add(i, doc);
                break;
            }
        }
    }

    static class ChildViewHolder {
        ImageView img_file_type;
        TextView txt_file_name;
        ImageView img_eye;
        ImageView img_delete;
    }

    static class GroupViewHolder {
        TextView text_file_type;
        ImageView img_file_switch;
    }
}
