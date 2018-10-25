package com.example.yeon1213.myweather_v2.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.yeon1213.myweather_v2.adapter.LifeIndexAdapter;
import com.example.yeon1213.myweather_v2.data.Index;
import com.example.yeon1213.myweather_v2.R;

import java.util.ArrayList;
import java.util.List;

public class LifeIndexActivity extends AppCompatActivity {

    public static final String EXTRA_ACTIVITY_POSITION="com.example.yeon1213.myweather_v2.Activity.MainActivity.MenuItem_tag";

    private RecyclerView mLivingRecyclerView;
    private LifeIndexAdapter mLivingAdapter;
    private RecyclerView.LayoutManager mLivingLayoutManager;

    private List<Index> mLivingWeatherList;
    private int mItemId;
    private SharedPreferences mIndexPref;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_living_weather);

        initView();

        checkActivity();
    }

    private void initView(){

        mLivingWeatherList =new ArrayList<>();
        mIndexPref =getSharedPreferences("index_setting", Activity.MODE_PRIVATE);
        mEditor = mIndexPref.edit();

        mLivingRecyclerView =findViewById(R.id.recycler_view);
        mLivingRecyclerView.setHasFixedSize(true);

        mLivingLayoutManager =new LinearLayoutManager(getApplicationContext());
        mLivingRecyclerView.setLayoutManager(mLivingLayoutManager);

        mLivingAdapter =new LifeIndexAdapter(this, mLivingWeatherList);
        mLivingRecyclerView.setAdapter(mLivingAdapter);
    }

    private void checkActivity(){

        mItemId =getIntent().getIntExtra(EXTRA_ACTIVITY_POSITION,0);

        if(mItemId ==R.id.life){
            getSupportActionBar().setTitle("생활기상 지수 선택");
            LivingData();
        }
        else if(mItemId ==R.id.health){
            getSupportActionBar().setTitle("보건기상 지수 선택");
            HealthData();
        }
    }

    private void LivingData(){

        Index index_data =new Index("더위체감지수", "기온,습도 햇볕 등을 고려해 인체가 느끼는 더위를 지수로 환산(서비스 기간: 5월~ 9월)");
        mLivingWeatherList.add(index_data);

        index_data =new Index("자외선지수", "태양고도가 최대인 남중시간 때 지표에 도달하는 자외선의 복사랑을 지수로 환산(서비스 기간: 3월~ 11월)");
        mLivingWeatherList.add(index_data);

        index_data =new Index("식중독지수", "최근 5년('10년~14년)동안의 세균성, 바이러스성 식중독 발생 유무를 기반으로 기상에 따른 식중독 발생 가능성을 나타내는 것(서비스 기간: 연중)");
        mLivingWeatherList.add(index_data);

        index_data =new Index("불쾌지수", "기온과 습도의 조합으로 사람이 느끼는 온도를 표현한 것으로 온습도지수(THI)라고도 함(서비스 기간: 6월~ 9월)");
        mLivingWeatherList.add(index_data);

        index_data =new Index("열지수", "기온과 습도에 따른 사람이 실제로 느끼는 더위를 지수화한 것(서비스 기간: 6월~ 9월)");
        mLivingWeatherList.add(index_data);

        index_data =new Index("체감온도", "외부에 있는 사람이나 동물이 바람과 한기에 노출된 피부로부터 열을 빼앗길 때 느끼는 추운 정도를 나타내는 지수(서비스 기간: 11월~ 3월)");
        mLivingWeatherList.add(index_data);

        index_data =new Index("동파가능지수", "기온과 일최저기온을 이용하여, 겨울철 한파로 인해 발생되는 수도관 및 계량기의 동파발생가능성을 나타낸 지수(서비스 기간: 12월~ 2월)");
        mLivingWeatherList.add(index_data);

        index_data =new Index("대기확산지수", "오염물질이 대기 중에 유입되어 존재할 경우, 대기상태(소산과 관련된 기상요소)에 의해 변화될 수 있는 가능성 예보를 의미(서비스 기간: 11월~ 5월)");
        mLivingWeatherList.add(index_data);

        index_data =new Index("세차지수", "날씨에 따른 세차하기 좋은 정도(서비스 기간: 연중)");
        mLivingWeatherList.add(index_data);

        index_data =new Index("빨래지수", "날씨에 따른 빨래하기 좋은 정도(서비스 기간: 연중)");
        mLivingWeatherList.add(index_data);

        mLivingAdapter.notifyDataSetChanged();
    }

    private void HealthData(){
        Index index_data =new Index("천식폐질환가능지수", "기상조건(최저기온, 일교차, 현지기압, 상대습도)에 따른 천식·폐질환 발생 가능정도를 지수화(서비스 기간: 연중)");
        mLivingWeatherList.add(index_data);

        index_data =new Index("뇌졸중가능지수", "기상조건(최저기온, 일교차, 현지기압, 상대습도)에 따른 뇌졸중 발생 가능정도를 지수화(서비스 기간: 3월~ 11월)");
        mLivingWeatherList.add(index_data);

        index_data =new Index("피부질환가능지수", "기상조건(최고기온, 상대습도)에 따른 피부질환 발생 가능정도를 지수화(서비스 기간: 연중)");
        mLivingWeatherList.add(index_data);

        index_data =new Index("감기가능지수", "기상조건(최저기온, 일교차, 현지기압, 상대습도)에 따른 감기 발생 가능정도를 지수화(서비스 기간: 9월~4월)");
        mLivingWeatherList.add(index_data);

        index_data =new Index("꽃가루농도위험지수", "기상조건(최고기온, 최저기온, 강수량, 평균풍속 등)에 따른 꽃가루 알레르기 발생 가능정도를 지수화(서비스 기간: 4월~5월(참나무, 소나무),9월~10월(잡초류))");
        mLivingWeatherList.add(index_data);

        mLivingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
