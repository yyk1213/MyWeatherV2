package com.example.yeon1213.myweather_v2.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.yeon1213.myweather_v2.interfaces.MainPresenterInterface;
import com.example.yeon1213.myweather_v2.interfaces.MainView;
import com.example.yeon1213.myweather_v2.interfaces.ModelListener;
import com.example.yeon1213.myweather_v2.model.MainModel;
import com.example.yeon1213.weatherdatalibrary.data.interfaces.DataResponseListener;
import com.example.yeon1213.weatherdatalibrary.data.network.WeatherManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainPresenter implements MainPresenterInterface, ModelListener, DataResponseListener {

    MainView mainView;
    MainModel mainModel;
    Context mContext;

    WeatherManager mWeatherManager;
    HashMap<String, String> mIndexData;

    public MainPresenter(com.example.yeon1213.myweather_v2.interfaces.MainView mainView, Context context) {
        this.mainView = mainView;
        mContext = context;

        this.mainModel = new MainModel(context,this);
    }

    @Override
    public void onCreate() {
        mainModel.getCurrentLocation();
        mWeatherManager = mainModel.getWeatherData(this);

        mIndexData = mainModel.getIndexData(this);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onInteractionListener(String s) {
        mainView.changeTitle(s);
    }

    @Override
    public void onWeatherResponseAvailable() {
        mainView.getWeatherData(mWeatherManager);
    }

    @Override
    public void onIndexResponseAvailable() {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("index_setting", Activity.MODE_PRIVATE);
        List<String> checkValue = mainModel.checkPreference(sharedPreferences);
        List<String> value = new ArrayList<>();

        if (mIndexData != null) {
            for (int i = 0; i < checkValue.size(); i++) {
                value.add(checkValue.get(i) + ": " + mIndexData.get(checkValue.get(i)));
            }
        }

        mainView.getIndexData(value);
    }
}
