package com.eduhdsdk.ui;


import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.fragment.FaceShareFragment;
import com.classroomsdk.interfaces.FragmentUserVisibleHint;
import com.classroomsdk.interfaces.IWBStateCallBack;
import com.classroomsdk.manage.ProLoadingDoc;
import com.classroomsdk.manage.WBSession;
import com.classroomsdk.manage.WhiteBoradConfig;
import com.classroomsdk.tools.ScreenScale;
import com.classroomsdk.viewUi.DownloadProgressView;
import com.eduhdsdk.BuildConfig;
import com.eduhdsdk.R;
import com.eduhdsdk.adapter.ChatListAdapter;
import com.eduhdsdk.adapter.FileExpandableListAdapter;
import com.eduhdsdk.adapter.MediaExpandableListAdapter;
import com.eduhdsdk.adapter.MemberListAdapter;
import com.eduhdsdk.entity.ChatData;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.interfaces.TranslateCallback;
import com.eduhdsdk.message.SendingSignalling;
import com.eduhdsdk.room.RoomCheck;
import com.eduhdsdk.room.RoomClient;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.room.RoomDeviceSet;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.room.RoomOperation;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.room.RoomVariable;
import com.eduhdsdk.room.SetRoomInfor;
import com.eduhdsdk.toolcase.LayoutPopupWindow;
import com.eduhdsdk.toolcase.ToolCaseMgr;
import com.eduhdsdk.toolcase.ToolsPopupWindow;
import com.eduhdsdk.tools.AnimationUtil;
import com.eduhdsdk.tools.HttpTextView;
import com.eduhdsdk.tools.KeyBoardUtil;
import com.eduhdsdk.tools.MonitorService;
import com.eduhdsdk.tools.PhotoUtils;
import com.eduhdsdk.tools.ResourceSetManage;
import com.eduhdsdk.tools.ShowTrophyUtil;
import com.eduhdsdk.tools.SkinTool;
import com.eduhdsdk.tools.Tools;
import com.eduhdsdk.tools.Translate;
import com.eduhdsdk.ui.holder.OneToOneRootHolder;
import com.eduhdsdk.ui.holder.VideoItem;
import com.eduhdsdk.viewutils.AllActionUtils;
import com.eduhdsdk.viewutils.CommonUtil;
import com.eduhdsdk.viewutils.CoursePopupWindowUtils;
import com.eduhdsdk.viewutils.EyeProtectionUtil;
import com.eduhdsdk.viewutils.FullScreenControlUtil;
import com.eduhdsdk.viewutils.InputWindowPop;
import com.eduhdsdk.viewutils.MemberListPopupWindowUtils;
import com.eduhdsdk.viewutils.MoveFullBoardUtil;
import com.eduhdsdk.viewutils.PlayBackSeekPopupWindow;
import com.eduhdsdk.viewutils.PlaybackControlUtils;
import com.eduhdsdk.viewutils.SendGiftPopUtils;
import com.eduhdsdk.viewutils.UploadPhotoPopupWindowUtils;
import com.eduhdsdk.viewutils.WifiStatusPop;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;
import com.talkcloud.room.TkVideoStatsReport;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.tkwebrtc.EglBase;
import org.tkwebrtc.RendererCommon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import skin.support.annotation.Skinable;

@Skinable
public class OneToOneActivity extends TKBaseActivity implements View.OnClickListener,
        IWBStateCallBack, CompoundButton.OnCheckedChangeListener, InputWindowPop.InputSelectImageListener,
        CoursePopupWindowUtils.PopupWindowClick, MemberListPopupWindowUtils.CloseMemberListWindow, TranslateCallback,
        UploadPhotoPopupWindowUtils.UploadPhotoPopupWindowClick, FragmentUserVisibleHint, AllActionUtils.AllPopupWindowClick,
        LayoutPopupWindow.SwitchLayout {

    private VideoFragment videofragment;
    private MovieFragment movieFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction ft;
    private ScreenFragment screenFragment;
    private Fragment wbFragment;

    private VideoItem teacherItem, stu_in_sd;
    private RelativeLayout.LayoutParams fullscreen_video_param;
    private GifDrawable gifDrawable;
    private Map<String, Object> mediaAttrs;
    private double vol = 0.5;

    private String mediaPeerId;

    private boolean isMediaMute = false;
    private boolean isZoom = false;
    private boolean isBackApp = false;
    private boolean isOpenCamera = false;
    private boolean isFrontCamera = true;
    private boolean isPauseLocalVideo = false;

    private Animation operatingAnim;

    //全体控制
    private AllActionUtils allActionUtils;
    //自定义奖杯
    private SendGiftPopUtils sendGiftPopUtils;

    //数据
    private MemberListAdapter memberListAdapter;
    private FileExpandableListAdapter fileListAdapter;
    private MediaExpandableListAdapter mediaListAdapter;
    private ChatListAdapter chlistAdapter;

    int webandsufwidth;

    /**
     * 判断当前是否是纯音频教室
     */
    boolean isAudioTeaching = false;

    //老师视频框
    private RelativeLayout.LayoutParams ter_par_menu;
    //学生视频框
    private RelativeLayout.LayoutParams stu_par_menu;
    //正常视频框宽高
    private int surfaceVideoHeight;
    private int surfaceVideoWidth;

    private int defaultVideoWidth = 0;

    //视频布局排列规则
    int[] sortRule = {RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.ALIGN_PARENT_RIGHT};
    int[] mRemoveRules = {RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.ALIGN_PARENT_TOP,
            RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.ALIGN_PARENT_BOTTOM};

    //双师布局右上角小视频收起状态
    private boolean mRetractState;

    OneToOneRootHolder mRootHolder;
    private PlaybackControlUtils playbackControlUtils;//回放进度条的显示隐藏工具类

    //画笔的工具栏
    private ToolsView toolsView;
    //翻页
    private PagesView mPagesView;
    private View view;
    public WifiStatusPop wifiStatusPop;
    private Fragment mWb_proto;
    //预加载时是否点击跳过
    private boolean isJumpOver = false;
    public RelativeLayout.LayoutParams tool_bar_param;

    // 全屏大图
    private FullScreenImageView mFullScreenImageView;
    private boolean playBackIsInflate;                  //playback 布局是否inflate
	
		public void ShowNavigationBar(boolean show)
    {
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            if (show)
                newUiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            else
                newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            if (show)
                newUiOptions &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
            else
            newUiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
		
		
		  if (Build.VERSION.SDK_INT >= 18) {
			if (show)
				newUiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE;
			else
				newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
		 }
		
		 
		/*
        if (Build.VERSION.SDK_INT >= 18) {
            if (show)
                newUiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            else
            newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }*/
	/*	
		if (show)
		{	
			newUiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            newUiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            newUiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		}
		else
		{
			newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

		}*/
        this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.tk_activity_one_to_one, null, false);
        setContentView(view);

        mRootHolder = new OneToOneRootHolder(view);
        initData();
        initVideoItem();
        bindListener();
		ShowNavigationBar(false);
		
		View decorView = getWindow().getDecorView();
		decorView.setOnSystemUiVisibilityChangeListener
			(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				// Note that system bars will only be "visible" if none of the
				// LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
				if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
					// TODO: The system bars are visible. Make any desired
					// adjustments to your UI, such as showing the action bar or
					// other navigational controls.
					ShowNavigationBar(false);
				} else {
					// TODO: The system bars are NOT visible. Make any desired
					// adjustments to your UI, such as hiding the action bar or
					// other navigational controls.
				}
			}
		});

    }

    private void crateToolsPage(View view) {
        //工具条添加
        if (toolsView == null) {
            toolsView = new ToolsView(this, mRootHolder, view);
        }
        toolsView.setonClick();

        //底部翻页添加
        if (mPagesView == null) {
            mPagesView = new PagesView(this, mRootHolder, view);
        }
        mPagesView.setonClick();

        //当没有配置企业和不是老师时不显示是备注
        if (!RoomControler.isHasCoursewareNotes() || TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_TEACHER) {
            //翻页工具条备注按钮隐藏
            if (mPagesView != null) {
                mPagesView.setiVRemark(false);
                mPagesView.setVisiblityremark(false);
            }
        }

        if (!RoomSession.isClassBegin || TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL) {
            toolsView.showTools(false);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back || id == R.id.img_play_back_out) {
            showExitDialog();
        } else if (id == R.id.txt_class_begin) {
            if (RoomSession.isClassBegin) {
                showClassDissMissDialog();
            } else {
                if (RoomControler.haveTimeQuitClassroomAfterClass()) {
                    RoomOperation.getInstance().getSystemTime(this);
                } else {
                    RoomOperation.getInstance().startClass();
                }
            }
        } else if (id == R.id.flip_camera) {
            if (TKRoomManager.getInstance().getMySelf().hasVideo) {
                TKRoomManager.getInstance().selectCameraPosition(isFrontCamera);
                isFrontCamera = !isFrontCamera;
            } else {
                Toast.makeText(OneToOneActivity.this, getString(R.string.tips_camera),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.iv_open_input) {  //打开键盘
            boolean disablechat = TKRoomManager.getInstance().getMySelf().properties.containsKey("disablechat");
            if (Tools.isTure(disablechat) && Tools.isTure(TKRoomManager.getInstance().getMySelf().properties.get("disablechat"))) {
                Toast.makeText(OneToOneActivity.this, getString(R.string.the_user_is_forbid_speak), Toast.LENGTH_SHORT).show();
            } else {
                if (isHaiping) {
                    mInputWindowPop.showInputPopupWindow(mRootHolder.ll_wb_container.getWidth() * 9 / 10,
                            mRootHolder.ll_wb_container.getHeight() * 9 / 10, mRootHolder.ll_wb_container,
                            mRootHolder.rl_message, webandsufwidth, true, this);
                } else {
                    mInputWindowPop.showInputPopupWindow(mRootHolder.ll_wb_container.getWidth() * 9 / 10,
                            mRootHolder.ll_wb_container.getHeight() * 9 / 10, mRootHolder.ll_wb_container,
                            mRootHolder.rl_message, webandsufwidth, false, this);
                }
            }
        } else if (id == R.id.cb_choose_shut_chat) {

            SendingSignalling.getInstance().sendBanChatMessage(mRootHolder.cb_choose_shut_chat.isChecked());

        } else if (id == R.id.lin_wifi) {
            if (wifiStatusPop.wifiStatusPop.isShowing()) {
                wifiStatusPop.dismiss();
                return;
            }
            wifiStatusPop.showWifiStatusPop(view);
            if (wifiStatusPop.wifiStatus == 1) {
                mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_you_sanjiao_up);
            } else if (wifiStatusPop.wifiStatus == 2) {
                mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_zhong_sanjiao_up);
            } else {
                mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_cha_sanjiao_up);
            }
        } else if (id == R.id.iv_video_change) {
            sortSurfaceView();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_file_person_media_list) {   //文件列表
            KeyBoardUtil.hideInputMethod(this);
            if (isChecked) {
                mRootHolder.cb_file_person_media_list.setEnabled(false);
                if (mRootHolder.cb_tool_case.isChecked()) {
                    mRootHolder.cb_tool_case.setChecked(false);
                }
                coursePopupWindowUtils.showCoursePopupWindow(mRootHolder.rl_web, mRootHolder.cb_file_person_media_list,
                        mRootHolder.rl_web.getWidth() / 10 * 5, mRootHolder.rl_web.getHeight());
            } else {
                coursePopupWindowUtils.dismissPopupWindow();
            }
        } else if (id == R.id.cb_member_list) {    //  花名册
            KeyBoardUtil.hideInputMethod(this);
            mRootHolder.iv_hand.setVisibility(View.INVISIBLE);
            if (isChecked) {
                mRootHolder.cb_member_list.setEnabled(false);
                memberListPopupWindowUtils.showMemberListPopupWindow(mRootHolder.rl_web, mRootHolder.rl_member_list,
                        mRootHolder.rl_web.getWidth() / 10 * 5, mRootHolder.rl_web.getHeight());
                if (mRootHolder.cb_tool_case.isChecked()) {
                    mRootHolder.cb_tool_case.setChecked(false);
                }
            } else {
                memberListPopupWindowUtils.dismissPopupWindow();
            }

        } else if (id == R.id.cb_tool_case) {  //工具箱
            if (isChecked) {
                mRootHolder.cb_tool_case.setEnabled(false);
                ToolsPopupWindow.getInstance().showPopupWindow(buttonView);
                ToolsPopupWindow.getInstance().getToolsPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mRootHolder.cb_tool_case.setChecked(false);
                        mRootHolder.cb_tool_case.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRootHolder.cb_tool_case.setEnabled(true);
                            }
                        }, 100);
                    }
                });
            } else {
                ToolsPopupWindow.getInstance().dismiss();
            }
            KeyBoardUtil.hideInputMethod(this);

        } else if (id == R.id.cb_control) {   //  全体行为
            KeyBoardUtil.hideInputMethod(this);
            if (isChecked) {
                mRootHolder.cb_control.setEnabled(false);
                if (mRootHolder.cb_tool_case.isChecked()) {
                    mRootHolder.cb_tool_case.setChecked(false);
                }
                allActionUtils.showAllActionView(buttonView, mRootHolder.cb_control, true, true, isAudioTeaching);
            } else {
                allActionUtils.dismissPopupWindow();
            }
        } else if (id == R.id.cb_tool_layout) {  //布局控制
            if (isChecked) {
                mRootHolder.cb_tool_layout.setEnabled(false);
                LayoutPopupWindow.getInstance().showPopupWindow(buttonView);
                LayoutPopupWindow.getInstance().getLayoutPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mRootHolder.cb_tool_layout.setChecked(false);
                        mRootHolder.cb_tool_layout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRootHolder.cb_tool_layout.setEnabled(true);
                            }
                        }, 100);
                    }
                });
            } else {
                LayoutPopupWindow.getInstance().dismiss();
            }
            KeyBoardUtil.hideInputMethod(this);
        } else if (id == R.id.cb_message) {  //  聊天列表
            if (isChecked) {
                clearNoReadChatMessage();
                showChatPopupWindow();
            } else {
                closeChatWindow();
            }
        } else if (id == R.id.cb_choose_photo) {
            if (isChecked) {
                UploadPhotoPopupWindowUtils.getInstance().showPopupWindow(this, mRootHolder.cb_choose_photo, this);
            } else {
                UploadPhotoPopupWindowUtils.getInstance().setDismiss();
            }
        }
    }

    /***
     *   用户音频状态改变
     * @param userIdAudio     用户 id
     * @param statusAudio      音频状态 0 取消发布 1 发布
     */
    public void onUserAudioStatus(String userIdAudio, int statusAudio) {
        if (statusAudio > 0) {
            if (RoomControler.isOnlyShowTeachersAndVideos() && TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_LASSPATROL) {
                if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT) {
                    RoomUser roomUser = TKRoomManager.getInstance().getUser(userIdAudio);
                    if (userIdAudio.equals(TKRoomManager.getInstance().getMySelf().peerId) || roomUser.role == Constant.USERROLE_TEACHER) {
                        doPlayAudio(userIdAudio);
                    } else {
                        TKRoomManager.getInstance().playAudio(userIdAudio);
                    }
                } else {
                    doPlayAudio(userIdAudio);
                }
            } else {
                doPlayAudio(userIdAudio);
            }
        } else {
            doUnPlayAudio(userIdAudio);
        }
        changeUserState(TKRoomManager.getInstance().getUser(userIdAudio));
        memberListAdapter.notifyDataSetChanged();
    }

    private void doUnPlayAudio(String audioUserId) {
        if (TextUtils.isEmpty(audioUserId)) {
            return;
        }
        RoomUser roomUser = TKRoomManager.getInstance().getUser(audioUserId);
        if (roomUser == null) {
            return;
        }
        TKRoomManager.getInstance().unPlayAudio(audioUserId);
        if (roomUser.role == 0) {
            changeVideoItemState(roomUser, teacherItem);
        } else if (roomUser.role == 2) {
            changeVideoItemState(roomUser, stu_in_sd);
        }
    }

    private void doPlayAudio(String audioUserId) {
        if (TextUtils.isEmpty(audioUserId)) {
            return;
        }
        RoomUser roomUser = TKRoomManager.getInstance().getUser(audioUserId);
        if (roomUser == null) {
            return;
        }
        TKRoomManager.getInstance().playAudio(roomUser.peerId);
        if (roomUser.role == 0) {
            changeVideoItemState(roomUser, teacherItem);
        } else if (roomUser.role == 2) {
            changeVideoItemState(roomUser, stu_in_sd);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        switch (requestCode) {
            case PhotoUtils.PHOTO_REQUEST_CAREMA:
                if (isPauseLocalVideo) {
                    TKRoomManager.getInstance().resumeLocalCamera();
                    isPauseLocalVideo = !isPauseLocalVideo;
                }
                isOpenCamera = false;
                if (resultCode == RESULT_OK) {
                    //拍照上传图片
                    PhotoUtils.uploadCaremaImage(uri, this, requestCode, data);
                }
                break;
            case PhotoUtils.ALBUM_IMAGE:
                if (resultCode == RESULT_OK) {
                    //选择上传图片
                    PhotoUtils.uploadAlbumImage(this, data, mSelectImageType);
                }
                break;
            case EyeProtectionUtil.requestCodes:
                if (resultCode == RESULT_OK) {
                    mRootHolder.eye_protection.setChecked(true);
                }
                break;
        }
    }

    @Override
    public void onRoomDocChange(boolean isdel, boolean ismedia, ShareDoc doc) {
        if (RoomControler.isDocumentClassification()) {
            WhiteBoradConfig.getsInstance().getClassDocList();
            WhiteBoradConfig.getsInstance().getAdminDocList();
            WhiteBoradConfig.getsInstance().getClassMediaList();
            WhiteBoradConfig.getsInstance().getAdminmMediaList();
        } else {
            WhiteBoradConfig.getsInstance().getDocList();
            WhiteBoradConfig.getsInstance().getMediaList();
        }
        if (!ismedia) {
            fileListAdapter.changDocData(doc);
        }
        fileListAdapter.notifyDataSetChanged();
        mediaListAdapter.notifyDataSetChanged();
    }

    /***
     *     白板放大缩小
     * @param isZoom  true放大  false缩小
     */
    @Override
    public void onWhiteBoradZoom(final boolean isZoom) {
        this.isZoom = isZoom;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isZoom) {
                    setWhiteBoradEnlarge(isZoom);
                } else {
                    setWhiteBoradNarrow(isZoom);
                }
                if (RoomSession.isClassBegin && TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER
                        && RoomControler.isFullScreenVideo()) {
                    //发送全屏信令
                    SendingSignalling.getInstance().sendFullScreenMsg(isZoom);
                }
            }
        });
    }

    /***
     *  JS调用我们的方法（改变按钮的状态）
     */
    int dolayoutsum4 = 0;
    int dolayoutsum16 = 0;
    int dolayoutsumall = 0;
    int scale = 0;
    int board_wid_ratio = 4;
    int board_hid_ratio = 3;
    double irregular = 0.0;

    @Override
    public void onWhiteBoradAction(final String stateJson) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(stateJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonObject != null) {
                        if (jsonObject.has("scale")) {
                            scale = jsonObject.optInt("scale");
                            if (scale == 2) {
                                irregular = jsonObject.optDouble("irregular");
                                // GeneralFile(PDF / 图片) 的 scale 都是2，需要重新计算白板大小
                                dolayoutsumall = 0;
                            }
                        }

                        //如果是第一次进入遇到 4:3  先执行一次  下次遇到不执行，并恢复16:9 下次可进  ，同理相反
                        if (scale == 0) {
                            board_wid_ratio = 4;
                            board_hid_ratio = 3;
                            if (dolayoutsum4 == 0) {
                                dolayoutsum4++; //1
                            }
                        } else {
                            board_wid_ratio = 16;
                            board_hid_ratio = 9;
                            if (dolayoutsum16 == 0) {
                                dolayoutsum16++;
                            }
                        }
                        if (dolayoutsum4 == 1) {
                            dolayoutsum4++;
                            doLayout();
                            dolayoutsum16 = 0;
                        }
                        if (dolayoutsum16 == 1) {
                            dolayoutsum16++;
                            doLayout();
                            dolayoutsum4 = 0;
                        }

                        if (scale == 0) {
                            if (dolayoutsum4 == 0) {
                                dolayoutsum4++; //1
                            }
                        } else if (scale == 1) {
                            if (dolayoutsum16 == 0) {
                                dolayoutsum16++;
                            }
                        } else if (scale == 2) {
                            if (dolayoutsumall == 0) {
                                dolayoutsumall++;
                            }
                        }

                        if (dolayoutsum4 == 1) {
                            dolayoutsum4++;  //2
                            doLayout();
                            dolayoutsum16 = 0;
                            dolayoutsumall = 0;
                        }
                        if (dolayoutsum16 == 1) {
                            dolayoutsum16++;
                            doLayout();
                            dolayoutsum4 = 0;
                            dolayoutsumall = 0;
                        }

                        if (dolayoutsumall == 1) {
                            dolayoutsumall++;
                            doLayout();
                            dolayoutsum4 = 0;
                            dolayoutsum16 = 0;
                        }

                        if (mPagesView != null) {
                            mPagesView.setAction(stateJson);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //设置View的大小
        setViewSize();
		ShowNavigationBar(false);
    }

    /**
     * 文件下载解压进度回调
     *
     * @param index 进度
     * @param type  1 下载 2 解压
     */
    @Override
    public void onDownloadProgress(final int index, final int type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRootHolder != null) {
                    if (type == 1) {
                        if (mRootHolder.fl_downloadProgress.getVisibility() == View.GONE && !isJumpOver) {
                            mRootHolder.fl_downloadProgress.setVisibility(View.VISIBLE);
                            setTitleBarContentVisibility(View.GONE);
                        }
                        mRootHolder.fl_downloadProgress.setProgress(getString(R.string.docDownload), index);
                    }
                    if (type == 2) {
                        if (index >= 0 && index <= 99) {
                            if (mRootHolder.fl_downloadProgress.getVisibility() == View.GONE && !isJumpOver) {
                                mRootHolder.fl_downloadProgress.setVisibility(View.VISIBLE);
                                setTitleBarContentVisibility(View.GONE);
                            }
                        }

                        mRootHolder.fl_downloadProgress.setProgress(getString(R.string.doc_unzipping), index);
                        if (index == 100) {
                            mRootHolder.fl_downloadProgress.setVisibility(View.GONE);
                            setTitleBarContentVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void hideDownload(final boolean ishide) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRootHolder.fl_downloadProgress != null) {
                    if (ishide && !isJumpOver) {
                        mRootHolder.fl_downloadProgress.setVisibility(View.VISIBLE);
                        setTitleBarContentVisibility(View.GONE);
                    } else {
                        mRootHolder.fl_downloadProgress.setVisibility(View.GONE);
                        setTitleBarContentVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public void closeChatWindow() {
        //聊天列表隐藏动画
        AnimationUtil.getInstance(this).hideChatLists(mRootHolder.lv_chat_list, this);
        //输入框按钮隐藏动画
        AnimationUtil.getInstance(this).hideViewAniamtion(mRootHolder.iv_open_input, this);
        //全体禁言按钮隐藏动画
        AnimationUtil.getInstance(this).hideViewAniamtion(mRootHolder.cb_choose_shut_chat, this);
    }

    @Override
    public void close_window() {
        mRootHolder.cb_file_person_media_list.setChecked(false);
        mRootHolder.cb_file_person_media_list.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRootHolder.cb_file_person_media_list.setEnabled(true);
            }
        }, 100);
    }

    @Override
    public void take_photo() {
        cameraClickListener(TKBaseActivity.KEJIAN_SELECT_IMAGE);
    }

    @Override
    public void choose_photo() {
        photoClickListener(TKBaseActivity.KEJIAN_SELECT_IMAGE);
    }

    @Override
    public void close_member_list_window() {
        mRootHolder.cb_member_list.setChecked(false);
        mRootHolder.cb_member_list.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRootHolder.cb_member_list.setEnabled(true);
            }
        }, 100);
    }

    @Override
    protected void onStart() {
        if (TKRoomManager.getInstance().getMySelf() != null && RoomSession.isInRoom) {
            if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT || TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                TKRoomManager.getInstance().setInBackGround(false);
                if (isBackApp) {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "isInBackGround", false);
                }
                mRootHolder.flipCamera.setVisibility(View.VISIBLE);
            } else {
                mRootHolder.flipCamera.setVisibility(View.GONE);
            }
        }
        if (isBackApp) {
            stopService(new Intent(this, MonitorService.class));
        }
        isBackApp = false;
        isOpenCamera = false;
        super.onStart();

        if (!RoomSession.isInRoom) {
            mRootHolder.re_loading.setVisibility(View.VISIBLE);
            mRootHolder.tv_load.setText(R.string.joining_classroom_home);
        } else {
            mRootHolder.re_loading.setVisibility(View.GONE);
        }
        if (mRootHolder.eye_protection.isChecked()) {
            EyeProtectionUtil.openSuspensionWindow(OneToOneActivity.this, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (RoomControler.haveTimeQuitClassroomAfterClass() && !RoomSession.isClassBegin) {
            if (RoomOperation.timerAfterLeaved != null) {
                RoomOperation.timerAfterLeaved.cancel();
                RoomOperation.timerAfterLeaved = null;
            }
            RoomOperation.getInstance().getSystemNowTime(this);
        }
		ShowNavigationBar(false);
    }

    @Override
    protected void onStop() {
        if (!isFinishing()) {
            TKRoomManager.getInstance().setInBackGround(true);
            if (!isBackApp) {
                Intent mMonitorService = new Intent(this, MonitorService.class);
                mMonitorService.putExtra(MonitorService.KEY, OneToOneActivity.class.getName());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(mMonitorService);
                } else {
                    startService(mMonitorService);
                }
                isBackApp = true;
            }
            // publishState != 0 时再发送isInBackGround信令，修复 学生下台后，按home键，老师端依然显示学生按home键了
            if (TKRoomManager.getInstance().getMySelf() != null && TKRoomManager.getInstance().getMySelf().publishState != 0) {
                if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT || TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "isInBackGround", true);
                }
            }
        }
        if (mRootHolder.eye_protection.isChecked()) {
            EyeProtectionUtil.openSuspensionWindow(OneToOneActivity.this, false);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mRootHolder.fullscreen_sf_video != null) {
            mRootHolder.fullscreen_sf_video.release();
            mRootHolder.fullscreen_sf_video = null;
        }

        if (teacherItem != null && teacherItem.sf_video != null) {
            teacherItem.sf_video.release();
            teacherItem.sf_video = null;
        }
        if (stu_in_sd != null && stu_in_sd.sf_video != null) {
            stu_in_sd.sf_video.release();
            stu_in_sd.sf_video = null;
        }

        if (mRootHolder.lin_menu != null) {
            mRootHolder.lin_menu.removeAllViews();
        }
        RoomClient.getInstance().onResetVideo();
        RoomSession.getInstance().resetRoomSession();
    }

    private void initData() {
        if (mRootHolder.loadingImageView != null) {
            SkinTool.getmInstance().setLoadingSkin(this, mRootHolder.loadingImageView);
        }

        try {
            mRootHolder.fullscreen_sf_video.init(EglBase.create().getEglBaseContext(), null);
        } catch (RuntimeException e) {
            if (BuildConfig.DEBUG)
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        mRootHolder.fullscreen_sf_video.setZOrderOnTop(true);
        mRootHolder.fullscreen_sf_video.setZOrderMediaOverlay(true);

        mRootHolder.sek_mp3.setPadding((int) (10 * ScreenScale.getWidthScale()), 0,
                (int) (10 * ScreenScale.getWidthScale()), 0);
        mRootHolder.sek_voice_mp3.setProgress((int) (vol * 100));
        mRootHolder.sek_voice_mp3.setPadding((int) (10 * ScreenScale.getWidthScale()), 0,
                (int) (10 * ScreenScale.getWidthScale()), 0);

        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.tk_play_mp3_gif);
            mRootHolder.img_disk.setImageDrawable(gifDrawable);
            gifDrawable.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //课件库
        coursePopupWindowUtils = new CoursePopupWindowUtils(this);
        coursePopupWindowUtils.setPopupWindowClick(this);

        //花名册
        memberListPopupWindowUtils = new MemberListPopupWindowUtils(this, RoomSession.memberList);
        memberListPopupWindowUtils.setPopupWindowClick(this);

        //聊天键盘PopupWindow
        mInputWindowPop = new InputWindowPop(this, RoomSession.chatList);

        //全体控制
        allActionUtils = new AllActionUtils(this, this);

        //发送奖杯
        sendGiftPopUtils = new SendGiftPopUtils(this);
        sendGiftPopUtils.preLoadImage();

        operatingAnim = AnimationUtils.loadAnimation(OneToOneActivity.this, R.anim.tk_disk_aim);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        fileListAdapter = coursePopupWindowUtils.getFileExpandableListAdapter();
        mediaListAdapter = coursePopupWindowUtils.getMediaExpandableListAdapter();
        memberListAdapter = memberListPopupWindowUtils.getMemberListAdapter();

        chlistAdapter = new ChatListAdapter(RoomSession.chatList, this);
        mRootHolder.lv_chat_list.setAdapter(chlistAdapter);

        chlistAdapter.setOnChatListImageClickListener(new ChatListAdapter.OnChatListImageClickListener() {
            @Override
            public void onChatListImageClick(String image) {
                if (mFullScreenImageView == null) {
                    mFullScreenImageView = new FullScreenImageView(OneToOneActivity.this, (RelativeLayout) view);
                }
                mFullScreenImageView.show(image);
            }
        });

        ToolsPopupWindow.getInstance().setActivityAndCall(this);
        LayoutPopupWindow.getInstance().setActivityAndCall(this, 0);
        LayoutPopupWindow.getInstance().setSwitchLayout(this);

        wifiStatusPop = new WifiStatusPop(this);
        wifiStatusPop.wifiStatusPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (wifiStatusPop.wifiStatus == 1) {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_you_sanjiao_down);
                } else if (wifiStatusPop.wifiStatus == 2) {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_zhong_sanjiao_down);
                } else {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_cha_sanjiao_down);
                }
            }
        });

        wbFragment = WhiteBoradConfig.getsInstance().CreateWhiteBorad(this);
        WhiteBoradConfig.getsInstance().setPlayBack(RoomSession.isPlayBack);
        WhiteBoradConfig.getsInstance().isLiuHaiping(isHaiping);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (!wbFragment.isAdded()) {
            ft.add(R.id.wb_container, wbFragment);
            ft.commit();
        }

        mWb_proto = WhiteBoradConfig.getsInstance().CreateWhiteBoardView();

        FragmentTransaction ft1 = fragmentManager.beginTransaction();
        if (!mWb_proto.isAdded()) {
            ft1.add(R.id.wb_protogenesis, mWb_proto);
            ft1.commit();
        }

        if (mWb_proto instanceof FaceShareFragment) {
            ((FaceShareFragment) mWb_proto).setFragmentUserVisibleHint(this);
        }

        ToolCaseMgr.getInstance().setActivity(this, mRootHolder.rl_web);
        ToolCaseMgr.getInstance().setLiuHaiping(isHaiping);
        //初始化工具箱
        ToolCaseMgr.getInstance().setHasWhiteBoradAction();
    }

    /**
     * 截图显示隐藏
     *
     * @param visibility View.VISIBLE View.GONE
     */
    @Override
    public void setWbProtoVisibility(int visibility) {
        if (mWb_proto != null) {
            ((FaceShareFragment) mWb_proto).setVisibility(visibility);
        }
    }

    private void bindListener() {

        Translate.getInstance().setCallback(this);

        mRootHolder.cb_file_person_media_list.setOnCheckedChangeListener(this);
        mRootHolder.cb_tool_case.setOnCheckedChangeListener(this);
        mRootHolder.cb_tool_layout.setOnCheckedChangeListener(this);
        mRootHolder.cb_message.setOnCheckedChangeListener(this);
        mRootHolder.cb_member_list.setOnCheckedChangeListener(this);
        mRootHolder.cb_choose_photo.setOnCheckedChangeListener(this);
        mRootHolder.cb_choose_photo.setOnClickListener(this);
        mRootHolder.cb_control.setOnCheckedChangeListener(this);
        mRootHolder.flipCamera.setOnClickListener(this);
        mRootHolder.txt_class_begin.setOnClickListener(this);
        mRootHolder.txt_hand_up.setOnClickListener(this);
        mRootHolder.img_back.setOnClickListener(this);
        mRootHolder.iv_open_input.setOnClickListener(this);
        mRootHolder.cb_choose_shut_chat.setOnClickListener(this);
        mRootHolder.lin_wifi.setOnClickListener(this);
        mRootHolder.iv_video_change.setOnClickListener(this);
        //预加载跳过
        mRootHolder.fl_downloadProgress.setJumpOverClieck(new DownloadProgressView.JumpOverClieck() {
            @Override
            public void jumpover() {
                isJumpOver = true;
                ProLoadingDoc.getInstance().postTksdk();
                mRootHolder.fl_downloadProgress.setVisibility(View.GONE);
                setTitleBarContentVisibility(View.VISIBLE);
            }
        });

        if (mRootHolder != null) {
            MoveFullBoardUtil.getInstance().SetViewOnTouchListener(mRootHolder.rel_fullscreen_videoitem);
        }

        //关闭mp3
        mRootHolder.img_close_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TKRoomManager.getInstance().stopShareMedia();
                mRootHolder.lin_audio_seek.setVisibility(View.INVISIBLE);
                mRootHolder.fl_play_disk.setVisibility(View.INVISIBLE);
            }
        });

        //播放暂停mp3
        mRootHolder.img_play_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaAttrs != null) {
                    if (RoomSession.isPublishMp3) {
                        TKRoomManager.getInstance().playMedia((Boolean) mediaAttrs.get("pause") == null ?
                                false : (Boolean) mediaAttrs.get("pause"));
                    } else {
                        ShareDoc media = WhiteBoradConfig.getsInstance().getCurrentMediaDoc();
                        WhiteBoradConfig.getsInstance().setCurrentMediaDoc(media);
                        String strSwfpath = media.getSwfpath();
                        int pos = strSwfpath.lastIndexOf('.');
                        strSwfpath = String.format("%s-%d%s", strSwfpath.substring(0, pos), 1, strSwfpath.substring(pos));
                        String url = "http://" + WhiteBoradConfig.getsInstance().getFileServierUrl() + ":" +
                                WhiteBoradConfig.getsInstance().getFileServierPort() + strSwfpath;
                        HashMap<String, Object> attrMap = new HashMap<String, Object>();
                        attrMap.put("filename", media.getFilename());
                        attrMap.put("fileid", media.getFileid());

                        if (RoomSession.isClassBegin) {
                            TKRoomManager.getInstance().startShareMedia(url, false, "__all", attrMap);
                        } else {
                            TKRoomManager.getInstance().startShareMedia(url, false,
                                    TKRoomManager.getInstance().getMySelf().peerId, attrMap);
                        }
                    }
                }
            }
        });

        //进度改变监听
        mRootHolder.sek_mp3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int pro = 0;
            boolean isfromUser = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    pro = progress;
                    isfromUser = fromUser;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double currenttime = 0;
                if (isfromUser && mediaAttrs != null) {
                    currenttime = ((double) pro / (double) seekBar.getMax()) * (int) mediaAttrs.get("duration");
                    TKRoomManager.getInstance().seekMedia((long) currenttime);
                }
            }
        });

        //声音喇叭点击监听
        mRootHolder.img_voice_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMediaMute) {
                    TKRoomManager.getInstance().setRemoteAudioVolume(vol, mediaPeerId, 2);
                    mRootHolder.img_voice_mp3.setImageResource(R.drawable.tk_icon_voice);
                    mRootHolder.sek_voice_mp3.setProgress((int) (vol * 100));
                } else {
                    TKRoomManager.getInstance().setRemoteAudioVolume(0, mediaPeerId, 2);
                    mRootHolder.img_voice_mp3.setImageResource(R.drawable.tk_icon_no_voice);
                    mRootHolder.sek_voice_mp3.setProgress(0);
                }
                isMediaMute = !isMediaMute;
            }
        });

        mRootHolder.sek_voice_mp3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float vol = (float) progress / (float) seekBar.getMax();
                if (vol > 0) {
                    mRootHolder.img_voice_mp3.setImageResource(R.drawable.tk_icon_voice);
                } else {
                    mRootHolder.img_voice_mp3.setImageResource(R.drawable.tk_icon_no_voice);
                }
                TKRoomManager.getInstance().setRemoteAudioVolume(vol, mediaPeerId, 2);
                if (fromUser) {
                    OneToOneActivity.this.vol = vol;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mRootHolder.eye_protection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //判断是否有悬浮窗的权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//版本6.0以上的设备需要动态申请该权限
                    if (!Settings.canDrawOverlays(OneToOneActivity.this)) {//没有该权限
                        mRootHolder.eye_protection.setChecked(false);
                        EyeProtectionUtil.showDialog(OneToOneActivity.this);
                    } else {//有该权限
                        EyeProtectionUtil.openSuspensionWindow(OneToOneActivity.this, isChecked);
                    }
                } else {
                    EyeProtectionUtil.openSuspensionWindow(OneToOneActivity.this, isChecked);
                }
            }
        });
    }

    /**
     * 设置标题栏处返回按键外的其他控件显示隐藏
     *
     * @param visible View.Visible View.Gone
     */
    private void setTitleBarContentVisibility(int visible) {
        CommonUtil.setTimeVisibility(mRootHolder, visible);
        mRootHolder.lin_wifi.setVisibility(visible);
        mRootHolder.re_top_right.setVisibility(visible);
    }

    private void doLayout() {
        switch (mLayoutState) {
            case LayoutPopupWindow.LAYOUT_NORMAL:
                //常规布局
                doLayoutNormal();
                break;
            case LayoutPopupWindow.LAYOUT_DOUBLE:
                //双师布局
                doLayoutDoubleTeacher();
                break;
            case LayoutPopupWindow.LAYOUT_VIDEO:
                //纯视频布局
                doLayoutFullScreen();
                break;
        }
        if (studentPopupWindow != null && studentPopupWindow.isShowing()) {
            studentPopupWindow.dismiss();
        }
    }

    /**
     * 常规布局
     */
    private void doLayoutNormal() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {

                    setDefaultVideoSize();
                    //右边老师和学生视频框大小
                    RelativeLayout.LayoutParams menu_param = (RelativeLayout.LayoutParams) mRootHolder.lin_menu.getLayoutParams();
                    menu_param.width = surfaceVideoWidth;
                    menu_param.height = surfaceVideoHeight * 2;
                    menu_param.leftMargin = 0;
                    mRootHolder.lin_menu.setLayoutParams(menu_param);

                    //老师视频框
                    ter_par_menu = (RelativeLayout.LayoutParams) teacherItem.parent.getLayoutParams();
                    ter_par_menu.height = surfaceVideoHeight;
                    ter_par_menu.width = surfaceVideoWidth;
                    removeRules(ter_par_menu);
                    ter_par_menu.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    teacherItem.parent.setLayoutParams(ter_par_menu);
                    teacherItem.ll_retract.setVisibility(View.GONE);

                    //学生视频框
                    stu_par_menu = (RelativeLayout.LayoutParams) stu_in_sd.parent.getLayoutParams();
                    stu_par_menu.height = surfaceVideoHeight;
                    stu_par_menu.width = surfaceVideoWidth;
                    removeRules(stu_par_menu);
                    stu_par_menu.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    stu_in_sd.parent.setLayoutParams(stu_par_menu);
                    stu_in_sd.ll_retract.setVisibility(View.GONE);

                    mRootHolder.rel_video_change.setVisibility(View.GONE);

                    //设置白板大小
                    setWhiteBoradSize(mScreenValueWidth - surfaceVideoWidth, surfaceVideoHeight * 2);

                    //视频框内的View设置属性
                    setLayoutParamsForGeneral(teacherItem, surfaceVideoWidth, surfaceVideoHeight);
                    setLayoutParamsForGeneral(stu_in_sd, surfaceVideoWidth, surfaceVideoHeight);
                    setLayoutParamsForStudent(stu_in_sd, surfaceVideoWidth, surfaceVideoHeight);
                }
            }
        });
    }

    private void setDefaultVideoSize() {
        // 设高为 白板高为b   求  (b * 4 / 3) + ( b / 2 * 4 / 3 ) = 屏幕宽  4:3
        //   b = 屏幕宽 / 2
        // 设高为 白板高为b   求  (b * 16 / 9) + ( b / 2 * 4 / 3 ) = 屏幕宽  16:9
        //  22 * b / 9 = 屏幕宽
        //  b = 屏幕宽 / 2.44444444
        int webhight = 0;
        if (isHaiping) {
            if (board_wid_ratio == 4 && board_hid_ratio == 3) {
                webhight = (int) (mScreenValueWidth / 2);
            }
            if (board_wid_ratio == 16 && board_hid_ratio == 9) {
                webhight = (int) (mScreenValueWidth * 1000 / 2444);
            }
        } else {
            if (board_wid_ratio == 4 && board_hid_ratio == 3) {
                webhight = (int) (wid / 2);
            }
            if (board_wid_ratio == 16 && board_hid_ratio == 9) {
                webhight = (int) (wid * 1000 / 2444);
            }
        }
        //如果 屏幕高 - titile  > 白板高（webhight）
        if (webhight > (hid - toolBarHeight)) {
            webhight = (int) (hid - toolBarHeight - 20);
        }
        int webWidth = (int) (webhight * board_wid_ratio / board_hid_ratio);
        surfaceVideoHeight = webhight / 2;
        surfaceVideoWidth = surfaceVideoHeight * 4 / 3;
        defaultVideoWidth = surfaceVideoWidth;
    }


    /**
     * 双师布局
     */
    private void doLayoutDoubleTeacher() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {


                    // 设高为 白板高为b   求  (b * 4 / 3) + ( b / 2 * 4 / 3 ) = 屏幕宽  4:3
                    //   b = 屏幕宽 / 2
                    // 设高为 白板高为b   求  (b * 16 / 9) + ( b / 2 * 4 / 3 ) = 屏幕宽  16:9
                    //  22 * b / 9 = 屏幕宽
                    //  b = 屏幕宽 / 2.44444444
                    mRootHolder.rel_video_change.setVisibility(View.GONE);

                    surfaceVideoWidth = mScreenValueWidth / 2;
                    surfaceVideoHeight = surfaceVideoWidth * 3 / 4;

                    int webWidth = mScreenValueWidth - surfaceVideoWidth;
                    int webHight = surfaceVideoHeight;

                    //右边老师和学生视频框大小
                    RelativeLayout.LayoutParams menu_param = (RelativeLayout.LayoutParams) mRootHolder.lin_menu.getLayoutParams();
                    menu_param.width = surfaceVideoWidth;
                    menu_param.height = surfaceVideoHeight;
                    menu_param.leftMargin = 0;
                    mRootHolder.lin_menu.setLayoutParams(menu_param);

                    if (teacherItem.ll_retract.getVisibility() == View.VISIBLE) {  //老师视频显示在右上角
                        //学生视频框
                        stu_par_menu = (RelativeLayout.LayoutParams) stu_in_sd.parent.getLayoutParams();
                        stu_par_menu.width = surfaceVideoWidth;
                        stu_par_menu.height = surfaceVideoHeight;
                        removeRules(stu_par_menu);
                        stu_par_menu.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        stu_par_menu.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        stu_in_sd.parent.setLayoutParams(stu_par_menu);
                        stu_in_sd.ll_retract.setVisibility(View.GONE);

                        //控制收起布局
                        teacherItem.ll_retract.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams ll_retract_param = (LinearLayout.LayoutParams) teacherItem.ll_retract.getLayoutParams();
                        ll_retract_param.width = 40;
                        teacherItem.ll_retract.setLayoutParams(ll_retract_param);

                        //老师视频框
                        ter_par_menu = (RelativeLayout.LayoutParams) teacherItem.parent.getLayoutParams();
                        ter_par_menu.width = surfaceVideoWidth / 3 + ll_retract_param.width;
                        ter_par_menu.height = surfaceVideoHeight / 3;
                        removeRules(ter_par_menu);
                        ter_par_menu.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        ter_par_menu.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        teacherItem.parent.setLayoutParams(ter_par_menu);

                        teacherItem.sf_video.setZOrderMediaOverlay(true);
                        stu_in_sd.sf_video.setZOrderMediaOverlay(false);

                        setRetractListener(teacherItem, ter_par_menu.width);
                    } else {
                        //老师视频框
                        ter_par_menu = (RelativeLayout.LayoutParams) teacherItem.parent.getLayoutParams();
                        ter_par_menu.width = surfaceVideoWidth;
                        ter_par_menu.height = surfaceVideoHeight;
                        removeRules(ter_par_menu);
                        ter_par_menu.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        ter_par_menu.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        teacherItem.parent.setLayoutParams(ter_par_menu);
                        teacherItem.ll_retract.setVisibility(View.GONE);

                        //控制收起布局
                        stu_in_sd.ll_retract.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams ll_retract_param = (LinearLayout.LayoutParams) stu_in_sd.ll_retract.getLayoutParams();
                        ll_retract_param.width = 40;
                        stu_in_sd.ll_retract.setLayoutParams(ll_retract_param);

                        //学生视频框
                        stu_par_menu = (RelativeLayout.LayoutParams) stu_in_sd.parent.getLayoutParams();
                        stu_par_menu.width = surfaceVideoWidth / 3 + ll_retract_param.width;
                        stu_par_menu.height = surfaceVideoHeight / 3;
                        removeRules(stu_par_menu);
                        stu_par_menu.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        stu_par_menu.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        stu_in_sd.parent.setLayoutParams(stu_par_menu);

                        teacherItem.sf_video.setZOrderMediaOverlay(false);
                        stu_in_sd.sf_video.setZOrderMediaOverlay(true);

                        setRetractListener(stu_in_sd, stu_par_menu.width);
                    }

                    //设置白板大小
                    setWhiteBoradSize(webWidth, webHight);

                    //视频框内view属性
                    setLayoutParamsForGeneral(teacherItem, ter_par_menu.width, ter_par_menu.height);
                    setLayoutParamsForGeneral(stu_in_sd, stu_par_menu.width, stu_par_menu.height);
                    setLayoutParamsForStudent(stu_in_sd, stu_par_menu.width, stu_par_menu.height);
                }
            }
        });
    }

    /**
     * 全屏视频布局
     */
    private void doLayoutFullScreen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {

                    mRootHolder.ll_wb_container.setVisibility(View.VISIBLE);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRootHolder.lin_menu.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, 0);
                    mRootHolder.lin_menu.setLayoutParams(layoutParams);

                    mRootHolder.rel_video_change.setVisibility(View.VISIBLE);

                    //视频有效区域
                    int valueHeight = hid - toolBarHeight;
                    int valueWidth = mScreenValueWidth;

                    //以高为基准
                    surfaceVideoHeight = valueHeight;
                    surfaceVideoWidth = surfaceVideoHeight * 4 / 3;

                    if (surfaceVideoWidth * 2 > valueWidth) {
                        //以宽为基准
                        surfaceVideoWidth = valueWidth / 2;
                        surfaceVideoHeight = surfaceVideoWidth * 3 / 4;
                    }

                    //右边老师和学生视频框大小
                    RelativeLayout.LayoutParams menu_param = (RelativeLayout.LayoutParams) mRootHolder.lin_menu.getLayoutParams();
                    menu_param.width = surfaceVideoWidth * 2;
                    menu_param.height = surfaceVideoHeight;
                    menu_param.leftMargin = heightStatusBar;
                    mRootHolder.lin_menu.setLayoutParams(menu_param);

                    //设置白板大小
                    //白板大小
                    RelativeLayout.LayoutParams rel_wb_param = (RelativeLayout.LayoutParams) mRootHolder.ll_wb_container.getLayoutParams();
                    rel_wb_param.width = surfaceVideoHeight;
                    rel_wb_param.height = surfaceVideoHeight;
                    mRootHolder.ll_wb_container.setLayoutParams(rel_wb_param);

                    RelativeLayout.LayoutParams video_change_params = (RelativeLayout.LayoutParams) mRootHolder.rel_video_change.getLayoutParams();
                    video_change_params.width = menu_param.width;
                    video_change_params.height = menu_param.height;
                    video_change_params.leftMargin = heightStatusBar;
                    mRootHolder.rel_video_change.setLayoutParams(video_change_params);

                    //老师视频框
                    ter_par_menu = (RelativeLayout.LayoutParams) teacherItem.parent.getLayoutParams();
                    ter_par_menu.width = surfaceVideoWidth;
                    ter_par_menu.height = surfaceVideoHeight;
                    removeRules(ter_par_menu);
                    ter_par_menu.addRule(sortRule[0], R.id.lin_menu);
                    teacherItem.parent.setLayoutParams(ter_par_menu);
                    teacherItem.ll_retract.setVisibility(View.GONE);

                    //学生视频框
                    stu_par_menu = (RelativeLayout.LayoutParams) stu_in_sd.parent.getLayoutParams();
                    stu_par_menu.height = surfaceVideoHeight;
                    stu_par_menu.width = surfaceVideoWidth;
                    removeRules(stu_par_menu);
                    stu_par_menu.addRule(sortRule[1], R.id.lin_menu);
                    stu_in_sd.parent.setLayoutParams(stu_par_menu);
                    stu_in_sd.ll_retract.setVisibility(View.GONE);

                    //视频框内的View设置属性
                    setLayoutParamsForGeneral(teacherItem, surfaceVideoWidth, surfaceVideoHeight);
                    setLayoutParamsForGeneral(stu_in_sd, surfaceVideoWidth, surfaceVideoHeight);
                    setLayoutParamsForStudent(stu_in_sd, surfaceVideoWidth, surfaceVideoHeight);
                }
            }
        });
    }

    /**
     * 设置白板大小
     *
     * @param webWidth
     * @param webHeight
     */
    private void setWhiteBoradSize(int webWidth, int webHeight) {
        mRootHolder.ll_wb_container.setVisibility(View.VISIBLE);

        //放大时
        if (isZoom) {
            RelativeLayout.LayoutParams rel_wb_param = (RelativeLayout.LayoutParams) mRootHolder.ll_wb_container.getLayoutParams();
            rel_wb_param.width = mScreenValueWidth;
            rel_wb_param.height = hid;
            mRootHolder.ll_wb_container.setLayoutParams(rel_wb_param);

            if (wbFragment != null && WBSession.isPageFinish) {
                WhiteBoradConfig.getsInstance().SetTransmitWindowSize(rel_wb_param.width, rel_wb_param.height);

                int faceShareheight = rel_wb_param.height;
                int faceSharewidth = rel_wb_param.width;
                if (scale == 0) {//4:3
                    faceSharewidth = faceShareheight * 4 / 3;
                    if (faceSharewidth > mScreenValueWidth) {//宽度超出屏幕
                        faceSharewidth = mScreenValueWidth;
                        faceShareheight = mScreenValueWidth * 3 / 4;
                    }
                } else if (scale == 1) {//16:9
                    faceSharewidth = faceShareheight * 16 / 9;
                    if (faceSharewidth > mScreenValueWidth) {//宽度超出屏幕
                        faceSharewidth = mScreenValueWidth;
                        faceShareheight = mScreenValueWidth * 9 / 16;
                    }
                } else if (scale == 2) {//没有比例 设置为最大
                    faceSharewidth = mScreenValueWidth;
                    if (irregular * faceShareheight <= faceSharewidth) {
                        faceSharewidth = (int) (irregular * faceShareheight);
                    } else {
                        faceShareheight = (int) (faceSharewidth / irregular);
                    }
                }
                WhiteBoradConfig.getsInstance().SetFaceShareSize(faceSharewidth, faceShareheight);
                WhiteBoradConfig.getsInstance().setPaintFaceShareFullScreen(isZoom);

            }
            return;
        }

        //白板大小
        RelativeLayout.LayoutParams rel_wb_param = (RelativeLayout.LayoutParams) mRootHolder.ll_wb_container.getLayoutParams();
        rel_wb_param.width = webWidth;
        rel_wb_param.height = webHeight;
        rel_wb_param.leftMargin = heightStatusBar;
        mRootHolder.ll_wb_container.setLayoutParams(rel_wb_param);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRootHolder.lin_menu.getLayoutParams();
        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.ll_wb_container);
        mRootHolder.lin_menu.setLayoutParams(layoutParams);
        if (wbFragment != null) {

            WhiteBoradConfig.getsInstance().SetTransmitWindowSize(webWidth, webHeight);

            int faceSharewidth = webWidth;
            int faceShareheight = webHeight;
            if (scale == 0) {//4:3
                faceSharewidth = faceShareheight * 4 / 3;
                if (faceSharewidth > webWidth) {//宽度超出屏幕
                    faceSharewidth = webWidth;
                    faceShareheight = webWidth * 3 / 4;
                }
            } else if (scale == 1) {//16:9
                faceSharewidth = faceShareheight * 16 / 9;
                if (faceSharewidth > webWidth) {//宽度超出屏幕
                    faceSharewidth = webWidth;
                    faceShareheight = webWidth * 9 / 16;
                }
            } else if (scale == 2) {//没有比例 设置为最大
                if (irregular * faceShareheight <= faceSharewidth) {
                    faceSharewidth = (int) (irregular * faceShareheight);
                } else {
                    faceShareheight = (int) (faceSharewidth / irregular);
                }
            }
            WhiteBoradConfig.getsInstance().SetFaceShareSize(faceSharewidth, faceShareheight);
        }

        webandsufwidth = rel_wb_param.width + stu_par_menu.width + 10;

        if (webandsufwidth >= wid) {
            webandsufwidth = 0;
        } else {
            if (isHaiping) {
                webandsufwidth = (wid - (rel_wb_param.width + stu_par_menu.width + 10) - ScreenScale.getStatusBarHeight()) / 2;
            } else {
                webandsufwidth = (wid - (rel_wb_param.width + stu_par_menu.width + 10)) / 2;
            }
        }

        if (toolsView != null) {
            toolsView.doLayout(rel_wb_param.width, rel_wb_param.height);
        }
        if (mPagesView != null) {
            mPagesView.doLayout(rel_wb_param.width, rel_wb_param.height);
            mPagesView.setfull(isZoom);
            mPagesView.SetFragementView(mRootHolder.ll_wb_container);
        }
    }

    /**
     * 老师和学生视频框内相同View设置属性
     *
     * @param videoItem
     * @param surfaceVideoWidth
     * @param surfaceVideoHeight
     */
    private void setLayoutParamsForGeneral(VideoItem videoItem, int surfaceVideoWidth, int surfaceVideoHeight) {

        //视频框底部阴影高度
        RelativeLayout.LayoutParams stu_name_menu = (RelativeLayout.LayoutParams) videoItem.lin_name_label.getLayoutParams();
        stu_name_menu.width = surfaceVideoWidth;
        if (stu_name_menu.width > defaultVideoWidth) {
            stu_name_menu.height = (int) (defaultVideoWidth * ((double) 30 / (double) 350));
        } else {
            stu_name_menu.height = (int) (stu_name_menu.width * ((double) 30 / (double) 350));
        }
        videoItem.lin_name_label.setLayoutParams(stu_name_menu);

        //教室用户昵称
        RelativeLayout.LayoutParams txt_name_par = (RelativeLayout.LayoutParams) videoItem.txt_name.getLayoutParams();
        txt_name_par.height = stu_name_menu.height;
        txt_name_par.addRule(RelativeLayout.CENTER_IN_PARENT);
        videoItem.txt_name.setLayoutParams(txt_name_par);

        //音量
        RelativeLayout.LayoutParams img_mic_par = (RelativeLayout.LayoutParams) videoItem.img_mic.getLayoutParams();
        img_mic_par.height = (int) (stu_name_menu.height);
        img_mic_par.width = (int) (stu_name_menu.height);
        videoItem.img_mic.setLayoutParams(img_mic_par);

        //画笔
        RelativeLayout.LayoutParams img_pen_par = (RelativeLayout.LayoutParams) videoItem.img_pen.getLayoutParams();
        img_pen_par.height = (int) (stu_name_menu.height);
        img_pen_par.width = (int) (stu_name_menu.height);
        videoItem.img_pen.setLayoutParams(img_pen_par);

        //音量条
        RelativeLayout.LayoutParams volue = (RelativeLayout.LayoutParams) videoItem.volume.getLayoutParams();
        volue.height = (int) stu_name_menu.height;
        volue.width = (int) (stu_name_menu.height * 52 / 42);
        videoItem.volume.setLayoutParams(volue);

    }

    /**
     * 学生视频框内 View设置属性
     *
     * @param videoItem
     * @param surfaceVideoWidth
     * @param surfaceVideoHeight
     */
    private void setLayoutParamsForStudent(VideoItem videoItem, int surfaceVideoWidth, int surfaceVideoHeight) {

        //举手
        RelativeLayout.LayoutParams img_hand_par = (RelativeLayout.LayoutParams) videoItem.img_hand.getLayoutParams();
        if (surfaceVideoWidth > defaultVideoWidth) {
            img_hand_par.height = (int) (defaultVideoWidth * ((double) 30 / (double) 350));
        } else {
            img_hand_par.height = (int) (surfaceVideoWidth * ((double) 30 / (double) 350));
        }
        img_hand_par.width = (int) (img_hand_par.height);
        videoItem.img_hand.setLayoutParams(img_hand_par);

        //奖杯图标
        FrameLayout.LayoutParams icon_gif_par = (FrameLayout.LayoutParams) videoItem.icon_gif.getLayoutParams();
        icon_gif_par.height = img_hand_par.height;
        icon_gif_par.width = img_hand_par.height;
        videoItem.icon_gif.setLayoutParams(icon_gif_par);

        //奖杯礼物数量
        FrameLayout.LayoutParams txt_gift_par = (FrameLayout.LayoutParams) videoItem.txt_gift_num.getLayoutParams();
        txt_gift_par.height = img_hand_par.height / 10 * 9;
        videoItem.txt_gift_num.setPadding(icon_gif_par.width + icon_gif_par.height / 4,
                0, icon_gif_par.height / 3, 0);
        videoItem.txt_gift_num.setLayoutParams(txt_gift_par);
    }

    /**
     * 双师布局 交换视频框
     */
    private void doubleVideoChange() {
        int ter_width = ter_par_menu.width;
        int ter_height = ter_par_menu.height;

        //老师视频框
        ter_par_menu.width = stu_par_menu.width;
        ter_par_menu.height = stu_par_menu.height;
        teacherItem.parent.setLayoutParams(ter_par_menu);

        //学生视频框
        stu_par_menu.width = ter_width;
        stu_par_menu.height = ter_height;
        stu_in_sd.parent.setLayoutParams(stu_par_menu);

        mRootHolder.lin_menu.removeAllViews();

        if (ter_par_menu.width > stu_par_menu.width) {
            stu_in_sd.ll_retract.setVisibility(View.VISIBLE);
            teacherItem.ll_retract.setVisibility(View.GONE);
            setRetractListener(stu_in_sd, stu_par_menu.width);

            teacherItem.sf_video.setZOrderMediaOverlay(false);
            stu_in_sd.sf_video.setZOrderMediaOverlay(true);

            mRootHolder.lin_menu.addView(teacherItem.parent);
            mRootHolder.lin_menu.addView(stu_in_sd.parent);

        } else {
            teacherItem.ll_retract.setVisibility(View.VISIBLE);
            stu_in_sd.ll_retract.setVisibility(View.GONE);
            setRetractListener(teacherItem, ter_par_menu.width);

            teacherItem.sf_video.setZOrderMediaOverlay(true);
            stu_in_sd.sf_video.setZOrderMediaOverlay(false);

            mRootHolder.lin_menu.addView(stu_in_sd.parent);
            mRootHolder.lin_menu.addView(teacherItem.parent);
        }

        //视频框内view属性
        setLayoutParamsForGeneral(teacherItem, ter_par_menu.width, ter_par_menu.height);
        setLayoutParamsForGeneral(stu_in_sd, stu_par_menu.width, stu_par_menu.height);
        setLayoutParamsForStudent(stu_in_sd, stu_par_menu.width, stu_par_menu.height);

    }

    /**
     * 显示收起布局
     */
    private void setRetractListener(final VideoItem videoItem, final int distance) {
        videoItem.ll_retract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRetractState) {
                    videoShow(videoItem, 300);
                } else {
                    videoRetract(videoItem, distance - 40);
                }
                mRetractState = !mRetractState;
            }
        });
    }

    /**
     * 重置双师右上角小视频状态 切换布局和切换视频时重置
     */
    private void resetDoubleSmallVideo() {
        if (mLayoutState == LayoutPopupWindow.LAYOUT_DOUBLE) {
            if (mRetractState) {
                if (teacherItem.ll_retract.getVisibility() == View.VISIBLE) {
                    videoShow(teacherItem, 0);
                }

                if (stu_in_sd.ll_retract.getVisibility() == View.VISIBLE) {
                    videoShow(stu_in_sd, 0);
                }
            }
            mRetractState = false;
        }
    }

    /**
     * @param videoItem 视频收起
     */
    private void videoRetract(VideoItem videoItem, int distance) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(videoItem.parent, "translationX", distance);
        animator.setDuration(300);
        animator.start();
        videoItem.iv_retract.setImageResource(R.drawable.tk_show);
    }

    /**
     * @param videoItem 视频展示
     */
    private void videoShow(VideoItem videoItem, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(videoItem.parent, "translationX", 0);
        animator.setDuration(duration);
        animator.start();
        videoItem.iv_retract.setImageResource(R.drawable.tk_shouqi);
    }

    /**
     * 全屏视频布局下 视频框位置切换
     */
    private void sortSurfaceView() {
        ter_par_menu.removeRule(sortRule[0]);
        stu_par_menu.removeRule(sortRule[1]);
        // sortRule重新排序
        toSort();
        //老师视频框
        ter_par_menu.addRule(sortRule[0], R.id.lin_menu);
        teacherItem.parent.setLayoutParams(ter_par_menu);
        //学生视频框
        stu_par_menu.addRule(sortRule[1], R.id.lin_menu);
        stu_in_sd.parent.setLayoutParams(stu_par_menu);
    }

    private void toSort() {
        if (sortRule != null && sortRule.length > 0) {
            int t = sortRule[0];
            sortRule[0] = sortRule[1];
            sortRule[1] = t;
        }
    }

    /**
     * 清除 视频框 布局规则
     *
     * @param params
     */
    private void removeRules(RelativeLayout.LayoutParams params) {
        for (int i = 0; i < mRemoveRules.length; i++) {
            params.removeRule(mRemoveRules[i]);
        }
    }

    private void doPlayVideo() {
        for (int i = 0; i < RoomSession.playingList.size(); i++) {
            RoomUser roomUser = RoomSession.playingList.get(i);
            if (roomUser == null) {
                return;
            }
            if (!RoomSession.fullScreen) {
                if (roomUser.role == 0) {
                    changeVideoItemState(roomUser, teacherItem);
                } else if (roomUser.role == 2) {
                    changeVideoItemState(roomUser, stu_in_sd);
                }
            }
            changeFullScreenState(roomUser);
        }
    }

    /**
     * 控制课件同步  小视频框
     *
     * @param roomUser
     */
    private void controlFullScreen(RoomUser roomUser) {
        if (videofragment != null) {
            videofragment.setFullscreenShow(roomUser.peerId);
        } else {
            if (movieFragment != null) {
                movieFragment.setFullscreenShow(roomUser.peerId);
            } else {
                FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                        roomUser, true);
            }
        }
    }

    boolean is_show_teacher_window = true;

    private void showTeacherControlPop(final RoomUser user) {

        if (!is_show_teacher_window) {
            is_show_teacher_window = true;
            return;
        }

        if (!(TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER)) {
            return;
        }

        if ((teaPopupWindow != null && teaPopupWindow.isShowing()) ||
                (!RoomControler.isReleasedBeforeClass() && !RoomSession.isClassBegin)) {
            return;
        }

        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tk_popup_av_control, null);
        LinearLayout lin_video_control = (LinearLayout) contentView.findViewById(R.id.lin_video_control);
        LinearLayout lin_audio_control = (LinearLayout) contentView.findViewById(R.id.lin_audio_control);
        LinearLayout lin_change = (LinearLayout) contentView.findViewById(R.id.lin_change);

        final ImageView img_video_control = (ImageView) contentView.findViewById(R.id.img_camera);
        final ImageView img_audio_control = (ImageView) contentView.findViewById(R.id.img_audio);
        final TextView txt_video = (TextView) contentView.findViewById(R.id.txt_camera);
        final TextView txt_audio = (TextView) contentView.findViewById(R.id.txt_audio);
        ImageView right_arr = (ImageView) contentView.findViewById(R.id.right_arr);

        if (RoomSession.isOnliyAudioRoom) {
            lin_video_control.setVisibility(View.GONE);
        } else {
            lin_video_control.setVisibility(View.VISIBLE);
        }

        if (mLayoutState == LayoutPopupWindow.LAYOUT_DOUBLE && TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
            lin_change.setVisibility(View.VISIBLE);
            teaPopupWindow = new PopupWindow(KeyBoardUtil.dp2px(OneToOneActivity.this, 80f),
                    KeyBoardUtil.dp2px(OneToOneActivity.this, 170));
        } else {
            lin_change.setVisibility(View.GONE);
            teaPopupWindow = new PopupWindow(KeyBoardUtil.dp2px(OneToOneActivity.this, 80),
                    KeyBoardUtil.dp2px(OneToOneActivity.this, 115));
        }

        teaPopupWindow.setContentView(contentView);

        teaPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                is_show_teacher_window = !Tools.isInView(event, teacherItem.parent);
                return false;
            }
        });

        if (user.getPublishState() == 0 || user.getPublishState() == 2 || user.getPublishState() == 4) {
            img_audio_control.setImageResource(R.drawable.tk_icon_close_audio);
            txt_audio.setText(R.string.close_audio);
        } else {
            img_audio_control.setImageResource(R.drawable.tk_icon_open_audio);
            txt_audio.setText(R.string.open_audio);
        }
        if (user.getPublishState() == 0 || user.getPublishState() == 1 || user.getPublishState() == 4) {
            img_video_control.setImageResource(R.drawable.tk_icon_close_vidio);
            txt_video.setText(R.string.video_off);
        } else {
            img_video_control.setImageResource(R.drawable.tk_icon_open_vidio);
            txt_video.setText(R.string.video_on);
        }

        lin_video_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //当全音频时不开启视频
                if (RoomSession.isOnliyAudioRoom) {
                    return;
                }
                if (user.getPublishState() == 0 || user.getPublishState() == 1 || user.getPublishState() == 4) {
                    img_video_control.setImageResource(R.drawable.tk_icon_open_vidio);
                    txt_video.setText(R.string.video_on);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                            "__all", "publishstate", user.getPublishState() == 0 ||
                                    user.getPublishState() == 4 ? 2 : 3);
                } else {
                    img_video_control.setImageResource(R.drawable.tk_icon_close_vidio);
                    txt_video.setText(R.string.video_off);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                            "__all", "publishstate", user.getPublishState() == 2 ? 4 : 1);
                }
                teaPopupWindow.dismiss();
            }
        });

        lin_audio_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getPublishState() == 0 || user.getPublishState() == 2 || user.getPublishState() == 4) {
                    img_audio_control.setImageResource(R.drawable.tk_icon_open_audio);
                    txt_audio.setText(R.string.open_audio);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                            "__all", "publishstate", user.getPublishState() == 0 ||
                                    user.getPublishState() == 4 ? 1 : 3);
                } else {
                    img_audio_control.setImageResource(R.drawable.tk_icon_close_audio);
                    txt_audio.setText(R.string.close_audio);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId,
                            "__all", "publishstate", user.getPublishState() == 3 ? 2 : 4);
                }
                teaPopupWindow.dismiss();
            }
        });

        lin_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重置双师右上角小视频状态 切换布局和切换视频时重置
                resetDoubleSmallVideo();
                //双师布局 交换视频框
                doubleVideoChange();
                teaPopupWindow.dismiss();
            }
        });

        teaPopupWindow.setFocusable(false);
        teaPopupWindow.setOutsideTouchable(true);
        teaPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        if (mLayoutState == LayoutPopupWindow.LAYOUT_VIDEO) {
            right_arr.setVisibility(View.GONE);
            teaPopupWindow.showAsDropDown(teacherItem.parent, teacherItem.parent.getWidth() / 2 - teaPopupWindow.getWidth() / 2,
                    -teacherItem.parent.getHeight() / 2 - teaPopupWindow.getHeight() / 2);
        } else {
            right_arr.setVisibility(View.VISIBLE);
            teaPopupWindow.showAsDropDown(teacherItem.parent, -(teaPopupWindow.getWidth()),
                    -(teacherItem.parent.getMeasuredHeight() + teaPopupWindow.getHeight()) / 2, Gravity.CENTER_VERTICAL);
        }
    }

    private void initViewByRoomTypeAndTeacher() {
        //全体禁言  花名册  课件库  工具箱  布局控制
        mRootHolder.cb_control.setVisibility(View.GONE);
        mRootHolder.rl_member_list.setVisibility(View.GONE);
        mRootHolder.cb_file_person_media_list.setVisibility(View.GONE);
        mRootHolder.cb_tool_case.setVisibility(View.GONE);
        mRootHolder.cb_tool_layout.setVisibility(View.GONE);

        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {//老师
            if (RoomControler.isSwitchAudioClassroom()) {
                mRootHolder.cb_control.setVisibility(View.VISIBLE);
            }
            if (mLayoutState == 3) {
                mRootHolder.cb_file_person_media_list.setVisibility(View.GONE);
            } else {
                mRootHolder.cb_file_person_media_list.setVisibility(View.VISIBLE);
            }
            mRootHolder.cb_tool_layout.setVisibility(View.VISIBLE);
            mRootHolder.rl_member_list.setVisibility(View.VISIBLE);

            if (RoomSession.isClassBegin) {
                if (!RoomControler.isHasAnswerMachine() && !RoomControler.isHasTurntable() &&
                        !RoomControler.isHasTimer() && !RoomControler.isHasResponderAnswer() && !RoomControler.isHasWhiteBoard()) {
                    mRootHolder.cb_tool_case.setVisibility(View.GONE);
                } else {
                    mRootHolder.cb_tool_case.setVisibility(View.VISIBLE);
                }
            }

            if (!RoomControler.isShowClassBeginButton()) {
                mRootHolder.txt_class_begin.setVisibility(View.VISIBLE);
                if (RoomSession.isClassBegin) {
                    mRootHolder.txt_class_begin.setText(R.string.classdismiss);
                } else {
                    mRootHolder.txt_class_begin.setText(R.string.classbegin);
                }
            } else {
                mRootHolder.txt_class_begin.setVisibility(View.GONE);
            }

        } else if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT) {//学生

            mRootHolder.txt_class_begin.setVisibility(View.GONE);
            mRootHolder.eye_protection.setVisibility(View.VISIBLE);
            if (!RoomSession.isClassBegin) {
                mRootHolder.txt_hand_up.setVisibility(View.GONE);
                mRootHolder.cb_choose_photo.setVisibility(View.GONE);
                if (mLayoutState == 3) {
                    mRootHolder.cb_file_person_media_list.setVisibility(View.GONE);
                } else {
                    mRootHolder.cb_file_person_media_list.setVisibility(View.VISIBLE);
                }
            } else {
                mRootHolder.cb_file_person_media_list.setVisibility(View.GONE);
            }
            if (RoomControler.isHideStudentWifiStatus()) {
                mRootHolder.lin_wifi.setVisibility(View.GONE);
            }
        } else if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL) {  //巡课

            if (RoomControler.isShowClassBeginButton() || RoomControler.patrollerCanClassDismiss()
                    || !RoomSession.isClassBegin) {
                mRootHolder.txt_class_begin.setVisibility(View.GONE);
                mRootHolder.cb_choose_photo.setVisibility(View.GONE);
            } else {
                mRootHolder.txt_class_begin.setVisibility(View.VISIBLE);
                mRootHolder.txt_class_begin.setText(R.string.classdismiss);
            }
            mRootHolder.rl_member_list.setVisibility(View.VISIBLE);
            mRootHolder.flipCamera.setVisibility(View.GONE);
            mRootHolder.cb_file_person_media_list.setVisibility(View.VISIBLE);

        } else if (TKRoomManager.getInstance().getMySelf().role == -1) {  //回放
            CommonUtil.setTimeVisibility(mRootHolder, View.GONE);
            mRootHolder.flipCamera.setVisibility(View.GONE);
            mRootHolder.txt_class_begin.setVisibility(View.GONE);
            mRootHolder.txt_hand_up.setVisibility(View.GONE);
            if (mRootHolder.re_play_back != null)
                mRootHolder.re_play_back.setVisibility(View.VISIBLE);
        }
    }

    private void initVideoItem() {
        RelativeLayout.LayoutParams menu_param = (RelativeLayout.LayoutParams) mRootHolder.lin_menu.getLayoutParams();
        menu_param.width = 0;
        menu_param.height = 0;
        mRootHolder.lin_menu.setLayoutParams(menu_param);
        //创建老师视频框
        teacherItem = new VideoItem(OneToOneActivity.this);

        initVideoItemWidget(0, teacherItem);

        //创建学生视频框
        stu_in_sd = new VideoItem(OneToOneActivity.this);

        initVideoItemWidget(2, stu_in_sd);

        //视频框添加到界面
        mRootHolder.lin_menu.addView(teacherItem.parent);
        mRootHolder.lin_menu.addView(stu_in_sd.parent);
    }

    public void showExitDialog() {
        Tools.showDialog(OneToOneActivity.this, R.string.remind, getString(R.string.logouts), new Tools.OnDialogClick() {
            @Override
            public void dialog_ok(Dialog dialog) {
                sendGiftPopUtils.deleteImage();
                TKRoomManager.getInstance().leaveRoom();
                RoomClient.getInstance().onLeaveRoom();
                dialog.dismiss();
            }
        });
    }

    //下课
    public void showClassDissMissDialog() {
        Tools.showDialog(OneToOneActivity.this, R.string.remind, getString(R.string.make_sure_class_dissmiss), new Tools.OnDialogClick() {
            @Override
            public void dialog_ok(Dialog dialog) {
                try {
                    TKRoomManager.getInstance().delMsg("ClassBegin", "ClassBegin",
                            "__all", new JSONObject().put("recordchat", true).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mRootHolder.txt_class_begin.setVisibility(View.GONE);
                RoomSession.playingList.clear();
                RoomOperation.getInstance().sendClassDissToPhp();
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 清空未读消息数
     */
    private void clearNoReadChatMessage() {
        RoomSession.chatDataCache.clear();
        if (mRootHolder.tv_no_read_message_number != null) {
            mRootHolder.tv_no_read_message_number.setVisibility(View.GONE);
            mRootHolder.tv_no_read_message_number.setText("");
        }
    }

    private void setWhiteBoradEnlarge(boolean isZoom) {
        //隐藏视频框
        hideSurfaceview();
        //聊天框
//        mRootHolder.cb_message.setChecked(false);
//        mRootHolder.cb_message.setVisibility(View.GONE);
//        mRootHolder.iv_open_input.setVisibility(View.GONE);
//        mRootHolder.cb_choose_shut_chat.setVisibility(View.GONE);
//        mRootHolder.tv_no_read_message_number.setVisibility(View.GONE);
//        mRootHolder.rl_message.setVisibility(View.GONE);
        mRootHolder.lin_menu.setVisibility(View.GONE);
        mRootHolder.lin_audio_seek.setVisibility(View.INVISIBLE);
        mRootHolder.fl_play_disk.setVisibility(View.INVISIBLE);
        mRootHolder.rel_tool_bar.setVisibility(View.GONE);

        RelativeLayout.LayoutParams rel_wb_param = (RelativeLayout.LayoutParams) mRootHolder.ll_wb_container.getLayoutParams();
        rel_wb_param.width = LinearLayout.LayoutParams.MATCH_PARENT;
        rel_wb_param.height = LinearLayout.LayoutParams.MATCH_PARENT;
        mRootHolder.ll_wb_container.setLayoutParams(rel_wb_param);


        if (wbFragment != null && WBSession.isPageFinish) {
            WhiteBoradConfig.getsInstance().SetTransmitWindowSize(rel_wb_param.width, rel_wb_param.height);
        }

        WhiteBoradConfig.getsInstance().setPaintFaceShareFullScreen(isZoom);
        WhiteBoradConfig.getsInstance().sendJSPageFullScreen(isZoom);

        if (toolsView != null) {
            toolsView.doLayout(wid, hid);
        }
        if (mPagesView != null) {
            mPagesView.doLayout(wid, hid);
            mPagesView.setfull(isZoom);
            mPagesView.SetFragementView(mRootHolder.ll_wb_container);
        }
    }

    private void setWhiteBoradNarrow(boolean isZoom) {
        this.isZoom = isZoom;
        //聊天
        mRootHolder.cb_message.setVisibility(View.VISIBLE);
        mRootHolder.rl_message.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(mRootHolder.tv_no_read_message_number.getText().toString())) {
            mRootHolder.tv_no_read_message_number.setVisibility(View.VISIBLE);
        }

        mRootHolder.lin_menu.setVisibility(View.VISIBLE);
        mRootHolder.rel_tool_bar.setVisibility(View.VISIBLE);

        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER && RoomSession.isPublishMp3) {
            mRootHolder.lin_audio_seek.setVisibility(View.VISIBLE);
            mRootHolder.fl_play_disk.setVisibility(View.VISIBLE);
        } else {
            mRootHolder.lin_audio_seek.setVisibility(View.INVISIBLE);
            if (RoomSession.isPublishMp3) {
                mRootHolder.fl_play_disk.setVisibility(View.VISIBLE);
            } else {
                mRootHolder.fl_play_disk.setVisibility(View.INVISIBLE);
            }
        }

        if (!RoomSession.isClassBegin) {
            playSelfBeforeClassBegin();
        }

        if (TKRoomManager.getInstance().getMySelf() != null &&
                !TextUtils.isEmpty(TKRoomManager.getInstance().getMySelf().peerId)) {
            if (!RoomSession.isPublishMp4 && !RoomSession.isShareFile && !RoomSession.isShareScreen) {
                doPlayVideo();
            }
        }

        WhiteBoradConfig.getsInstance().sendJSPageFullScreen(isZoom);
        WhiteBoradConfig.getsInstance().setPaintFaceShareFullScreen(isZoom);
        doLayout();
    }

    //上课前播自己本地视频
    private void playSelfBeforeClassBegin() {
        RoomUser roomUser = TKRoomManager.getInstance().getMySelf();
        if (roomUser == null || roomUser.role == 4 || roomUser.role == -1) {
            return;
        }
        if (RoomControler.isReleasedBeforeClass() && roomUser.publishState == 0) {
            if (RoomSession.isOnliyAudioRoom) {
                TKRoomManager.getInstance().changeUserProperty(roomUser.peerId, "__all", "publishstate", 1);
            } else {
                TKRoomManager.getInstance().changeUserProperty(roomUser.peerId, "__all", "publishstate", 3);
            }
        } else {
            if (roomUser.role == 0) {
                playSelfBeforeClassBeginVideoState(teacherItem, roomUser);
            } else if (roomUser.role == 2) {
                playSelfBeforeClassBeginVideoState(stu_in_sd, roomUser);
            }
        }
    }

    //自己本地视频框上View的状态
    private void playSelfBeforeClassBeginVideoState(VideoItem videoItem, RoomUser roomUser) {
        if (videoItem == null || roomUser == null) {
            return;
        }
        videoItem.img_mic.setVisibility(View.VISIBLE);
        videoItem.img_mic.setImageResource(R.drawable.tk_icon_close_voice);
        videoItem.volume.setVisibility(View.GONE);
        videoItem.rel_group.setVisibility(View.VISIBLE);
        videoItem.txt_name.setVisibility(View.VISIBLE);
        videoItem.lin_name_label.setVisibility(View.VISIBLE);
        videoItem.txt_name.setText(roomUser.nickName);

        if (roomUser.disablevideo || !roomUser.hasVideo) {
            videoItem.sf_video.setVisibility(View.INVISIBLE);
            videoItem.bg_video_back.setVisibility(View.VISIBLE);
            videoItem.img_video_back.setVisibility(View.VISIBLE);
            videoItem.img_video_back.setImageResource(R.drawable.one_2_one_camera_zw);
        } else {
            videoItem.sf_video.setVisibility(View.VISIBLE);
            videoItem.bg_video_back.setVisibility(View.GONE);
            TKRoomManager.getInstance().playVideo(roomUser.peerId, videoItem.sf_video,
                    RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
        }
    }

    /***
     *    视频数据首帧回调
     * @param peerIdVideo      peerID
     */
    @Override
    public void onFirstVideoFrame(String peerIdVideo) {
        if (videofragment != null) {
            videofragment.hideLaoding(peerIdVideo);
        }
    }

    /***
     *  音量回调
     * @param volumePeerId   用户 id
     * @param volume   音量
     */
    public void onAudioVolume(String volumePeerId, int volume) {
        RoomUser roomUser = TKRoomManager.getInstance().getUser(volumePeerId);
        if (roomUser != null && roomUser.role == 0) {
            if (roomUser.getPublishState() == 1 || roomUser.getPublishState() == 3) {
                teacherItem.img_mic.setImageResource(R.drawable.tk_icon_sound);
                teacherItem.img_mic.setVisibility(View.VISIBLE);
                teacherItem.volume.setVisibility(View.VISIBLE);
                if (volume <= 5) {
                    teacherItem.volume.setIndex(0);
                } else if (volume > 5 && volume < 5000) {
                    teacherItem.volume.setIndex(1);
                } else if (volume > 5000 && volume < 10000) {
                    teacherItem.volume.setIndex(2);
                } else if (volume > 10000 && volume < 20000) {
                    teacherItem.volume.setIndex(3);
                } else if (volume > 20000 && volume < 30000) {
                    teacherItem.volume.setIndex(4);
                }
            } else {
                teacherItem.img_mic.setImageResource(R.drawable.tk_img_mic_ban);
                teacherItem.volume.setVisibility(View.INVISIBLE);
            }
        } else if (roomUser != null && roomUser.role == 2) {
            if (roomUser.getPublishState() == 1 || roomUser.getPublishState() == 3) {
                stu_in_sd.img_mic.setImageResource(R.drawable.tk_icon_sound);
                stu_in_sd.img_mic.setVisibility(View.VISIBLE);
                stu_in_sd.volume.setVisibility(View.VISIBLE);
                if (volume <= 5) {
                    stu_in_sd.volume.setIndex(0);
                } else if (volume > 5 && volume < 5000) {
                    stu_in_sd.volume.setIndex(1);
                } else if (volume > 5000 && volume < 10000) {
                    stu_in_sd.volume.setIndex(2);
                } else if (volume > 10000 && volume < 20000) {
                    stu_in_sd.volume.setIndex(3);
                } else if (volume > 20000 && volume < 30000) {
                    stu_in_sd.volume.setIndex(4);
                }
            } else {
                if (roomUser.getPublishState() == 0) {
                    stu_in_sd.img_mic.setImageResource(R.drawable.tk_img_mic_ban);
                    stu_in_sd.volume.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void removeVideoFragment() {
        //显示popupwindow
        setPopupWindowVisibility(View.VISIBLE);
        if (mRootHolder.video_container != null) {
            mRootHolder.video_container.setVisibility(View.GONE);
        }
        videofragment = VideoFragment.getInstance();
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        mediaListAdapter.setLocalfileid(-1);
        ft.remove(videofragment);
        ft.commitAllowingStateLoss();
        videofragment = null;
    }

    /***
     *  视频框状态改变
     */
    private void changeVideoState() {
        //当全屏且画中画时候
        if (RoomSession.fullScreen && RoomControler.isFullScreenVideo()) {
            if (TKRoomManager.getInstance().getMySelf().role == 2) {
                if (!TextUtils.isEmpty(teacherItem.peerid)) {
                    FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                            TKRoomManager.getInstance().getUser(teacherItem.peerid), true);
                }
            } else {
                if (!TextUtils.isEmpty(stu_in_sd.peerid)) {
                    FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                            TKRoomManager.getInstance().getUser(stu_in_sd.peerid), true);
                }
            }
        } else {
            //视频结束时播放自己视频
            if (RoomSession.isClassBegin) {
                doPlayVideo();
            } else {
                playSelfBeforeClassBegin();
            }
            FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem, null, false);
        }
    }

    public void readyForPlayVideo(String shareMediaPeerId, Map<String, Object> shareMediaAttrs) {

        if (isZoom && RoomSession.isClassBegin && RoomControler.isFullScreenVideo()) {
            FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                    null, false);
        }

        //隐藏popupwindow
        setPopupWindowVisibility(View.GONE);
        mRootHolder.video_container.setVisibility(View.VISIBLE);
        videofragment = VideoFragment.getInstance();
        videofragment.setStream(shareMediaPeerId, shareMediaAttrs);
        videofragment.setFullscreen_video_param(fullscreen_video_param);
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        ft.replace(R.id.video_container, videofragment);
        ft.commitAllowingStateLoss();
    }

    /***
     *  有文件媒体共享的回调
     * @param peerIdShareFile      共享者用户 id
     * @param stateShareFile       媒体共享状态 0 停止 1 开始
     */
    public void onShareFileState(String peerIdShareFile, int stateShareFile) {

        mRootHolder.cb_message.setChecked(false);

        if (stateShareFile == 0) {
            removeMovieFragment();
            changeVideoState();
            TKRoomManager.getInstance().unPlayFile(peerIdShareFile);
            mRootHolder.video_container.setVisibility(View.GONE);
            WhiteBoradConfig.getsInstance().hideWalkView(false);

        } else if (stateShareFile == 1) {

            movieFragment = MovieFragment.getInstance();
            movieFragment.setFullscreen_video_param(fullscreen_video_param);
            fragmentManager = getSupportFragmentManager();
            ft = fragmentManager.beginTransaction();

            hidePopupWindow();
            hideSurfaceview();
            //关闭画笔工具弹窗
            toolsView.dismissPop();

            if (wbFragment != null) {
                WhiteBoradConfig.getsInstance().closeNewPptVideo();
                WhiteBoradConfig.getsInstance().hideWalkView(true);
            }

            movieFragment.setShareFilePeerId(peerIdShareFile);
            if (!movieFragment.isAdded()) {
                mRootHolder.video_container.setVisibility(View.VISIBLE);
                ft.replace(R.id.video_container, movieFragment);
                ft.commitAllowingStateLoss();
                //隐藏popupwindow
                setPopupWindowVisibility(View.GONE);
            }

            if (RoomSession.fullScreen && RoomSession.isClassBegin && RoomControler.isFullScreenVideo()) {
                FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                        null, true);
            }
        }
    }

    /***
     *   有屏幕共享的回调
     * @param peerIdScreen     共享者用户 id
     * @param stateScreen      媒体共享状态 0 停止 1 开始
     */
    public void onShareScreenState(String peerIdScreen, int stateScreen) {
        mRootHolder.cb_message.setChecked(false);

        if (stateScreen == 0) {
            removeScreenFragment();
            changeVideoState();
            doLayout();
            TKRoomManager.getInstance().unPlayScreen(peerIdScreen);
            mRootHolder.video_container.setVisibility(View.GONE);

        } else {
            screenFragment = ScreenFragment.getInstance();
            fragmentManager = getSupportFragmentManager();
            ft = fragmentManager.beginTransaction();

            hideSurfaceview();
            hidePopupWindow();
            //关闭画笔工具弹窗
            toolsView.dismissPop();

            if (wbFragment != null) {
                WhiteBoradConfig.getsInstance().closeNewPptVideo();
            }
            screenFragment.setPeerId(peerIdScreen);
            if (!screenFragment.isAdded()) {
                mRootHolder.video_container.setVisibility(View.VISIBLE);
                ft.replace(R.id.video_container, screenFragment);
                ft.commitAllowingStateLoss();
                //隐藏popupwindow
                setPopupWindowVisibility(View.GONE);
            }
        }
    }

    /***
     *   隐藏视频框
     */
    private void hideSurfaceview() {
        teacherItem.sf_video.setZOrderMediaOverlay(false);
        teacherItem.sf_video.setVisibility(View.INVISIBLE);
        stu_in_sd.sf_video.setZOrderMediaOverlay(false);
        stu_in_sd.sf_video.setVisibility(View.INVISIBLE);
    }

    /***
     *   有网络媒体文件共享的回调    mp4/mp3
     * @param shareMediaPeerId   共享者用户 id
     * @param shareMediaState    媒体共享状态 0 停止 1 开始
     * @param shareMediaAttrs    自定义数据
     */
    public void onShareMediaState(String shareMediaPeerId, int shareMediaState, Map<String, Object> shareMediaAttrs) {

        this.mediaAttrs = shareMediaAttrs;
        this.mediaPeerId = shareMediaPeerId;
        mediaListAdapter.setShareMediaAttrs(shareMediaAttrs);

        if (shareMediaState == 0) {    //媒体结束播放
            mediaListAdapter.setLocalfileid(-1);
            TKRoomManager.getInstance().unPlayMedia(shareMediaPeerId);
            if (shareMediaAttrs.containsKey("video")) {
                if ((boolean) shareMediaAttrs.get("video")) {
                    removeVideoFragment();
                    changeVideoState();
                } else {
                    mRootHolder.lin_audio_seek.setVisibility(View.INVISIBLE);
                    mRootHolder.img_disk.clearAnimation();
                    mRootHolder.fl_play_disk.setVisibility(View.INVISIBLE);
                }
            }
            WhiteBoradConfig.getsInstance().hideWalkView(false);
        } else if (shareMediaState == 1) {    //媒体开始播放
            if (wbFragment != null) {
                WhiteBoradConfig.getsInstance().closeNewPptVideo();
            }
            //关闭画笔工具弹窗
            toolsView.dismissPop();
            isMediaMute = false;
            mediaListAdapter.setLocalfileid(shareMediaAttrs.get("fileid"));

            if (RoomSession.isPublishMp4) {
                //隐藏按钮弹窗
                hidePopupWindow();
                //当播放视频时 隐藏学生老师窗口
                hideSurfaceview();
                mRootHolder.cb_message.setChecked(false);
                WhiteBoradConfig.getsInstance().hideWalkView(true);
                readyForPlayVideo(shareMediaPeerId, shareMediaAttrs);
            } else {
                if (TKRoomManager.getInstance().getMySelf().role == 0) {
                    mRootHolder.lin_audio_seek.setVisibility(View.VISIBLE);
                    mRootHolder.img_play_mp3.setVisibility(View.VISIBLE);
                    mRootHolder.fl_play_disk.setVisibility(View.VISIBLE);
                } else {
                    mRootHolder.lin_audio_seek.setVisibility(View.INVISIBLE);
                    mRootHolder.img_play_mp3.setVisibility(View.INVISIBLE);
                    mRootHolder.fl_play_disk.setVisibility(View.VISIBLE);
                }

                gifDrawable.start();

                if (shareMediaAttrs.containsKey("pause")) {
                    if ((boolean) shareMediaAttrs.get("pause")) {
                        gifDrawable.stop();
                    }
                }

                mRootHolder.img_voice_mp3.setImageResource(R.drawable.tk_icon_voice);
                vol = 0.5;
                mRootHolder.sek_voice_mp3.setProgress((int) (0.5 * 100));
                int da = (int) shareMediaAttrs.get("duration");
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss ");
                Date daDate = new Date(da);
                String mp3Duration = formatter.format(daDate);
                mRootHolder.txt_mp3_time.setText("00:00" + "/" + mp3Duration);
                if (mRootHolder.txt_mp3_name != null) {
                    mRootHolder.txt_mp3_name.setText((String) shareMediaAttrs.get("filename"));
                }
            }
        }
    }

    /***
     *   回放结束回调
     */
    public void onPlayBackEnd() {
        if (mPlayBackSeekPopupWindow != null) {
            mPlayBackSeekPopupWindow.onPlayBackEnd();
        }
    }

    /***
     *    回放清除所有的数据的回调
     */
    public void onPlayBackClearAll() {

        if (mRootHolder.rel_fullscreen_videoitem != null) {
            mRootHolder.rel_fullscreen_videoitem.setVisibility(View.GONE);
        }

        ToolCaseMgr.getInstance().cleanData(true);
        RoomSession.getInstance().resetRoomSession();

        if (mPlayBackSeekPopupWindow != null) {
            mPlayBackSeekPopupWindow.onPlayBackClearAll();
        }
        if (chlistAdapter != null) {
            chlistAdapter.notifyDataSetChanged();
        }

        if (videofragment != null) {
            fragmentManager = getSupportFragmentManager();
            ft = fragmentManager.beginTransaction();
            ft.remove(videofragment);
            ft.commitAllowingStateLoss();
            videofragment = null;
        }

        if (screenFragment != null) {
            screenFragment = ScreenFragment.getInstance();
            fragmentManager = getSupportFragmentManager();
            ft = fragmentManager.beginTransaction();
            if (screenFragment.isAdded()) {
                ft.remove(screenFragment);
                ft.commitAllowingStateLoss();
            }
            screenFragment = null;
        }

        if (movieFragment != null) {
            movieFragment = MovieFragment.getInstance();
            fragmentManager = getSupportFragmentManager();
            ft = fragmentManager.beginTransaction();
            if (movieFragment.isAdded()) {
                ft.remove(movieFragment);
                ft.commitAllowingStateLoss();
            }
            movieFragment = null;
        }
        mRootHolder.video_container.setVisibility(View.GONE);
    }

    /***
     *     回放播放进度回调
     * @param backTimePos   当前时间
     */
    public void onPlayBackUpdateTime(long backTimePos) {
        if (mPlayBackSeekPopupWindow != null) {
            mPlayBackSeekPopupWindow.onPlayBackUpdateTime(backTimePos);
        }
    }

    /***
     *    网络媒体播放进度，状态回调
     * @param attributesPeerId    用户 Id
     * @param streamPos       播放进度
     * @param isPlay    是否在播放
     * @param dateAttributeAttrs   流自定义扩展属性
     */
    public void onUpdateAttributeStream(String attributesPeerId, long streamPos, boolean isPlay, Map<String, Object> dateAttributeAttrs) {
        if (dateAttributeAttrs.containsKey("video") && (boolean) dateAttributeAttrs.get("video")) {
            if (videofragment == null) {
                if (wbFragment != null) {
                    WhiteBoradConfig.getsInstance().closeNewPptVideo();
                }
                isMediaMute = false;
                mediaListAdapter.setLocalfileid(dateAttributeAttrs.get("fileid"));
            } else {
                videofragment.controlMedia(dateAttributeAttrs, streamPos, isPlay);
            }
        } else {
            if (mRootHolder.sek_mp3 != null) {
                int curtime = (int) ((double) streamPos / (int) dateAttributeAttrs.get("duration") * 100);
                mRootHolder.sek_mp3.setProgress(curtime);
            }
            if (mRootHolder.img_play_mp3 != null) {
                if (!isPlay) {
                    mRootHolder.img_play_mp3.setImageResource(R.drawable.tk_play);
                    gifDrawable.start();
                } else {
                    mRootHolder.img_play_mp3.setImageResource(R.drawable.tk_pause);
                    gifDrawable.stop();
                }
            }
            if (mRootHolder.txt_mp3_time != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss ");
                Date curDate = new Date(streamPos);//获取当前时间
                Date daDate = new Date((int) dateAttributeAttrs.get("duration"));
                String strcur = formatter.format(curDate);
                String strda = formatter.format(daDate);
                mRootHolder.txt_mp3_time.setText(strcur + "/" + strda);
            }
            if (mRootHolder.txt_mp3_name != null) {
                mRootHolder.txt_mp3_name.setText((String) dateAttributeAttrs.get("filename"));
            }
        }
    }

    /***
     *    上课后取消发布本地视频
     */
    private void unPlaySelfAfterClassBegin() {
        if (RoomInfo.getInstance().getRoomType() == 0) {
            RoomUser roomUser = TKRoomManager.getInstance().getUser(TKRoomManager.getInstance().getMySelf().peerId);
            if (roomUser == null) {
                return;
            }
            if (roomUser.role == 2 && roomUser.getPublishState() == 0) {
                TKRoomManager.getInstance().unPlayVideo(TKRoomManager.getInstance().getMySelf().peerId);
                stu_in_sd.sf_video.setVisibility(View.GONE);
                stu_in_sd.lin_name_label.setVisibility(View.GONE);
                stu_in_sd.bg_video_back.setVisibility(View.VISIBLE);
                stu_in_sd.img_video_back.setVisibility(View.VISIBLE);
            }
        }
    }

    /***
     *    用户属性改变
     * @param user
     */
    private void changeUserState(final RoomUser user) {
        if (user == null) {
            return;
        }
        if (user.role == 0) {
            if (RoomSession.isClassBegin) {
                teacherItem.img_pen.setVisibility(View.VISIBLE);
                if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
                    toolsView.showTools(true);
                }
            } else {
                teacherItem.img_pen.setVisibility(View.GONE);
                if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
                    toolsView.showTools(false);
                }
            }

            if (user.properties.containsKey("primaryColor")) {
                String primaryColor = (String) user.properties.get("primaryColor");
                if (!TextUtils.isEmpty(primaryColor)) {
                    CommonUtil.changeBtimapColor(teacherItem.img_pen, primaryColor);
                    toolsView.setToolPenColor();
                }
            } else {
                SetRoomInfor.getInstance().setUserPenColor(user);
            }


            teacherItem.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user.getPublishState() != 0 && !isZoom && !RoomSession.isPublishMp4) {
                        showTeacherControlPop(user);
                    }
                }
            });
        }

        if (user.role == 2) {
            if (user.properties.containsKey("candraw")) {
                boolean candraw = Tools.isTure(user.properties.get("candraw"));
                if (candraw) {
                    stu_in_sd.img_pen.setVisibility(View.VISIBLE);//可以画图
                    if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
                        toolsView.showTools(true);
                    }

                } else {
                    stu_in_sd.img_pen.setVisibility(View.INVISIBLE);//不可以画图
                    if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
                        toolsView.showTools(false);
                    }
                }
            } else {
                stu_in_sd.img_pen.setVisibility(View.INVISIBLE);//没给过画图权限
                if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
                    toolsView.showTools(false);
                }
            }

            if (user.properties.containsKey("giftnumber")) {
                long giftnumber = user.properties.get("giftnumber") instanceof
                        Integer ? (int) user.properties.get("giftnumber") : (long) user.properties.get("giftnumber");
                stu_in_sd.txt_gift_num.setText(String.valueOf(giftnumber));
            } else {
                stu_in_sd.txt_gift_num.setText("0");
            }

            if (user.properties.containsKey("primaryColor")) {
                String primaryColor = (String) user.properties.get("primaryColor");
                if (!TextUtils.isEmpty(primaryColor)) {
                    CommonUtil.changeBtimapColor(stu_in_sd.img_pen, primaryColor);
                    toolsView.setToolPenColor();
                }
            } else {
                SetRoomInfor.getInstance().setUserPenColor(user);
            }

            stu_in_sd.rel_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user.getPublishState() != 0 && !isZoom && !RoomSession.isPublishMp4) {
                        showStudentControlPop(user);
                    }
                }
            });
        }

        if (user.properties.containsKey("isInBackGround")) {
            boolean isinback = Tools.isTure(user.properties.get("isInBackGround"));
            setBackgroundOrReception(isinback, user);
        }
        // 修复学生按home键后，老师点击学生下台时，学生仍然显示按home键了
        if (user.publishState == 0) {
            setBackgroundOrReception(false, user);
        }
    }

    boolean is_show_student_window = true;

    private void showStudentControlPop(final RoomUser user) {

        if (!is_show_student_window) {
            is_show_student_window = true;
            return;
        }

        if (studentPopupWindow != null && studentPopupWindow.isShowing()) {
            return;
        }

        if (!RoomSession.isClassBegin && TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
            return;
        }

        if (!(TKRoomManager.getInstance().getMySelf().role == 0) &&
                !user.peerId.endsWith(TKRoomManager.getInstance().getMySelf().peerId)) {
            return;
        }
        if (user.peerId.endsWith(TKRoomManager.getInstance().getMySelf().peerId)
                && !RoomControler.isAllowStudentControlAV()) {
            return;
        }
        if (!RoomSession.isClassBegin) {
            if (!RoomControler.isReleasedBeforeClass()) {
                return;
            }
        }
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tk_popup_student_control_one_to_one, null);

        LinearLayout lin_candraw = (LinearLayout) contentView.findViewById(R.id.lin_candraw);
        LinearLayout lin_audio = (LinearLayout) contentView.findViewById(R.id.lin_audio);
        LinearLayout lin_gift = (LinearLayout) contentView.findViewById(R.id.lin_gift);
        LinearLayout lin_change = (LinearLayout) contentView.findViewById(R.id.lin_change);

        final ImageView img_candraw = (ImageView) contentView.findViewById(R.id.img_candraw);
        final ImageView img_audio = (ImageView) contentView.findViewById(R.id.img_audio);
        final TextView txt_candraw = (TextView) contentView.findViewById(R.id.txt_candraw);
        final TextView txt_audio = (TextView) contentView.findViewById(R.id.txt_audio);
        ImageView right_arr = (ImageView) contentView.findViewById(R.id.right_arr);

        if (TKRoomManager.getInstance().getMySelf().role == 2) {
            lin_candraw.setVisibility(View.GONE);
        }

        LinearLayout lin_video_control = (LinearLayout) contentView.findViewById(R.id.lin_video_control);
        final ImageView img_video_control = (ImageView) contentView.findViewById(R.id.img_camera);
        final TextView txt_video = (TextView) contentView.findViewById(R.id.txt_camera);

        if (RoomSession.isOnliyAudioRoom) {
            lin_video_control.setVisibility(View.GONE);
        } else {
            lin_video_control.setVisibility(View.VISIBLE);
        }

        if (user.role == 1) {
            lin_gift.setVisibility(View.GONE);
        } else {
            if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
                lin_gift.setVisibility(View.GONE);
            } else {
                lin_gift.setVisibility(View.VISIBLE);
            }
        }

        if (user.getPublishState() == 0 || user.getPublishState() == 1 || user.getPublishState() == 4) {
            img_video_control.setImageResource(R.drawable.tk_icon_close_vidio);
            txt_video.setText(R.string.video_off);
        } else {
            img_video_control.setImageResource(R.drawable.tk_icon_open_vidio);
            txt_video.setText(R.string.video_on);
        }

        if (user.disableaudio) {
            lin_audio.setVisibility(View.GONE);
        } else {
            lin_audio.setVisibility(View.VISIBLE);
            if (user.getPublishState() == 0 || user.getPublishState() == 2 || user.getPublishState() == 4) {
                img_audio.setImageResource(R.drawable.tk_icon_close_audio);
                txt_audio.setText(R.string.close_audio);
            } else {
                img_audio.setImageResource(R.drawable.tk_icon_open_audio);
                txt_audio.setText(R.string.open_audio);
            }
        }

        if (user.properties.containsKey("candraw")) {
            boolean candraw = Tools.isTure(user.properties.get("candraw"));
            if (candraw) {
                img_candraw.setImageResource(R.drawable.tk_icon_shouquan);
                txt_candraw.setText(R.string.candraw);
            } else {
                img_candraw.setImageResource(R.drawable.tk_icon_quxiaoshouquan);
                txt_candraw.setText(R.string.no_candraw);
            }
        } else {
            img_candraw.setImageResource(R.drawable.tk_icon_quxiaoshouquan);
            txt_candraw.setText(R.string.no_candraw);
        }

        if (mLayoutState == LayoutPopupWindow.LAYOUT_DOUBLE) {
            lin_change.setVisibility(View.VISIBLE);
            studentPopupWindow = new PopupWindow(KeyBoardUtil.dp2px(OneToOneActivity.this, 80f),
                    KeyBoardUtil.dp2px(OneToOneActivity.this, 325f));
        } else {
            lin_change.setVisibility(View.GONE);
            studentPopupWindow = new PopupWindow(KeyBoardUtil.dp2px(OneToOneActivity.this, 80f),
                    KeyBoardUtil.dp2px(OneToOneActivity.this, 260f));
        }

        studentPopupWindow.setContentView(contentView);
        studentPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                is_show_student_window = !Tools.isInView(event, stu_in_sd.rel_group);
                return false;
            }
        });

        lin_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getPublishState() == 0 || user.getPublishState() == 2 || user.getPublishState() == 4) {
                    img_audio.setImageResource(R.drawable.tk_icon_open_audio);
                    txt_audio.setText(R.string.open_audio);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                            "publishstate", user.getPublishState() == 0 || user.getPublishState() == 4 ? 1 : 3);
                } else {
                    img_audio.setImageResource(R.drawable.tk_icon_close_audio);
                    txt_audio.setText(R.string.close_audio);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                            "publishstate", user.getPublishState() == 3 ? 2 : 4);

                }
                studentPopupWindow.dismiss();
            }
        });

        lin_candraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.properties.containsKey("candraw")) {
                    boolean candraw = Tools.isTure(user.properties.get("candraw"));
                    if (candraw) {   //不可以画图
                        img_candraw.setImageResource(R.drawable.tk_icon_quxiaoshouquan);
                        txt_candraw.setText(R.string.no_candraw);
                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", false);
                    } else {  //可以画图
                        img_candraw.setImageResource(R.drawable.tk_icon_shouquan);
                        txt_candraw.setText(R.string.candraw);
                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", true);
                    }
                } else {    //可以画图
                    img_candraw.setImageResource(R.drawable.tk_icon_shouquan);
                    txt_candraw.setText(R.string.candraw);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", true);
                }
                studentPopupWindow.dismiss();
            }
        });

        lin_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, RoomUser> receiverMap = new HashMap<String, RoomUser>();
                receiverMap.put(user.peerId, user);
                int size = RoomInfo.getInstance().getTrophyList().size();
                if (size == 1) {
                    // 只有一个奖杯，直接发送，不显示选择框
                    sendGiftPopUtils.sendGift(RoomInfo.getInstance().getTrophyList().get(0), receiverMap);

                } else if (size > 1) {
                    //自定义奖杯
                    if (isHaiping) {
                        sendGiftPopUtils.showSendGiftPop(mRootHolder.ll_wb_container.getWidth() / 10 * 6,
                                mRootHolder.ll_wb_container.getHeight() / 10 * 6,
                                mRootHolder.ll_wb_container, receiverMap, true, webandsufwidth);
                    } else {
                        sendGiftPopUtils.showSendGiftPop(mRootHolder.ll_wb_container.getWidth() / 10 * 6,
                                mRootHolder.ll_wb_container.getHeight() / 10 * 6,
                                mRootHolder.ll_wb_container, receiverMap, false, webandsufwidth);
                    }
                } else {
                    //默认奖杯
                    RoomOperation.getInstance().sendGift(receiverMap, null, OneToOneActivity.this);
                }
                studentPopupWindow.dismiss();
            }
        });

        lin_video_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //当全音频时不开启视频
                if (RoomSession.isOnliyAudioRoom) {
                    return;
                }
                if (user.getPublishState() == 0 || user.getPublishState() == 1 || user.getPublishState() == 4) {
                    img_video_control.setImageResource(R.drawable.tk_icon_open_vidio);
                    txt_video.setText(R.string.video_on);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                            "publishstate", user.getPublishState() == 0 || user.getPublishState() == 4 ? 2 : 3);
                } else {
                    img_video_control.setImageResource(R.drawable.tk_icon_close_vidio);
                    txt_video.setText(R.string.video_off);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all",
                            "publishstate", user.getPublishState() == 2 ? 4 : 1);

                }
                studentPopupWindow.dismiss();
            }
        });

        lin_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重置双师右上角小视频状态 切换布局和切换视频时重置
                resetDoubleSmallVideo();
                //双师布局 交换视频框
                doubleVideoChange();
                studentPopupWindow.dismiss();
            }
        });

        studentPopupWindow.setFocusable(false);
        studentPopupWindow.setOutsideTouchable(true);
        studentPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        if (RoomInfo.getInstance().getRoomType() == 0) {
            if (mLayoutState == LayoutPopupWindow.LAYOUT_VIDEO) {
                right_arr.setVisibility(View.GONE);
                studentPopupWindow.showAsDropDown(stu_in_sd.parent, stu_in_sd.parent.getWidth() / 2 - studentPopupWindow.getWidth() / 2,
                        -stu_in_sd.parent.getHeight() / 2 - studentPopupWindow.getHeight() / 2);
            } else {
                right_arr.setVisibility(View.VISIBLE);
                studentPopupWindow.showAsDropDown(stu_in_sd.parent, -(studentPopupWindow.getWidth()),
                        -(stu_in_sd.parent.getMeasuredHeight() + studentPopupWindow.getHeight()) / 2,
                        Gravity.CENTER_VERTICAL);
            }
        }
    }

    @Override
    public void onResult(final int index, final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (RoomSession.chatList.size() > index) {
                    RoomSession.chatList.get(index).setTrans(true);
                    RoomSession.chatList.get(index).setTrans(result);
                    View view = mRootHolder.lv_chat_list.getChildAt(index - mRootHolder.lv_chat_list.getFirstVisiblePosition());
                    HttpTextView txt_ch_msg = (HttpTextView) view.findViewById(R.id.txt_ch_msg);
                    ImageView img_translation = (ImageView) view.findViewById(R.id.img_translation);
                    TextView txt_eng_msg = (TextView) view.findViewById(R.id.txt_eng_msg);
                    View viewDivider = (View) view.findViewById(R.id.view);
                    ChatData ch = RoomSession.chatList.get(index);
                    chlistAdapter.setTranslation(ch, index, txt_ch_msg, txt_eng_msg, img_translation, viewDivider);
                }
            }
        });
    }

    /**
     * 上传图片，选择相机
     */
    @Override
    public void cameraClickListener() {
        cameraClickListener(TKBaseActivity.KEJIAN_SELECT_IMAGE);
    }

    /**
     * 上传图片，选择相册
     */
    @Override
    public void photoClickListener() {
        photoClickListener(TKBaseActivity.KEJIAN_SELECT_IMAGE);
    }

    /**
     * 上传图片，选择相机
     */
    public void cameraClickListener(int type) {
        mSelectImageType = type;
        if (!isPauseLocalVideo) {
            TKRoomManager.getInstance().pauseLocalCamera();
            isPauseLocalVideo = !isPauseLocalVideo;
        }
        isOpenCamera = true;
        isBackApp = true;
        PhotoUtils.openCamera(OneToOneActivity.this);
    }

    /**
     * 上传图片，选择相册
     */
    @Override
    public void photoClickListener(int type) {
        mSelectImageType = type;
        isBackApp = true;
        PhotoUtils.openAlbum(OneToOneActivity.this);
    }

    @Override
    public void setUserVisibleHint() {
        ScreenScale.scaleView(view, "OneToOneActivity  ----    onCreate");
        //创建侧边工具条及 底部翻页按钮
        crateToolsPage(view);
    }

    @Override
    public void toSwitch(int layoutState) {
        if (mLayoutState == layoutState) {
            return;
        }
        //重置双师右上角小视频状态 切换布局和切换视频时重置
        resetDoubleSmallVideo();
        mLayoutState = layoutState;
        mRootHolder.lin_menu.removeAllViews();
        mRootHolder.lin_menu.addView(teacherItem.parent);
        mRootHolder.lin_menu.addView(stu_in_sd.parent);

        if (TKRoomManager.getInstance().getMySelf().role == 0) {
            if (LayoutPopupWindow.LAYOUT_VIDEO == layoutState) {
                mRootHolder.cb_tool_case.setVisibility(View.GONE);
                mRootHolder.cb_file_person_media_list.setVisibility(View.GONE);
            } else {
                if (RoomSession.isClassBegin) {
                    mRootHolder.cb_tool_case.setVisibility(View.VISIBLE);
                }
                mRootHolder.cb_file_person_media_list.setVisibility(View.VISIBLE);
            }
        } else if (TKRoomManager.getInstance().getMySelf().role == 2) {
            if (TKRoomManager.getInstance().getMySelf().canDraw && !(mLayoutState == LayoutPopupWindow.LAYOUT_VIDEO)) {
                mRootHolder.cb_choose_photo.setVisibility(View.VISIBLE);
            } else {
                mRootHolder.cb_choose_photo.setVisibility(View.GONE);
            }
        }

        doLayout();
    }

    @Override
    public void all_send_gift() {

    }

    @Override
    public void all_recovery() {

    }

    @Override
    public void all_control_window_close() {
        mRootHolder.cb_control.setChecked(false);
        mRootHolder.cb_control.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRootHolder.cb_control.setEnabled(true);
            }
        }, 100);
    }

    class AddTime extends TimerTask {
        @Override
        public void run() {
            RoomOperation.serviceTime += 1;
            RoomOperation.localTime = RoomOperation.serviceTime - RoomOperation.classStartTime;
            if (RoomSession.isClassBegin) {
                showTime();
            }
        }
    }

    private void showTime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String H = "";
                String M = "";
                String S = "";
                long temps = RoomOperation.localTime;
                long tempm = temps / 60;
                long temph = tempm / 60;
                long sec = temps - tempm * 60;
                tempm = tempm - temph * 60;
                H = temph == 0 ? "00" : temph >= 10 ? temph + "" : "0" + temph;
                M = tempm == 0 ? "00" : tempm >= 10 ? tempm + "" : "0" + tempm;
                S = sec == 0 ? "00" : sec >= 10 ? sec + "" : "0" + sec;
                mRootHolder.txt_hour.setText(H);
                mRootHolder.txt_min.setText(M);
                mRootHolder.txt_ss.setText(S);

                try {
                    if (TKRoomManager.getInstance().getRoomProperties() != null && TKRoomManager.getInstance().getRoomProperties().optLong("endtime") - RoomOperation.serviceTime == 60 * 5) {
                        if (TKRoomManager.getInstance().getMySelf().role == 0 && RoomControler.haveTimeQuitClassroomAfterClass()) {
                            Toast.makeText(OneToOneActivity.this, getString(R.string.end_class_time), Toast.LENGTH_LONG).show();
                        }
                    }

                    if (TKRoomManager.getInstance().getRoomProperties() != null && RoomControler.haveTimeQuitClassroomAfterClass()
                            && RoomOperation.serviceTime >= TKRoomManager.getInstance().getRoomProperties().optLong("endtime")
                            && TKRoomManager.getInstance().getMySelf().role != -1) {
                        if (RoomOperation.timerAddTime != null) {
                            RoomOperation.timerAddTime.cancel();
                            RoomOperation.timerAddTime = null;
                        }
                        if (RoomSession.isClassBegin && TKRoomManager.getInstance().getMySelf().role == 0) {
                            try {
                                TKRoomManager.getInstance().delMsg("ClassBegin", "ClassBegin", "__all", new JSONObject().put("recordchat", true).toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            RoomOperation.getInstance().sendClassDissToPhp();
                        }
                        RoomClient.getInstance().onClassDismiss();
                        TKRoomManager.getInstance().leaveRoom();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /***
     *  收到信令消息回调
     * @param namePub        消息名字
     * @param pubMsgTS          消息发送时间戳
     * @param dataPub        消息携带数据
     * @param inListPub      消息是否在消息列表中
     */
    public void onRemotePubMsg(String namePub, long pubMsgTS, Object dataPub, boolean inListPub) {
        String strdata = null;
        if (dataPub != null) {
            if (dataPub instanceof String) {
                strdata = (String) dataPub;
            } else if (dataPub instanceof Map) {
                strdata = new org.json.JSONObject((Map) dataPub).toString();
            }
        }

        switch (namePub) {
            case "ClassBegin":  // 上课
                acceptSignalingClassBegin(inListPub);
                break;
            case "UpdateTime":   // 时间
                acceptSignalingUpdateTime();
                break;
            case "StreamFailure":   // 流失败
                acceptSignalingStreamFailure(strdata);
                break;
            case "EveryoneBanChat":   // 全体禁言
                acceptSignalingEveryoneBanChat(pubMsgTS, inListPub);
                break;
            case "FullScreen":    // 课件全屏同步
                acceptSignalingFullScreen(pubMsgTS, strdata, inListPub);
                break;
            case "OnlyAudioRoom":   // 纯音频
                acceptSignalingOnlyAudioRoom(inListPub);
                break;
            case "ShowPage":
                mPagesView.onHidePageNumberPop();
                break;
            case "switchLayout":   // 切换布局
                mPagesView.onHidePageNumberPop();
                acceptSwitchLayout(strdata);
                break;
        }
    }

    /**
     * 接收视频布局切换
     */
    private void acceptSwitchLayout(Object data) {
        //白板全屏时先缩回白板
        if (isZoom) {
            onWhiteBoradZoom(false);
        }

        Map<String, Object> mapdata = null;
        if (data instanceof String) {
            String str = (String) data;
            try {
                JSONObject js = new JSONObject(str);
                mapdata = Tools.toMap(js);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mapdata = (Map<String, Object>) data;
        }

        String nowLayout = (String) mapdata.get("nowLayout");
        if ("oneToOne".equals(nowLayout)) {
            // 常规布局
            LayoutPopupWindow.getInstance().clickItem(LayoutPopupWindow.LAYOUT_NORMAL, nowLayout);
            setPopupWindowVisibility(View.VISIBLE);
        } else if ("oneToOneDoubleDivision".equals(nowLayout)) {
            // 双师布局
            LayoutPopupWindow.getInstance().clickItem(LayoutPopupWindow.LAYOUT_DOUBLE, nowLayout);
            setPopupWindowVisibility(View.VISIBLE);
        } else if ("oneToOneDoubleVideo".equals(nowLayout)) {
            // 视频布局
            LayoutPopupWindow.getInstance().clickItem(LayoutPopupWindow.LAYOUT_VIDEO, nowLayout);
            setPopupWindowVisibility(View.GONE);
        }
    }

    /**
     * 接受纯音频教室切换
     *
     * @param inList
     */
    private void acceptSignalingOnlyAudioRoom(boolean inList) {
        isAudioTeaching = true;
        allActionUtils.setTeachingState(isAudioTeaching);
        SetRoomInfor.getInstance().closeVideoAfterOpenOnlyAudioRoom(inList);
        mediaListAdapter.notifyDataChangeOnlyAudioRoom();

        teacherItem.sf_video.setVisibility(View.INVISIBLE);
        teacherItem.bg_video_back.setVisibility(View.VISIBLE);
        teacherItem.img_video_back.setVisibility(View.VISIBLE);

        stu_in_sd.sf_video.setVisibility(View.INVISIBLE);
        stu_in_sd.bg_video_back.setVisibility(View.VISIBLE);
        stu_in_sd.img_video_back.setVisibility(View.VISIBLE);
    }

    /***
     *  接受到课件全屏同步信令
     * @param pubMsgTS
     * @param inListPub
     */
    private void acceptSignalingFullScreen(long pubMsgTS, Object dataPub, boolean inListPub) {
        JSONObject jsonObject = null;
        if (dataPub instanceof String) {
            String str = (String) dataPub;
            try {
                jsonObject = new JSONObject(str);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonObject == null) {
            return;
        }
        String fullScreenType = jsonObject.optString("fullScreenType");
        //{"fullScreenType":"courseware_file","needPictureInPictureSmall":true}
        //{"fullScreenType": "stream_video","needPictureInPictureSmall": true,
        // "mainPictureInPictureStreamRoleStreamRole": 0,"fullScreenStreamExtensionId":
        // "b29ace48-2916-6627-3cca-b976ef497a56"}
        if (fullScreenType.equals("courseware_file") || fullScreenType.equals("stream_media")) {//白板全屏
            if (RoomControler.isFullScreenVideo() && RoomSession.isClassBegin) {
                isZoom = true;
                hidePopupWindow();
                //关闭画笔工具弹窗
                toolsView.dismissPop();
                setWhiteBoradEnlarge(true);

                if (TKRoomManager.getInstance().getMySelf().role == 0 || TKRoomManager.getInstance().getMySelf().role == 4) {
                    if (!TextUtils.isEmpty(stu_in_sd.peerid)) {
                        controlFullScreen(TKRoomManager.getInstance().getUser(stu_in_sd.peerid));
                    } else {
                        FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                                null, true);
                    }
                } else {
                    if (!TextUtils.isEmpty(teacherItem.peerid)) {
                        controlFullScreen(TKRoomManager.getInstance().getUser(teacherItem.peerid));
                    } else {
                        FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                                null, true);
                    }
                }
            }
        }
    }

    /***
     *     接受到全体禁言信令
     * @param pubMsgTS
     *  @param inListPub
     */
    private void acceptSignalingEveryoneBanChat(long pubMsgTS, boolean inListPub) {
        ChatData ch = new ChatData();
        ch.setStystemMsg(true);
        ch.setMsgTime(System.currentTimeMillis());
        ch.setMessage(getString(R.string.chat_prompt_yes));
        ch.setTrans(false);
        ch.setChatMsgState(1);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = null;
        if (StringUtils.isEmpty(RoomVariable.path)) {
            curDate = new Date(System.currentTimeMillis());//获取当前时间
        } else {
            curDate = new Date(pubMsgTS);
        }
        String str = formatter.format(curDate);
        ch.setTime(str);
        RoomSession.chatList.add(ch);

        chlistAdapter.notifyDataSetChanged();

        if (TKRoomManager.getInstance().getMySelf().role != 0) {
            if (inListPub) {
                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                        "__all", "disablechat", true);
            }
        }
        //更新全体禁言状态
        mRootHolder.cb_choose_shut_chat.setChecked(true);
    }

    /***
     *
     * @param data  接受到失败信令
     */
    private void acceptSignalingStreamFailure(Object data) {
        Map<String, Object> mapdata = null;
        if (data instanceof String) {
            String str = (String) data;
            try {
                JSONObject js = new JSONObject(str);
                mapdata = Tools.toMap(js);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mapdata = (Map<String, Object>) data;
        }

        String stupeerid = (String) mapdata.get("studentId");

        if (TKRoomManager.getInstance().getMySelf().role == 0) {
            RoomUser u = TKRoomManager.getInstance().getUser(stupeerid);
            if (u != null) {
                if (u.properties.containsKey("passivityPublish")) {
                    int failuretype = -1;
                    if (u.properties.get("failuretype") != null) {
                        failuretype = (Integer) u.properties.get("failuretype");
                    }
                    switch (failuretype) {
                        case 1:
                            Toast.makeText(this, R.string.udp_faild, Toast.LENGTH_LONG).show();
                            break;
                        case 2:
                            Toast.makeText(this, R.string.publish_faild, Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            Toast.makeText(this, R.string.member_overload, Toast.LENGTH_LONG).show();
                            break;
                        case 4:
                            Toast.makeText(this, u.nickName + getResources().getString(R.string.select_back_hint), Toast.LENGTH_LONG).show();
                            break;
                        case 5:
                            Toast.makeText(this, R.string.udp_break, Toast.LENGTH_LONG).show();
                            break;
                    }
                }
                u.properties.remove("passivityPublish");
            }
        }
    }


    /***
     *   接受到时间更新信令
     */
    private void acceptSignalingUpdateTime() {
        if (RoomSession.isClassBegin) {
            if (RoomOperation.timerAddTime == null) {
                RoomOperation.timerAddTime = new Timer();
                RoomOperation.timerAddTime.schedule(new AddTime(), 1000, 1000);
            }
        }
    }

    /**
     * 接受到上课信令
     */
    private void acceptSignalingClassBegin(boolean inListPub) {
        //改变按钮状态
        initViewByRoomTypeAndTeacher();
        setWhiteBoradNarrow(false);

        if (!RoomControler.isReleasedBeforeClass()) {
            unPlaySelfAfterClassBegin();
        }

        if (TKRoomManager.getInstance().getMySelf().role == 0 && !inListPub) {
            LayoutPopupWindow.getInstance().setPubMsg();
        }

        //上课后是否自动发布音视频
        SetRoomInfor.getInstance().publishVideoAfterClass();

        if (TKRoomManager.getInstance().getMySelf().role == 2 || TKRoomManager.getInstance().getMySelf().role == 4) {
            if (TKRoomManager.getInstance().getMySelf().canDraw) {
                toolsView.showTools(true);
            } else {
                toolsView.showTools(false);
            }
        } else {
            //上课时设置老师画笔颜色
            SetRoomInfor.getInstance().setUserPenColor(TKRoomManager.getInstance().getMySelf());
            toolsView.showTools(true);
        }
    }

    /***
     *   收到信令消息删除回调
     * @param nameDel            消息名字
     * @param delMsgTS              消息发送时间戳
     */
    public void onRemoteDelMsg(String nameDel, long delMsgTS) {
        switch (nameDel) {
            case "ClassBegin":   // 下课
                acceptSignalingClassOver();
                break;

            case "EveryoneBanChat":  //取消全体禁言
                acceptSignalingCancelEveryoneBanChat(delMsgTS);
                break;

            case "FullScreen":  //取消全屏
                acceptSignalingCancelFullScreen();
                break;

            case "OnlyAudioRoom":  // 取消全音频教室
                mediaListAdapter.notifyDataChangeOnlyAudioRoom();
                isAudioTeaching = false;
                allActionUtils.setTeachingState(isAudioTeaching);
                break;
        }
    }

    /***
     *    接受到取消课件全屏同步
     */
    private void acceptSignalingCancelFullScreen() {
        isZoom = false;
        setWhiteBoradNarrow(false);
        if (videofragment != null) {
            videofragment.setFullscreenHide();
        } else {
            if (movieFragment != null) {
                movieFragment.setFullscreenHide();
            } else {
                //关闭画笔工具弹窗
                toolsView.dismissPop();
                MoveFullBoardUtil.getInstance().clean();
                FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                        null, false);
            }
        }
    }

    /***
     *     接受到取消全体禁言
     * @param delMsgTS
     */
    private void acceptSignalingCancelEveryoneBanChat(long delMsgTS) {
        ChatData ch = new ChatData();
        ch.setStystemMsg(true);
        ch.setMsgTime(System.currentTimeMillis());
        ch.setMessage(getString(R.string.chat_prompt_no));
        ch.setTrans(false);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = null;
        if (StringUtils.isEmpty(RoomVariable.path)) {
            curDate = new Date(System.currentTimeMillis());
        } else {
            curDate = new Date(delMsgTS);
        }
        String str = formatter.format(curDate);
        ch.setTime(str);

        RoomSession.chatList.add(ch);
        chlistAdapter.notifyDataSetChanged();
        mRootHolder.cb_choose_shut_chat.setChecked(false);
    }

    /***
     *   收到下课信令
     */
    private void acceptSignalingClassOver() {
        if (TKRoomManager.getInstance().getMySelf().role == -1) {
            finish();
            return;
        }
        //下课后改变按钮状态
        initViewByRoomTypeAndTeacher();

        if (TKRoomManager.getInstance().getMySelf().role != 0) {
            if (!RoomControler.isNotLeaveAfterClass()) {
                sendGiftPopUtils.deleteImage();
                TKRoomManager.getInstance().leaveRoom();
            }
        }
        mRootHolder.txt_hand_up.setText(R.string.raise);
        mRootHolder.txt_hour.setText("00");
        mRootHolder.txt_min.setText("00");
        mRootHolder.txt_ss.setText("00");

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!RoomControler.isNotLeaveAfterClass()) {
                    RoomSession.chatList.clear();
                    chlistAdapter.notifyDataSetChanged();
                }
            }
        }, 250);

        toolsView.showTools(false);
    }

    /***
     *    收到文本消息
     * @param chatUser     发送文本消息的用户
     */
    public void onMessageReceived(RoomUser chatUser) {
        if (!chatUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && !mRootHolder.cb_message.isChecked()) {
            setNoReadChatMessage(RoomSession.chatDataCache.size());
        } else if (mRootHolder.cb_message.isChecked()) {
            RoomSession.chatDataCache.clear();
        }

        chlistAdapter.notifyDataSetChanged();
    }

    /**
     * 设置未读消息数
     *
     * @param num
     */
    private void setNoReadChatMessage(int num) {
        if (mRootHolder.tv_no_read_message_number != null) {
            if (!isZoom) {
                mRootHolder.tv_no_read_message_number.setVisibility(View.VISIBLE);
            }
            if (num > 99) {
                mRootHolder.tv_no_read_message_number.setText("99+");
            } else {
                mRootHolder.tv_no_read_message_number.setText(num + "");
            }
        }
    }

    /***
     *  用户视频状态改变
     * @param peerId   用户 id
     * @param state    视频状态 0 取消发布 1 发布
     */
    public void onUserVideoStatus(String peerId, int state) {
        if (state > 0) {
            if (RoomControler.isOnlyShowTeachersAndVideos() && TKRoomManager.getInstance().getMySelf().role != 4) {
                if (TKRoomManager.getInstance().getMySelf().role == 2) {
                    RoomUser roomUser = TKRoomManager.getInstance().getUser(peerId);
                    if (peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) || roomUser.role == 0) {
                        doPlayVideo();
                    }
                } else {
                    doPlayVideo();
                }
            } else {
                doPlayVideo();
            }
        } else {
            doUnPlayVideo(TKRoomManager.getInstance().getUser(peerId));
        }
        changeUserState(TKRoomManager.getInstance().getUser(peerId));
        memberListAdapter.notifyDataSetChanged();
        if (studentPopupWindow != null) {
            studentPopupWindow.dismiss();
        }
    }

    /***
     *   视频播放和停止时视频框的状态
     * @param roomUser
     * @param videoItem
     */
    private void changeVideoItemState(RoomUser roomUser, VideoItem videoItem) {
        if (TKRoomManager.getInstance().getMySelf().role == roomUser.role) {
            if (TKRoomManager.getInstance().getMySelf().peerId != roomUser.peerId) {
                return;
            }
        }
        videoItem.peerid = roomUser.peerId;
        if (roomUser.getPublishState() == 0) {
            initVideoItemWidget(roomUser.role, videoItem);
        } else {
            videoItem.rel_group.setVisibility(View.VISIBLE);
            videoItem.lin_name_label.setVisibility(View.VISIBLE);
            if (roomUser.role == 0) {
                videoItem.lin_gift.setVisibility(View.INVISIBLE);
            } else {
                videoItem.lin_gift.setVisibility(View.VISIBLE);
            }
            videoItem.txt_name.setText(roomUser.nickName);
            videoItem.txt_name.setVisibility(View.VISIBLE);

            if (roomUser.disablevideo || !roomUser.hasAudio || roomUser.getPublishState() == 4 || roomUser.getPublishState() == 2) {
                videoItem.img_mic.setVisibility(View.VISIBLE);
                videoItem.volume.setVisibility(View.INVISIBLE);
                videoItem.img_mic.setImageResource(R.drawable.tk_img_mic_ban);
            } else {
                if (roomUser.getPublishState() == 1 || roomUser.getPublishState() == 3) {
                    videoItem.img_mic.setVisibility(View.VISIBLE);
                    videoItem.volume.setVisibility(View.VISIBLE);
                    videoItem.img_mic.setImageResource(R.drawable.tk_icon_sound);
                }
            }

            if (RoomSession.isOnliyAudioRoom) {
                videoItem.bg_video_back.setVisibility(View.VISIBLE);
                videoItem.img_video_back.setVisibility(View.VISIBLE);
                videoItem.sf_video.setVisibility(View.INVISIBLE);
                videoItem.img_video_back.setImageResource(R.drawable.one_2_one_audio_zw);
            } else {
                videoItem.img_video_back.setImageResource(R.drawable.one_2_one_no_camera_zw);

                if (roomUser.disablevideo || !roomUser.hasVideo) {
                    videoItem.sf_video.setVisibility(View.INVISIBLE);
                    videoItem.img_video_back.setImageResource(R.drawable.one_2_one_camera_zw);
                    videoItem.bg_video_back.setVisibility(View.VISIBLE);
                    videoItem.img_video_back.setVisibility(View.VISIBLE);
                } else {
                    if (roomUser.getPublishState() > 1 && roomUser.getPublishState() < 4 &&
                            !RoomSession.isPublishMp4 && !RoomSession.isShareFile && !RoomSession.isShareScreen) {
                        videoItem.sf_video.setVisibility(View.VISIBLE);
                        videoItem.bg_video_back.setVisibility(View.GONE);
                        TKRoomManager.getInstance().playVideo(roomUser.peerId, videoItem.sf_video,
                                RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
                    } else {
                        videoItem.re_background.setVisibility(View.INVISIBLE);
                        videoItem.sf_video.setVisibility(View.INVISIBLE);
                        videoItem.bg_video_back.setVisibility(View.VISIBLE);
                        videoItem.img_video_back.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    /**
     * 初始化视频框中控件
     *
     * @param userRole
     * @param videoItem
     */
    private void initVideoItemWidget(int userRole, VideoItem videoItem) {
        videoItem.bg_video_back.setVisibility(View.VISIBLE);
        videoItem.img_video_back.setVisibility(View.VISIBLE);
        videoItem.img_hand.setVisibility(View.INVISIBLE);
        videoItem.lin_gift.setVisibility(View.INVISIBLE);
        videoItem.img_pen.setVisibility(View.INVISIBLE);
        videoItem.txt_name.setVisibility(View.INVISIBLE);
        videoItem.sf_video.setVisibility(View.INVISIBLE);
        videoItem.img_mic.setVisibility(View.INVISIBLE);
        videoItem.volume.setVisibility(View.INVISIBLE);
        videoItem.lin_name_label.setVisibility(View.INVISIBLE);
        videoItem.re_background.setVisibility(View.INVISIBLE);
        if (userRole == 0) {
            videoItem.img_video_back.setImageResource(R.drawable.one_2_one_teacher_zw);
            videoItem.bg_video_back.setBackgroundResource(R.color.teacher_video_bg);
        } else if (userRole == 2) {
            videoItem.img_video_back.setImageResource(R.drawable.one_2_one_student_zw);
            videoItem.bg_video_back.setBackgroundResource(R.color.student_video_bg);
        }
    }

    /***
     *  改变小视频框的状态（前提：课件全屏同步）
     * @param roomUser
     */
    private void changeFullScreenState(RoomUser roomUser) {
        if (RoomSession.fullScreen && RoomControler.isFullScreenVideo() && isZoom) {
            hideSurfaceview();
            if (roomUser.getPublishState() == 0 || roomUser.getPublishState() == 1 || roomUser.getPublishState() == 4) {
                if (TKRoomManager.getInstance().getMySelf().role != 2 && roomUser.role == 2) {
                    if (RoomSession.fullScreen) {
                        FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                                roomUser, false);
                    }
                }

                if (TKRoomManager.getInstance().getMySelf().role == 2 && roomUser.role == 0) {
                    if (RoomSession.fullScreen) {
                        FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                                roomUser, false);
                    }
                }
            } else if (roomUser.getPublishState() == 2 || roomUser.getPublishState() == 3) {
                if (!roomUser.disablevideo && roomUser.hasVideo) {
                    if (TKRoomManager.getInstance().getMySelf().role == 2 && roomUser.role == 0) {
                        controlFullScreen(roomUser);
                    }
                    if (TKRoomManager.getInstance().getMySelf().role != 2 && roomUser.role == 2) {
                        controlFullScreen(roomUser);
                    }
                }
            }
        }
    }

    private void doUnPlayVideo(RoomUser roomUser) {
        if (roomUser == null) {
            return;
        }
        if (roomUser.role == 0) {
            changeVideoItemState(roomUser, teacherItem);
        } else if (roomUser.role == 2) {
            changeVideoItemState(roomUser, stu_in_sd);
        }
        changeFullScreenState(roomUser);
    }

    /***
     *   用户属性改变
     * @param propertyUser    改变属性的用户
     * @param map          改变的属性集合
     * @param fromId      改变该用户属性的用户的用户 ID
     */
    public void onUserPropertyChanged(RoomUser propertyUser, Map<String, Object> map, String fromId) {

        if (map.containsKey("publishstate")) {
            if (propertyUser.getPublishState() == 0) {
                doUnPlayAudio(propertyUser.peerId);
            } else if (propertyUser.getPublishState() == 4) {
                doPlayVideo();
            }
        }

        //插播摄像头切换画中画状态
        if (RoomSession.isClassBegin && RoomControler.isFullScreenVideo() && RoomSession.fullScreen && map.containsKey("hasvideo")) {
            boolean hasvideo = (boolean) map.get("hasvideo");
            String userid = "";
            if (TKRoomManager.getInstance().getMySelf().role == 2) {
                if (propertyUser.peerId.equals(teacherItem.peerid)) {
                    userid = propertyUser.peerId;
                }
            } else {
                if (propertyUser.peerId.equals(stu_in_sd.peerid)) {
                    userid = propertyUser.peerId;
                }
            }

            if (!userid.isEmpty()) {
                if (videofragment != null) {
                    videofragment.setFullscreenShow(userid, hasvideo);
                } else {
                    if (movieFragment != null) {
                        movieFragment.setFullscreenShow(userid, hasvideo);
                    } else {
                        FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                                propertyUser, hasvideo);
                    }
                }
            }
        }

        //禁言
        if (map.containsKey("disablechat")) {
            if (propertyUser == null) {
                return;
            }
            boolean disablechat = Tools.isTure(map.get("disablechat"));
            if (TKRoomManager.getInstance().getMySelf().peerId.equals(propertyUser.peerId)) {
                if (disablechat) {
                    mRootHolder.iv_open_input.setImageResource(R.drawable.tk_shuru_default);
                } else {
                    mRootHolder.iv_open_input.setImageResource(R.drawable.tk_shuru);
                }
            }
        }

        if (propertyUser.properties.containsKey("raisehand")) {
            boolean israisehand = Tools.isTure(TKRoomManager.getInstance().getMySelf().properties.get("raisehand"));
            if (israisehand) {
                mRootHolder.iv_hand.setVisibility(View.VISIBLE);
                mRootHolder.txt_hand_up.setText(R.string.raiseing);
                mRootHolder.txt_hand_up.setBackgroundResource(R.drawable.tk_commom_btn_handup);
                mRootHolder.txt_hand_up.setTextAppearance(OneToOneActivity.this, R.style.three_color_hands_up);
            } else {
                mRootHolder.iv_hand.setVisibility(View.INVISIBLE);
                mRootHolder.txt_hand_up.setText(R.string.raise); //同意了，或者拒绝了
                mRootHolder.txt_hand_up.setBackgroundResource(R.drawable.tk_commom_btn_xiake);
                mRootHolder.txt_hand_up.setTextAppearance(OneToOneActivity.this, R.style.three_color_hands_up);
            }
        } else {
            if (map.containsKey("raisehand")) {
                boolean israisehand = Tools.isTure(map.get("raisehand"));
                if (israisehand) {
                    mRootHolder.iv_hand.setVisibility(View.VISIBLE);
                } else {
                    mRootHolder.iv_hand.setVisibility(View.INVISIBLE);
                }
            }
            mRootHolder.txt_hand_up.setText(R.string.raise); //还没举手
            mRootHolder.txt_hand_up.setBackgroundResource(R.drawable.tk_commom_btn_xiake);
            mRootHolder.txt_hand_up.setTextAppearance(OneToOneActivity.this, R.style.three_color_hands_up);
        }

        if (map.containsKey("giftnumber") && !propertyUser.peerId.equals(fromId)) {
            ShowTrophyUtil.showOneTrophyIntention(stu_in_sd, map,
                    this, mRootHolder.rl_web, mRootHolder.rel_tool_bar);
        }

        if (propertyUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
            if (map.containsKey("candraw")) {
                if (Tools.isTure(map.get("candraw"))) {
                    if (TKRoomManager.getInstance().getMySelf().role != 4 && !(mLayoutState == LayoutPopupWindow.LAYOUT_VIDEO)) {
                        mRootHolder.cb_choose_photo.setVisibility(View.VISIBLE);
                    }
                    toolsView.showTools(true);
                } else {
                    mRootHolder.cb_choose_photo.setVisibility(View.GONE);
                    toolsView.showTools(false);
                }
            }
        }

        if (propertyUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && map.containsKey("volume")) {
            Number n_volume = (Number) map.get("volume");
            int int_volume = n_volume.intValue();
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, int_volume, 0);
        }
        changeUserState(propertyUser);
        memberListAdapter.notifyDataSetChanged();
    }

    private void setBackgroundOrReception(boolean b, RoomUser RoomUser) {
        if (RoomUser != null && RoomUser.role == 0) {
            if (b) {
                teacherItem.re_background.setVisibility(View.VISIBLE);
                teacherItem.tv_home.setText(R.string.tea_background);
            } else {
                teacherItem.re_background.setVisibility(View.GONE);
            }
            //修改画中画情况下
            if (videofragment != null) {
                videofragment.changeInBackground(b, 0);
            } else if (movieFragment != null) {
                movieFragment.changeInBackground(b, 0);
            } else if (isZoom && RoomControler.isFullScreenVideo() && RoomSession.fullScreen) {
                FullScreenControlUtil.changeInBackground(OneToOneActivity.this, mRootHolder, b, 0);
            }
        }

        if (stu_in_sd.parent != null) {
            if (stu_in_sd.peerid != null) {
                if (!stu_in_sd.peerid.isEmpty()) {
                    if (stu_in_sd.peerid.equals(RoomUser.peerId)) {
                        if (b) {
                            stu_in_sd.re_background.setVisibility(View.VISIBLE);
                            stu_in_sd.tv_home.setText(R.string.stu_background);
                        } else {
                            stu_in_sd.re_background.setVisibility(View.GONE);
                        }
                        //修改画中画情况下
                        if (videofragment != null) {
                            videofragment.changeInBackground(b, 2);
                        } else if (movieFragment != null) {
                            movieFragment.changeInBackground(b, 2);
                        } else if (isZoom && RoomControler.isFullScreenVideo() && RoomSession.fullScreen) {
                            FullScreenControlUtil.changeInBackground(OneToOneActivity.this, mRootHolder, b, 2);
                        }
                    }
                }
            }
        }
    }

    /***
     *     其他用户离开房间
     * @param roomUser      离开的用户
     */
    public void onUserLeft(RoomUser roomUser) {
        chlistAdapter.notifyDataSetChanged();
        memberListAdapter.notifyDataSetChanged();
        memberListPopupWindowUtils.setTiteNumber(RoomSession.memberList.size());

        if (roomUser.role == 0) {//老师
            if (roomUser.peerId.equals(teacherItem.peerid)) {
                initVideoItemWidget(roomUser.role, teacherItem);
            }
        } else if (roomUser.role == 2) {//学生
            if (roomUser.peerId.equals(stu_in_sd.peerid)) {
                initVideoItemWidget(roomUser.role, stu_in_sd);
            }
        }

        if (isZoom && RoomControler.isFullScreenVideo() && RoomSession.isClassBegin) {
            if (roomUser.role == 0 || roomUser.role == 2) {
                if (videofragment != null) {
                    videofragment.setFullscreenHide();
                } else {
                    if (movieFragment != null) {
                        movieFragment.setFullscreenHide();
                    } else {
                        FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                                null, true);
                    }
                }
            }
        }
    }

    /***
     *    其他用户进入房间的回调
     * @param roomUser          进入的用户
     * @param inList            是否在我之前进入房间，true—之前，false—之后
     */
    public void onUserJoined(RoomUser roomUser, boolean inList) {
        chlistAdapter.notifyDataSetChanged();
        memberListAdapter.notifyDataSetChanged();
        memberListPopupWindowUtils.setTiteNumber(RoomSession.memberList.size());
    }

    /***
     *     警告回调
     * @param onWarning   1751 摄像头打开   1752 摄像头关闭
     */
    public void onWarning(int onWarning) {
        if (10001 == onWarning) {
            if (isOpenCamera) {
                if (10001 == onWarning) {
                    if (isOpenCamera) {
                        PhotoUtils.openCamera(OneToOneActivity.this);
                    }
                }
            }
        }
    }

    /***
     *    进入房间成功回调
     */
    @Override
    public void onRoomJoin() {

        TKRoomManager.getInstance().setInBackGround(false);
        mRootHolder.re_loading.setVisibility(View.GONE);

        if (TKRoomManager.getInstance().getMySelf().role == -1) {
            //回放进度条的显示隐藏工具类,初始化
            if (mRootHolder.vs_play_back != null && !playBackIsInflate) {
                View inflate = mRootHolder.vs_play_back.inflate();
                playBackIsInflate = true;
                mRootHolder.re_play_back = inflate.findViewById(R.id.re_play_back);
                //******   回放titleBar
                mRootHolder.rel_play_back_bar = inflate.findViewById(R.id.re_back_bar);
                if (tool_bar_param != null) {
                    mRootHolder.rel_play_back_bar.setLayoutParams(tool_bar_param);
                }
                //返回
                mRootHolder.img_play_back_out = inflate.findViewById(R.id.img_play_back_out);
                //回放媒体名字
                mRootHolder.tv_back_name = inflate.findViewById(R.id.tv_back_name);
            }
            mRootHolder.img_play_back_out.setOnClickListener(this);
            mRootHolder.tv_back_name.setText(RoomInfo.getInstance().getRoomName());

            if (playbackControlUtils == null) {
                playbackControlUtils = new PlaybackControlUtils(this, new PlaybackControlUtils.DismissPopupWindowListener() {
                    @Override
                    public void dismissPopupWindow() {
                        if (mPlayBackSeekPopupWindow != null) {
                            mPlayBackSeekPopupWindow.dismiss();
                        }
                    }
                });
            }
            if (mPlayBackSeekPopupWindow == null) {
                mPlayBackSeekPopupWindow = new PlayBackSeekPopupWindow(OneToOneActivity.this, mRootHolder.re_play_back);
            }
            mRootHolder.tv_back_name.post(new Runnable() {
                @Override
                public void run() {
                    mPlayBackSeekPopupWindow.startTimer(playbackControlUtils);
                }
            });
            ToolCaseMgr.getInstance().setPlayBackSeekPopupWindow(mPlayBackSeekPopupWindow);

            mRootHolder.re_play_back.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                        if (Tools.isInView(event, mRootHolder.cb_message)) {
                            if (mRootHolder.cb_message.isChecked()) {
                                mRootHolder.cb_message.setChecked(false);
                            } else {
                                mRootHolder.cb_message.setChecked(true);
                            }
                        } else {
                            if (!playbackControlUtils.isShowing) {
                                playbackControlUtils.startHideTimer(mPlayBackSeekPopupWindow.rel_play_back);
                                mRootHolder.re_play_back.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPlayBackSeekPopupWindow.showPopupWindow();
                                    }
                                });
                            }
                        }
                    }
                    return true;
                }
            });
        }

        setDefaultVideoSize();

        //举手逻辑
        RoomOperation.getInstance().handAction(mRootHolder.txt_hand_up);
        //检测摄像头
        RoomCheck.getInstance().checkCamera(this);
        //检测麦克风
        RoomCheck.getInstance().checkMicrophone(this);
        setCheckBoxEnabled();
        //弹窗聊天列表
        showChatPopupWindow();
        wifiStatusPop.setRoomId(RoomInfo.getInstance().getSerial());

        int roomLayoutState = RoomInfo.getInstance().getRoomlayout();
        if (roomLayoutState == 2) {
            // 双师布局
            LayoutPopupWindow.getInstance().clickItem(LayoutPopupWindow.LAYOUT_DOUBLE, "oneToOneDoubleDivision");
        } else if (roomLayoutState == 3) {
            // 视频布局
            LayoutPopupWindow.getInstance().clickItem(LayoutPopupWindow.LAYOUT_VIDEO, "oneToOneDoubleVideo");
        } else {
            // 常规布局
            LayoutPopupWindow.getInstance().clickItem(LayoutPopupWindow.LAYOUT_NORMAL, "oneToOne");
        }

        initViewByRoomTypeAndTeacher();
    }

    /***
     *  设置View的大小
     */
    private void setViewSize() {
        //顶部工具栏
        tool_bar_param = (RelativeLayout.LayoutParams) mRootHolder.rel_tool_bar.getLayoutParams();
        tool_bar_param.width = LinearLayout.LayoutParams.MATCH_PARENT;
        tool_bar_param.height = toolBarHeight;
        mRootHolder.rel_tool_bar.setLayoutParams(tool_bar_param);
        if (mRootHolder.rel_play_back_bar != null) {
            mRootHolder.rel_play_back_bar.setLayoutParams(tool_bar_param);
        }

        if (isHaiping) {
            /*RelativeLayout.LayoutParams ll_par = (RelativeLayout.LayoutParams) mRootHolder.ll_top.getLayoutParams();
            ll_par.leftMargin = ScreenScale.getStatusBarHeight();
            mRootHolder.ll_top.setLayoutParams(ll_par);*/

            RelativeLayout.LayoutParams re_top_right_par = (RelativeLayout.LayoutParams) mRootHolder.re_top_right.getLayoutParams();
            re_top_right_par.rightMargin = ScreenScale.getStatusBarHeight();
            mRootHolder.re_top_right.setLayoutParams(re_top_right_par);
        }

        //MP3播放图标layout
        FrameLayout.LayoutParams imgDiskParam = (FrameLayout.LayoutParams) mRootHolder.fl_play_disk.getLayoutParams();
        //MP3播放进度条界面
        FrameLayout.LayoutParams audioParam = (FrameLayout.LayoutParams) mRootHolder.lin_audio_seek.getLayoutParams();
        audioParam.width = (wid - ScreenScale.getStatusBarHeight()) * 4 / 11;
        audioParam.height = hid * 29 / 194;

        imgDiskParam.height = audioParam.height * 24 / 25;
        imgDiskParam.width = imgDiskParam.height;

        mRootHolder.lin_audio_seek.setPadding(imgDiskParam.height, 0, 0, 0);
        mRootHolder.lin_audio_seek.setLayoutParams(audioParam);
        mRootHolder.fl_play_disk.setLayoutParams(imgDiskParam);

        //MP3播放 聊天 控制层布局
        RelativeLayout.LayoutParams relControlLayout = (RelativeLayout.LayoutParams) mRootHolder.rel_control_layout.getLayoutParams();
        relControlLayout.leftMargin = chatControlLeftMargin;
        mRootHolder.rel_control_layout.setLayoutParams(relControlLayout);

        //聊天界面大小
        ViewGroup.LayoutParams chatlistparams = mRootHolder.lin_bottom_chat.getLayoutParams();
        chatlistparams.width = wid * 4 / 13;
        chatlistparams.height = hid * 3 / 5 + mRootHolder.rl_message.getMeasuredHeight();
        mRootHolder.lin_bottom_chat.setLayoutParams(chatlistparams);

        RelativeLayout.LayoutParams chatLayoutParams = (RelativeLayout.LayoutParams) mRootHolder.lv_chat_list.getLayoutParams();
        chatLayoutParams.width = mScreenValueWidth * 4 / 13;
        chatLayoutParams.height = hid * 3 / 5;
        mRootHolder.lv_chat_list.setLayoutParams(chatLayoutParams);

        //白板全屏右下角界面大小
        fullscreen_video_param = new RelativeLayout.LayoutParams(0, 0);
        fullscreen_video_param.width = (wid - 8 * 8) / 7;
        fullscreen_video_param.height = (wid - 8 * 8) / 7 * hid_ratio / wid_ratio + 16;
        fullscreen_video_param.rightMargin = KeyBoardUtil.dp2px(OneToOneActivity.this, 10);
        fullscreen_video_param.bottomMargin = KeyBoardUtil.dp2px(OneToOneActivity.this, 10);

        if (mRootHolder.fullscreen_inback != null) {
            mRootHolder.fullscreen_inback.setLayoutParams(fullscreen_video_param);
        }

        if (mRootHolder.fullscreen_sf_video != null) {
            mRootHolder.fullscreen_sf_video.setLayoutParams(fullscreen_video_param);
        }

        if (mRootHolder.fullscreen_bg_video_back != null) {
            mRootHolder.fullscreen_bg_video_back.setLayoutParams(fullscreen_video_param);
        }

        if (mRootHolder.fullscreen_img_video_back != null) {
            mRootHolder.fullscreen_img_video_back.setLayoutParams(fullscreen_video_param);
        }
    }

    private void setCheckBoxEnabled() {
        mRootHolder.cb_choose_photo.setEnabled(true);
        mRootHolder.cb_member_list.setEnabled(true);
        mRootHolder.cb_file_person_media_list.setEnabled(true);
        mRootHolder.cb_tool_case.setEnabled(true);
        mRootHolder.cb_tool_layout.setEnabled(true);
        mRootHolder.cb_control.setEnabled(true);
        mRootHolder.cb_message.setEnabled(true);
        mRootHolder.cb_choose_shut_chat.setEnabled(true);
    }

    /**
     * 展示聊天列表弹窗
     */
    private void showChatPopupWindow() {
        if (mRootHolder.lv_chat_list.getVisibility() == View.VISIBLE) {
            return;
        }
        mRootHolder.lv_chat_list.setVisibility(View.VISIBLE);
        mRootHolder.lv_chat_list.setEnabled(true);
        mRootHolder.cb_message.setChecked(true);
        if (TKRoomManager.getInstance().getMySelf().role == 0) {//老师
            //留言板   聊天输入框 全体禁言
            mRootHolder.cb_message.setVisibility(View.VISIBLE);
            mRootHolder.iv_open_input.setVisibility(View.VISIBLE);
            mRootHolder.cb_choose_shut_chat.setVisibility(View.VISIBLE);

        } else if (TKRoomManager.getInstance().getMySelf().role == 2) {//学生
            mRootHolder.cb_message.setVisibility(View.VISIBLE);
            mRootHolder.iv_open_input.setVisibility(View.VISIBLE);
            mRootHolder.cb_choose_shut_chat.setVisibility(View.INVISIBLE);
        } else if (TKRoomManager.getInstance().getMySelf().role == 4) {  //巡课
            mRootHolder.cb_message.setVisibility(View.VISIBLE);
            mRootHolder.iv_open_input.setVisibility(View.INVISIBLE);
            mRootHolder.cb_choose_shut_chat.setVisibility(View.INVISIBLE);
            mRootHolder.lin_wifi.setVisibility(View.GONE);//巡课隐藏网络状态.
        } else if (TKRoomManager.getInstance().getMySelf().role == -1) {  //回放
            mRootHolder.cb_message.setVisibility(View.VISIBLE);
            mRootHolder.iv_open_input.setVisibility(View.VISIBLE);
            mRootHolder.cb_choose_shut_chat.setVisibility(View.INVISIBLE);
        }
        if (mRootHolder.lv_chat_list.getVisibility() == View.VISIBLE) {
            mRootHolder.lv_chat_list.startAnimation((AlphaAnimation) AnimationUtils.loadAnimation(OneToOneActivity.this, R.anim.tk_chatpopshow_anim));
        }
        if (mRootHolder.iv_open_input.getVisibility() == View.VISIBLE) {
            mRootHolder.iv_open_input.startAnimation((AnimationSet)
                    AnimationUtils.loadAnimation(OneToOneActivity.this, R.anim.tk_chat_button_show_anim));
        }
        if (mRootHolder.cb_choose_shut_chat.getVisibility() == View.VISIBLE) {
            mRootHolder.cb_choose_shut_chat.startAnimation((AnimationSet)
                    AnimationUtils.loadAnimation(OneToOneActivity.this, R.anim.tk_chat_button_show_anim));
        }
    }

    /***
     *    离开房间回调
     */
    public void onRoomLeave() {

        removeVideoFragment();
        removeScreenFragment();
        removeMovieFragment();

        mediaListAdapter.setLocalfileid(-1);

        if (gifDrawable != null) {
            gifDrawable.stop();
        }

        if (mRootHolder.img_disk != null) {
            mRootHolder.img_disk.clearAnimation();
        }

        TKRoomManager.getInstance().destroy();
        clear();
        finish();
    }

    /***
     *    网络连接丢失
     */
    public void onConnectionLost() {

        mRootHolder.re_loading.setVisibility(View.VISIBLE);
        mRootHolder.tv_load.setText(getString(R.string.connected));

        UploadPhotoPopupWindowUtils.getInstance().setDismiss();

        mediaListAdapter.setLocalfileid(-1);

        removeVideoFragment();
        removeScreenFragment();
        removeMovieFragment();
        changeVideoState();
        mRootHolder.video_container.setVisibility(View.GONE);

        if (!RoomSession.isPublishMp3) {
            if (mRootHolder.img_disk != null) {
                mRootHolder.img_disk.clearAnimation();
                if (gifDrawable != null) {
                    gifDrawable.stop();
                }
            }
            mRootHolder.lin_audio_seek.setVisibility(View.INVISIBLE);
            mRootHolder.fl_play_disk.setVisibility(View.INVISIBLE);
        }
        if (mPlayBackSeekPopupWindow != null) {
            mPlayBackSeekPopupWindow.connectLost();
        }
        ToolCaseMgr.getInstance().cleanData(true);
        if (mPagesView != null) {
            mPagesView.resetLargeOrSmallView();
        }
    }

    /***
     *    错误信息回调
     * @param errorCode    错误码
     * @param errMsg        错误信息
     */
    public void onError(int errorCode, String errMsg) {
        if (errorCode == 10004) {  //UDP连接不同
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.showAlertDialog(OneToOneActivity.this,
                            getString(R.string.udp_alert));
                }
            });

        } else if (10002 == errorCode) {
            mRootHolder.re_loading.setVisibility(View.GONE);
            removeVideoFragment();
            removeScreenFragment();
            removeMovieFragment();
            mRootHolder.video_container.setVisibility(View.GONE);
            mediaListAdapter.setLocalfileid(-1);
            clear();
            finish();
        } else if (errorCode == 10005) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.showAlertDialog(OneToOneActivity.this, getString(R.string.fire_wall_alert));
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "udpstate", 2);
                }
            });
        }
    }

    private void removeScreenFragment() {
        //显示popupwindow
        setPopupWindowVisibility(View.VISIBLE);
        screenFragment = ScreenFragment.getInstance();
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        if (screenFragment.isAdded()) {
            ft.remove(screenFragment);
            ft.commitAllowingStateLoss();
        }
        screenFragment = null;
    }

    private void removeMovieFragment() {
        //显示popupwindow
        setPopupWindowVisibility(View.VISIBLE);
        movieFragment = MovieFragment.getInstance();
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        if (movieFragment.isAdded()) {
            ft.remove(movieFragment);
            ft.commitAllowingStateLoss();
        }
        movieFragment = null;
    }

    private void clear() {
        TKRoomManager.getInstance().registerRoomObserver(null);
        teacherItem.sf_video.release();
        stu_in_sd.sf_video.release();
        RoomDeviceSet.closeSpeaker(this);
    }

    @Override
    public void onVideoStatsReport(String videoId, TkVideoStatsReport tkVideoStatsReport) {
        if (TKRoomManager.getInstance().getMySelf().peerId.equals(videoId)) {
            if (tkVideoStatsReport.video_net_level <= 2) {
                mRootHolder.img_wifi.setImageResource(R.drawable.tk_wifi_you);
                mRootHolder.txt_wifi_status.setText(R.string.wifi_you);
                mRootHolder.txt_wifi_status.setTextColor(getResources().getColor(R.color.wifi_you));
                if (wifiStatusPop.wifiStatusPop.isShowing()) {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_you_sanjiao_up);
                } else {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_you_sanjiao_down);
                }
                wifiStatusPop.setWifiStatus(1);
            } else if (tkVideoStatsReport.video_net_level <= 4) {
                mRootHolder.img_wifi.setImageResource(R.drawable.tk_wifi_zhong);
                mRootHolder.txt_wifi_status.setText(R.string.wifi_zhong);
                mRootHolder.txt_wifi_status.setTextColor(getResources().getColor(R.color.wifi_zhong));
                if (wifiStatusPop.wifiStatusPop.isShowing()) {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_zhong_sanjiao_up);
                } else {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_zhong_sanjiao_down);
                }
                wifiStatusPop.setWifiStatus(2);
            } else if (tkVideoStatsReport.video_net_level <= 6) {
                mRootHolder.img_wifi.setImageResource(R.drawable.tk_wifi_cha);
                mRootHolder.txt_wifi_status.setText(R.string.wifi_cha);
                mRootHolder.txt_wifi_status.setTextColor(getResources().getColor(R.color.wifi_cha));
                if (wifiStatusPop.wifiStatusPop.isShowing()) {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_cha_sanjiao_up);
                } else {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_cha_sanjiao_down);
                }
                wifiStatusPop.setWifiStatus(3);
            }
            if (tkVideoStatsReport.totalPackets > 0) {
                wifiStatusPop.setPageloseAndDelay(String.valueOf(tkVideoStatsReport.packetsLost / tkVideoStatsReport.totalPackets), String.valueOf(tkVideoStatsReport.currentDelay));
            }
        }
    }

    /**
     * 在进入教室之前的信令发送完毕后会调用
     * 用于教室的初始化
     *
     * @param infoCode
     * @param message
     */
    @Override
    public void onInfo(int infoCode, String message) {
        super.onInfo(infoCode, message);
        if (infoCode == 1506) {
            if (!RoomSession.isClassBegin) {
                playSelfBeforeClassBegin();
            }
        }
    }

}
