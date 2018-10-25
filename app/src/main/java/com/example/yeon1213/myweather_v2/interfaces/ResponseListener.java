package com.example.yeon1213.myweather_v2.interfaces;

import android.location.LocationManager;

public interface ResponseListener {

    void onPermissionChecked();

    void onLocationChecked(LocationManager locationManager);
}
