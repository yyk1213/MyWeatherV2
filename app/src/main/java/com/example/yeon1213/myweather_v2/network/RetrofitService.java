package com.example.yeon1213.myweather_v2.network;

import com.example.yeon1213.myweather_v2.data.Weather.Data;
import com.example.yeon1213.myweather_v2.data.Weather.FineDust.FineDust;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {
    //날씨 API
    String WEATHER_BASEURL ="https://api2.sktelecom.com/weather/";
    String WEATHER_APPKEY ="6dac13f4-6c4b-499a-a48d-c5b6cdff97c2";
    //미세먼지 API
    String DUST_BASEURL="http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/";
    String DUST_APPKEY="MATtWt3RcZepkS0jjT/K7V0A3Tw4EQoCRafIiHry+VgNWmhD+LqoYZeDwHvM4c9lSAz1CM2VtogaDyCIGcTH9w==";
    //보건기상지수 API
    String HEALTH_BASEURL="http://newsky2.kma.go.kr/iros/RetrieveWhoIndexService2/";

    @GET("current/hourly")
    Call<Data> getHourly(@Header("appKey") String appKey, @Query("version") int version,
                         @Query("lat") double lat, @Query("lon") double lon);
    @GET("current/minutely")
    Call<Data> getMinutely(@Header("appKey") String appKey, @Query("version") int version,
                                @Query("lat") double lat, @Query("lon") double lon);

    @GET("current/minutely")
    Call<JsonObject> getTest(@Header("appKey") String appKey, @Query("version") int version,
                            @Query("lat") double lat, @Query("lon") double lon);

    //생활기상지수 받아오기
    @Headers("appKey: 6dac13f4-6c4b-499a-a48d-c5b6cdff97c2")
    @GET("index/{index_name}")
    Call<Data> getLivingIndex(@Path("index_name") String indexName,@Query("version") int version,
                              @Query("lat") double lat, @Query("lon") double lon);
    //미세먼지 불러오기
    @GET("getMsrstnAcctoRltmMesureDnsty")
    Call<FineDust> getDust(@Query("serviceKey") String serviceKey, @Query("numOfRows") double numOfRows,
                           @Query("pageSize") double pageSize, @Query("pageNo") double pageNo, @Query("startPage") double startPage,
                           @Query("stationName") String stationName, @Query("dataTerm") String dataTerm, @Query("ver") double ver, @Query("_returnType") String _returnType);
    //보건기상지수 받아오기
    @GET("getAsthmaWhoList")
    Call<JsonObject> getFolwer(@Query("serviceKey") String serviceKey, @Query("areaNo") int areaNo, @Query("_type") String _type);
}
