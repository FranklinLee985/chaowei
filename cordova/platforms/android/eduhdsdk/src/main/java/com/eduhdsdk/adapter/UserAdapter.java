package com.eduhdsdk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.classroomsdk.bean.SmallPaintBean;
import com.classroomsdk.bean.StudentListBean;
import com.classroomsdk.manage.SharePadMgr;
import com.classroomsdk.utils.Tools;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import skin.support.annotation.Skinable;

@Skinable
public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    //显示数据
    private List<StudentListBean> mList = new ArrayList<>();
    //学生数据
    private List<StudentListBean> indeStudent = new ArrayList<>();


    private int mRecyclerWidth = 0;
    private int mRecyclerHeight = 0;

    //数据
    private static final int TYPE = 1;
    //翻页
    private static final int PAGE = 2;


    //当前选中
//    private int current = 0;
    int currpage = 0;
    //总页
    int count = 0;
    //每页显示数量
    private int pagesize = 0;
    private int index = 0;

    //学生退出跳会默认老师状态
    private int viewHight;


    public void setmRecyclerWidth(int width, int height) {
        this.mRecyclerWidth = width;
        this.mRecyclerHeight = height;
        notifyDataSetChanged();
    }


    public UserAdapter(Context context) {
        this.mContext = context;
    }

    //设置数据
    public void SetData(List<StudentListBean> list) {
        mList.clear();
        indeStudent.clear();
        indeStudent.addAll(list);
        for (int i = 0; i < list.size(); i++) {
            if (getmSall() != null) {
                if (getmSall().getCurrentTapKey().equals(list.get(i).getId())) {
                    currpage = i;
                    break;
                }
            }
        }

        //如果是平板 最多显示7人 超出7人显示6人 第7人位置添加翻页
        if (Tools.isTablet(mContext)) {
            if (list.size() <= 7) {
                mList.addAll(list);
            } else {
                mList.addAll(ListPage(currpage + 1, 6, list));
            }
        }
        //如果是手机 最多显示3 超过3人显示2人 第三人位置显示翻页
        else {
            if (list.size() <= 3) {
                mList.addAll(list);
            } else {
                mList.addAll(ListPage(currpage + 1, 2, list));
            }
        }
        if (mRecyclerWidth != 0) {
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.tk_item_small_buttom, parent, false);
            if (Tools.isTablet(mContext)) {
                view.getLayoutParams().width = mRecyclerWidth / 7;
            } else {
                view.getLayoutParams().width = mRecyclerWidth / 3;
            }
            view.getLayoutParams().height = mRecyclerHeight;
            return new ViewHolderButtom(view);
        } else if (viewType == PAGE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.tk_item_page_leftorright, parent, false);
            if (Tools.isTablet(mContext)) {
                view.getLayoutParams().width = mRecyclerWidth / 7;
            } else {
                view.getLayoutParams().width = mRecyclerWidth / 3;
            }
            view.getLayoutParams().height = mRecyclerHeight;
            return new ViewHolderLeftOrRight(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderButtom) {
            ViewHolderButtom viewHolderButtom = (ViewHolderButtom) holder;
            if (getmSall() != null && getmSall().getCurrentTapKey().equals(mList.get(position).getId())) {
                viewHolderButtom.butUser.setBackgroundResource(R.drawable.item_small_top_buttom_white);
                viewHolderButtom.butUser.setTextAppearance(mContext, R.style.white_board_lord);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewHolderButtom.butUser.getLayoutParams();
                layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                viewHolderButtom.butUser.setLayoutParams(layoutParams);

            } else {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewHolderButtom.butUser.getLayoutParams();
                layoutParams.height = mRecyclerHeight * 4 / 5;
                layoutParams.gravity = Gravity.BOTTOM;
                viewHolderButtom.butUser.setLayoutParams(layoutParams);
                viewHolderButtom.butUser.setTextAppearance(mContext, R.style.whiteboard);
                viewHolderButtom.butUser.setBackgroundResource(R.drawable.tk_item_small_top_buttom);
            }

            viewHolderButtom.butUser.setText(mList.get(position).getNickname());
            if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                viewHolderButtom.butUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        name：BlackBoard_new
//                        {"blackBoardState":"_dispenseed","currentTapKey":"ec3b0cc4-26c8-0071-4ae1-651ebe32c05d","currentTapPage":1}
                        //老师选中发信令
                        Map<String, Object> prepareing = new HashMap<>();
                        prepareing.put("blackBoardState", getmSall().getBlackBoardState());
                        prepareing.put("currentTapKey", mList.get(position).getId());
                        prepareing.put("currentTapPage", 1);
                        TKRoomManager.getInstance().pubMsg("BlackBoard_new", "BlackBoard_new", "__all", new JSONObject(prepareing).toString(), true, "ClassBegin", "");
                    }
                });
            }


        } else if (holder instanceof ViewHolderLeftOrRight) {
            ViewHolderLeftOrRight viewHolderLeftOrRight = (ViewHolderLeftOrRight) holder;

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolderLeftOrRight.iv_left.getLayoutParams();
            layoutParams.height = mRecyclerHeight * 4 / 5;
            viewHolderLeftOrRight.iv_left.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) viewHolderLeftOrRight.iv_right.getLayoutParams();
            layoutParams1.height = mRecyclerHeight * 4 / 5;
            viewHolderLeftOrRight.iv_right.setLayoutParams(layoutParams1);

            int countpage = 0;
            if ((count % pagesize) > 0) {
                countpage = count / pagesize + 1;
            } else {
                countpage = count / pagesize;
            }

            //如果是第一页  并且 当前页 小 总页  不可左翻 可右翻
            if (index == 1 && index < countpage) {
                viewHolderLeftOrRight.iv_left.setEnabled(false);
                viewHolderLeftOrRight.iv_right.setEnabled(true);
                viewHolderLeftOrRight.iv_left.setImageResource(R.drawable.tk_small_page_left_disable);
                viewHolderLeftOrRight.iv_right.setImageResource(R.drawable.tk_small_page_right_default);

            } else if (index != 1 && index == countpage) {
                viewHolderLeftOrRight.iv_left.setEnabled(true);
                viewHolderLeftOrRight.iv_right.setEnabled(false);
                viewHolderLeftOrRight.iv_left.setImageResource(R.drawable.tk_small_page_left_default);
                viewHolderLeftOrRight.iv_right.setImageResource(R.drawable.tk_small_page_right_disable);
            } else if (index > 1 && index < countpage) {
                viewHolderLeftOrRight.iv_left.setEnabled(true);
                viewHolderLeftOrRight.iv_right.setEnabled(true);
                viewHolderLeftOrRight.iv_left.setImageResource(R.drawable.tk_small_page_left_default);
                viewHolderLeftOrRight.iv_right.setImageResource(R.drawable.tk_small_page_right_default);
            }

            if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                viewHolderLeftOrRight.iv_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<StudentListBean> listBeans = ListPageLeftOrRight(index + 1, indeStudent);
                        if (listBeans != null) {
                            mList.clear();
                            mList.addAll(listBeans);
                            notifyDataSetChanged();
                        }
                    }
                });
                viewHolderLeftOrRight.iv_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<StudentListBean> listBeans = ListPageLeftOrRight(index - 1, indeStudent);
                        if (listBeans != null) {
                            mList.clear();
                            mList.addAll(listBeans);
                            notifyDataSetChanged();
                        }
                    }
                });
            }

        }

    }

    @Override
    public int getItemViewType(int position) {
        //平板
        if (Tools.isTablet(mContext)) {
            if (indeStudent.size() > 7) {
                if (position == 6) {
                    return PAGE;
                }
                return TYPE;
            } else {
                return TYPE;
            }
        } else {
            if (indeStudent.size() > 3) {
                if (position == 2) {
                    return PAGE;
                }
                return TYPE;
            } else {
                return TYPE;
            }
        }

    }

    @Override
    public int getItemCount() {
        //平板
        if (Tools.isTablet(mContext)) {
            if (indeStudent.size() > 7) {
                return mList.size() + 1;
            } else {
                return mList.size();
            }
        } else {
            if (indeStudent.size() > 3) {
                return mList.size() + 1;
            } else {
                return mList.size();
            }
        }
    }


    class ViewHolderLeftOrRight extends RecyclerView.ViewHolder {

        ImageView iv_left;
        ImageView iv_right;

        public ViewHolderLeftOrRight(View itemView) {
            super(itemView);
            iv_left = itemView.findViewById(R.id.iv_small_page_left);
            iv_right = itemView.findViewById(R.id.iv_small_page_right);
        }

    }


    class ViewHolderButtom extends RecyclerView.ViewHolder {

        Button butUser;

        public ViewHolderButtom(View itemView) {
            super(itemView);
            butUser = itemView.findViewById(R.id.small_user_but);
            butUser.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            viewHight = butUser.getMeasuredHeight();
        }
    }

    private SmallPaintBean getmSall() {
        SmallPaintBean smallPaintBean = SharePadMgr.getInstance().mSmallPaintDoc;
        if (smallPaintBean != null) {
            return smallPaintBean;
        }
        return null;
    }

    /**
     * 选中分页
     *
     * @param current  当前选中项
     * @param pagesize 每页
     * @param ts       集和
     * @return
     */
    public List<StudentListBean> ListPage(int current, int pagesize, List<StudentListBean> ts) {


        //当前选中
        this.currpage = current;
        //总数
        this.count = ts.size();

        //每页显示数量
        this.pagesize = pagesize;

        //第几页
        this.index = 0;

        int startindex = 0;
        int endindex = 0;

        if (current % pagesize > 0) {
            index = current / pagesize + 1;

            int currentpage = index * pagesize;
            if (count >= currentpage) {
                startindex = currentpage - pagesize;
                endindex = currentpage;
            } else {
                startindex = count - pagesize;
                endindex = count;
            }
        } else {
            index = current / pagesize;
            int currentpage = index * pagesize;
            startindex = currentpage - pagesize;
            endindex = currentpage;
        }

        if (ts.size() > 0) {
            if (startindex < ts.size() && endindex <= ts.size()) {
                List<StudentListBean> listBeans = ts.subList(startindex, endindex);
                return listBeans;
            }
        }
        return new ArrayList<>();
    }


    /**
     * 正常分页
     *
     * @param index
     * @param ts
     * @return
     */
    public List<StudentListBean> ListPageLeftOrRight(int index, List<StudentListBean> ts) {

        int startindex = 0;
        int endindex = 0;
        this.index = index;
        int currentpage = index * pagesize;
        if (count >= currentpage) {
            startindex = currentpage - pagesize;
            endindex = currentpage;
        } else {
            startindex = count - pagesize;
            endindex = count;
        }

        if (ts.size() > 0) {
            List<StudentListBean> listBeans = ts.subList(startindex, endindex);
            return listBeans;
        }

        return null;
    }

}
