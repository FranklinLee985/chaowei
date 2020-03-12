package com.classroomsdk.interfaces;


import com.classroomsdk.bean.SmallPaintBean;
import com.classroomsdk.bean.StudentListBean;

import java.util.List;

/**
 * 小白板状态至小白板popwindow界面回调
 */
public interface SmallBoardInterface {

    //白板分发时老师
    void setTeacher(StudentListBean bean);

    //学生加入时
    void setStudents(List<StudentListBean> listBeans);

    //当前小白板状态
    void setStatus(SmallPaintBean smallPaintBean);
}
