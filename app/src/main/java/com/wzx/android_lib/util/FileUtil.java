package com.wzx.android_lib.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * 简易的处理文件的工具类
 */
public class FileUtil {
    public static final String TAG = "FileUtil";
    protected static final int BUFFER_SIZE = 4096;

    public abstract static class ProgressListener {
        public void onStart(long total){

        }
        public void onProgress(long current, long total){

        }
        public void onSuccess(){

        }
        public void onFailure(){

        }
    }

    public static boolean writeInputStreamToFile(InputStream inputStream, File file, long totalSize, ProgressListener listener) {
        if (inputStream == null || file == null) {
            Log.d(TAG, "writeInputStreamToFile inputStream or file == null");
            return false;
        }
        if (listener != null) {
            listener.onStart(totalSize);
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, false);
            byte[] tmp = new byte[BUFFER_SIZE];
            int size = 0, count;
            while ((count = inputStream.read(tmp)) != -1) {
                size += count;
                outputStream.write(tmp, 0, count);
                if (listener != null) {
                    listener.onProgress(size, totalSize);
                }
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onFailure();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.w(TAG, "Cannot flush output stream");
            }
        }
        if (listener != null) {
            listener.onSuccess();
        }
        return true;
    }

    /**
     * 获取一个文件夹的大小，单位字节
     */
    public static long getFolderSize(File file) {
        if (file == null) {
            return 0;
        }
        long size = 0;
        try {
            if (file.isFile()) {
                return file.length();
            }
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (File f : fileList) {
                    size = size + getFolderSize(f);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getFolderSize exception");
            e.printStackTrace();
        }
        return size;
    }

    public static void deleteFile(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                deleteContents(file, true);
            } else {
                try {
                    if (!file.delete()) {
                        throw new IOException("failed to delete file: " + file);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "getFolderSize exception");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除指定目录下的所有内容
     */
    public static void deleteContents(File dir, boolean deleteSelf) {
        if (dir == null) {
            return;
        }
        if (!dir.isDirectory()) {
            try {
                dir.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            try {
                if (file.isDirectory()) {
                    deleteContents(file, true);
                    if (deleteSelf) {
                        dir.delete();
                    }
                } else {
                    file.delete();
                }
            } catch (Exception e) {
                Log.e(TAG, "getFolderSize exception");
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取格式化的文件大小数字
     */
    public static String getFormatSize(double size) {
        double kb = size / 1024;
        if (kb < 1) {
            return size + "Byte";
        }
        DecimalFormat format = new DecimalFormat(".00");//必须保留两位小数，不够0补零
        double mb = kb / 1024;
        if (mb < 1) {
            return format.format(kb) + "KB";
        }

        double gb = mb / 1024;
        if (gb < 1) {
            return format.format(mb) + "MB";
        }

        double tb = gb / 1024;
        if (tb < 1) {
            return format.format(gb) + "GB";
        }
        return format.format(gb) + "TB";
    }
    /**
     * 保存bitmap到本地
     */
    public static boolean saveBitmapToFile(Context context, Bitmap destBitmap, File f, final boolean notifySystem) {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (fos == null) {
            return false;
        }

        destBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        boolean success = false;
        try {
            fos.flush();
            fos.close();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //发送扫描文件的广播,使系统读取到刚才存的图片
            if (notifySystem && f.exists() && success) {
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(f));
                context.sendBroadcast(intent);
            }
        }
        return success;
    }
}
