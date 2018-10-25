package com.example.yeon1213.myweather_v2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.yeon1213.myweather_v2.database.LocationData;
import com.example.yeon1213.myweather_v2.adapter.RadiusAdapter;
import com.example.yeon1213.myweather_v2.database.LocationDatabase;
import com.example.yeon1213.myweather_v2.R;

import java.util.List;

public class LivingRadiusActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRadiusRecyclerView;
    private RadiusAdapter mAdapter;
    private RecyclerView.LayoutManager mRadiusLayoutManager;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_radius);

        getSupportActionBar().setTitle("생활반경 설정");

        initView();

        updateUI();
    }

    private void initView(){

        mRadiusRecyclerView = findViewById(R.id.radius_recycler_view);
        mRadiusRecyclerView.setHasFixedSize(true);

        mRadiusLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRadiusRecyclerView.setLayoutManager(mRadiusLayoutManager);

        fab = findViewById(R.id.radius_plus_btn);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //플로팅 버튼 누를 때
        Intent settingRadiusIntent = new Intent(LivingRadiusActivity.this, LifeRadiusSettingActivity.class);

        startActivityForResult(settingRadiusIntent,0);
    }

    private void updateUI() {

        List<LocationData> radius = LocationDatabase.getDataBase(this).getLocationDAO().getLocation();

        mAdapter = new RadiusAdapter(this, radius);

        mRadiusRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_OK){
            updateUI();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
