package com.example.yeon1213.myweather_v2.network;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitNetwork {

    public static String TAG = "Retrofit Network";

    private static RetrofitNetwork sRetrofitNetwork;

    public static RetrofitNetwork get() {
        if (sRetrofitNetwork == null) {
            sRetrofitNetwork = new RetrofitNetwork();
        }
        return sRetrofitNetwork;
    }

    public RetrofitNetwork() {
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();

            okhttp3.Response response=chain.proceed(request);

            if(response.body()==null){
                throw new IOException("Response Body is null");
            }
            return chain.proceed(request);
        }
    }).build();

    Retrofit retrofit = new Retrofit.Builder().baseUrl(RetrofitService.WEATHER_BASEURL).client(client).addConverterFactory(GsonConverterFactory.create()).build();
    RetrofitService retrofitService = retrofit.create(RetrofitService.class);

    public RetrofitService getRetrofitService() {
        return retrofitService;
    }
}
