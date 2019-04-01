package com.wzx.wzxfoundation.util;

import android.util.ArrayMap;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2018/10/17.
 * 在activity之间传递大列表的中间类,
 * 直接用intent和parcelable传递只能传递单值, 不能传递数据量未知的列表
 */
public class MyPassValueUtil {
    //单例begin
    private static MyPassValueUtil sInstance = null;
    private static final String key = "extra";

    private MyPassValueUtil() {
        mMap = new ArrayMap<>();
    }

    public static MyPassValueUtil getInstance() {
        if (sInstance == null) {
            synchronized (MyPassValueUtil.class) {
                if (sInstance == null) {
                    sInstance = new MyPassValueUtil();
                }
            }
        }
        return sInstance;
    }
    //单例end

    private ArrayMap<String, Object> mMap;


    public <T> void putList(ArrayList<T> value) {
        mMap.clear();
        ArrayList<T> list = new ArrayList<>();//为了把放入和取出的list分离开, 如果是同一个的话, 下一个的对集合的操作会影响前一个页面集合
        list.addAll(value);
        mMap.put(key, list);
    }

    public <T> ArrayList<T> getList() {
        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (ArrayList<T>) o;
        } catch (ClassCastException e) {
            LogHelper.d("MyPassValueUtil", "getList ClassCastException = " + e.getMessage());
            return null;
        } finally {
            mMap.clear();
        }
    }
}
