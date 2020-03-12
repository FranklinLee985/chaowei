package com.classroomsdk.interfaces;

/**
 *
 * 小白板输入文字的回调
 * Created by fucc on 2019/3/8.
 */

public interface EditTextInputControl {
    //显示位置
    void showTextInput(float x, float y, int textSize, int textColor);

    //变化文本内容
    void changeTextInput(String text);

    //移除EditText
    void removeEditText();
}
