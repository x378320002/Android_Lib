package com.wzx.wzxfoundation.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.wzx.wzxfoundation.R;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 View.SYSTEM_UI_FLAG_VISIBLE：  显示状态栏，Activity不全屏显示(恢复到有状态的正常情况)。<br/>
 View.INVISIBLE：  隐藏状态栏，同时Activity会伸展全屏显示。<br/>
 View.SYSTEM_UI_FLAG_FULLSCREEN：  隐藏状态栏，同时Activity会伸展全屏显示, 同 View.INVISIBLE.<br/>
 View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：  显示状态栏, Activity全屏显示, 伸展到状态栏下面，所以Activity顶端布局 部分会被状态遮住, 配合透明状态栏, 即可实现沉浸式状态栏。<br/>
 View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION：  效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN.<br/>
 View.SYSTEM_UI_FLAG_HIDE_NAVIGATION：  隐藏虚拟按键(导航栏)。有些手机会用虚拟按键来代替物理按键, HIDE_NAVIGATION和FULLSCREEN都属于
 全屏类属性, 设置了全屏类属性, 会使系统布局的的fitsystemwindow失效, 因为fitsystemwindow本身就是为了控制状态栏和导航栏的,状态栏导航栏等由用户控制<br/>
 View.SYSTEM_UI_LAYOUT_FLAGS：  效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION<br/>
 View.SYSTEM_UI_FLAG_LOW_PROFILE：  状态栏显示处于低能显示状态(low profile模式)，状态栏上一些图标显示会被隐藏。<br/>
 View.SYSTEM_UI_FLAG_IMMERSIVE:  当设置为SYSTEM_UI_FLAG_FULLSCREEN或者SYSTEM_UI_FLAG_HIDE_NAVIGATION时, 向内滑动会退出全屏, 显示状态栏
 和导航条, 会触发window.getDecorView().setOnSystemUiVisibilityChangeListener <br/>
 View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY:  和SYSTEM_UI_FLAG_IMMERSIVE类似, 当设置为SYSTEM_UI_FLAG_FULLSCREEN或者SYSTEM_UI_FLAG_HIDE_NAVIGATION时, 向内滑动会退出全屏, 显示状态栏
 和导航条, 区别是显示的状态栏会覆盖到内容上, 半透明的, 内容不动, 并且过几秒会自动消失, 不会触发window.getDecorView().setOnSystemUiVisibilityChangeListener <br/>
 View.SYSTEM_UI_FLAG_LAYOUT_STABLE: 稳定的, 内容区域不变, 如由非全屏状态显示到全屏SYSTEM_UI_FLAG_FULLSCREEN状态, 如果SYSTEM_UI_FLAG_FULLSCREEN没有和STABLE
 一起使用, 则内容会自动填上去, 此时内容布局会变动, 因为是非全屏到全屏, 而加上STABLE后, 内容布局就不变, 顶部状态栏位置会留一个黑条<br/><br/>

 window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);<br/>
 window.setStatusBarColor(Color.TRANSPARENT);<br/>
 window.setNavigationBarColor(Color.TRANSPARENT); :是状态栏和导航栏透明 api21以上使用(5.0)<br/><br/>

 Android4.4的时候，加了个windowTranslucentStatus属性，实现了状态栏导航栏半透明效果，而Android5.0之后以上状态栏、导航栏支持颜色随意设定，
 所以，5.0之后一般不使用需要使用该属性，而且设置状态栏颜色与windowTranslucentStatus是互斥的。
 所以，默认情况下android:windowTranslucentStatus是false。
 也就是说：‘windowTranslucentStatus’和‘windowTranslucentNavigation’设置为true后就
 再设置‘statusBarColor’和‘navigationBarColor’就没有效果了。。<br/><br/>

 window还可以直接设置全屏模式:<br/>
 window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); 此种状态相当于 View的 SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|SYSTEM_UI_FLAG_IMMERSIVE_STICKY一起使用
 <br/><br/>

 同样, 去除全屏 可以用<br/>
 window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
 <br/>
 此时
 window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);<br/>
 window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);<br/>
 window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);<br/>
 一起使用, 会是屏幕内容填充进statusbar下面, statusbar半透明, 相当于View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN和View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION, 外加状态栏和导航栏透明<br/>
 <br/>

 综上:<br/>
 一, 屏幕全屏, 不显示状态栏, 可以使用下面两种方法:<br/>
 1, <br/>
 window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);和在activity主题中添加全屏选项等价<br/>
 2, <br/>
 用activity.getWindow().getDecorView().setSystemUiVisibility(visibility)<br/>
 visibilityq取值View.SYSTEM_UI_FLAG_FULLSCREEN(状态栏), View.SYSTEM_UI_FLAG_HIDE_NAVIGATION(导航栏)<br/><br/>

 二, 屏幕全屏, 显示半透明状态栏, 屏幕内容填充进状态栏下, 可以使用下面两种方法:<br/>
 1, <br/>
 window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);<br/>
 window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);<br/>
 window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);<br/>
 2,<br/>
 window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);<br/>
 window.setStatusBarColor(Color.TRANSPARENT);<br/>
 window.setNavigationBarColor(Color.TRANSPARENT); :是状态栏和导航栏透明 api21以上使用(5.0)<br/>
 然后<br/>
 View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREE或者 View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION<br/><br/>

 使用view的方式控制, 优势在于可以分别控制状态栏和导航栏, 并且不会影响软键盘弹出时的界面移动逻辑, window级别的全屏会使软键盘弹出时界面自动适应, 不可控<br/><br/>
 一个较少导航栏和状态栏显示原理的博客, 写的不错:https://www.jianshu.com/p/28f1954812b3?from=groupmessage
 */
public class StatusBarUtil {
    /**
     * 设置状态栏，白底黑字
     * @param activity
     */
    public static void setStatusBarWhiteBg_BlackText(Activity activity) {
        Window window = activity.getWindow();
        String os = Build.BRAND;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if ("xiaomi".equalsIgnoreCase(os)) {
                setStatusBarBgColor(activity, R.color.bai);
                setStatusBarLightModeMIUI(window, true);
            } else if ("meizu".equalsIgnoreCase(os)){
                setStatusBarBgColor(activity, R.color.bai);
                setStatusBarLightModeFlyme(window, true);
            } else if ("kubi".equalsIgnoreCase(os)){
                setStatusBarBgColor(activity, R.color.bai);
                setStatusBarLightModeKUBI(window, true);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ("google".equalsIgnoreCase(os)
                        || "Samsung".equalsIgnoreCase(os)
                        || "HUAWEI".equalsIgnoreCase(os)){
                    setStatusBarBgColor(activity, R.color.bai);
                    setStatusBarTxtColorDark(activity, true);
                } else {
                    //其他未适配厂商
                }
            }
        }
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本,如果要设置成透明请直接调用设置透明的方法
     * @param activity
     * @param colorResId
     */
    public static void setStatusBarBgColor(Activity activity,int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorResId));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTint库使4.4版本状态栏变色，需要先将状态栏设置为透明
            Window window =activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(colorResId);
        }
    }

    public static boolean setStatusBarTxtColorDark(Activity activity, boolean isDark) {
        if (activity != null) {
            int flag = activity.getWindow().getDecorView().getSystemUiVisibility();
            if (isDark) {
//                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                activity.getWindow().getDecorView().setSystemUiVisibility(flag|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                activity.getWindow().getDecorView().setSystemUiVisibility(flag&~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            return true;
        }
        return false;
    }

    /**
     * 设置状态栏图标为黑色/白色，和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     */
    public static boolean setStatusBarLightModeFlyme(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为黑色/白色，需要MIUIV6以上
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     */
    public static boolean setStatusBarLightModeMIUI(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if(dark){
                    extraFlagField.invoke(window,darkModeFlag,darkModeFlag);//状态栏透明且黑色字体
                }else{
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result=true;
            }catch (Exception e){

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为黑色/白色
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     */
    public static boolean setStatusBarLightModeKUBI(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Class layoutParams = Class.forName("android.app.StatusBarManager");
                String inverse = "";
                if (dark) {
                    inverse = "STATUS_BAR_INVERSE_GRAY";
                } else {
                    inverse = "STATUS_BAR_INVERSE_WHITE";
                }
                Field darkFlag = layoutParams.getDeclaredField(inverse);
                Field kubiFlags = WindowManager.LayoutParams.class.getDeclaredField("statusBarInverse");
                darkFlag.setAccessible(true);
                kubiFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                kubiFlags.setInt(lp, bit);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    public static void showStatusBar(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void hideStatusBar(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 当activity未非全屏模式时, 让activity内容填充进statusbar下面, 并且使状态栏透明
     * @param window
     */
    public static void statusBarOverrideContent(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 当activity未非全屏模式时, 让activity内容填充进statusbar下面, 并且使状态栏透明
     */
    public static void statusBarOverrideContent2(Activity activity) {
        Window window = activity.getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = window.getDecorView();
            int visibility = decorView.getSystemUiVisibility();
            decorView.setSystemUiVisibility(visibility|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * 设置状态栏透明
     */
    public static void setStatusBarTransparnet(Activity activity) {
        Window window = activity.getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void hideNavigation(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int visibility = decorView.getSystemUiVisibility();
        visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;//隐藏导航栏
        visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;//view获取焦点后导航栏不显示. 边缘向内化导航栏暂时显示, 不触发listener
        decorView.setSystemUiVisibility(visibility);
    }
}
