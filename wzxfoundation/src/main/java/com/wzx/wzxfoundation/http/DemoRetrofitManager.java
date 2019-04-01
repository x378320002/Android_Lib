package com.wzx.wzxfoundation.http;

import com.wzx.wzxfoundation.util.LogHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 示例, demo, 不能直接用
 */
public class DemoRetrofitManager {
    //单例begin
    private static DemoRetrofitManager sInstance = null;
    private final DemoRetrofitApiInterface mApis;

    public static DemoRetrofitApiInterface getInstance() {
        if (sInstance == null) {
            synchronized (DemoRetrofitManager.class) {
                if (sInstance == null) {
                    sInstance = new DemoRetrofitManager();
                }
            }
        }
        return sInstance.mApis;
    }
    //单例end

    private DemoRetrofitManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("Connection", "close")
                        .addHeader("accept", "application/json")
                        .build();
                Response response = chain.proceed(request);
                return response;
            }
        });

        if (LogHelper.DEBUG) { //okttp显示log
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }

        OkHttpClient build = builder.build();
        Retrofit mRetrofit = new Retrofit.Builder()
                .client(build)
                .baseUrl("http://192.168.31.210:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mApis = mRetrofit.create(DemoRetrofitApiInterface.class);
    }
}
