package com.wzx.wzxfoundation.http;

/**
 * Created by wangzixu on 2016/11/30.
 */
public class dataResListenerAdapter<T> implements dataResListener<T> {
    @Override
    public void onBegin() {}

    @Override
    public void onDataEmpty() {}

    @Override
    public void onDataFailed(String errmsg) {}

    @Override
    public void onDataSucess(T t) {}
}
