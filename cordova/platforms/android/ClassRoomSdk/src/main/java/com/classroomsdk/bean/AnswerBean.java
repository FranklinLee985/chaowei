package com.classroomsdk.bean;

/**答题器 答案的bean
 * Created by YF on 2018/12/28 0028.
 */

public class AnswerBean {

    private String answerame;
    private boolean checked;


    public AnswerBean() {
    }

    public AnswerBean(String name, boolean checked) {
        this.answerame = name;
        this.checked = checked;
    }

    public String getName() {
        return answerame;
    }

    public void setName(String name) {
        this.answerame = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
