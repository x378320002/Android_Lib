package com.wzx.wzxfoundation.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogHelper {
    public static boolean DEBUG = true;

    public static void i(String TAG, String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void w(String TAG, String msg) {
        if (DEBUG) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String TAG, String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void d(String TAG, String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    // 使用Log来显示调试信息,因为log在实现上每个message有4k字符长度限制
    // 所以这里使用自己分节的方式来输出足够长度的message
    public static void iLongLog(String TAG, String str) {
        if (DEBUG) {
            str = str.trim();
            int index = 0;
            int maxLength = 4000;
            String sub;
            while (index < str.length()) {
                // java的字符不允许指定超过总的长度end
                if (str.length() <= index + maxLength) {
                    sub = str.substring(index);
                } else {
                    sub = str.substring(index, index + maxLength);
                }

                index += maxLength;
                Log.i(TAG, sub);
            }
        }
    }

    public static void writeLog(final Context context, final String log) {
        if (!DEBUG) {
            return;
        }

//        final Scheduler.Worker worker = Schedulers.io().createWorker();
//        worker.schedule(new Action0() {
//            @Override
//            public void call() {
//                String time = WzxDateTimeUtil.getCurrentLongTime();
//                write(context, time + " @ " + log);
//                worker.unsubscribe();
//            }
//        });
    }

    /**
     * 写log进文件系统,方便以后查看
     */
    private static void write(final Context context, final String log) {
        BufferedWriter writer = null;
        try {
            final String path;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WzxLog";
            } else {
                path = context.getFilesDir().getAbsolutePath() + "/WzxLog";
            }
            LogHelper.d("wangzixu", "write path = " + path + ", log = " + log);

            final File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            final File file = new File(dir, "hklog.txt");

            boolean append = true;
            if (file.exists() && file.length() > 1048576) { //最大存1M的log
                append = false;
            } else if (!file.exists()) {
                file.createNewFile();
            }

            writer = new BufferedWriter(new FileWriter(file, append), 1024);
            writer.write(log);
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
