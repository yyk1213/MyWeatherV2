package com.example.yeon1213.myweather_v2.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitNetwork {

    private static RetrofitNetwork sRetrofitNetwork;
    public static String TAG="Retrofit Network";

    public static RetrofitNetwork get(){
        if(sRetrofitNetwork ==null){
            sRetrofitNetwork =new RetrofitNetwork();
        }
        return sRetrofitNetwork;
    }

    public RetrofitNetwork() {
    }


    HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient client=new OkHttpClient.Builder().addInterceptor(interceptor).build();

    Retrofit retrofit=new Retrofit.Builder().client(client).baseUrl(RetrofitService.WEATHER_BASEURL).addConverterFactory(GsonConverterFactory.create()).build();
    RetrofitService retrofitService=retrofit.create(RetrofitService.class);


    public RetrofitService getRetrofitService() {
        return retrofitService;
    }
    
    //인터셉트 해서 body 값 까지가 null일 경우 어떻게 할지 알려주기
}
