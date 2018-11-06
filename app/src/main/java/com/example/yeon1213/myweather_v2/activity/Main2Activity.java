package com.example.yeon1213.myweather_v2.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.yeon1213.myweather_v2.R;
import com.example.yeon1213.myweather_v2.adapter.MainIndexAdapter;
import com.example.yeon1213.myweather_v2.interfaces.MainPresenterInterface;
import com.example.yeon1213.myweather_v2.interfaces.MainView;
import com.example.yeon1213.myweather_v2.model.MainModel;
import com.example.yeon1213.myweather_v2.presenter.MainPresenter;
import com.example.yeon1213.weatherdatalibrary.data.interfaces.DataResponseListener;
import com.example.yeon1213.weatherdatalibrary.data.network.WeatherManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements MainView {

    private TextView tv_Temperature, tv_FineDust, tv_Precipitation, tv_Humidity, tv_Wind, tv_CurrentLocation;
    private RecyclerView rv_MainIndex;
    private MainIndexAdapter adt_MainIndex;
    private RecyclerView.LayoutManager lay_MainIndex;

    private MainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        mMainPresenter = new MainPresenter(this, this);

        mMainPresenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.onDestroy();
    }


    private void initView() {
        tv_Temperature = findViewById(R.id.temperature);
        tv_FineDust = findViewById(R.id.fine_dust);
        tv_Precipitation = findViewById(R.id.precipitation);
        tv_Humidity = findViewById(R.id.humidity);
        tv_Wind = findViewById(R.id.wind);
        tv_CurrentLocation = findViewById(R.id.current_location);

        rv_MainIndex = findViewById(R.id.main_recycler_view);
        rv_MainIndex.setHasFixedSize(true);
        lay_MainIndex = new LinearLayoutManager(getApplicationContext());
        rv_MainIndex.setLayoutManager(lay_MainIndex);
    }

    @Override
    public void changeTitle(String address) {
        tv_CurrentLocation.setText(address);
    }

    @Override
    public void getWeatherData(WeatherManager weatherManager) {
        tv_Temperature.setText(weatherManager.getTemperature());
        tv_FineDust.setText(weatherManager.getDust() + " " + weatherManager.getDust_comment());
        tv_Precipitation.setText(weatherManager.getPrecipitation());
        tv_Humidity.setText(weatherManager.getHumidity());
        tv_Wind.setText(weatherManager.getHumidity());
    }

    @Override
    public void getIndexData(List<String> indexData) {
        if (indexData.size() > 0) {
            adt_MainIndex = new MainIndexAdapter(getApplicationContext(), indexData);
            rv_MainIndex.setAdapter(adt_MainIndex);
            adt_MainIndex.notifyDataSetChanged();
        }
    }
}
