package com.classroomsdk.interfaces;

import android.graphics.PointF;

/**
 * Created by fucc on 2019/1/17.
 *
 * 画板提笔回调
 */

public interface PaintPadActionUp {
    void drawActionUp(PointF stopPoint,String fromId);
}
