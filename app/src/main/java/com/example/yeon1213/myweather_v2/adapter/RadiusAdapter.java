package com.example.yeon1213.myweather_v2.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.yeon1213.myweather_v2.activity.LifeRadiusSettingActivity;
import com.example.yeon1213.myweather_v2.data.Alarm;
import com.example.yeon1213.myweather_v2.database.LocationData;
import com.example.yeon1213.myweather_v2.database.LocationDatabase;
import com.example.yeon1213.myweather_v2.R;

import java.util.List;

public class RadiusAdapter extends RecyclerView.Adapter<RadiusAdapter.RadiusHolder> {
    private List<LocationData> mRadius;
    private Context mContext;

    private LocationDatabase mDatabase;
    private Alarm mAlarm;

    public RadiusAdapter(Context context, List<LocationData> radius) {
        this.mRadius = radius;
        this.mContext = context;
        mDatabase = LocationDatabase.getDataBase(context);
    }

    @Override
    public RadiusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.radius_row, parent, false);

        return new RadiusHolder(view);
    }

    @Override
    public void onBindViewHolder(RadiusHolder holder, int position) {
        holder.mView.setTag(position);

        LocationData radius = this.mRadius.get(position);
        holder.tv_LocationName.setText(radius.getMLocation_name());
        holder.tv_Time.setText(radius.getMTime());
        holder.tv_DayOfWeek.setText(getDayOfWeek(radius.getMDayOfWeek()) + "요일");
        holder.sc_AlarmSwitch.setChecked(radius.getMAlarmCheck());

        Log.d("요일", "" + getDayOfWeek(radius.getMDayOfWeek()));
    }

    @Override
    public int getItemCount() {
        return mRadius.size();
    }

    //일 월화 static 변수로 바꿔보기
    private String getDayOfWeek(int dayOfWeek) {

        String mDayOfWeek = "";

        for (int day = 0; day < 7; day++) {
            switch (day) {
                case 0:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        mDayOfWeek += "일";
                    }
                    break;
                case 1:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        mDayOfWeek += "월";
                    }
                    break;
                case 2:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        mDayOfWeek += "화";
                    }
                    break;
                case 3:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        mDayOfWeek += "수";
                    }
                    break;
                case 4:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        mDayOfWeek += "목";
                    }
                    break;
                case 5:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        mDayOfWeek += "금";
                    }
                    break;
                case 6:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        mDayOfWeek += "토";
                    }
                    break;
            }
        }
        return mDayOfWeek;
    }

    class RadiusHolder extends RecyclerView.ViewHolder implements View.OnClickListener, SwitchCompat.OnCheckedChangeListener {

        public TextView tv_LocationName;
        public TextView tv_Time;
        public TextView tv_DayOfWeek;
        public SwitchCompat sc_AlarmSwitch;
        public View mView;

        public RadiusHolder(View itemView) {
            super(itemView);

            tv_LocationName = itemView.findViewById(R.id.radius_location);
            tv_Time = itemView.findViewById(R.id.radius_time);
            tv_DayOfWeek = itemView.findViewById(R.id.radius_day_of_week);
            sc_AlarmSwitch = itemView.findViewById(R.id.radius_check);

            mView = itemView;

            mView.setOnClickListener(this);
            sc_AlarmSwitch.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = (int) mView.getTag();
            int id = mDatabase.getLocationDAO().getLocation().get(position).getMId();

            Intent settingRadiusIntent = new Intent(mContext, LifeRadiusSettingActivity.class);
            settingRadiusIntent.putExtra(LifeRadiusSettingActivity.EXTRA_DATA_ID, id);
            settingRadiusIntent.putExtra(LifeRadiusSettingActivity.EXTRA_DATA_POSITION, position);

            ((Activity) mContext).startActivityForResult(settingRadiusIntent, 0);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            int position = (int) mView.getTag();
            mAlarm =new Alarm();// 이 위치 다시 확인
            LocationData locationData = mDatabase.getLocationDAO().getLocation().get(position);

            if (isChecked) {
                //체크가 되면 뷰항목을 누를 수 있고 알람이 설정된다.
                itemView.setEnabled(true);
                mAlarm.setAlarm(mContext, locationData);
                locationData.setMAlarmCheck(true);

            } else {
                //뷰항목이 비활성화 되고 알람이 취소된다.
                itemView.setEnabled(false);
                mAlarm.removeAlarm(mContext, locationData);
                locationData.setMAlarmCheck(false);
            }

            mDatabase.getLocationDAO().update(locationData);
        }
    }
}
