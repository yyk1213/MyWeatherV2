package com.example.yeon1213.myweather_v2.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.yeon1213.myweather_v2.data.Weather.Data;
import com.example.yeon1213.myweather_v2.data.Weather.FineDust.FineDust;
import com.example.yeon1213.myweather_v2.data.Weather.Weather;
import com.example.yeon1213.myweather_v2.interfaces.DataResponseListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
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

    private static final int WEATHER_VERSION = 2;
    private static final String TAG = "WeatherData";
    private static final String WEATHER_DATA_NULL_ERROR = "weather data response body is null";

    private Weather weatherData; //날씨 데이터 넣는 것
    private DataResponseListener listener;//응답 값이 왔는지 확인하는 리스너
    private Context context;
    private HashMap<String,String> IndexData;
    private double latitude;
    private double longitude;
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
    private int temp = 0;

    public WeatherData(Context context, double latitude, double longitude, DataResponseListener dataResponseListener) {

        this.context = context;
        this.listener = dataResponseListener;
        this.latitude = latitude;
        this.longitude = longitude;

        getWeatherAPIData(latitude, longitude);//이 함수를 여기서 부를 경우 생길 수 있는 문제--리스너로 해결
    }

    public Weather getWeatherData() {
        return weatherData;
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

    public String getDust() {
        return dust;
    }

    public String getDust_comment() {
        return dust_comment;
    }

    public Context getContext() {
        return context;
    }

    public void callIndexAPI() {

        IndexData = new HashMap<>();

        getHeatIndex();
        getWctIndex();
        getUvIndex();
        getThIndex();
        getCarWashIndex();
        getLaundryIndex();
    }

    public HashMap<String, String> getIndexData() {
        return IndexData;
    }

    public void getAPIData(double latitude, double longitude) {

        //시간별 날씨
        Call<JsonObject> call = RetrofitNetwork.get().retrofitService.getTest(RetrofitService.WEATHER_APPKEY, WEATHER_VERSION, latitude, longitude);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body().get("weather");
                JsonElement tempe = jsonElement.getAsJsonObject().get("minutely").getAsJsonArray().get(0).getAsJsonObject().get("temperature").getAsJsonObject().get("tc");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void getWeatherAPIData(final double latitude, final double longitude) {

        //시간별 날씨
        Call<Data> call = RetrofitNetwork.get().retrofitService.getMinutely(RetrofitService.WEATHER_APPKEY, WEATHER_VERSION, latitude, longitude);

        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body().getWeather() != null) {
                            weatherData = response.body().getWeather();

                            temperature = weatherData.getMinutely().get(0).getTemperature().getTc();
                            precipitation = weatherData.getMinutely().get(temp).getPrecipitation().getSinceOntime();
                            humidity = weatherData.getMinutely().get(temp).getHumidity();

                            if (humidity.equals("")) {
                                humidity = weatherData.getMinutely().get(0).getNearValue().getHumidity();
                            }

                            wind = weatherData.getMinutely().get(temp).getWind().getWspd();
                            station = weatherData.getMinutely().get(temp).getStation().getName();

                            listener.onWeatherResponseAvailable();

                        } else {//에러 처리는 사용하는 곳에서 한다. 로그랑 Exception 둘다 하는 건 의미 없음
                            Log.e(TAG, WEATHER_DATA_NULL_ERROR);
                            throw new InternalError(WEATHER_DATA_NULL_ERROR);
                        }
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

                Log.e(TAG, "" + t.toString());
                throw new InternalError(t.toString());
            }
        });

        Retrofit dust_retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(RetrofitService.DUST_BASEURL).build();
        RetrofitService dust_apiService = dust_retrofit.create(RetrofitService.class);
        //minutely의 station 값을 미세먼지가 늦게 받아와서 오류가 뜬다.// 어떻게 받을지 생각하기...
        Call<FineDust> call_dust = dust_apiService.getDust(RetrofitService.DUST_APPKEY, 10, 10, 1, 1, "강남구", "DAILY", 1.3, "json");
        call_dust.enqueue(new Callback<FineDust>() {
            @Override
            public void onResponse(Call<FineDust> call_dust, Response<FineDust> response) {
                if (response.isSuccessful()) {
                    //통합대기환경지수
                    if (response.body() != null) {
                        dust = response.body().getList().get(0).getKhaiGrade();
                        dust_comment = commentFineDust(dust);

                        listener.onWeatherResponseAvailable();
                    } else {
                        Log.e(TAG, WEATHER_DATA_NULL_ERROR);
                        throw new InternalError(WEATHER_DATA_NULL_ERROR);
                    }
                }
            }

            @Override
            public void onFailure(Call<FineDust> call_dust, Throwable t) {

                Log.e(TAG, "" + t.toString());
                throw new InternalError(WEATHER_DATA_NULL_ERROR);
            }
        });
    }

    public void getHealthIndex() {

        //통신 가로채서 값 보여주는 것
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Call<JsonObject> flower_object = RetrofitNetwork.get().retrofitService.getFolwer(RetrofitService.DUST_APPKEY, 1100000000, "json");
        flower_object.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String result = response.body().getAsJsonObject("Response").getAsJsonObject("body").getAsJsonObject("indexModel").get("today").toString();
                    Log.d("", " " + result);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> flower_object, Throwable t) {
                //프로그래스바 날씨 데이터를 받아오고 있습니다 띄우기
                Log.e("fail", "" + t.toString());
            }
        });
    }

    public void getHeatIndex() {
        //열지수
        Call<Data> call_heat = RetrofitNetwork.get().retrofitService.getLivingIndex("heat", WEATHER_VERSION, latitude, longitude);
        call_heat.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call_heat, Response<Data> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        heatIndex = response.body().getWeather().getWIndex().getHeatIndex().get(0).getCurrent().getIndex();

                        IndexData.put("열지수",heatIndex);

                        listener.onIndexResponseAvailable();

                    } else {
                        throw new InternalError("unable to get heat index data");
                    }
                }
            }

            @Override
            public void onFailure(Call<Data> call_heat, Throwable t) {
                throw new InternalError(t.toString());
            }
        });
    }

    public void getWctIndex() {

        Call<Data> call_wct = RetrofitNetwork.get().retrofitService.getLivingIndex("wct", WEATHER_VERSION, latitude, longitude);
        call_wct.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call_wct, Response<Data> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        wctIndex = response.body().getWeather().getWIndex().getWctIndex().get(0).getCurrent().getIndex();
                        IndexData.put("체감온도",heatIndex);
                        listener.onIndexResponseAvailable();
                    }
                }
            }

            @Override
            public void onFailure(Call<Data> call_wct, Throwable t) {
                Log.e(TAG, "" + t.toString());
                Toast.makeText(context, "일시적으로 체감온도를 받아오지 못했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getThIndex() {
        Call<Data> call_thIndex = RetrofitNetwork.get().retrofitService.getLivingIndex("th", WEATHER_VERSION, latitude, longitude);
        call_thIndex.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call_thIndex, Response<Data> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        thIndex = response.body().getWeather().getWIndex().getThIndex().get(0).getCurrent().getIndex();
                        IndexData.put("불쾌지수",thIndex);

                        listener.onIndexResponseAvailable();
                    }
                }
            }

            @Override
            public void onFailure(Call<Data> call_thIndex, Throwable t) {
                Log.e(TAG, "" + t.toString());
                Toast.makeText(context, "일시적으로 불쾌지수를 받아오지 못했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCarWashIndex() {

        Call<Data> call_carwash = RetrofitNetwork.get().retrofitService.getLivingIndex("carwash", WEATHER_VERSION, latitude, longitude);
        call_carwash.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call_carwash, Response<Data> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        carWash = response.body().getWeather().getWIndex().getCarWash().get(0).getComment();
                        IndexData.put("세차지수",carWash);

                        listener.onIndexResponseAvailable();
                    }
                }
            }

            @Override
            public void onFailure(Call<Data> call_carwash, Throwable t) {
                Log.e(TAG, "" + t.toString());
                Toast.makeText(context, "일시적으로 세차지수를 받아오지 못했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUvIndex() {

        Call<Data> call_uvIndex = RetrofitNetwork.get().retrofitService.getLivingIndex("uv", WEATHER_VERSION, latitude, longitude);
        call_uvIndex.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call_uvIndex, Response<Data> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        uvIndex = response.body().getWeather().getWIndex().getUvIndex().get(0).getDay01().getComment();
                        IndexData.put("자외선지수",uvIndex);

                        listener.onIndexResponseAvailable();
                    }
                }
            }

            @Override
            public void onFailure(Call<Data> call_uvIndex, Throwable t) {
                Log.e(TAG, "" + t.toString());
                Toast.makeText(context, "일시적으로 자외선지수를 받아오지 못했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getLaundryIndex() {
        Call<Data> call_Laundry = RetrofitNetwork.get().retrofitService.getLivingIndex("laundry", WEATHER_VERSION, latitude, longitude);

        call_Laundry.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call_Laundry, Response<Data> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        laundry = response.body().getWeather().getWIndex().getLaundry().get(0).getDay01().getComment();
                        IndexData.put("빨래지수",laundry);

                        listener.onIndexResponseAvailable();
                    }
                }
            }

            @Override
            public void onFailure(Call<Data> call_Laundry, Throwable t) {
                Log.e(TAG, "" + t.toString());
            }
        });
    }

    private String commentFineDust(String dust) {
        if (dust.equals("1")) return "좋음";
        else if (dust.equals("2")) return "보통";
        else if (dust.equals("3")) return "나쁨";
        else if (dust.equals("4")) return "매우 나쁨";

        return "";
    }
}

