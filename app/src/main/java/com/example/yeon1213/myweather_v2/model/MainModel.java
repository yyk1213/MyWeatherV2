package com.example.yeon1213.myweather_v2.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.yeon1213.myweather_v2.interfaces.ModelListener;
import com.example.yeon1213.weatherdatalibrary.data.interfaces.DataResponseListener;
import com.example.yeon1213.weatherdatalibrary.data.network.WeatherManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.util.Log.e;

public class MainModel {

    public static String TAG = "MAIN_PRESENTER";

    private Context mContext;
    private double mLatitude;
    private double mLongitude;
    private ModelListener modelListener;

    public MainModel(Context mContext,ModelListener listener) {
        this.mContext = mContext;
        modelListener = listener;
    }

    class LocationListener implements android.location.LocationListener {
        Location mLocation;

        public LocationListener(String provider) {
            mLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {

            if (location != null) {
                mLocation.set(location);

                mLatitude = mLocation.getLatitude();
                mLongitude = mLocation.getLongitude();

                getAddress();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    }

    public void getCurrentLocation() {
        //위치 매니저 초기화
        LocationManager mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        LocationListener mLocationListener = new LocationListener(LocationManager.NETWORK_PROVIDER);

        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, mLocationListener); //위치가 업데이트 되는 간격이 10초까지 걸릴 수 있다. null이 올 확률이 있다.
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

        Location locations = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //위치정보 미 수신할 때 자원해제-- 위치 정보를 업데이트 받고나서, 자원해제 하기 때문에 null값은 안들어올 것
        try {
            mLocationManager.removeUpdates(mLocationListener);
        } catch (Exception ex) {
            Log.i(TAG, "fail to remove location listners, ignore", ex);
        }

        mLatitude = locations.getLatitude();
        mLongitude = locations.getLongitude();
    }

    public String getAddress() {
        //좌표를 주소로 변환
        final Geocoder geocoder = new Geocoder(mContext);
        String mBeforeAddressCode = null;
        String mBeforeAddress = null;

        try {
            List<Address> list = geocoder.getFromLocation(mLatitude, mLongitude, 10);
            //앱이 처음 실행되는 경우
            if (mBeforeAddressCode == null) {
                mBeforeAddressCode = list.get(0).getPostalCode();
                mBeforeAddress = list.get(0).getAddressLine(0).substring(5);
            } else {

                if (mBeforeAddressCode.substring(0, 2).equals(list.get(0).getPostalCode().substring(0, 2))) {

                } else {
                    mBeforeAddressCode = list.get(0).getPostalCode();
                    mBeforeAddress = list.get(0).getAddressLine(0).substring(5);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            e(TAG, "입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if (modelListener != null) {

            modelListener.onInteractionListener(mBeforeAddress);
        }
        return mBeforeAddress;
    }

    public WeatherManager getWeatherData(DataResponseListener listener) {

        return new WeatherManager(mLatitude, mLongitude, listener);
    }

    public List<String> checkPreference(SharedPreferences sharedPreferences) {

        Map<String, ?> indexPref = sharedPreferences.getAll();
        List<String> checkValue = new ArrayList<>();

        //똑같은게 들어가면 안된다.
        for (Map.Entry<String, ?> entry : indexPref.entrySet()) {

            if ((Boolean) entry.getValue() == true) {
                checkValue.add(entry.getKey());
            }
        }

        return checkValue;
    }

    public HashMap<String, String> getIndexData(DataResponseListener dataResponseListener) {
        WeatherManager weatherManager = new WeatherManager(mLatitude, mLongitude, dataResponseListener);
        weatherManager.getIndexAPIData();
        //원래 여기에 리스너가 있어야 한다.
        return weatherManager.getIndexHashMap();
    }
}
