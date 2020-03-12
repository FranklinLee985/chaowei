package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.BuildVars;
import com.eduhdsdk.R;
import com.eduhdsdk.adapter.SendGiftAdapter;
import com.eduhdsdk.entity.Trophy;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.room.RoomOperation;
import com.eduhdsdk.room.RoomVariable;
import com.eduhdsdk.tools.FullScreenTools;
import com.eduhdsdk.tools.SoundPlayUtils;
import com.eduhdsdk.ui.BasePopupWindow;
import com.talkcloud.room.RoomUser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2018/4/24/024.
 * 发送自定义奖杯
 */

public class SendGiftPopUtils {

    private Activity activity;
    private PopupWindow sendGiftWindow;

    //奖杯集合
    public static List<String> trophy_icon = new ArrayList<String>();

    //获取奖杯数
    public void preLoadImage() {
        trophy_icon.clear();
        String url = BuildVars.REQUEST_HEADER + RoomVariable.host + ":" + RoomVariable.port;
        List<Trophy> trophyList = RoomInfo.getInstance().getTrophyList();
        if (trophyList != null && trophyList.size() > 0) {
            for (int i = 0; i < trophyList.size(); i++) {
                trophy_icon.add(url + RoomInfo.getInstance().getTrophyList().get(i).getTrophyIcon());
                //将图片地址加载到缓存中
                Glide.with(activity).load(url + trophyList.get(i).getTrophyIcon()).
                        preload();
            }
        }
    }

    //删除奖杯图片
    public void deleteImage() {

        List<Trophy> trophyList = RoomInfo.getInstance().getTrophyList();
        if (trophyList != null && trophyList.size() > 0) {
            for (int x = 0; x < trophyList.size(); x++) {
                String MP3File = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        "Trophyvoice";
                String imgFile = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + "Trophyimg";
                String iconFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        "TrophyIcon";
                delete(MP3File);
                delete(imgFile);
                delete(iconFile);
            }
        }
        SoundPlayUtils.release();
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        if (files == null) {
            return false;
        }
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public SendGiftPopUtils(Activity activity) {
        this.activity = activity;
    }


    /**
     * @param width
     * @param height
     * @param view
     * @param receiverMap
     */
    public void showSendGiftPop(int width, int height, final View view, final HashMap<String, RoomUser> receiverMap, boolean padLeft, int webwidth) {

        preLoadImage();
        List<Trophy> trophyList = RoomInfo.getInstance().getTrophyList();
        if (trophyList != null && trophyList.size() == 1) {
            //只有一个奖杯时直接发送，不显示弹窗
            sendGift(trophyList.get(0), receiverMap);
        } else if (trophyList.size() > 1) {
            View contentView = LayoutInflater.from(activity).inflate(R.layout.tk_layout_send_gift_pop, null);

            ((TextView) contentView.findViewById(R.id.tv_popup_title)).setText(R.string.send_gift);
            ScreenScale.scaleView(contentView, "SendGiftUtils");

            if (sendGiftWindow == null) {
                sendGiftWindow = new BasePopupWindow(activity);
            }
            sendGiftWindow.setWidth(width);
            int result = (int) (width * 0.6);
            if (result < height) {
                sendGiftWindow.setHeight(result);
            } else {
                sendGiftWindow.setHeight(height);
            }

            contentView.findViewById(R.id.iv_popup_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sendGiftWindow != null) {
                        sendGiftWindow.dismiss();
                    }
                }
            });

            GridView gridView = (GridView) contentView.findViewById(R.id.gv_send_gift);

            SendGiftAdapter adapter = new SendGiftAdapter(activity, trophy_icon);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    List<Trophy> trophyList = RoomInfo.getInstance().getTrophyList();
                    if (trophyList != null && trophyList.size() > position) {
                        sendGift(trophyList.get(position), receiverMap);
                        if (sendGiftWindow != null) {
                            sendGiftWindow.dismiss();
                        }
                    }
                }
            });

            sendGiftWindow.setContentView(contentView);
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        //这里给它设置了弹出的时间，
//        imm.toggleSoftInput(1000, InputMethodManager.HIDE_NOT_ALWAYS);
            sendGiftWindow.setBackgroundDrawable(new BitmapDrawable());
            sendGiftWindow.setFocusable(false);
            sendGiftWindow.setOutsideTouchable(true);

            int[] reb_wb_board = new int[2];
            view.getLocationInWindow(reb_wb_board);

            //popupwindow基于屏幕左上角位移到给定view中心的偏移量
            int x = 0;
            if (padLeft) {
                x = Math.abs(view.getWidth() - sendGiftWindow.getWidth()) / 2 + FullScreenTools.getStatusBarHeight(activity) + webwidth;
            } else {
                x = Math.abs(view.getWidth() - sendGiftWindow.getWidth()) / 2 + webwidth;
            }
            int y = Math.abs(reb_wb_board[1] + view.getHeight() / 2 - sendGiftWindow.getHeight() / 2);

            sendGiftWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);

            sendGiftWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    Glide.get(activity).clearMemory();
                }
            });
        }
    }

    /***
     *    发送奖杯
     * @param trophy
     * @param receiverMap
     */
    public void sendGift(Trophy trophy, HashMap<String, RoomUser> receiverMap) {
        if (trophy != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("trophyeffect", trophy.getTrophyeffect());
            map.put("trophyvoice", trophy.getTrophyvoice());
            map.put("trophyname", trophy.getTrophyname());
            map.put("trophyimg", trophy.getTrophyimg());
            RoomOperation.getInstance().sendGift(receiverMap, map, activity);
        }
    }
}
