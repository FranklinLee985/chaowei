package com.classroomsdk.bean;

/**
 * Created by Administrator on 2018/12/10/010.
 */

public class RoomCacheMessage {

     //键值
    int key;
    //缓存消息
    Object[] objects;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }
}
