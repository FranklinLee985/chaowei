package com.classroomsdk.bean;

/**
 * 二层数据集
 * @param <T>
 */
public class Wb_commonTwo<T>{

    private String className;

    private T data;

    private String id;


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "{" +
                "\"className\":\"" + className + '\"' +
                ",\"data\":" + data +
                ",\"id\":\"" + id + '\"' +
                '}';
    }
}
