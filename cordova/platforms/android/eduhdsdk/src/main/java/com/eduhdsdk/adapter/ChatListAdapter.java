package com.eduhdsdk.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.ChatData;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.tools.HttpTextView;
import com.eduhdsdk.tools.Translate;
import com.eduhdsdk.ui.MyIm;
import com.talkcloud.room.TKRoomManager;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import skin.support.annotation.Skinable;


/**
 * Created by Administrator on 2017/4/28.
 */
@Skinable
public class ChatListAdapter extends BaseAdapter {

    private ArrayList<ChatData> chatlist;
    private Context context;

    private OnChatListImageClickListener mOnChatListImageClickListener;

    //根据消息类型判断加载那个布局
    private final int CHAT_BG_NORMAL = 0;
    private final int CHAT_BG_SYSTEM = 1;
    private final int CHAT_BG_IMAGE = 2;

    public ChatListAdapter(ArrayList<ChatData> chatlist, Context context) {
        this.chatlist = chatlist;
        this.context = context;
    }

    public void setArrayList(ArrayList<ChatData> chatlist) {
        this.chatlist = chatlist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return chatlist.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }


    @Override
    public int getItemViewType(int position) {
        if (chatlist.size() > 0) {
            if (chatlist.get(position).isStystemMsg()) {
                return CHAT_BG_SYSTEM;
            } else if (!TextUtils.isEmpty(chatlist.get(position).getImage())) {
                return CHAT_BG_IMAGE;
            } else {
                return CHAT_BG_NORMAL;
            }
        } else {
            return CHAT_BG_NORMAL;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ViewHolderSystem viewHolderSystem = null;
        ImageHolder imageHolder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case CHAT_BG_NORMAL:
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.tk_layout_chat_list_item, parent, false);
                    holder.lin_normal = (LinearLayout) convertView.findViewById(R.id.lin_normal);
                    holder.txt_msg_nickname = (TextView) convertView.findViewById(R.id.txt_msg_nickname);
                    holder.txt_ch_msg = (HttpTextView) convertView.findViewById(R.id.txt_ch_msg);
                    holder.img_translation = (ImageView) convertView.findViewById(R.id.img_translation);
                    holder.txt_eng_msg = (TextView) convertView.findViewById(R.id.txt_eng_msg);
                    holder.view = (View) convertView.findViewById(R.id.view);
                    convertView.setTag(holder);
                    break;

                case CHAT_BG_IMAGE:
                    imageHolder = new ImageHolder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.tk_layout_image_chat_list_item, parent, false);
                    imageHolder.iv_chat = convertView.findViewById(R.id.iv_chat);
                    imageHolder.tv_name = convertView.findViewById(R.id.txt_msg_nickname);
                    convertView.setTag(R.id.iv_chat, imageHolder);
                    break;

                case CHAT_BG_SYSTEM:
                    viewHolderSystem = new ViewHolderSystem();
                    convertView = LayoutInflater.from(context).inflate(R.layout.tk_layout_system_chat_list_item, parent, false);
                    viewHolderSystem.rel_system = (FrameLayout) convertView.findViewById(R.id.rel_system);
                    viewHolderSystem.txt_ch_msg = (TextView) convertView.findViewById(R.id.txt_ch_msg);
                    viewHolderSystem.view = (View) convertView.findViewById(R.id.view);
                    convertView.setTag(viewHolderSystem);
                    break;
            }
        } else {
            switch (type) {
                case CHAT_BG_NORMAL:
                    holder = (ViewHolder) convertView.getTag();
                    break;
                case CHAT_BG_IMAGE:
                    imageHolder = (ImageHolder) convertView.getTag(R.id.iv_chat);
                    break;
                case CHAT_BG_SYSTEM:
                    viewHolderSystem = (ViewHolderSystem) convertView.getTag();
                    break;
            }
        }
        if (chatlist.size() > 0) {
            ChatData ch = chatlist.get(position);
            switch (type) {
                case CHAT_BG_NORMAL:
                    if (ch != null && ch.getUser() != null) {
                        ch.getUser().nickName = StringEscapeUtils.unescapeHtml4(ch.getUser().nickName);
                        if (TKRoomManager.getInstance().getMySelf().peerId.equals(ch.getUser().peerId)) {
                            holder.txt_msg_nickname.setTextAppearance(context, R.style.msg_nickname_color);
                            holder.txt_msg_nickname.setText(context.getString(R.string.me));
                        } else {
                            holder.txt_msg_nickname.setTextAppearance(context, R.style.white);
                            holder.txt_msg_nickname.setText(ch.getUser().nickName);
                        }
                        setTranslation(ch, position, holder.txt_ch_msg, holder.txt_eng_msg, holder.img_translation, holder.view);
                        holder.lin_normal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                    break;

                case CHAT_BG_IMAGE:
                    if (TKRoomManager.getInstance().getMySelf().peerId.equals(ch.getUser().peerId)) {
                        imageHolder.tv_name.setTextAppearance(context, R.style.msg_nickname_color);
                        imageHolder.tv_name.setText(context.getString(R.string.me));
                    } else {
                        imageHolder.tv_name.setTextAppearance(context, R.style.white);
                        imageHolder.tv_name.setText(String.format("%s：", ch.getUser().nickName));
                    }
                    final ImageView iv_chat = imageHolder.iv_chat;
                    final String image = ch.getImage();
                    Glide.with(context)
                            .asBitmap()
                            .load(image)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    int width = resource.getWidth();
                                    int height = resource.getHeight();
                                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iv_chat.getLayoutParams();
                                    layoutParams.width = 200;
                                    layoutParams.height = 200 * height / width;
                                    iv_chat.setLayoutParams(layoutParams);
                                    iv_chat.setImageBitmap(resource);
                                }
                            });
                    iv_chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnChatListImageClickListener != null) {
                                mOnChatListImageClickListener.onChatListImageClick(image);
                            }
                        }
                    });
                    break;

                case CHAT_BG_SYSTEM:
                    /*viewHolderSystem.txt_ch_msg.setTextAppearance(context, R.style.white);*/
                    if (ch != null) {
                        if (ch.getUser() != null) {
                            if (ch.isStystemMsg()) {
                                if (ch.getState() == 1) {
                                    viewHolderSystem.txt_ch_msg.setTextAppearance(context, R.style.msg_system_enter);
                                    viewHolderSystem.txt_ch_msg.setText(ch.getUser().nickName + " (" + getRoleStr(ch.getUser().role) + ") " + (ch.isInOut() ? context.getString(R.string.join) : context.getString(R.string.leave)));
                                } else {
                                    if (ch.getChatMsgState() == 1) {
                                        viewHolderSystem.txt_ch_msg.setTextAppearance(context, R.style.msg_system_ban);
                                    }
                                    String strIsHold = ch.isHold() ? context.getString(R.string.back_msg) : context.getString(R.string.re_back_msg);
                                    String temp = (String) ch.getUser().properties.get("devicetype");
                                    strIsHold = temp + strIsHold;
                                    viewHolderSystem.txt_ch_msg.setText(strIsHold);
                                }
                            }
                        } else {
                            if (ch.isStystemMsg()) {
                                if (ch.getChatMsgState() == 1) {
                                    viewHolderSystem.txt_ch_msg.setTextAppearance(context, R.style.msg_system_ban);
                                }else {
                                    viewHolderSystem.txt_ch_msg.setTextAppearance(context, R.style.msg_system_cancle);
                                }
                                viewHolderSystem.txt_ch_msg.setText(ch.getMessage());
                            }
                        }
                    }
                    viewHolderSystem.rel_system.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    break;
            }
        }
        return convertView;
    }

    public void setTranslation(final ChatData ch, final int position, final HttpTextView txt_ch_msg,
                               TextView txt_eng_msg, ImageView img_translation, View view) {

        if (!TextUtils.isEmpty(ch.getMessage())) {
            SpannableStringBuilder sb = getFace(ch.getMessage());
            txt_ch_msg.setUrlText(sb);
        }

        txt_ch_msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(ch.getMessage());
                Toast.makeText(context, context.getString(R.string.copy_success), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        img_translation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(ch.getMessage())) {
                    Translate.getInstance().translate(position, ch.getMessage().replaceAll("(\\[em_)\\d{1}(\\])",
                            ""));
                }
            }
        });

        if (!TextUtils.isEmpty(ch.getTrans())) {
            SpannableStringBuilder sbTrans = getFace(ch.getTrans());
            txt_eng_msg.setText(sbTrans);
        }
        if (RoomControler.isChineseJapaneseTranslation()) {//配置项：是否翻译日文
            if (ch.isTrans()) {
                //已经被翻译
                img_translation.setImageResource(R.drawable.tk_translation_zhongri_disable);
                view.setVisibility(View.VISIBLE);
                txt_eng_msg.setVisibility(View.VISIBLE);
            } else {
                //未被翻译
                img_translation.setImageResource(R.drawable.tk_translation_zhongri_default);
                txt_eng_msg.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            }
        } else {
            if (ch.isTrans()) {
                //已经被翻译
                img_translation.setImageResource(R.drawable.tk_translation_disable);
                view.setVisibility(View.VISIBLE);
                txt_eng_msg.setVisibility(View.VISIBLE);
            } else {
                //未被翻译
                img_translation.setImageResource(R.drawable.tk_translation_default);
                txt_eng_msg.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            }
        }

    }

    private SpannableStringBuilder getFace(String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\[em_)\\d{1}(\\])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            try {
                String png = tempText.substring("[".length(), tempText.length() - "]".length()) + ".png";
                Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open("face/" + png));
               /* Drawable drawable = new BitmapDrawable(bitmap);
                drawable.setBounds(0, 0, 30, 30);*/
                /* ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);*/
                MyIm imageSpan = new MyIm(context, bitmap);
                sb.setSpan(imageSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb;
    }

    private String getRoleStr(int role) {
        if (role == 0) {
            return context.getString(R.string.teacher);
        } else if (role == 2) {
            return context.getString(R.string.student);
        } else if (role == 4) {
            //巡課
            return context.getString(R.string.lass_patrol);
        } else if (role == 1) {  //助教
            return context.getString(R.string.assistant);
        } else {
            return "";
        }
    }

    class ViewHolder {
        TextView txt_msg_nickname, txt_ts, txt_eng_msg;
        HttpTextView txt_ch_msg;
        ImageView img_translation;
        View view;
        LinearLayout lin_normal;
    }

    class ViewHolderSystem {
        TextView txt_ch_msg, txt_ts;
        View view;
        FrameLayout rel_system;
    }

    class ImageHolder {
        TextView tv_name;
        ImageView iv_chat;
    }

    public void setOnChatListImageClickListener(OnChatListImageClickListener listImageClickListener) {
        mOnChatListImageClickListener = listImageClickListener;
    }

    public interface OnChatListImageClickListener {
        void onChatListImageClick(String image);
    }
}
