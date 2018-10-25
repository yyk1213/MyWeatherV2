package com.example.yeon1213.myweather_v2.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.health.PackageHealthStats;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.yeon1213.myweather_v2.adapter.MainIndexAdapter;
import com.example.yeon1213.myweather_v2.interfaces.DataResponseListener;
import com.example.yeon1213.myweather_v2.interfaces.PermissionResponseListener;
import com.example.yeon1213.myweather_v2.network.WeatherData;
import com.example.yeon1213.myweather_v2.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.util.Log.e;
import static com.example.yeon1213.myweather_v2.activity.LifeIndexActivity.EXTRA_ACTIVITY_POSITION;

public class MainActivity extends AppCompatActivity implements DataResponseListener, PermissionResponseListener {

    public static final String EXTRA_LATITUDE = "com.example.yeon1213.myweather_v2.Data.Alarm.mLatitude";
    public static final String EXTRA_LONGITUDE = "com.example.yeon1213.myweather_v2.Data.Alarm.longtitude";
    public static final String TAG = "MainActivity";
    public static final String NETWORK_REQUEST_MESSAGE = "네트워크를 연결해주세요";
    public static final String LOCATION_REQUEST_MESSAGE = "위치 정보 제공 동의가 필요합니다.";
    public static final int LOCATION_PERMISSIONS_REQUEST = 0;

    private TextView tv_Temperature, tv_FineDust, tv_Precipitation, tv_Humidity, tv_Wind, tv_CurrentLocation;
    private RecyclerView mMainRecyclerView;
    private MainIndexAdapter mMainAdapter;
    private RecyclerView.LayoutManager mMainLayoutManager;

    private List<String> mRecyclerLivingData;
    private WeatherData mWeatherData;
    private double mLatitude, mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if(checkPermission()){

            checkLocation();

            reverseAddress();

            //날씨 값 가져오기
            mWeatherData.getWeatherAPIData(mLatitude, mLongitude);
            //선택 지수 값 가져오기
            mWeatherData.getIndexData(mLatitude, mLongitude);
            //보건 지수 가져오기
            //mWeatherData.getHealthIndex();
        }
    }

    private boolean checkPermission() {

        if (!isNetworkAvailable()) {

            showAlert(NETWORK_REQUEST_MESSAGE);

            return false;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSIONS_REQUEST);
            return false;

        }else{

            return true;
        }
    }

    @Override
    public void onPermissionChecked() {
        //날씨 값 가져오기
        mWeatherData.getWeatherAPIData(mLatitude, mLongitude);
        //선택 지수 값 가져오기
        mWeatherData.getIndexData(mLatitude, mLongitude);
        //보건 지수 가져오기
        //mWeatherData.getHealthIndex();
    }

    private void showAlert(String alertMessage) {
        String message = alertMessage;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        switch (message) {
            case NETWORK_REQUEST_MESSAGE:
                alertDialogBuilder.setMessage(NETWORK_REQUEST_MESSAGE)
                        .setPositiveButton("재시도", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkPermission();
                            }
                        }).create();
                break;
            case LOCATION_REQUEST_MESSAGE:
                alertDialogBuilder.setMessage(LOCATION_REQUEST_MESSAGE)
                        .setPositiveButton("동의", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkPermission();
                            }
                        })
                        .create();
                break;
        }

        alertDialogBuilder.show();
    }

    @Override
    public void onWeatherResponseAvailable() {
        tv_Temperature.setText(mWeatherData.getTemperature());
        tv_FineDust.setText(mWeatherData.getDust() + " " + mWeatherData.getDust_comment());
        tv_Precipitation.setText(" " + mWeatherData.getPrecipitation());
        tv_Humidity.setText(" " + mWeatherData.getHumidity());
        tv_Wind.setText(" " + mWeatherData.getWind());
    }

    @Override
    public void onIndexResponseAvailable() {
        mRecyclerLivingData = mWeatherData.getLivingData();
        mMainAdapter = new MainIndexAdapter(getApplicationContext(), mRecyclerLivingData);
        mMainRecyclerView.setAdapter(mMainAdapter);
        mMainAdapter.notifyDataSetChanged();
    }

    private void reverseAddress() {
        //좌표를 주소로 변환
        final Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(mLatitude, mLongitude, 10);
            tv_CurrentLocation.setText(list.get(1).getAddressLine(0).substring(5));

        } catch (IOException e) {
            e.printStackTrace();
            e(TAG, "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            mRecyclerLivingData.clear();
            mWeatherData.getIndexData(mLatitude, mLongitude);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        setIntent(intent);

        //들어온 인텐트 값이 있으면
        if ((getIntent().getDoubleExtra(EXTRA_LATITUDE, 0.0) != 0.0) && (getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0.0) != 0.0)) {
            mLatitude = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0.0);
            mLongitude = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0.0);

            mRecyclerLivingData.clear();
            reverseAddress();
            //날씨 값 가져오기
            mWeatherData.getWeatherAPIData(mLatitude, mLongitude);
            //선택 지수 값 가져오기
            mWeatherData.getIndexData(mLatitude, mLongitude);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.setting_menu, menu);
        inflater.inflate(R.menu.action_button, menu);

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSIONS_REQUEST) {
            if(grantResults.length==2 && grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                checkLocation();
                reverseAddress();

                onPermissionChecked();
            }

            else{
                showAlert(LOCATION_REQUEST_MESSAGE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.life:
                Intent lifeIntent = new Intent(MainActivity.this, LifeIndexActivity.class);
                lifeIntent.putExtra(EXTRA_ACTIVITY_POSITION, item.getItemId());

                startActivityForResult(lifeIntent, 0);
                break;
            case R.id.health:
                Intent healthIntent = new Intent(MainActivity.this, LifeIndexActivity.class);
                healthIntent.putExtra(EXTRA_ACTIVITY_POSITION, item.getItemId());

                startActivityForResult(healthIntent, 0);
                break;
            case R.id.life_radius_setting:
                Intent lifeSettingIntent = new Intent(MainActivity.this, LivingRadiusActivity.class);
                startActivity(lifeSettingIntent);
                break;

            case R.id.change_location:
                changeLocation();
                break;
        }
        return true;
    }

    private void initView() {
        tv_Temperature = findViewById(R.id.temperature);
        tv_FineDust = findViewById(R.id.fine_dust);
        tv_Precipitation = findViewById(R.id.precipitation);
        tv_Humidity = findViewById(R.id.humidity);
        tv_Wind = findViewById(R.id.wind);
        tv_CurrentLocation = findViewById(R.id.current_location);

        mMainRecyclerView = findViewById(R.id.main_recycler_view);
        mMainRecyclerView.setHasFixedSize(true);
        mMainLayoutManager = new LinearLayoutManager(getApplicationContext());
        mMainRecyclerView.setLayoutManager(mMainLayoutManager);

        //데이터 생성
        mRecyclerLivingData = new ArrayList<>();
        mWeatherData = new WeatherData();
        mWeatherData.setContext(this);
        //리스너 등록
        mWeatherData.setListener(this);
    }

    private void checkLocation() {
        //들어온 인텐트 값이 있으면
        if ((getIntent().getDoubleExtra(EXTRA_LATITUDE, 0.0) != 0.0) && (getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0.0) != 0.0)) {
            mLatitude = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0.0);
            mLongitude = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0.0);

        } else {
            //현재위치 값 구하기
            getCurrentLocation();
        }
    }

    class LocationListener implements android.location.LocationListener {
        Location mLocation;

        public LocationListener(String provider) {
            mLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLocation.set(location);

            mLatitude = mLocation.getLatitude();
            mLongitude = mLocation.getLongitude();
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

    private void getCurrentLocation() {

        //위치 매니저 초기화
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener[] mLocationListeners = new LocationListener[]{
                new LocationListener(LocationManager.GPS_PROVIDER),
                new LocationListener(LocationManager.NETWORK_PROVIDER)
        };

        try {
            //GPS 제공자 정보가 바뀌면 콜백하도록 리스너 등록
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

        try {
            //GPS 제공자 정보가 바뀌면 콜백하도록 리스너 등록
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

        //최근 위치 정보 확인
        Location[] locations = {mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER),
                mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)};

        for (int i = 0; i < locations.length; i++) {
            if (locations[i] != null) {
                mLatitude = locations[i].getLatitude();
                mLongitude = locations[i].getLongitude();
            }
        }

        //위치정보 미 수신할 때 자원해제
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        //네트워크 연결 안될 시 팝업창 뜨게 하기
        return isNetworkConnected;
    }

    private void changeLocation() {

        getCurrentLocation();
        reverseAddress();
        mRecyclerLivingData.clear();

        //날씨 값 가져오기
        mWeatherData.getWeatherAPIData(mLatitude, mLongitude);
        //선택 지수 값 가져오기
        mWeatherData.getIndexData(mLatitude, mLongitude);
    }
}
