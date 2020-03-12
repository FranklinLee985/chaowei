package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.manage.WhiteBoradConfig;
import com.classroomsdk.tools.ScreenScale;
import com.classroomsdk.utils.SortFileUtil;
import com.eduhdsdk.R;
import com.eduhdsdk.adapter.FileExpandableListAdapter;
import com.eduhdsdk.adapter.MediaExpandableListAdapter;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.tools.Tools;
import com.talkcloud.room.TKRoomManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2018/4/20.
 * 课件库
 */

public class CoursePopupWindowUtils implements View.OnClickListener {

    private Activity activity;
    private PopupWindow popupWindowCourse;
    private LinearLayout ll_course_list, ll_media_list;
    private LinearLayout ll_temp;
    private ExpandableListView lv_course_data;
    private ExpandableListView lv_media_data;

    private FileExpandableListAdapter fileExpandableListAdapter;
    private MediaExpandableListAdapter mediaExpandableListAdapter;

    private PopupWindowClick popup_click;
    private ImageView iv_media_time_sort, iv_media_type_sort, iv_media_name_sort,
            iv_file_time_sort, iv_file_type_sort, iv_file_name_sort;


    private LinearLayout ll_course_library, ll_media;
    private TextView tv_courselibrary_name, tv_media_name;
    private View view_courselibrary_point, view_media_point;

    /**
     * 记录时间，类型，名称的排序状态      0 未选中此类型  1 上三角被选中  2  下三角被选中
     */
    int media_time_status = 0, media_type_status = 0, media_name_status = 0;
    int file_time_status = 0, file_type_status = 0, file_name_status = 0;
    private TextView txt_file_time_sort, txt_media_time_sort;
    private TextView txt_file_type_sort, txt_media_type_sort;
    private TextView txt_file_name_sort, txt_media_name_sort;

    public CoursePopupWindowUtils(Activity activity) {
        this.activity = activity;
        fileExpandableListAdapter = new FileExpandableListAdapter(activity);
        mediaExpandableListAdapter = new MediaExpandableListAdapter(activity);
    }

    public FileExpandableListAdapter getFileExpandableListAdapter() {
        return fileExpandableListAdapter;
    }

    public MediaExpandableListAdapter getMediaExpandableListAdapter() {
        return mediaExpandableListAdapter;
    }

    public void setPopupWindowClick(PopupWindowClick popup_click) {
        this.popup_click = popup_click;
    }

    private View popup_window_view;

    /**
     * 预加载popupwindow的view
     * <p>
     */
    public void initCoursePopupWindow() {

        if (popupWindowCourse != null) {
            return;
        }

        View contentView = LayoutInflater.from(activity).inflate(R.layout.tk_layout_course_popupwindow, null);

        ScreenScale.scaleView(contentView, "CoursePopupWindowUtils");

        ll_course_library = (LinearLayout) contentView.findViewById(R.id.ll_course_library);
        ll_media = (LinearLayout) contentView.findViewById(R.id.ll_media);
        tv_courselibrary_name = (TextView) contentView.findViewById(R.id.tv_courselibrary_name);
        tv_media_name = (TextView) contentView.findViewById(R.id.tv_media_name);
        view_courselibrary_point = contentView.findViewById(R.id.view_courselibrary_point);
        view_media_point = contentView.findViewById(R.id.view_media_point);

        tv_courselibrary_name.setText(R.string.doclist);

        ll_course_library.setOnClickListener(this);
        ll_media.setOnClickListener(this);

        ll_course_list = contentView.findViewById(R.id.ll_course_list);
        ll_media_list = contentView.findViewById(R.id.ll_media_list);

        ll_temp = (LinearLayout) contentView.findViewById(R.id.ll_temp);

        LinearLayout popup_file_title_layout = (LinearLayout) contentView.findViewById(R.id.popup_file_title_layout);

        iv_file_time_sort = (ImageView) popup_file_title_layout.findViewById(R.id.iv_time_sort);
        iv_file_type_sort = (ImageView) popup_file_title_layout.findViewById(R.id.iv_type_sort);
        iv_file_name_sort = (ImageView) popup_file_title_layout.findViewById(R.id.iv_name_sort);


        txt_file_time_sort = popup_file_title_layout.findViewById(R.id.txt_time_sort);
        txt_file_type_sort = popup_file_title_layout.findViewById(R.id.txt_type_sort);
        txt_file_name_sort = popup_file_title_layout.findViewById(R.id.txt_name_sort);

        LinearLayout ll_file_time_sort = (LinearLayout) popup_file_title_layout.findViewById(R.id.ll_time_sort);
        LinearLayout ll_file_type_sort = (LinearLayout) popup_file_title_layout.findViewById(R.id.ll_type_sort);
        LinearLayout ll_file_name_sort = (LinearLayout) popup_file_title_layout.findViewById(R.id.ll_name_sort);

        ll_file_time_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (file_time_status) {
                    case 1:  //时间正序
                        sortTime(false, fileExpandableListAdapter.getClassArrayList(), fileExpandableListAdapter.getAdminArrayList(), 0);
                        iv_file_time_sort.setImageResource(R.drawable.tk_arrange_down);

                        file_time_status = 2;
                        break;
                    case 2:   //时间倒序
                        sortTime(true, fileExpandableListAdapter.getClassArrayList(), fileExpandableListAdapter.getAdminArrayList(), 0);
                        iv_file_time_sort.setImageResource(R.drawable.tk_arrange_up);
                        file_time_status = 1;
                        break;
                }
                file_type_status = 1;
                file_name_status = 1;

                txt_file_time_sort.setTextAppearance(activity, R.style.course_sort_select);
                txt_file_type_sort.setTextColor(Color.WHITE);
                txt_file_name_sort.setTextColor(Color.WHITE);

                iv_file_name_sort.setImageResource(R.drawable.tk_arrange_none);
                iv_file_type_sort.setImageResource(R.drawable.tk_arrange_none);

            }
        });

        ll_file_type_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (file_type_status) {
                    case 1:    //类型正序
                        sortType(true, fileExpandableListAdapter.getClassArrayList(), fileExpandableListAdapter.getAdminArrayList(), 0);
                        iv_file_type_sort.setImageResource(R.drawable.tk_arrange_down);
                        file_type_status = 2;
                        break;
                    case 2:    //类型倒序
                        sortType(false, fileExpandableListAdapter.getClassArrayList(), fileExpandableListAdapter.getAdminArrayList(), 0);
                        iv_file_type_sort.setImageResource(R.drawable.tk_arrange_up);
                        file_type_status = 1;
                        break;
                }
                file_time_status = 1;
                file_name_status = 1;

                txt_file_time_sort.setTextColor(Color.WHITE);
                txt_file_type_sort.setTextAppearance(activity, R.style.course_sort_select);
                txt_file_name_sort.setTextColor(Color.WHITE);

                iv_file_name_sort.setImageResource(R.drawable.tk_arrange_none);
                iv_file_time_sort.setImageResource(R.drawable.tk_arrange_none);
            }
        });

        ll_file_name_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (file_name_status) {
                    case 1:   //名称倒序
                        sortName(true, fileExpandableListAdapter.getClassArrayList(), fileExpandableListAdapter.getAdminArrayList(), 0);
                        iv_file_name_sort.setImageResource(R.drawable.tk_arrange_down);
                        file_name_status = 2;
                        break;
                    case 2:     //名称正序
                        sortName(false, fileExpandableListAdapter.getClassArrayList(), fileExpandableListAdapter.getAdminArrayList(), 0);
                        iv_file_name_sort.setImageResource(R.drawable.tk_arrange_up);
                        file_name_status = 1;
                        break;
                }
                file_time_status = 1;
                file_type_status = 1;

                txt_file_time_sort.setTextColor(Color.WHITE);
                txt_file_type_sort.setTextColor(Color.WHITE);
                txt_file_name_sort.setTextAppearance(activity, R.style.course_sort_select);

                iv_file_time_sort.setImageResource(R.drawable.tk_arrange_none);
                iv_file_type_sort.setImageResource(R.drawable.tk_arrange_none);
            }
        });

        LinearLayout popup_media_title_layout = (LinearLayout) contentView.findViewById(R.id.popup_media_title_layout);

        iv_media_time_sort = (ImageView) popup_media_title_layout.findViewById(R.id.iv_time_sort);
        iv_media_type_sort = (ImageView) popup_media_title_layout.findViewById(R.id.iv_type_sort);
        iv_media_name_sort = (ImageView) popup_media_title_layout.findViewById(R.id.iv_name_sort);
        LinearLayout ll_media_time_sort = (LinearLayout) popup_media_title_layout.findViewById(R.id.ll_time_sort);
        LinearLayout ll_media_type_sort = (LinearLayout) popup_media_title_layout.findViewById(R.id.ll_type_sort);
        LinearLayout ll_media_name_sort = (LinearLayout) popup_media_title_layout.findViewById(R.id.ll_name_sort);
        popup_media_title_layout.findViewById(R.id.ll_temp).setVisibility(View.INVISIBLE);

        txt_media_time_sort = popup_media_title_layout.findViewById(R.id.txt_time_sort);
        txt_media_type_sort = popup_media_title_layout.findViewById(R.id.txt_type_sort);
        txt_media_name_sort = popup_media_title_layout.findViewById(R.id.txt_name_sort);

        ll_media_time_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (media_time_status) {
                    case 1:  //时间正序
                        sortTime(false, mediaExpandableListAdapter.getClassArrayList(), mediaExpandableListAdapter.getAdminArrayList(), 1);
                        iv_media_time_sort.setImageResource(R.drawable.tk_arrange_down);
                        media_time_status = 2;
                        break;
                    case 2:   //时间倒序
                        sortTime(true, mediaExpandableListAdapter.getClassArrayList(), mediaExpandableListAdapter.getAdminArrayList(), 1);
                        iv_media_time_sort.setImageResource(R.drawable.tk_arrange_up);
                        media_time_status = 1;
                        break;
                }
                media_type_status = 1;
                media_name_status = 1;

                iv_media_name_sort.setImageResource(R.drawable.tk_arrange_none);
                iv_media_type_sort.setImageResource(R.drawable.tk_arrange_none);

                txt_media_time_sort.setTextAppearance(activity, R.style.course_sort_select);
                txt_media_type_sort.setTextColor(Color.WHITE);
                txt_media_name_sort.setTextColor(Color.WHITE);
            }
        });

        ll_media_type_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (media_type_status) {
                    case 1:    //类型正序
                        sortType(true, mediaExpandableListAdapter.getClassArrayList(), mediaExpandableListAdapter.getAdminArrayList(), 1);
                        iv_media_type_sort.setImageResource(R.drawable.tk_arrange_down);
                        media_type_status = 2;
                        break;
                    case 2:    //类型倒序
                        sortType(false, mediaExpandableListAdapter.getClassArrayList(), mediaExpandableListAdapter.getAdminArrayList(), 1);
                        iv_media_type_sort.setImageResource(R.drawable.tk_arrange_up);
                        media_type_status = 1;
                        break;
                }
                media_time_status = 1;
                media_name_status = 1;
                iv_media_name_sort.setImageResource(R.drawable.tk_arrange_none);
                iv_media_time_sort.setImageResource(R.drawable.tk_arrange_none);

                txt_media_time_sort.setTextColor(Color.WHITE);
                txt_media_type_sort.setTextAppearance(activity, R.style.course_sort_select);
                txt_media_name_sort.setTextColor(Color.WHITE);
            }
        });

        ll_media_name_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (media_name_status) {
                    case 1:   //名称倒序
                        sortName(true, mediaExpandableListAdapter.getClassArrayList(), mediaExpandableListAdapter.getAdminArrayList(), 1);
                        iv_media_name_sort.setImageResource(R.drawable.tk_arrange_down);
                        media_name_status = 2;
                        break;
                    case 2:     //名称正序
                        sortName(false, mediaExpandableListAdapter.getClassArrayList(), mediaExpandableListAdapter.getAdminArrayList(), 1);
                        iv_media_name_sort.setImageResource(R.drawable.tk_arrange_up);
                        media_name_status = 1;
                        break;
                }
                media_time_status = 1;
                media_type_status = 1;
                iv_media_time_sort.setImageResource(R.drawable.tk_arrange_none);
                iv_media_type_sort.setImageResource(R.drawable.tk_arrange_none);

                txt_media_time_sort.setTextColor(Color.WHITE);
                txt_media_type_sort.setTextColor(Color.WHITE);
                txt_media_name_sort.setTextAppearance(activity, R.style.course_sort_select);
            }
        });

        lv_course_data = contentView.findViewById(R.id.lv_course_data);
        lv_media_data = contentView.findViewById(R.id.lv_media_data);

        contentView.findViewById(R.id.popup_take_photo).setOnClickListener(this);
        contentView.findViewById(R.id.popup_choose_photo).setOnClickListener(this);

        changeBackground(true);
        popup_window_view = contentView;

        if (TKRoomManager.getInstance().getMySelf().role == 4 || TKRoomManager.getInstance().getMySelf().role == 2) {
            ll_temp.setVisibility(View.GONE);
        }

        if (TKRoomManager.getInstance().getMySelf().role == 2) {
            ll_media.setVisibility(View.GONE);
        }
    }


    //判断弹框弹出时，用户的点击是在底部控件的内部还是外部
    boolean isInView = true;

    /**
     * 弹出popupwindow
     *
     * @param view
     * @param cb_view
     * @param width
     * @param height
     */
    public void showCoursePopupWindow(View view, final View cb_view, int width, int height) {

        if (popupWindowCourse == null) {
            initCoursePopupWindow();
        }

        if (popup_window_view == null) {
            return;
        }

        popupWindowCourse = new PopupWindow(width, height);
        popupWindowCourse.setContentView(popup_window_view);

        lv_course_data.setAdapter(fileExpandableListAdapter);
        lv_media_data.setAdapter(mediaExpandableListAdapter);

        if (RoomControler.isDocumentClassification()) {
            fileExpandableListAdapter.setArrayList(WhiteBoradConfig.getsInstance().getClassDocList(),
                    WhiteBoradConfig.getsInstance().getAdminDocList());
            mediaExpandableListAdapter.setArrayList(WhiteBoradConfig.getsInstance().getClassMediaList(),
                    WhiteBoradConfig.getsInstance().getAdminmMediaList());
        } else {
            fileExpandableListAdapter.setArrayList(WhiteBoradConfig.getsInstance().getDocList(), new ArrayList<ShareDoc>());
            mediaExpandableListAdapter.setArrayList(WhiteBoradConfig.getsInstance().getMediaList(), new ArrayList<ShareDoc>());

            lv_course_data.expandGroup(0);
            lv_media_data.expandGroup(0);
        }

        sortTime(false, fileExpandableListAdapter.getClassArrayList(), fileExpandableListAdapter.getAdminArrayList(), 0);
        sortTime(false, mediaExpandableListAdapter.getClassArrayList(), mediaExpandableListAdapter.getAdminArrayList(), 1);

        txt_file_time_sort.setTextAppearance(activity, R.style.course_sort_select);
        iv_file_time_sort.setImageResource(R.drawable.tk_arrange_down);
        iv_file_name_sort.setImageResource(R.drawable.tk_arrange_none);
        iv_file_type_sort.setImageResource(R.drawable.tk_arrange_none);

        file_time_status = 2;

        popupWindowCourse.setBackgroundDrawable(new BitmapDrawable());
        popupWindowCourse.setFocusable(false);
        popupWindowCourse.setOutsideTouchable(true);

        popupWindowCourse.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popup_click != null) {
                    popup_click.close_window();
                }
            }
        });

        popupWindowCourse.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isInView = Tools.isInView(event, cb_view);
                return false;
            }
        });

        fileExpandableListAdapter.setPop(popupWindowCourse);
        mediaExpandableListAdapter.setPop(popupWindowCourse);

        popupWindowCourse.setAnimationStyle(R.style.three_popup_animation);
        popupWindowCourse.showAtLocation(view, Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
    }

    public void dismissPopupWindow() {
        if (popupWindowCourse != null) {
            popupWindowCourse.dismiss();
        }
    }

    /**
     * @param b true：课件库 false：媒体库
     */
    public void changeBackground(boolean b) {
        if (b) {
            ll_course_library.setBackgroundResource(R.drawable.tk_selector_library_default);
            view_courselibrary_point.setBackgroundResource(R.drawable.tk_shape_popou_course_point_select);
            view_courselibrary_point.setVisibility(View.VISIBLE);
            tv_courselibrary_name.setTextAppearance(activity, R.style.three_color_chose_number);

            ll_media.setBackgroundResource(R.drawable.tk_selector_library_select);
            view_media_point.setBackgroundResource(R.drawable.tk_shape_popou_course_point_default);
            view_media_point.setVisibility(View.GONE);
            tv_media_name.setTextAppearance(activity, R.style.course_white);

            ll_course_list.setVisibility(View.VISIBLE);
            ll_media_list.setVisibility(View.GONE);
            //   tv_popup_title.setText(activity.getString(R.string.doclist) + "（" +
            //           WhiteBoradConfig.getsInstance().getDocList().size() + "）");
        } else {
            ll_media.setBackgroundResource(R.drawable.tk_selector_library_default);
            view_media_point.setBackgroundResource(R.drawable.tk_shape_popou_course_point_select);
            view_media_point.setVisibility(View.VISIBLE);
            tv_media_name.setTextAppearance(activity, R.style.three_color_chose_number);

            ll_course_library.setBackgroundResource(R.drawable.tk_selector_library_select);
            view_courselibrary_point.setBackgroundResource(R.drawable.tk_shape_popou_course_point_default);
            view_courselibrary_point.setVisibility(View.GONE);
            tv_courselibrary_name.setTextAppearance(activity, R.style.course_white);


            ll_course_list.setVisibility(View.GONE);
            ll_media_list.setVisibility(View.VISIBLE);

//            tv_popup_title.setText(activity.getString(R.string.medialist) + "（" +
//                    WhiteBoradConfig.getsInstance().getMediaList().size() + "）");

            sortTime(false, mediaExpandableListAdapter.getClassArrayList(), mediaExpandableListAdapter.getAdminArrayList(), 1);
            iv_media_time_sort.setImageResource(R.drawable.tk_arrange_down);
            iv_media_name_sort.setImageResource(R.drawable.tk_arrange_none);
            iv_media_type_sort.setImageResource(R.drawable.tk_arrange_none);
            media_time_status = 2;
            txt_media_time_sort.setTextAppearance(activity, R.style.course_sort_select);
            txt_media_type_sort.setTextColor(Color.WHITE);
            txt_media_name_sort.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.popup_take_photo) {
            if (popup_click != null) {
                popup_click.take_photo();
            }
        } else if (v.getId() == R.id.popup_choose_photo) {
            if (popup_click != null) {
                popup_click.choose_photo();
            }
        } else if (v.getId() == R.id.ll_course_library) {//课件库
            changeBackground(true);

        } else if (v.getId() == R.id.ll_media) {//媒体库
            changeBackground(false);
        }
    }

    /**
     * 定义popupwindow的接口，通过接口和activity进行通信
     */
    public interface PopupWindowClick {
        void close_window();

        void take_photo();

        void choose_photo();
    }

    /**
     * @param isup           排序方式
     * @param classArrayList 教室文件
     * @param adminArrayList 公共文件
     * @param type1          排序对象 0 是课件 1 是媒体
     */
    private void sortTime(Boolean isup, List<ShareDoc> classArrayList, List<ShareDoc> adminArrayList, int type1) {
        //对教室文件排序
        classArrayList = SortFileUtil.getInstance().toSort(SortFileUtil.SORT_TYPE_TIME, isup, classArrayList,true);

        //对公共文件排序
        adminArrayList = SortFileUtil.getInstance().toSort(SortFileUtil.SORT_TYPE_TIME, isup, adminArrayList,false);

        if (type1 == 0) {
            fileExpandableListAdapter.setArrayList(classArrayList, adminArrayList);
        } else {
            mediaExpandableListAdapter.setArrayList(classArrayList, adminArrayList);
        }
    }

    private void sortName(Boolean isup, List<ShareDoc> classArrayList, List<ShareDoc> adminArrayList, int type1) {
         //对教室文件按名字排序
        classArrayList = SortFileUtil.getInstance().toSort(SortFileUtil.SORT_TYPE_NAME, isup, classArrayList,true);

        //对公共文件按名字排序
        adminArrayList = SortFileUtil.getInstance().toSort(SortFileUtil.SORT_TYPE_NAME, isup, adminArrayList,false);

        if (type1 == 0) {
            fileExpandableListAdapter.setArrayList(classArrayList, adminArrayList);
        } else {
            mediaExpandableListAdapter.setArrayList(classArrayList, adminArrayList);
        }
    }

    private void sortType(Boolean isup, List<ShareDoc> classArrayList, List<ShareDoc> adminArrayList, int type1) {

        classArrayList = SortFileUtil.getInstance().toSort(SortFileUtil.SORT_TYPE_TYPE, isup, classArrayList,true);

        adminArrayList = SortFileUtil.getInstance().toSort(SortFileUtil.SORT_TYPE_TYPE, isup, adminArrayList,false);

        if (type1 == 0) {
            fileExpandableListAdapter.setArrayList(classArrayList, adminArrayList);
        } else {
            mediaExpandableListAdapter.setArrayList(classArrayList, adminArrayList);
        }
    }
}
