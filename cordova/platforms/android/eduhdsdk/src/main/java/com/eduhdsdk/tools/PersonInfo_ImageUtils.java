package com.eduhdsdk.tools;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Pei on 2016-12-27.
 */
public class PersonInfo_ImageUtils {

    public static int externalCacheNotAvailableState = 0;

    public static String scaleAndSaveImage(String strPath, float maxWidth, float maxHeight, Context context) {
        // 图片旋转角度
        int bitmapDegree = getBitmapDegree(strPath);
        String picLastName = strPath.substring(strPath.lastIndexOf(".") + 1).toLowerCase();
        if (picLastName.equals("bmp") || picLastName.equals("jpeg")
                || picLastName.equals("png") || picLastName.equals("jpg")) {
            Bitmap bitmap = null;
            try {
                InputStream in = new FileInputStream(strPath);
                int size = in.available();
                BitmapFactory.Options opts = new BitmapFactory.Options();
              /*  if (size > 1024 * 1024)
                    opts.inSampleSize = 2;*/
                try {
                    bitmap = BitmapFactory.decodeStream(in, null, opts);
                } catch (OutOfMemoryError e) {
                    return null;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            if (bitmap == null) {
                return null;
            }
            if (bitmapDegree != 0) {
                bitmap = rotaingImageView(bitmapDegree, bitmap);
            }
            float photoW = bitmap.getWidth();
            float photoH = bitmap.getHeight();
            if (photoW == 0 || photoH == 0) {
                return null;
            }
            float scaleFactor = Math.max(photoW / maxWidth, photoH / maxHeight);
            Bitmap scaledBitmap = null;
            if (scaleFactor <= 1) {
                scaledBitmap = bitmap;
            } else {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                        (int) (photoW / scaleFactor), (int) (photoH / scaleFactor),
                        true);
                bitmap.recycle();
            }

            String picUrl = strPath.substring(0, strPath.lastIndexOf("/"));
            String picName = "/" + strPath.substring(strPath.lastIndexOf("/") + 1);

            File f = null;
            try {
                f = new File(getCacheDir(context), picName);
            } catch (Exception e) {
                f = new File(picUrl, picName);
            }

            if (f.exists()) {
                f.delete();
            }
            try {
                FileOutputStream out1 = new FileOutputStream(f);
                if (picLastName.equalsIgnoreCase("jpg")
                        || picLastName.equalsIgnoreCase("jpeg")) {
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out1);
                } else if (picLastName.equalsIgnoreCase("png")) {
                    scaledBitmap.compress(Bitmap.CompressFormat.PNG, 90, out1);
                }
                out1.flush();
                out1.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return getCacheDir(context) + picName;
            } catch (Exception e) {
                return picUrl + picName;
            }
        } else if (picLastName.equals("doc") || picLastName.equals("docx")
                || picLastName.equals("xls") || picLastName.equals("xlsx")
                || picLastName.equals("xlt") || picLastName.equals("xlsm")
                || picLastName.equals("ppt") || picLastName.equals("pptx")
                || picLastName.equals("pps") || picLastName.equals("pos")
                || picLastName.equals("pdf") || picLastName.equals("txt")) {
            return strPath;
        } else {
            return null;
        }
    }

    /**
     * 旋转图片
     *
     * @param degree 旋转的角度
     * @param bitmap 需要旋转的图片
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int degree, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static File getCacheDir(Context context) {
        if (externalCacheNotAvailableState == 1 || externalCacheNotAvailableState == 0 && Environment.getExternalStorageState().startsWith(Environment.MEDIA_MOUNTED)) {
            externalCacheNotAvailableState = 1;
            return context.getExternalCacheDir();
        }
        externalCacheNotAvailableState = 2;
        return context.getCacheDir();
    }

    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    @TargetApi(19)
    public static String getImageAfterKitKat(Intent data, Context context) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(context, uri)) {
            //如果是document类型的Uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];  //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(context, contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果不是document类型的Uri,则使用普通方式处理
            imagePath = getImagePath(context, uri, null);
        } else {
            imagePath = getFileUri(uri, context).toString();
        }
        return imagePath;
    }

    public static String getImageBeforeKitKat(Intent data, Context context) {
        Uri uri = data.getData();
        String imagePath = getImagePath(context, uri, null);
        return imagePath;
    }

    public static String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public static Uri getFileUri(Uri uri, Context context) {
        if (uri.getScheme().equals("file")) {
            String path = uri.getEncodedPath();

            if (path != null) {
                path = Uri.decode(path);

                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();

                buff.append("(")
                        .append(MediaStore.Images.ImageColumns.DATA)
                        .append("=").append("'" + path + "'").append(")");

                Cursor cur = cr.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);

                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    //do nothing
                } else {
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
