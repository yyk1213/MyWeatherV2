package com.example.yeon1213.myweather_v2.interfaces;

import com.example.yeon1213.weatherdatalibrary.data.network.WeatherManager;

import java.util.List;

public interface MainView {

    void changeTitle(String address);

    void getWeatherData(WeatherManager weatherManager);

    void getIndexData(List<String> indexData);
}
