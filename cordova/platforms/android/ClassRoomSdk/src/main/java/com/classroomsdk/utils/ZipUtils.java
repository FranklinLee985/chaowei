package com.classroomsdk.utils;

import com.classroomsdk.http.IProgress;
import com.classroomsdk.manage.ProLoadingDoc;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.util.List;

public class ZipUtils {

    public ZipUtils() {

    }

    /**
     * 解压zip到指定的路径
     *
     * @param zipFileString ZIP的名称
     * @param outPathString 要解压缩路径
     */
    public static void UnZipFolder(String zipFileString, String outPathString, IProgress iProgress) {
        if (!new File(zipFileString).exists()) {
            iProgress.onError("Zip不存在");
            return;
        }
        try {
            ZipFile zipFile = new ZipFile(zipFileString);
            List fileHeaderList = zipFile.getFileHeaders();
            if (fileHeaderList == null) {
                iProgress.onError("Zip错误");
                return;
            }
            for (int i = 0; i < fileHeaderList.size(); i++) {
                FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
                int progress = (((i + 1) * 100) / fileHeaderList.size());
                iProgress.onProgress(progress);
                zipFile.extractFile(fileHeader, outPathString);
            }
            if (ProLoadingDoc.getInstance().exit) {
                iProgress.onError("解压错误");
                return;
            }
            iProgress.onDone();
        } catch (ZipException e) {
            iProgress.onError(e.getMessage());
        } finally {
            //关闭之后可选择删除压缩包
            //deleteFile(filePath);
        }
    }

}