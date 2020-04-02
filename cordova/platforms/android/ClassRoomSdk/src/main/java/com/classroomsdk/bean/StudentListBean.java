package com.classroomsdk.bean;

/**
 * 小白板学生数据
 */
public class StudentListBean {


    /**
     * id : ec3b0cc4-26c8-0071-4ae1-651ebe32c05d
     * nickname : 1
     * role : 2
     * publishstate : 0
     */

    private String id;
    private String nickname;
    private int role;
    private Integer publishstate;


    public StudentListBean(String id, String nickname, int role) {
        this.id = id;
        this.nickname = nickname;
        this.role = role;
    }

    public StudentListBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public Integer getPublishstate() {
        return publishstate;
    }

    public void setPublishstate(Integer publishstate) {
        this.publishstate = publishstate;
    }

}
