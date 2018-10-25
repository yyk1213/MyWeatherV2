package com.example.yeon1213.myweather_v2.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.yeon1213.myweather_v2.data.Weather.Data;
import com.example.yeon1213.myweather_v2.data.Weather.FineDust.FineDust;
import com.example.yeon1213.myweather_v2.data.Weather.Weather;
import com.example.yeon1213.myweather_v2.interfaces.DataResponseListener;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class WeatherData {

    private Weather weatherData; //날씨 데이터 넣는 것
    private List<String> livingData;//리사이클러뷰에 담는 생활기상지수
    private DataResponseListener listener;//응답 값이 왔는지 확인하는 리스너
    private Context context;
    //날씨 데이터
    private String temperature;
    private String precipitation;
    private String humidity;
    private String wind;
    private String station;
    //생활기상지수 데이터
    private String heatIndex;
    private String wctIndex;
    private String thIndex;
    private String carWash;
    private String uvIndex;
    private String laundry;
    //미세먼지
    private String dust;
    private String dust_comment;
    private int temp=0;

    public WeatherData() {
    }

    public Weather getWeatherData() {
        return weatherData;
    }

    public List<String> getLivingData() {
        return livingData;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getWind() {
        return wind;
    }

    public void setListener(DataResponseListener listener) {
        this.listener = listener;
    }

    public String getDust() {
        return dust;
    }

    public String getDust_comment() {
        return dust_comment;
    }

    public void setDust_comment(String dust_comment) {
        this.dust_comment = dust_comment;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void getWeatherAPIData(double latitude, double longitude){

        //시간별 날씨
        Call<Data> call = RetrofitNetwork.get().retrofitService.getMinutely(RetrofitService.WEATHER_APPKEY, 2, latitude, longitude);

        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if (response.isSuccessful()) {

                    weatherData = response.body().getWeather();

                    if (weatherData != null) {
                        temperature = weatherData.getMinutely().get(temp).getTemperature().getTc();
                        precipitation = weatherData.getMinutely().get(temp).getPrecipitation().getSinceOntime();
                        humidity = weatherData.getMinutely().get(temp).getHumidity();

                        if(humidity==""){
                            humidity=weatherData.getMinutely().get(0).getNearValue().getHumidity();
                        }

                        wind = weatherData.getMinutely().get(temp).getWind().getWspd();
                        //station=weatherData.getMinutely().get(temp).getStation().getName();

                        listener.onWeatherResponseAvailable();
                    }
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                //프로그래스바 날씨 데이터를 받아오고 있습니다 띄우기
                Log.e("fail", "" + t.toString());
            }
        });

        //통신 가로채서 값 보여주는 것
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit dust_retrofit = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create()).baseUrl(RetrofitService.DUST_BASEURL).build();
        RetrofitService dust_apiService = dust_retrofit.create(RetrofitService.class);
        //minutely의 station 값을 미세먼지가 늦게 받아와서 오류가 뜬다.// 어떻게 받을지 생각하기...
        Call<FineDust> call_dust = dust_apiService.getDust(RetrofitService.DUST_APPKEY, 10, 10, 1, 1, "강남구", "DAILY", 1.3, "json");

        call_dust.enqueue(new Callback<FineDust>() {
            @Override
            public void onResponse(Call<FineDust> call_dust, Response<FineDust> response) {
                if (response.isSuccessful()) {
                    //통합대기환경지수
                    if(response.body()!=null) {
                        dust = response.body().getList().get(0).getKhaiGrade();
                        dust_comment = commentFineDust(dust);
                    }
                        Log.d("미세먼지", "" + dust);
                        listener.onWeatherResponseAvailable();
                }
            }

            @Override
            public void onFailure(Call<FineDust> call_dust, Throwable t) {
                Log.e("미세먼지 에러", "" + t.toString());
            }
        });
    }
    public void getHealthIndex(){

        //통신 가로채서 값 보여주는 것
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Call<JsonObject> flower_object= RetrofitNetwork.get().retrofitService.getFolwer(RetrofitService.DUST_APPKEY,1100000000,"json");
        flower_object.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                        String result=response.body().getAsJsonObject("Response").getAsJsonObject("body").getAsJsonObject("indexModel").get("today").toString();
                        Log.d(""," "+result);
                    }
                }
            @Override
            public void onFailure(Call<JsonObject> flower_object, Throwable t) {
                //프로그래스바 날씨 데이터를 받아오고 있습니다 띄우기
                Log.e("fail", "" + t.toString());
            }
        });
    }

    public void getIndexData(double latitude, double longitude) {

        livingData=new ArrayList<>();

        SharedPreferences indexSetting_pref=context.getSharedPreferences("index_setting", MODE_PRIVATE);

        boolean fire_Index=indexSetting_pref.getBoolean("열지수",false);

        if(fire_Index) {
            //열지수
            Call<Data> call_heat =  RetrofitNetwork.get().retrofitService.getHeat(RetrofitService.WEATHER_APPKEY, 2, latitude, longitude);
            call_heat.enqueue(new Callback<Data>() {
                @Override
                public void onResponse(Call<Data> call_heat, Response<Data> response) {
                    if (response.isSuccessful()) {
                        heatIndex = response.body().getWeather().getWIndex().getHeatIndex().get(0).getCurrent().getIndex();
                        Log.d("열지수", "" + heatIndex);
                        if (heatIndex != null) {
                            livingData.add("열지수: " + heatIndex);
                            listener.onIndexResponseAvailable();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Data> call_heat, Throwable t) {
                    Log.e("fail", "" + t.toString());
                }
            });
        }

        boolean tem_index=indexSetting_pref.getBoolean("체감온도",false);

        if(tem_index) {

            Call<Data> call_wct =  RetrofitNetwork.get().retrofitService.getWct(RetrofitService.WEATHER_APPKEY, 2, latitude, longitude);
            call_wct.enqueue(new Callback<Data>() {
                @Override
                public void onResponse(Call<Data> call_wct, Response<Data> response) {
                    if (response.isSuccessful()) {
                        wctIndex = response.body().getWeather().getWIndex().getWctIndex().get(0).getCurrent().getIndex();
                        Log.d("wctIndex", "" + wctIndex);
                        if (wctIndex != null) {
                            livingData.add("체감온도: " + wctIndex);
                            listener.onIndexResponseAvailable();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Data> call_wct, Throwable t) {

                }
            });
        }

        boolean uncomfortable_index=indexSetting_pref.getBoolean("불쾌지수",false);

        if(uncomfortable_index) {

            Call<Data> call_thIndex =  RetrofitNetwork.get().retrofitService.getTh(RetrofitService.WEATHER_APPKEY, 2, latitude, longitude);
            call_thIndex.enqueue(new Callback<Data>() {
                @Override
                public void onResponse(Call<Data> call_thIndex, Response<Data> response) {
                    if (response.isSuccessful()) {
                        thIndex = response.body().getWeather().getWIndex().getThIndex().get(0).getCurrent().getIndex();
                        Log.d("thIndex", "" + thIndex);
                        if (thIndex != null) {
                            livingData.add("불쾌지수: " + thIndex);
                            listener.onIndexResponseAvailable();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Data> call_thIndex, Throwable t) {

                }
            });
        }

        boolean carwash_index=indexSetting_pref.getBoolean("세차지수",false);

        if(carwash_index) {

            Call<Data> call_carwash =  RetrofitNetwork.get().retrofitService.getCarwash(RetrofitService.WEATHER_APPKEY, 2, latitude, longitude);
            call_carwash.enqueue(new Callback<Data>() {
                @Override
                public void onResponse(Call<Data> call_carwash, Response<Data> response) {

                    if (response.isSuccessful()) {
                        carWash = response.body().getWeather().getWIndex().getCarWash().get(0).getComment();
                        Log.d("세차지수", "" + carWash);
                        if (carWash != null) {
                            livingData.add("세차지수:" + carWash);
                            listener.onIndexResponseAvailable();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Data> call_carwash, Throwable t) {
                    Log.e("fail", "" + t.toString());
                }
            });
        }

        boolean uv_Index=indexSetting_pref.getBoolean("자외선지수",false);

        if(uv_Index) {

            Call<Data> call_uvIndex =  RetrofitNetwork.get().retrofitService.getUv(RetrofitService.WEATHER_APPKEY, 2, latitude, longitude);
            call_uvIndex.enqueue(new Callback<Data>() {
                @Override
                public void onResponse(Call<Data> call_uvIndex, Response<Data> response) {
                    if (response.isSuccessful()) {
                        uvIndex = response.body().getWeather().getWIndex().getUvIndex().get(0).getDay01().getComment();
                        Log.d("uvIndex", "" + uvIndex);
                        if (uvIndex != null) {
                            livingData.add("자외선지수: " + uvIndex);
                            listener.onIndexResponseAvailable();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Data> call_uvIndex, Throwable t) {

                }
            });
        }

        boolean laundry_index=indexSetting_pref.getBoolean("빨래지수",false);

        if(laundry_index) {

            Call<Data> call_Laundry =  RetrofitNetwork.get().retrofitService.getLaundry(RetrofitService.WEATHER_APPKEY, 2, latitude, longitude);

            call_Laundry.enqueue(new Callback<Data>() {
                @Override
                public void onResponse(Call<Data> call_Laundry, Response<Data> response) {

                    if (response.isSuccessful()) {
                        //시간별 데이터를 받음
                        laundry = response.body().getWeather().getWIndex().getLaundry().get(0).getDay01().getComment();
                        if (laundry != null) {
                            livingData.add("빨래지수: " + laundry);
                            listener.onIndexResponseAvailable();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Data> call_Laundry, Throwable t) {

                }
            });

        }

        else if(livingData.size()==0) {
            listener.onIndexResponseAvailable();
        }
    }

    private String commentFineDust(String dust){
        if(dust.equals("1")) return "좋음";
        else if(dust.equals("2")) return "보통";
        else if(dust.equals("3")) return "나쁨";
        else if(dust.equals("4")) return "매우 나쁨";

        return "";
    }
}

