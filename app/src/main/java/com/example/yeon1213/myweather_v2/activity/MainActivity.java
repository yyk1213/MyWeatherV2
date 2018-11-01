package com.example.yeon1213.myweather_v2.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
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
import android.widget.Toast;

import com.example.yeon1213.myweather_v2.adapter.MainIndexAdapter;
import com.example.yeon1213.myweather_v2.interfaces.DataResponseListener;
import com.example.yeon1213.myweather_v2.network.WeatherData;
import com.example.yeon1213.myweather_v2.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.util.Log.e;
import static com.example.yeon1213.myweather_v2.activity.LifeIndexActivity.EXTRA_ACTIVITY_POSITION;

public class MainActivity extends AppCompatActivity implements DataResponseListener {

    public static final String EXTRA_LATITUDE = "com.example.yeon1213.myweather_v2.Data.Alarm.mLatitude";
    public static final String EXTRA_LONGITUDE = "com.example.yeon1213.myweather_v2.Data.Alarm.longtitude";
    public static final String TAG = "MainActivity";
    public static final String NETWORK_REQUEST_MESSAGE = "네트워크를 연결해주세요";
    public static final String LOCATION_REQUEST_MESSAGE = "위치 정보 제공 동의가 필요합니다.";
    public static final int LOCATION_PERMISSIONS_REQUEST = 0;

    private TextView tv_Temperature, tv_FineDust, tv_Precipitation, tv_Humidity, tv_Wind, tv_CurrentLocation;
    private RecyclerView rv_MainIndex;
    private MainIndexAdapter adt_MainIndex;
    private RecyclerView.LayoutManager lay_MainIndex;

    private List<String> mRecyclerLivingData;
    private WeatherData mWeatherData;
    private double mLatitude, mLongitude;

    private SharedPreferences mSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if (checkPermission()) {

            if (checkLocation()) {

                reverseAddress();

                getData();
            }
        }
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

        //데이터 생성
        mRecyclerLivingData = new ArrayList<>();
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

        } else {

            return true;
        }
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
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

    private void reverseAddress() {
        //좌표를 주소로 변환
        final Geocoder geocoder = new Geocoder(this);
        List<Address> list;
        try {
            list = geocoder.getFromLocation(mLatitude, mLongitude, 10);
            tv_CurrentLocation.setText(list.get(1).getAddressLine(0).substring(5));

        } catch (IOException e) {
            e.printStackTrace();
            e(TAG, "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
    }

    public void getData() {

        mWeatherData = new WeatherData(this, mLatitude, mLongitude, this);
        //선택 지수 값 가져오기
        mWeatherData.getIndexAPIData();
        //보건 지수 가져오기
        //mWeatherData.getHealthIndex();
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

        checkPreference();
    }

    private void checkPreference() {

        mRecyclerLivingData.clear();

        mSharedPreference = this.getSharedPreferences("index_setting", Activity.MODE_PRIVATE);

        Map<String, ?> indexPref = mSharedPreference.getAll();

        //똑같은게 들어가면 안된다.
        for (Map.Entry<String, ?> entry : indexPref.entrySet()) {

            if ((Boolean) entry.getValue() == true) {
                if (mWeatherData.getIndexData().get(entry.getKey()) != null) {
                    switch (entry.getKey()) {
                        case "자외선지수":
                            mRecyclerLivingData.add("자외선지수: " + mWeatherData.getIndexData().get("자외선지수"));
                            break;
                        case "불쾌지수":
                            mRecyclerLivingData.add("불쾌지수: " + mWeatherData.getIndexData().get("불쾌지수"));
                            break;
                        case "열지수":
                            mRecyclerLivingData.add("열지수: " + mWeatherData.getIndexData().get("열지수"));
                            break;
                        case "체감온도":
                            mRecyclerLivingData.add("체감온도: " + mWeatherData.getIndexData().get("체감온도"));
                            break;
                        case "세차지수":
                            mRecyclerLivingData.add("세차지수: " + mWeatherData.getIndexData().get("세차지수"));
                            break;
                        case "빨래지수":
                            mRecyclerLivingData.add("빨래지수: " + mWeatherData.getIndexData().get("빨래지수"));
                            break;
                    }
                }
            }
        }

        adt_MainIndex = new MainIndexAdapter(getApplicationContext(), mRecyclerLivingData);
        rv_MainIndex.setAdapter(adt_MainIndex);
        adt_MainIndex.notifyDataSetChanged();

    }

    private void changeLocation() {

        if (checkPermission()) {

            if (checkLocation()) {
                reverseAddress();
                getData();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            mWeatherData.getIndexData();
            checkPreference();
            //mWeatherData.getIndexData(mLatitude, mLongitude);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        setIntent(intent);

        //들어온 인텐트 값이 있으면
        if ((getIntent().getDoubleExtra(EXTRA_LATITUDE, 0.0) != mLatitude) || (getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0.0) != mLongitude)) {
            mLatitude = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0.0);
            mLongitude = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0.0);

            reverseAddress();
            getData();
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
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (checkLocation()) {
                    reverseAddress();

                    getData();
                }
            } else {
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
            } else {
                Toast.makeText(MainActivity.this, "일시적으로 내 위치를 확인할 수 없습니다", Toast.LENGTH_SHORT).show();
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

    private boolean checkLocation() {
        //위치가 달라지면 true, 안 달라지면 false

        //위치 매니저 초기화
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

        if ((mLatitude == locations.getLatitude()) && (mLongitude == locations.getLongitude())) {
            return false;
        } else {

            mLatitude = locations.getLatitude();
            mLongitude = locations.getLongitude();

            return true;
        }
        //우려사항-- requestLocationUpdates 값을 받아오기 전에 위경도 값이 null로 들어가고, 자원이 해제돼 버릴 수 있다.
        //위 경도 값은 소수점으로 돼 있기 때문에 완전히 같기가 힘들다 -- 다른 경우 생각하기
    }
}
