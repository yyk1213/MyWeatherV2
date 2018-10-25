package com.example.yeon1213.myweather_v2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.yeon1213.myweather_v2.R;

import java.util.List;

public class MainIndexAdapter extends RecyclerView.Adapter<MainIndexAdapter.MainHolder> {

    private List<String> mLivingDataset;
    private Context mContext;

    public MainIndexAdapter(Context context,List<String> myDataset){
        this.mContext =context;
        this.mLivingDataset =myDataset;
    }

    @Override
    public MainHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.main_data_row, parent, false);

        return new MainHolder(view);
    }

    @Override
    public void onBindViewHolder(MainHolder holder, int position) {
        String weatherData = mLivingDataset.get(position);
        holder.tv_Index.setText(weatherData);
    }

    @Override
    public int getItemCount() {
        return mLivingDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class MainHolder extends RecyclerView.ViewHolder{

        public TextView tv_Index;
        public MainHolder(View itemView){
            super(itemView);
            tv_Index = itemView.findViewById(R.id.living_text);
        }
    }
}
