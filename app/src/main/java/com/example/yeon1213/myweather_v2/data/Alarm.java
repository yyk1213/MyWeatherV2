package com.example.yeon1213.myweather_v2.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.yeon1213.myweather_v2.database.LocationData;
import com.example.yeon1213.myweather_v2.receiver.AlarmReceiver;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class Alarm {

    private LocationData locationData;
    private int alarmId;

    public boolean setAlarm(Context context, LocationData locationData){

            String mTime = locationData.getMTime();

            int mHour, mMin;
            alarmId = locationData.getMId();

            //요일을 받아와서
            //요일 갯수에 맞게
            //알람을 각자 설정한다.
            //요일 받아오기
            //int mDayOfWeek=locationData.getMDayOfWeek();
//            int DB_ID=locationData.getMId();
//            getDayOfWeek(mDayOfWeek,DB_ID);//db에 설정된 요일이랑, 갯수 알기

            //시간 받아오기
            String[] time = mTime.split(":");
            mHour = Integer.parseInt(time[0].trim());
            mMin = Integer.parseInt(time[1].trim());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(calendar.HOUR_OF_DAY, mHour);//1시간 전에 설정하기
            calendar.set(calendar.MINUTE, mMin);
            calendar.set(Calendar.MILLISECOND,0);
            //요일 임의로 추가
            //calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);

            long currentTime = System.currentTimeMillis();
            long setTime = calendar.getTimeInMillis();
            //하루 시간
            long oneDay = 1000 * 60 * 60 * 24;
            //지난 알림 다음날 울리기
            while (currentTime > setTime) {
                setTime += oneDay;
            }

            Intent receiverIntent = new Intent(context, AlarmReceiver.class);
            receiverIntent.putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId);

            //알람매니저 등록
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            //알람 설정 시각에 발생하는 인텐트
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, receiverIntent, 0);
            //알람 설정
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setTime,AlarmManager.INTERVAL_DAY,pendingIntent);

            return true;
    }

    public  boolean removeAlarm(Context context,LocationData locationData){

        if(locationData.getMAlarmCheck()) {

            alarmId = locationData.getMId();

            Intent receiverIntent = new Intent(context, AlarmReceiver.class);
            receiverIntent.putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId);

            AlarmManager alarmManager=(AlarmManager)context.getSystemService(ALARM_SERVICE);
            PendingIntent pendingIntent=PendingIntent.getBroadcast(context, alarmId,receiverIntent,0);

            alarmManager.cancel(pendingIntent);
            return true;

        }else{
            return true;
        }
    }

    private void getDayOfWeek(int dayOfWeek,Calendar calendar) {
        for (int day = 0; day < 7; day++) {
            switch (day) {
                case 0:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    }
                    break;
                case 1:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    }
                    break;
                case 2:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                    }
                    break;
                case 3:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                    }
                    break;
                case 4:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                    }
                    break;
                case 5:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        calendar.add(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                    }
                    break;
                case 6:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                    }
                    break;
            }
        }
    }
}
