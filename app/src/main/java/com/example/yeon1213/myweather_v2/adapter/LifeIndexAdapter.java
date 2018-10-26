package com.example.yeon1213.myweather_v2.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.yeon1213.myweather_v2.data.Index;
import com.example.yeon1213.myweather_v2.R;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class LifeIndexAdapter extends RecyclerView.Adapter<LifeIndexAdapter.living_ViewHolder>{

    private List<Index> mIndexList;
    private Context mContext;
    private SharedPreferences mIndexPref;
    private SharedPreferences.Editor mEditor;

    public LifeIndexAdapter(Context context, List<Index> myDataset){
        this.mContext =context;
        mIndexList =myDataset;

        mIndexPref =context.getSharedPreferences("index_setting", Activity.MODE_PRIVATE);
        mEditor = mIndexPref.edit();
    }

    @Override
    public living_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.living_row,parent,false);
        return new living_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(living_ViewHolder living_viewHolder, int position) {
        living_viewHolder.itemView.setTag(position);

        Index LD= mIndexList.get(position);
        living_viewHolder.tv_LivingName.setText(LD.getLivingName());
        living_viewHolder.tv_LivingExpla.setText("- " + LD.getLivingExplanation());

        boolean index_check= mIndexPref.getBoolean(LD.getLivingName(),false);

        living_viewHolder.cb_Index.setChecked(index_check);
    }

    @Override
    public int getItemCount() {
        return mIndexList.size();
    }

    public class living_ViewHolder extends RecyclerView.ViewHolder implements CheckBox.OnCheckedChangeListener{

        public TextView tv_LivingName, tv_LivingExpla;
        public CheckBox cb_Index;

        public living_ViewHolder(View v){
            super(v);

            tv_LivingName =v.findViewById(R.id.livingName);
            tv_LivingExpla =v.findViewById(R.id.living_expla);
            cb_Index =v.findViewById(R.id.index_checkbox);

            cb_Index.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            int item_ID=(int) itemView.getTag();

            if(isChecked){
                mEditor.putBoolean(mIndexList.get(item_ID).getLivingName(),true);
            }else{
                mEditor.putBoolean(mIndexList.get(item_ID).getLivingName(),false);
            }
            mEditor.commit();

            ((Activity) mContext).setResult(RESULT_OK);
        }
    }
}
