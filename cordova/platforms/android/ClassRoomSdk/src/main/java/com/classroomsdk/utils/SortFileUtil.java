package com.classroomsdk.utils;

import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.comparator.NameComparator;
import com.classroomsdk.comparator.TimeComparator;
import com.classroomsdk.comparator.TypeComparator;

import java.util.Collections;
import java.util.List;

/**
 * date 2019/5/8
 * version
 * describe
 *
 * @author hxd
 */
public class SortFileUtil {

    public static final int SORT_TYPE_TIME = 1;
    public static final int SORT_TYPE_TYPE = 2;
    public static final int SORT_TYPE_NAME = 3;

    private static SortFileUtil mInstance;

    private SortFileUtil() {
    }

    public static SortFileUtil getInstance() {
        if (mInstance == null) {
            synchronized (SortFileUtil.class) {
                if (mInstance == null) {
                    mInstance = new SortFileUtil();
                }
            }
        }
        return mInstance;
    }

    private int mSortType = SORT_TYPE_TIME;
    private boolean isUp = false;
    private TimeComparator timeComparator;
    private TypeComparator typeComparator;
    private NameComparator nameComparator;

    public List<ShareDoc> toSort(int sortType, boolean isup, List<ShareDoc> fileList, boolean isAddList) {
        this.isUp = isup;
        this.mSortType = sortType;

        return toSort(fileList, isAddList);
    }

    public List<ShareDoc> toSort(List<ShareDoc> fileList, boolean isAddList) {

        ShareDoc shareDoc = null;

        //对教室文件排序
        if (fileList.size() > 0) {
            for (int x = fileList.size() - 1; x >= 0; x--) {
                if (fileList.get(x) != null && fileList.get(x).getFileid() == 0) {
                    shareDoc = fileList.get(x);
                    fileList.remove(x);
                }
            }

            getComparator(fileList);

            if (isAddList && shareDoc != null) {
                fileList.add(0, shareDoc);
                shareDoc = null;
            }
        }
        return fileList;
    }

    private void getComparator(List<ShareDoc> fileList) {
        switch (mSortType) {
            case SORT_TYPE_TIME:
                if (timeComparator == null) {
                    timeComparator = new TimeComparator();
                }
                timeComparator.setisUp(isUp);
                Collections.sort(fileList, timeComparator);
                break;
            case SORT_TYPE_TYPE:
                if (typeComparator == null) {
                    typeComparator = new TypeComparator();
                }
                typeComparator.setisUp(isUp);
                Collections.sort(fileList, typeComparator);
                break;
            case SORT_TYPE_NAME:
                if (nameComparator == null) {
                    nameComparator = new NameComparator();
                }
                nameComparator.setisUp(isUp);
                Collections.sort(fileList, nameComparator);
                break;
        }
    }

}
