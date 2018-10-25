package com.example.yeon1213.myweather_v2.activity;

import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.yeon1213.myweather_v2.adapter.AddressAutoCompleteAdapter;
import com.example.yeon1213.myweather_v2.data.Alarm;
import com.example.yeon1213.myweather_v2.database.LocationDAO;
import com.example.yeon1213.myweather_v2.database.LocationData;
import com.example.yeon1213.myweather_v2.database.LocationDatabase;
import com.example.yeon1213.myweather_v2.R;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

public class LifeRadiusSettingActivity extends AppCompatActivity implements View.OnClickListener, ToggleButton.OnCheckedChangeListener {

    private static final LatLngBounds BOUNDS_GRATER_KOREA = new LatLngBounds(new LatLng(35.9078, 127.7669), new LatLng(35.9078, 127.7669));
    public static final String EXTRA_DATA_ID = "com.example.yeon1213.myweather_v2.Activity.location_data_id";
    public static final String EXTRA_DATA_POSITION = "com.example.yeon1213.myweather_v2.Activity.location_data_position";

    private GeoDataClient mGeoDataClient;
    private AutoCompleteTextView mSearchPlace;
    private AddressAutoCompleteAdapter mAdapter;
    private TextView tv_PlaceDetailsAddress;
    private TextView tv_PlaceDetailsText;
    private TextView tv_StartTime;
    private Button btn_Time;
    private Button btn_Save;
    private Button btn_Clear;
    private Button btn_Remove;

    private LocationDatabase mDatabase;
    private Alarm mAlarm;
    //저장할 데이터
    private LocationData mLocationData;
    private String mDataLocationName;
    private String mDataLocationAddress;
    private int mDayOfWeek = 0;

    private int mItemId;
    private int mPosition;

    //Day of week buttons
    ToggleButton tb_Sun;
    ToggleButton tb_Mon;
    ToggleButton tb_Tue;
    ToggleButton tb_Wed;
    ToggleButton tb_Thur;
    ToggleButton tb_Fri;
    ToggleButton tb_Sat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_life_radius);

        getSupportActionBar().setTitle("생활반경 설정");

        initView();

        checkValue();
    }

    private void initView(){

        mSearchPlace = findViewById(R.id.place_autocomplete_powered_by_google);
        tv_PlaceDetailsText = findViewById(R.id.place_name);
        tv_PlaceDetailsAddress = findViewById(R.id.place_attribution);
        btn_Clear = findViewById(R.id.clear_btn);
        btn_Time = findViewById(R.id.timeBtn);
        tv_StartTime = findViewById(R.id.start_time);
        btn_Save = findViewById(R.id.saveBtn);
        btn_Remove = findViewById(R.id.removeBtn);

        //요일
        tb_Sun = findViewById(R.id.tSun);
        tb_Mon = findViewById(R.id.tMon);
        tb_Tue = findViewById(R.id.tTue);
        tb_Wed = findViewById(R.id.tWed);
        tb_Thur = findViewById(R.id.tThur);
        tb_Fri = findViewById(R.id.tFri);
        tb_Sat = findViewById(R.id.tSat);

        tb_Sun.setOnCheckedChangeListener(this);
        tb_Mon.setOnCheckedChangeListener(this);
        tb_Tue.setOnCheckedChangeListener(this);
        tb_Wed.setOnCheckedChangeListener(this);
        tb_Thur.setOnCheckedChangeListener(this);
        tb_Fri.setOnCheckedChangeListener(this);
        tb_Sat.setOnCheckedChangeListener(this);
        //places api 클라이언트
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mAdapter = new AddressAutoCompleteAdapter(this, mGeoDataClient, BOUNDS_GRATER_KOREA, null);//어댑터 안에 필터 있음
        mSearchPlace.setAdapter(mAdapter);

        mSearchPlace.setOnItemClickListener(mAutocompleteClickListener);
        btn_Time.setOnClickListener(this);
        btn_Clear.setOnClickListener(this);
        btn_Save.setOnClickListener(this);
        btn_Remove.setOnClickListener(this);
        //저장db가져오기
        mDatabase = LocationDatabase.getDataBase(this);
        mAlarm = new Alarm();
        mLocationData = new LocationData();
    }

    private void checkValue() {
        mPosition = getIntent().getIntExtra(EXTRA_DATA_POSITION, 0);
        mItemId = getIntent().getIntExtra(EXTRA_DATA_ID, 0);

        if (mItemId != 0) {
            mLocationData = mDatabase.getLocationDAO().getData(mItemId);

            int dayofWeek = mLocationData.getMDayOfWeek();

            tv_PlaceDetailsText.setText(mLocationData.getMLocation_name());
            tv_StartTime.setText(mLocationData.getMTime());
            tv_PlaceDetailsAddress.setText(mLocationData.getMLocation_address());
            //요일도 가져오기
            checkDayOfWeek(dayofWeek);
            //저장을 수정버튼으로 바꾸고 db update
            btn_Save.setText("수정");
            btn_Remove.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timeBtn:
                setTime();

                break;
            case R.id.clear_btn:
                mSearchPlace.setText("");

                break;
            case R.id.saveBtn:
                //설정이 완료되지 않은 경우
                if (mLocationData.getMTime() == null || mLocationData.getMLocation_name() == null || mLocationData.getMDayOfWeek() == 0) {
                    Toast.makeText(this, "설정을 마무리해주세요", Toast.LENGTH_SHORT).show();
                    break;
                }

                LocationDAO locationDAO = mDatabase.getLocationDAO();
                mLocationData.setMAlarmCheck(true);

                if (btn_Save.getText() == "수정") {
                    locationDAO.update(mLocationData);
                } else {
                    locationDAO.insert(mLocationData);
                    setAlarmID();
                }

                //알람매니저 등록
                mAlarm.setAlarm(this, mLocationData);

                setResult(RESULT_OK);
                finish();

                break;
            case R.id.removeBtn:

                removeData();

                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    private void setTime(){

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        boolean is24Hour = true;

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //시간 저장
                int hour = view.getCurrentHour();
                //DB에 시간 저장
                mLocationData.setMTime(hour + ":" + minute);

                tv_StartTime.setText(hour + ":" + minute);
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(LifeRadiusSettingActivity.this, onTimeSetListener, hour, minute, is24Hour);
        timePickerDialog.show();
    }

    private void removeData(){

        LocationData removeData = mDatabase.getLocationDAO().getLocation().get(mPosition);
        mDatabase.getLocationDAO().delete(removeData);//db항목 삭제
        //알람 지우기
        mAlarm.removeAlarm(this, removeData);

    }

    private void setAlarmID(){

        //locationData를 다시 가져오기<<알람 id를 가져오기 위해
        mDatabase =LocationDatabase.getDataBase(this);
        int max= mDatabase.getLocationDAO().getLocation().size()-1;
        int max_id= mDatabase.getLocationDAO().getLocation().get(max).getMId();

        mLocationData.setMId(max_id);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.tSun:
                setDayOfWeek(0, isChecked);
                break;
            case R.id.tMon:
                setDayOfWeek(1, isChecked);
                break;
            case R.id.tTue:
                setDayOfWeek(2, isChecked);
                break;
            case R.id.tWed:
                setDayOfWeek(3, isChecked);
                break;
            case R.id.tThur:
                setDayOfWeek(4, isChecked);
                break;
            case R.id.tFri:
                setDayOfWeek(5, isChecked);
                break;
            case R.id.tSat:
                setDayOfWeek(6, isChecked);
                break;
        }

        Log.d("요일 선택", "" + mDayOfWeek);
        mLocationData.setMDayOfWeek(mDayOfWeek);

    }

    //선택된 요일 bit계산
    private void setDayOfWeek(int day, boolean checked) {
        if (checked)
            mDayOfWeek |= (1 << day);
        else
            mDayOfWeek &= ~(1 << day);
    }

    private void checkDayOfWeek(int dayOfWeek) {
        for (int day = 0; day < 7; day++) {
            switch (day) {
                case 0:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        tb_Sun.setChecked(true);
                    }
                    break;
                case 1:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        tb_Mon.setChecked(true);
                    }
                    break;
                case 2:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        tb_Tue.setChecked(true);
                    }
                    break;
                case 3:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        tb_Wed.setChecked(true);
                    }
                    break;
                case 4:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        tb_Thur.setChecked(true);
                    }
                    break;
                case 5:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        tb_Fri.setChecked(true);
                    }
                    break;
                case 6:
                    if (((dayOfWeek >> day) & 1) == 1) {
                        tb_Sat.setChecked(true);
                    }
                    break;
            }
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    final AutocompletePrediction item = mAdapter.getPrediction_item(position);
                    final String placeId = item.getPlaceId();
                    //final CharSequence primaryText=item.getPrimaryText(null);

                    Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
                    placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);

                    Log.i("선택", "Called getPlaceById to get Place details for " + placeId);
                }
            };

    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback
            = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();

                // Get the Place object from the buffer.
                final Place place = places.get(0);

                LatLng latLng = place.getLatLng();

                mLocationData.setMLatitude(latLng.latitude);
                mLocationData.setMLongitude(latLng.longitude);

                tv_PlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName()));
                tv_PlaceDetailsAddress.setText(formatPlaceDetails(getResources(), place.getAddress()));

                mDataLocationName = formatPlaceDetails(getResources(), place.getName()).toString();
                mDataLocationAddress = formatPlaceDetails(getResources(), place.getAddress()).toString();

                //장소 저장
                mLocationData.setMLocation_name(mDataLocationName);
                mLocationData.setMLocation_address(mDataLocationAddress);

                // Display the third party attributions if set.
                final CharSequence thirdPartyAttribution = places.getAttributions();
                if (thirdPartyAttribution == null) {
                    tv_PlaceDetailsAddress.setVisibility(View.GONE);
                } else {
                    tv_PlaceDetailsAddress.setVisibility(View.VISIBLE);
                    tv_PlaceDetailsAddress.setText(
                            Html.fromHtml(thirdPartyAttribution.toString()));
                }

                Log.i("TAG", "Place details received: " + place.getName());
                places.release();

            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                Log.e("오류", "Place query did not complete.", e);
                return;
            }
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name) {

        return Html.fromHtml(res.getString(R.string.place_details, name));
    }
}
