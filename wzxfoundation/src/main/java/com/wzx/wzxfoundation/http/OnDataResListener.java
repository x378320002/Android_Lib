package com.wzx.wzxfoundation.http;

/**
 * Created by wangzixu on 2016/11/30.
 */
public interface OnDataResListener<T> {
    void onBegin();
    void onDataSucess(T t);
    void onDataEmpty();
    void onDataFailed(String errmsg);
}
