package com.example.yeon1213.myweather_v2.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.example.yeon1213.myweather_v2.activity.MainActivity;
import com.example.yeon1213.myweather_v2.database.LocationData;
import com.example.yeon1213.myweather_v2.database.LocationDatabase;
import com.example.yeon1213.myweather_v2.interfaces.DataResponseListener;
import com.example.yeon1213.myweather_v2.network.WeatherData;
import com.example.yeon1213.myweather_v2.R;

import static com.example.yeon1213.myweather_v2.activity.MainActivity.EXTRA_LATITUDE;
import static com.example.yeon1213.myweather_v2.activity.MainActivity.EXTRA_LONGITUDE;

public class AlarmReceiver extends BroadcastReceiver implements DataResponseListener {

    public static final String TAG = "AlarmReceiver";
    public static final String EXTRA_ALARM_ID = "com.example.yeon1213.myapplication.Alarm.alarm_id";

    private LocationDatabase mLocationDatabase;
    private String mLocationWeather;
    private String mLocationName;
    private Context mContext;

    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private PendingIntent mPendingIntent;

    private WeatherData mWeatherData;
    private int mId;

    @Override
    public void onWeatherResponseAvailable() {

        mLocationWeather = "";

        if (mWeatherData.getTemperature() != null) {
            mLocationWeather += "온도:" + mWeatherData.getTemperature() + " ";
        }

        if (mWeatherData.getPrecipitation() != null) {
            mLocationWeather += "강수량:" + mWeatherData.getPrecipitation() + " ";
        } else if (mWeatherData.getTemperature() == null && mWeatherData.getPrecipitation() == null)
            mLocationWeather += "정보 제공 불가";

        mNotification = new NotificationCompat.Builder(mContext, "default")
                .setSmallIcon(R.drawable.noti_launcher)
                .setContentIntent(mPendingIntent)
                .setContentTitle(mLocationName)
                .setContentText(mLocationWeather)
                .setVibrate(new long[]{1000, 1000, 1000})
                .setAutoCancel(true)
                .build();

        mNotificationManager.notify(1, mNotification);
    }

    @Override
    public void onIndexResponseAvailable() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        mLocationDatabase = LocationDatabase.getDataBase(context);
        mId = intent.getIntExtra(EXTRA_ALARM_ID, 0);

        LocationData locationData = mLocationDatabase.getLocationDAO().getData(mId);

        double latitude = locationData.getMLatitude();
        double longitude = locationData.getMLongitude();

        mWeatherData = new WeatherData(mContext, latitude,longitude,this);

        mLocationName = locationData.getMLocation_name();

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        Intent main_intent = new Intent(context, MainActivity.class);
        main_intent.putExtra(EXTRA_LATITUDE, latitude);
        main_intent.putExtra(EXTRA_LONGITUDE, longitude);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mPendingIntent = PendingIntent.getActivity(context, 2, main_intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
