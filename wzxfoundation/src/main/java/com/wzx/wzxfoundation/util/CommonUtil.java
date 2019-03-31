package com.wzx.wzxfoundation.util;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommonUtil {
    private static final String TAG = "CommonUtil";
    private static final String KEY_SP_PHONE_MODEL = "phonemodel";
    private static final String KEY_SP_IMEI = "phoneimei";

    /**
     * 方法名称:transMapToString
     * 传入参数:map
     * 返回值:String 形如 username'chenziwen^password'1234
     */
    public static String transMapToString(Map map) {
        if (map == null) {
            return "[null]";
        }
        Map.Entry entry;
        StringBuffer sb = new StringBuffer("{");
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            entry = (Map.Entry) iterator.next();
            sb.append(entry.getKey().toString()).append(":")
                    .append(null == entry.getValue() ? "null" : entry.getValue().toString())
                    .append(iterator.hasNext() ? " , " : "");
        }
        sb.append("}");
        return sb.toString();
    }

    public static boolean isPkgInstalled(Context context, String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (Exception e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    /**
     * 测量view的尺寸，实际上view的最终尺寸会由于父布局传递来的MeasureSpec和view本身的LayoutParams共同决定
     * 这里预先测量，由自己给出的MeasureSpec计算尺寸
     *
     * @param view
     */
    public static void haokanMeasure(View view) {
        int sizeWidth, sizeHeight, modeWidth, modeHeight;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            sizeWidth = 0;
            modeWidth = View.MeasureSpec.UNSPECIFIED;
        } else {
            sizeWidth = layoutParams.width;
            modeWidth = View.MeasureSpec.EXACTLY;
        }
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            sizeHeight = 0;
            modeHeight = View.MeasureSpec.UNSPECIFIED;
        } else {
            sizeHeight = layoutParams.height;
            modeHeight = View.MeasureSpec.EXACTLY;
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(sizeWidth, modeWidth),
                View.MeasureSpec.makeMeasureSpec(sizeHeight, modeHeight)
        );
    }

    //快速点击防抖相关***begin
    private static long sLastClickTime;
    private static Object sClickView;

    /**
     * 是否快速点击
     *
     * @return
     */
//    public static synchronized boolean isQuickClick() {
//        long time = SystemClock.uptimeMillis();
//        if (time - sLastClickTime < 250) {
//            return true;
//        }
//        sLastClickTime = time;
//        return false;
//    }

    /**
     * 是否快速点击
     *
     * @return
     */
    public static synchronized boolean isQuickClick(View view) {
        long time = SystemClock.uptimeMillis();
        if (sClickView == view) {
            if (time - sLastClickTime < 500) {
                return true;
            }
        }
        sClickView = view;
        sLastClickTime = time;
        return false;
    }
    //快速点击防抖相关***end

    /**
     * 获取应用程序版本的名称，清单文件中的versionName属性
     */
    public static String getLocalVersionName(Context c) {
        try {
            PackageManager manager = c.getPackageManager();
            PackageInfo info = manager.getPackageInfo(c.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    /**
     * 获取应用程序版本的名称，清单文件中的versionCode属性
     */
    public static int getLocalVersionCode(Context c) {
        try {
            PackageManager manager = c.getPackageManager();
            PackageInfo info = manager.getPackageInfo(c.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getPhoneModel(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String phone = sharedPreferences.getString(KEY_SP_PHONE_MODEL, "");
        if (TextUtils.isEmpty(phone)) {
            try {
                phone = Build.MODEL;
                if (!TextUtils.isEmpty(phone)) {
                    sharedPreferences.edit().putString(KEY_SP_PHONE_MODEL, phone).apply();
                }
            } catch (Exception e) {
                Log.e("CommonUtil", "getPhoneModel exception = " + e.getMessage());
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(phone)) {
            phone = "defaultPhone";
        }
        return phone;
    }

    /**
     * 获取渠道id
     */
    public static String getPid(Context c) {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        String pid = "";
        if (TextUtils.isEmpty(pid)) {
            ApplicationInfo appInfo = null;
            try {
                appInfo = c.getPackageManager().getApplicationInfo(c.getPackageName(), PackageManager.GET_META_DATA);
                pid = String.valueOf(appInfo.metaData.getInt("UMENG_CHANNEL"));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(pid)) {
                pid = "200";
            } else {
//                sharedPreferences.edit().putString(Values.PreferenceKey.KEY_SP_PID, pid).commit();
            }
        }
        return pid;
    }

    /**
     * 获取MAC地址android.os.Build.VERSION.SDK_INT
     * 需权限android.Manifest.permission.ACCESS_WIFI_STATE
     *
     * @return
     */
    public static String getMAC(Context context) {
//        if (checkPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
//            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifi.getConnectionInfo();
//            String macAddress = info.getMacAddress();
//            if (macAddress == null) {
//                return "";
//            } else {
//                return macAddress;
//            }
//        } else {
//            return "";
//        }
        //以上方法在6.0以上不能用了
//        String macString = "02:00:00:00:00:xx";
        String macString = "";
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    continue;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X-", b)); //转换成16进制, 宽度为2位, 不够的补零
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return macString;
    }

    /**
     * 目的就是获取一个唯一的值
     * 获取IMEI号, 有些手机获取不到, 或者没权限, 为了能保证获取到唯一的id, 随机生成一个保证唯一的id
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String imei = sharedPreferences.getString(KEY_SP_IMEI, "");
        if (TextUtils.isEmpty(imei)) {
            //首先获取一个类似唯一的东西, 不同品牌不同型号的手机, 这个值应该不同, 然后再加上一个时间戳, 再加个然随机数, 缺点是每次重装都会变
            String serial = Build.BRAND + Build.MANUFACTURER + Build.PRODUCT + System.currentTimeMillis() + Math.random();
            imei = BinaryUtil.calculateMd5Str(serial);
            sharedPreferences.edit().putString(KEY_SP_IMEI, imei).apply();
        }
        return imei;
//        if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
//            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            String imei = tm.getDeviceId();
//            if (imei != null) {
//                return imei;
//            } else {
//                return "";
//            }
//        } else {
//            return "";
//        }
    }

    public static boolean checkPermission(Context context, String permissionStr) {
        PackageManager pm = context.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permissionStr, context.getPackageName()));
        return permission;
    }

    public static String getDevice() {
        HashMap map = new HashMap();
        map.put("Build.MANUFACTURER", Build.MANUFACTURER);
        map.put("Build.MODEL", Build.MODEL);
        map.put("Build.VERSION.SDK_INT", Build.VERSION.SDK_INT);
        map.put("Build.BOARD", Build.BOARD);
        map.put("Build.BOOTLOADER", Build.BOOTLOADER);
        map.put("Build.PRODUCT", Build.PRODUCT);
        map.put("Build.DISPLAY", Build.DISPLAY);
        map.put("Build.FINGERPRINT", Build.FINGERPRINT);
        map.put("Build.getRadioVersion", Build.getRadioVersion());
        map.put("Build.SERIAL", Build.SERIAL);
        map.put("Build.ID", Build.ID);
        map.put("Build.VERSION.INCREMENTAL", Build.VERSION.INCREMENTAL);
        map.put("Build..VERSION.BASE_OS", Build.VERSION.BASE_OS);
        map.put("Build..VERSION.CODENAME", Build.VERSION.CODENAME);
        map.put("Build..VERSION.RELEASE", Build.VERSION.RELEASE);
        map.put("Build..VERSION.SECURITY_PATCH", Build.VERSION.SECURITY_PATCH);
        return transMapToString(map);
    }

    /**
     * 返回手机运营商名称，在调用支付前调用作判断
     * @return 0, 未授权或者没有 1 移动 2 联通 3 电信
     */
    public static int getProvidersName(Context context) {
        int ProvidersName = 0;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return 0;
        }
        String IMSI = telephonyManager.getSubscriberId();
        if (IMSI == null) {
            return 0;
        }

        if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
            ProvidersName = 1;
        } else if (IMSI.startsWith("46001")) {
            ProvidersName = 2;
        } else if (IMSI.startsWith("46003")) {
            ProvidersName = 3;
        }
        return ProvidersName;
    }

    /**
     * 查询是否有相应的intent的app
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean deviceCanHandleIntent(final Context context, final Intent intent) {
        try {
            final PackageManager packageManager = context.getPackageManager();
            final List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
            return activities != null && !activities.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


//    public static boolean checkIDValidator(String id) {
//        String str = "[1-9]{2}[0-9]{4}(19|20)[0-9]{2}"
//                + "((0[1-9]{1})|(1[1-2]{1}))((0[1-9]{1})|([1-2]{1}[0-9]{1}|(3[0-1]{1})))"
//                + "[0-9]{3}[0-9x]{1}";
//        Pattern pattern = Pattern.compile(str);
//        return pattern.matcher(id).matches() ? false : true;
//    }
    /**
     * 我国公民的身份证号码特点如下
     * 1.长度18位
     * 2.第1-17号只能为数字
     * 3.第18位只能是数字或者x
     * 4.第7-14位表示特有人的年月日信息
     * 请实现身份证号码合法性判断的函数，函数返回值：
     * 1.如果身份证合法返回0
     * 2.如果身份证长度不合法返回1
     * 3.如果第1-17位含有非数字的字符返回2
     * 4.如果第18位不是数字也不是x返回3
     * 5.如果身份证号的出生日期非法返回4
     * 校验身份证
     */
    public static boolean checkChinaShenfenzheng(String IDNumber) {
        if (IDNumber == null || "".equals(IDNumber)) {
            return false;
        }
        // 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
        String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
        boolean matches = IDNumber.matches(regularExpression);
        //判断第18位校验值
        if (matches) {
            if (IDNumber.length() == 18) {
                try {
                    char[] charArray = IDNumber.toCharArray();
                    //前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    //这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    if (idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase())) {
                        return true;
                    } else {
                        Log.d("checkChinaShenfenzheng", "异常:" + IDNumber);
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("checkChinaShenfenzheng", "异常:" + IDNumber);
                    return false;
                }
            }
        }
        return matches;
    }

    /**
     * 检查给出的权限中哪些是没有被授权的
     * @param pers
     * @return
     */
    public static String[] getDenyPermission(Context contex, String[] pers) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < pers.length; i++) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(contex, pers[i]);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) { //没有这个权限
                list.add(pers[i]);
            }
        }
        String[] strings = new String[list.size()];
        list.toArray(strings);
        return strings;
    }

    /**
     * 检查是否是手机号
     * @param phoneNumber
     * @return
     */
    public static boolean checkPhoneNum(String phoneNumber) {
        if (phoneNumber.toString().trim().replace(" ", "").matches("^[1][123456789]\\d{9}$")) {
            return true;
        }
        return false;
    }
}
