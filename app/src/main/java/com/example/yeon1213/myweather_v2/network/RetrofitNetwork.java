package com.example.yeon1213.myweather_v2.network;

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

    Retrofit retrofit=new Retrofit.Builder().baseUrl(RetrofitService.WEATHER_BASEURL).addConverterFactory(GsonConverterFactory.create()).build();
    RetrofitService retrofitService=retrofit.create(RetrofitService.class);

    public RetrofitService getRetrofitService() {
        return retrofitService;
    }
}
