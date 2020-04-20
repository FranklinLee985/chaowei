package com.eduhdsdk.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.bean.ShowPageBean;
import com.classroomsdk.common.RoomControler;
import com.classroomsdk.interfaces.ShowPageInterface;
import com.classroomsdk.manage.SharePadMgr;
import com.classroomsdk.manage.WBSession;
import com.classroomsdk.manage.WhiteBoradConfig;
import com.classroomsdk.manage.WhiteBoradManager;
import com.classroomsdk.utils.PPTRemarkUtil;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.toolcase.PageNumsPopupWindow;
import com.eduhdsdk.ui.holder.TKBaseRootHolder;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;
import static android.widget.RelativeLayout.CENTER_HORIZONTAL;

/***
 *   课件翻頁布局
 */
public class PagesView implements PPTRemarkUtil.ChangePPtRemarkIF {

    private Context mContext;
    private ShowPageBean mShowPageBean;
    //当前显示课件的id
    private String mFileId = "";
    private boolean isFullScreen = true;

    //底部page
    private PageNumsPopupWindow mPageNumsPopupWindow;

    private int viewWidth;
    private int viewHeight;

    //############备注##########
    //ppt标注是否显示
    private boolean isShowRemark = true;

    private TKBaseRootHolder mRootHolder;
    private View mview;

    private boolean isPageViewChange = false;
    private boolean isRemarkViewChange = false;

    public PagesView(Context context, TKBaseRootHolder mTKBaseRootHolder, View view) {
        this.mContext = context;
        this.mRootHolder = mTKBaseRootHolder;
        this.mview = view;
        initPop();
        mRootHolder.tv_remark.setMovementMethod(new ScrollingMovementMethod());
    }

    /**
     * 备注按钮显示与否
     *
     * @param isShow
     */
    public void setiVRemark(boolean isShow) {
        if (mRootHolder.iv_remark != null) {
            if (isShow) {
                mRootHolder.iv_remark.setVisibility(View.VISIBLE);
            } else {
                mRootHolder.iv_remark.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 全屏
     *
     * @param fullScreen
     */
    public void setfull(boolean fullScreen) {
        isFullScreen = fullScreen;
        if (fullScreen) {
            mRootHolder.iv_full.setImageResource(R.drawable.tk_icon_exit_screen_default);
        } else {
            mRootHolder.iv_full.setImageResource(R.drawable.tk_wb_page_icon_full_screen_default);
        }
    }

    /**
     * 翻页
     */
    ShowPageInterface showPageInterface = new ShowPageInterface() {
        @Override
        public synchronized void setShowPageBean(final ShowPageBean showPageBean) {
            if (showPageBean != null) {
                if (mShowPageBean == null) {
                    mShowPageBean = new ShowPageBean();
                }

                mShowPageBean = showPageBean.clone();
            }
        }

        @Override
        public synchronized void SetShowPage(final ShowPageBean showPageBean) {
            if (showPageBean != null) {

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mFileId == null) {
                            //重置放大缩小的比例
                            resetLargeOrSmallView();
                            mFileId = showPageBean.getFiledata().getFileid();
                        } else if (!mFileId.equals(showPageBean.getFiledata().getFileid())) {
                            //重置放大缩小的比例
                            resetLargeOrSmallView();
                            mFileId = showPageBean.getFiledata().getFileid();
                        }

                        if (mShowPageBean == null) {
                            mShowPageBean = new ShowPageBean();
                        }

                        mShowPageBean = showPageBean.clone();
                        ShowPageBean.FiledataBean filedataBean = mShowPageBean.getFiledata();
                        //当前页
                        int currpage = filedataBean.getCurrpage();
                        //总页
                        int pagenum = filedataBean.getPagenum();
                        //当前帧
                        int pptstep = filedataBean.getPptstep();
                        //总帧
                        int steptotal = filedataBean.getSteptotal();
                        //总页数不能为0
                        if (pagenum != 0) {
                            mRootHolder.tv_nums.setText(currpage + " / " + pagenum);
                        }

                        RoomUser user = TKRoomManager.getInstance().getMySelf();
                        if (user.role == 2) {
                            if (RoomControler.isHiddenPageFlipButton()) {
                                //配置学生没有翻页按钮
                                mRootHolder.iv_left.setVisibility(View.GONE);
                                mRootHolder.iv_right.setVisibility(View.GONE);
                                mRootHolder.tv_nums.setEnabled(false);
                            } else if (RoomControler.isStudentCanTurnPage()) {
                                //配置学生能翻页 但是本地行为
                                mRootHolder.iv_left.setImageResource(R.drawable.tk_wb_page_icon_left_default);
                                mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_default);
                                mRootHolder.iv_left.setVisibility(View.VISIBLE);
                                mRootHolder.iv_right.setVisibility(View.VISIBLE);
                                mRootHolder.iv_left.setEnabled(true);
                                mRootHolder.iv_right.setEnabled(true);
                                mRootHolder.tv_nums.setEnabled(true);
                            } else if (user.properties.containsKey("candraw") &&
                                    (boolean) user.properties.get("candraw")) {
                                //学生有画笔权限
                                mRootHolder.iv_left.setImageResource(R.drawable.tk_wb_page_icon_left_default);
                                mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_default);
                                mRootHolder.iv_left.setVisibility(View.VISIBLE);
                                mRootHolder.iv_right.setVisibility(View.VISIBLE);
                                mRootHolder.iv_left.setEnabled(true);
                                mRootHolder.iv_right.setEnabled(true);
                                mRootHolder.tv_nums.setEnabled(true);
                            } else {
                                mRootHolder.iv_left.setImageResource(R.drawable.tk_wb_page_icon_left_disable);
                                mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_disable);
                                mRootHolder.iv_left.setEnabled(false);
                                mRootHolder.iv_right.setEnabled(false);
                                mRootHolder.tv_nums.setEnabled(false);
                            }
                        } else {
                            mRootHolder.iv_left.setImageResource(R.drawable.tk_wb_page_icon_left_default);
                            mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_default);
                            mRootHolder.iv_left.setVisibility(View.VISIBLE);
                            mRootHolder.iv_right.setVisibility(View.VISIBLE);
                            mRootHolder.iv_left.setEnabled(true);
                            mRootHolder.iv_right.setEnabled(true);
                            mRootHolder.tv_nums.setEnabled(true);
                        }

                        //当前页码和总页码相等 并且总页码等于1 不可左翻页不可右翻页
                        if (currpage == pagenum && pagenum == 1 && pptstep == steptotal && steptotal == 0) {
                            mRootHolder.iv_left.setImageResource(R.drawable.tk_wb_page_icon_left_disable);
                            mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_disable);
                            mRootHolder.iv_left.setEnabled(false);
                            mRootHolder.iv_right.setEnabled(false);
                            //如果是老师可添加一页
                            if (user.role == 0 && filedataBean.getFileid().equals("0") && WBSession.isClassBegin) {
                                mRootHolder.iv_right.setEnabled(true);
                                mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_default);
                            }

                        } else if (currpage == pagenum && pagenum > 1 && (steptotal <= 0 || pptstep == (steptotal - 1))) {
                            mRootHolder.iv_left.setImageResource(R.drawable.tk_wb_page_icon_left_default);
                            mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_disable);
                            mRootHolder.iv_right.setEnabled(false);
                            //如果是老师可添加一页
                            if (user.role == 0 && filedataBean.getFileid().equals("0") && WBSession.isClassBegin) {
                                mRootHolder.iv_right.setEnabled(true);
                                mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_default);
                            }
                            if (user.role == 2 && user.properties.containsKey("candraw") &&
                                    !(boolean) user.properties.get("candraw") && !RoomControler.isStudentCanTurnPage()) {
                                mRootHolder.iv_left.setImageResource(R.drawable.tk_wb_page_icon_left_disable);
                                mRootHolder.iv_left.setEnabled(false);
                            }
                        } else if (currpage == 1 && pptstep == 0) {
                            mRootHolder.iv_left.setEnabled(false);
                            mRootHolder.iv_left.setImageResource(R.drawable.tk_wb_page_icon_left_disable);
                        } else if (currpage == pagenum && pptstep == 0) {
                            mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_disable);
                            mRootHolder.iv_right.setEnabled(false);
                        }

                        //当巡课时不能翻页
                        if (user.role == 4) {
                            mRootHolder.iv_left.setImageResource(R.drawable.tk_wb_page_icon_left_disable);
                            mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_disable);
                            mRootHolder.iv_left.setEnabled(false);
                            mRootHolder.iv_right.setEnabled(false);
                            mRootHolder.tv_nums.setEnabled(false);
                        }

                        //当时动态ppt和h5课件时候没有放大缩小
                        if (mShowPageBean != null && (mShowPageBean.isDynamicPPT()
                                || mShowPageBean.isH5Document() || mShowPageBean.isSvg()
                                || mShowPageBean.isGif())) {//动态ppt h5课件
                            mRootHolder.iv_large.setVisibility(View.GONE);
                            mRootHolder.iv_small.setVisibility(View.GONE);
                        } else {
                            mRootHolder.iv_large.setVisibility(View.VISIBLE);
                            mRootHolder.iv_small.setVisibility(View.VISIBLE);
                        }

                        // 翻页时，重置remark状态
                        if (!isShowRemark) {
                            showRemark();
                        }
                        changePageAndRemark(viewWidth, viewHeight);
                        PPTRemarkUtil.getInstance().getPPTRemark(SharePadMgr.getInstance().getHost(),
                                filedataBean.getFileid(), filedataBean.getCurrpage());
                    }
                });
            }
        }

        @Override
        public void setViewState() {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ShareDoc shareDoc = WhiteBoradManager.getInstance().getCurrentFileDoc();
                    if (shareDoc != null) {
                        if (shareDoc.getFileid() == 0 && TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                            if (WBSession.isClassBegin) {
                                mRootHolder.iv_right.setEnabled(true);
                                mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_default);
                            } else {
                                if (shareDoc.getCurrentPage() == shareDoc.getPagenum()) {
                                    mRootHolder.iv_right.setEnabled(false);
                                    mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_disable);
                                }
                            }
                        }
                    }
                }
            });
        }
    };

    /**
     * 点击事件
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.page_iv_left) {//左翻页
                if (mShowPageBean != null) {
                    if (WhiteBoradConfig.getsInstance().getCurrentFileDoc().isDynamicPPT()
                            || WhiteBoradConfig.getsInstance().getCurrentFileDoc().isH5Docment() || mShowPageBean.isSvg() || mShowPageBean.isGif()) {//动态ppt h5课件
                        WhiteBoradConfig.getsInstance().prePage();
                    } else {
                        resetLargeOrSmallView();
                        mShowPageBean.getFiledata().setCurrpage(mShowPageBean.getFiledata().getCurrpage() - 1);
                        RoomUser mySelf = TKRoomManager.getInstance().getMySelf();
                        //当上课时 自己是老师 或者是学生具有权限时 发送信令
                        if (WBSession.isClassBegin && (mySelf.role == 0
                                || (mySelf.role == 2 && mySelf.properties.containsKey("candraw") && (boolean) mySelf.properties.get("candraw")))) {
                            TKRoomManager.getInstance().pubMsg("ShowPage", "DocumentFilePage_ShowPage",
                                    "__all", mShowPageBean.toString(), true, null, null);
                        } else {
                            SharePadMgr.getInstance().showCourseSelectPage(mShowPageBean);
                            ShareDoc currentFileDoc = WhiteBoradManager.getInstance().getCurrentFileDoc();
                            currentFileDoc.setCurrentPage(mShowPageBean.getFiledata().getCurrpage());
                            WhiteBoradManager.getInstance().setCurrentFileDoc(currentFileDoc);
                        }
                    }
                }
            } else if (id == R.id.page_tv_nums) {//跳转到指定页
                if (mShowPageBean != null) {
                    mPageNumsPopupWindow.showPopPen(mRootHolder.tv_nums, mRootHolder.page_iv_arrow, mRootHolder.page_include.getHeight(), mShowPageBean);
                    resetLargeOrSmallView();
                }
            } else if (id == R.id.page_iv_right) {//右翻页
                if (mShowPageBean != null) {
                    if (WhiteBoradConfig.getsInstance().getCurrentFileDoc().isDynamicPPT()
                            || WhiteBoradConfig.getsInstance().getCurrentFileDoc().isH5Docment() || (mShowPageBean != null && (mShowPageBean.isSvg() || mShowPageBean.isGif()))) {//动态ppt h5课件
                        WhiteBoradConfig.getsInstance().nextPage();
                    } else {
                        resetLargeOrSmallView();
                        mShowPageBean.getFiledata().setCurrpage(mShowPageBean.getFiledata().getCurrpage() + 1);
                        RoomUser mySelf = TKRoomManager.getInstance().getMySelf();

                        //当上课时 自己是老师 或者是学生具有权限时 发送信令
                        if (WBSession.isClassBegin && (mySelf.role == 0
                                || (mySelf.role == 2 && mySelf.properties.containsKey("candraw") && (boolean) mySelf.properties.get("candraw")))) {
                            //只有白班老师才能添加
                            if (mShowPageBean.getFiledata().getFileid().equals("0") && mySelf.role == 0) {
                                //如果是白板 先发wbpagecount信令
                                //WBPageCount  {"totalPage":11,"fileid":0}  fa 信令不保存
                                Map<String, Object> count = new HashMap<>();
                                count.put("totalPage", mShowPageBean.getFiledata().getCurrpage());
                                count.put("fileid", 0);
                                count.put("sourceInstanceId", "default");
                                TKRoomManager.getInstance().pubMsg("WBPageCount", "WBPageCount",
                                        "__allExceptSender", new JSONObject(count).toString(),
                                        true, null, null);
                            }
                            if (mShowPageBean.getFiledata().getCurrpage() >= mShowPageBean.getFiledata().getPagenum()) {
                                mShowPageBean.getFiledata().setPagenum(mShowPageBean.getFiledata().getCurrpage());
                            } else {
                                mShowPageBean.getFiledata().setPagenum(mShowPageBean.getFiledata().getPagenum());
                            }

                            TKRoomManager.getInstance().pubMsg("ShowPage", "DocumentFilePage_ShowPage", "__all",
                                    mShowPageBean.toString(), true, null, null);
                        } else {
                            SharePadMgr.getInstance().showCourseSelectPage(mShowPageBean);
                            ShareDoc currentFileDoc = WhiteBoradManager.getInstance().getCurrentFileDoc();
                            currentFileDoc.setCurrentPage(mShowPageBean.getFiledata().getCurrpage());
                            WhiteBoradManager.getInstance().setCurrentFileDoc(currentFileDoc);
                        }
                    }

                }
            } else if (id == R.id.page_iv_large) {//放大
                //底部绘制放大
                setLargeAndSmall(WhiteBoradConfig.getsInstance().getScale(true, false));
                //顶部绘制放大
                setLargeAndSmall(WhiteBoradConfig.getsInstance().getScale(true, true));

                if (WhiteBoradConfig.getsInstance().getCurrentFileDoc().isDynamicPPT()
                        || WhiteBoradConfig.getsInstance().getCurrentFileDoc().isH5Docment()) {//动态ppt h5课件
                    WhiteBoradConfig.getsInstance().EnlargeOrNarrowWhiteboard(true);
                } else {
                    if (mShowPageBean != null) {
                        // svg和gif放大交给H5处理
                        if (mShowPageBean.isSvg() || mShowPageBean.isGif()) {
                            WhiteBoradConfig.getsInstance().EnlargeOrNarrowWhiteboard(true);
                        }
                    }
                }

            } else if (id == R.id.page_iv_small) {//缩小
                //底部绘制
                setLargeAndSmall(WhiteBoradConfig.getsInstance().getScale(false, false));
                //顶部绘制
                setLargeAndSmall(WhiteBoradConfig.getsInstance().getScale(false, true));

                if (WhiteBoradConfig.getsInstance().getCurrentFileDoc().isDynamicPPT()
                        || WhiteBoradConfig.getsInstance().getCurrentFileDoc().isH5Docment()) {//动态ppt h5课件
                    WhiteBoradConfig.getsInstance().EnlargeOrNarrowWhiteboard(false);
                } else {
                    if (mShowPageBean != null) {
                        // svg和gif缩小交给H5处理
                        if (mShowPageBean.isSvg() || mShowPageBean.isGif()) {
                            WhiteBoradConfig.getsInstance().EnlargeOrNarrowWhiteboard(false);
                        }
                    }
                }
            } else if (id == R.id.page_iv_full_screen) {//全屏
                if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                    WhiteBoradManager.getInstance().fullScreenToLc(!isFullScreen);
                } else {
                    if (!RoomSession.fullScreen) {
                        WhiteBoradManager.getInstance().fullScreenToLc(!isFullScreen);
                    }
                }
            } else if (id == R.id.page_iv_remark) {
                if (isShowRemark) {
                    hideRemark();
                } else {
                    showRemark();
                }
            }
        }
    };

    private void hideRemark() {
        isShowRemark = false;
        mRootHolder.ll_remark.setVisibility(View.GONE);
        mRootHolder.iv_remark.setImageResource(R.drawable.tk_wb_page_icon_remark_default);
    }

    private void showRemark() {
        isShowRemark = true;
        mRootHolder.ll_remark.setVisibility(View.VISIBLE);
        mRootHolder.iv_remark.setImageResource(R.drawable.tk_wb_page_icon_remark_press);
    }

    /**
     * 重置放大缩小的比例
     */
    public void resetLargeOrSmallView() {
        //底部绘制
        setLargeAndSmall(WhiteBoradConfig.getsInstance().getresetLargeOrSmallscale(false));
        //顶部绘制
        setLargeAndSmall(WhiteBoradConfig.getsInstance().getresetLargeOrSmallscale(true));
    }

    public void setLargeAndSmall(float scale) {
        if (scale == 1.0) {
            mRootHolder.iv_large.setEnabled(true);
            mRootHolder.iv_small.setEnabled(false);
            mRootHolder.iv_large.setImageResource(R.drawable.tk_wb_page_icon_large_default);
            mRootHolder.iv_small.setImageResource(R.drawable.tk_wb_page_icon_small_disable);
        } else if (scale == 3.0) {
            mRootHolder.iv_large.setEnabled(false);
            mRootHolder.iv_small.setEnabled(true);
            mRootHolder.iv_large.setImageResource(R.drawable.tk_wb_page_icon_large_disable);
            mRootHolder.iv_small.setImageResource(R.drawable.tk_wb_page_icon_small_default);
        } else {
            mRootHolder.iv_large.setEnabled(true);
            mRootHolder.iv_small.setEnabled(true);
            mRootHolder.iv_large.setImageResource(R.drawable.tk_wb_page_icon_large_default);
            mRootHolder.iv_small.setImageResource(R.drawable.tk_wb_page_icon_small_default);
        }
    }

    /**
     * h5和ppt从白板返回的状态
     *
     * @param stateJson
     */
    public void setAction(String stateJson) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(stateJson);
            JSONObject pagejson = jsonObject.optJSONObject("page");
            String fileTypeMark = jsonObject.optString("fileTypeMark");//文件类型
            if (pagejson != null) {
                boolean nextPage = pagejson.optBoolean("nextPage");
                boolean prevPage = pagejson.optBoolean("prevPage");
                boolean skipPage = pagejson.optBoolean("skipPage");
                boolean addPage = pagejson.optBoolean("addPage");
                boolean nextStep = pagejson.optBoolean("nextStep");
                boolean prevStep = pagejson.optBoolean("prevStep");
                int currentPage = pagejson.optInt("currentPage");
                int totalPage = pagejson.optInt("totalPage");
                if (prevPage || prevStep) {
                    mRootHolder.iv_left.setImageResource(R.drawable.tk_wb_page_icon_left_default);
                    mRootHolder.iv_left.setEnabled(true);
                } else {
                    mRootHolder.iv_left.setImageResource(R.drawable.tk_wb_page_icon_left_disable);
                    mRootHolder.iv_left.setEnabled(false);
                }
                if (nextPage || nextStep) {
                    mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_default);
                    mRootHolder.iv_right.setEnabled(true);
                } else {
                    mRootHolder.iv_right.setImageResource(R.drawable.tk_wb_page_icon_right_disable);
                    mRootHolder.iv_right.setEnabled(false);
                }
                String nums = currentPage + " / " + totalPage;
                mRootHolder.tv_nums.setText(nums);

                ShareDoc currentDoc = WhiteBoradConfig.getsInstance().getCurrentFileDoc();
                if (currentDoc != null && (currentDoc.isDynamicPPT() || currentDoc.isH5Docment() || (mShowPageBean != null
                        && (mShowPageBean.isSvg() || mShowPageBean.isGif())))) {

                    currentDoc.setCurrentPage(currentPage);
                    currentDoc.setPagenum(totalPage);

                    if (mShowPageBean != null) {
                        SharePadMgr.getInstance().mCurrentShareDoc = mShowPageBean;
                        ShowPageBean.FiledataBean filedataBean = mShowPageBean.getFiledata();

                        if (filedataBean != null) {
                            filedataBean.setCurrpage(currentPage);
                            filedataBean.setPagenum(totalPage);
                        }
                        if (RoomControler.isStudentCanTurnPage() && TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT
                                && !(TKRoomManager.getInstance().getMySelf().properties.containsKey("candraw")
                                && (boolean) TKRoomManager.getInstance().getMySelf().properties.get("candraw"))) {
                            SharePadMgr.getInstance().displayPage(mShowPageBean);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新计算工具条的大小
     */
    public void doLayout(int viewWidth, int viewHeight) {
        if (this.viewHeight == viewHeight && this.viewWidth == viewWidth) {
            return;
        }
        changePageAndRemark(viewWidth, viewHeight);
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    /**
     * 刷新翻页和备注框大小位置
     */
    private void changePageAndRemark(int viewWidth, int viewHeight) {
        if (this.viewHeight == 0 || this.viewWidth == 0)
            return;
        if (isPageViewChange) {
            RelativeLayout.LayoutParams ll_layoutParams = (RelativeLayout.LayoutParams) mRootHolder.pages_include_ll.getLayoutParams();
            ll_layoutParams.leftMargin = ll_layoutParams.leftMargin * viewWidth / this.viewWidth;
            ll_layoutParams.topMargin = ll_layoutParams.topMargin * viewHeight / this.viewHeight;
            if (ll_layoutParams.leftMargin > viewWidth - mRootHolder.pages_include_ll.getMeasuredWidth()) {
                ll_layoutParams.leftMargin = viewWidth - mRootHolder.pages_include_ll.getMeasuredWidth();
            }
            if (ll_layoutParams.topMargin > viewHeight - mRootHolder.pages_include_ll.getMeasuredHeight() - 5) {
                ll_layoutParams.topMargin = viewHeight - mRootHolder.pages_include_ll.getMeasuredHeight() - 5;
            }
            ll_layoutParams.rightMargin = 0;
            ll_layoutParams.bottomMargin = 0;
            ll_layoutParams.removeRule(ALIGN_PARENT_BOTTOM);
            ll_layoutParams.removeRule(CENTER_HORIZONTAL);
            mRootHolder.pages_include_ll.setLayoutParams(ll_layoutParams);
        } else {
            RelativeLayout.LayoutParams ll_layoutParams = (RelativeLayout.LayoutParams) mRootHolder.pages_include_ll.getLayoutParams();
            ll_layoutParams.leftMargin = (viewWidth - mRootHolder.pages_include_ll.getMeasuredWidth()) / 2;
            ll_layoutParams.topMargin = viewHeight - mRootHolder.pages_include_ll.getMeasuredHeight() - 5;
            ll_layoutParams.rightMargin = 0;
            ll_layoutParams.bottomMargin = 0;
            ll_layoutParams.removeRule(ALIGN_PARENT_BOTTOM);
            ll_layoutParams.removeRule(CENTER_HORIZONTAL);
            mRootHolder.pages_include_ll.setLayoutParams(ll_layoutParams);
        }

        if (isRemarkViewChange) {
            //设置备注框大小和位置
            RelativeLayout.LayoutParams ll_remarklp = (RelativeLayout.LayoutParams) mRootHolder.ll_remark.getLayoutParams();
            ll_remarklp.width = viewWidth * 680 / 1000;
            ll_remarklp.height = viewHeight * 160 / 567;
            ll_remarklp.leftMargin = ll_remarklp.leftMargin * viewWidth / this.viewWidth;
            ll_remarklp.topMargin = ll_remarklp.topMargin * viewHeight / this.viewHeight;
            if (ll_remarklp.leftMargin > viewWidth - ll_remarklp.width) {
                ll_remarklp.leftMargin = viewWidth - ll_remarklp.width;
            }
            if (ll_remarklp.topMargin > viewHeight - ll_remarklp.height) {
                ll_remarklp.topMargin = viewHeight - ll_remarklp.height;
            }
            mRootHolder.ll_remark.setLayoutParams(ll_remarklp);
        } else {
            //设置备注框大小和位置
            RelativeLayout.LayoutParams ll_remarklp = (RelativeLayout.LayoutParams) mRootHolder.ll_remark.getLayoutParams();
            ll_remarklp.width = viewWidth * 680 / 1000;
            ll_remarklp.height = viewHeight * 160 / 567;
            ll_remarklp.topMargin = viewHeight - ll_remarklp.height - mRootHolder.pages_include_ll.getMeasuredHeight() - 30;
            ll_remarklp.leftMargin = (viewWidth - ll_remarklp.width) / 2;
            mRootHolder.ll_remark.setLayoutParams(ll_remarklp);
        }
    }

    //初始化工具栏pop
    private void initPop() {
        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL) {
            mRootHolder.page_iv_arrow.setVisibility(View.INVISIBLE);
            mRootHolder.tv_nums.setTextColor(mContext.getResources().getColor(R.color.member_page_not));
        }
        mPageNumsPopupWindow = new PageNumsPopupWindow(mContext);
        mPageNumsPopupWindow.SetonNumListener(new PageNumsPopupWindow.onNumListener() {
            @Override
            public void setNum(int num, ShowPageBean showPageBean) {
                if (showPageBean != null) {
                    if (showPageBean.isDynamicPPT() || showPageBean.isH5Document()
                            || showPageBean.isSvg() || showPageBean.isGif()) {
                        WhiteBoradConfig.getsInstance().skipToPageNum(num);
                    } else {
                        mShowPageBean.getFiledata().setCurrpage(num);
                        RoomUser mySelf = TKRoomManager.getInstance().getMySelf();
                        //当上课时 自己是老师 或者是学生具有权限时 发送信令
                        if (WBSession.isClassBegin && (mySelf.role == 0
                                || (mySelf.role == 2 && mySelf.properties.containsKey("candraw") && (boolean) mySelf.properties.get("candraw")))) {

                            TKRoomManager.getInstance().pubMsg("ShowPage", "DocumentFilePage_ShowPage",
                                    "__all", mShowPageBean.toString(), true, null, null);
                        } else {
                            SharePadMgr.getInstance().showCourseSelectPage(mShowPageBean);
                            ShareDoc currentFileDoc = WhiteBoradManager.getInstance().getCurrentFileDoc();
                            currentFileDoc.setCurrentPage(mShowPageBean.getFiledata().getCurrpage());
                            WhiteBoradManager.getInstance().setCurrentFileDoc(currentFileDoc);
                        }
                    }
                }
            }
        });
    }

    private View view;

    public void SetFragementView(View view) {
        this.view = view;
    }

    private int lastX, lastY;

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (v instanceof LongToucherLinearLayout) {
                        String[] downs = v.getTag().toString().split("&&");
                        lastX = Integer.parseInt(downs[0]);
                        lastY = Integer.parseInt(downs[1]);
                        v.setTag((int) event.getRawX() + "&&" + (int) event.getRawY());
                        if (v.getId() == mRootHolder.ll_remark.getId()) {
                            isRemarkViewChange = true;
                        }
                        if (v.getId() == mRootHolder.pages_include_ll.getId()) {
                            isPageViewChange = true;
                        }
                    }

                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;

                    if (view != null && v != null) {
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();

                        int l = layoutParams.leftMargin + dx;
                        int t = layoutParams.topMargin + dy;
                        int b = view.getHeight() - t - v.getHeight();
                        int r = view.getWidth() - l - v.getWidth();
                        if (l < 0) {//处理按钮被移动到上下左右四个边缘时的情况，决定着按钮不会被移动到屏幕外边去
                            l = 0;
                            r = view.getWidth() - v.getWidth();
                        }
                        if (t < 0) {
                            t = 0;
                            b = view.getHeight() - v.getHeight();
                        }

                        if (r < 0) {
                            r = 0;
                            l = view.getWidth() - v.getWidth();
                        }
                        if (b < 0) {
                            b = 0;
                            t = view.getHeight() - v.getHeight();
                        }
                        layoutParams.leftMargin = l;
                        layoutParams.topMargin = t;
                        layoutParams.bottomMargin = b;
                        layoutParams.rightMargin = r;
                        v.setLayoutParams(layoutParams);

                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        v.postInvalidate();
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };

    //显示备注的回调
    @Override
    public void changePPtRemark(final String remark, String fileid, int pagenum) {
        if (mShowPageBean != null
                && !mShowPageBean.getFiledata().getFileid().equals(fileid)
                && mShowPageBean.getFiledata().getPagenum() != pagenum) {
            return;
        }
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(remark) && RoomControler.isHasCoursewareNotes()
                        && TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                    mRootHolder.tv_remark.setText(remark);
                    if (isShowRemark) {
                        mRootHolder.ll_remark.setVisibility(View.VISIBLE);
                    }
                    mRootHolder.iv_remark.setVisibility(View.VISIBLE);
                } else {
                    mRootHolder.iv_remark.setVisibility(View.GONE);
                    mRootHolder.ll_remark.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 备注显示隐藏
     *
     * @param isshow
     */
    public void setVisiblityremark(boolean isshow) {

        if (mRootHolder.ll_remark != null) {
            if (isshow) {
                mRootHolder.ll_remark.setVisibility(View.VISIBLE);
            } else {
                mRootHolder.ll_remark.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 切换课件
     */
    public void onHidePageNumberPop() {
        if (mPageNumsPopupWindow != null)
            mPageNumsPopupWindow.dismisspop();
    }

    /**
     * 各种监听事件
     */
    public void setonClick() {
        //翻页监听
        mRootHolder.iv_left.setOnClickListener(onClickListener);
        mRootHolder.iv_right.setOnClickListener(onClickListener);
        mRootHolder.tv_nums.setOnClickListener(onClickListener);
        mRootHolder.iv_large.setOnClickListener(onClickListener);
        mRootHolder.iv_small.setOnClickListener(onClickListener);
        mRootHolder.iv_full.setOnClickListener(onClickListener);
        mRootHolder.iv_remark.setOnClickListener(onClickListener);
        //ontouch事件
        mRootHolder.pages_include_ll.setOnTouchListener(onTouchListener);
        mRootHolder.ll_remark.setOnTouchListener(onTouchListener);
        //设置显示备注回调
        PPTRemarkUtil.getInstance().setChangePPtRemarkIF(this);
        PPTRemarkUtil.getInstance().setActivity((Activity) mContext);
        //文档切换回调
        SharePadMgr.getInstance().setShowPageOnclichListener(showPageInterface);
        //h5的页码刷新回调
        WhiteBoradConfig.getsInstance().setShowPageInterface(showPageInterface);
    }
}
