package com.classroomsdk.bean;

/**
 * 答题器 详情的bean
 * Created by YF on 2018/12/28 0028.
 */

public class AnswerDetailsBean {

    private String nickname;
    private String time;
    private String answer;

    public AnswerDetailsBean() {
    }

    public AnswerDetailsBean(String nickname, String time, String answer) {
        this.nickname = nickname;
        this.time = time;
        this.answer = answer;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
