package com.example.yeon1213.myweather_v2.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AddressAutoCompleteAdapter extends ArrayAdapter implements Filterable {
    public static final String TAG = "AddressAutoCompAdapter";

    private GeoDataClient mGeoDataClient;
    private AutocompleteFilter mPlaceFilter;
    private ArrayList<String> mResultList;
    private LatLngBounds mBounds;
    private ArrayList<AutocompletePrediction> mPredictionItem;

    public AddressAutoCompleteAdapter(@NonNull Context context, GeoDataClient mGeoDataClient, LatLngBounds mBounds, AutocompleteFilter mPlaceFilter) {
        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
        this.mGeoDataClient = mGeoDataClient;
        this.mPlaceFilter = mPlaceFilter;
        this.mBounds = mBounds;
    }

    public AutocompletePrediction getPredictionItem(int position) {
        return mPredictionItem.get(position);
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return mResultList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        return super.getView(position, view, parent);
    }

    //글씨 입력하면 필터가 자동으로 호출
    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();//필터 적용했을 때의 결과 값
                //필터 값 스트링을 받을 배열 선언
                ArrayList<String> filterData_String = new ArrayList<>();
                //자동완선 된 값을 필터 데이터 스트링에 담는다.
                if (constraint != null) {
                    filterData_String = getAutocomplete(constraint);
                }

                results.values = filterData_String;

                if (filterData_String != null) {
                    results.count = filterData_String.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results != null && results.count > 0) {
                    mResultList = (ArrayList<String>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                // Override this method to display a readable result in the AutocompleteTextView
                // when clicked.
                if (resultValue instanceof AutocompletePrediction) {
                    return ((AutocompletePrediction) resultValue).getPrimaryText(null);
                } else {
                    return super.convertResultToString(resultValue);
                }
            }
        };
    }

    //자동완성 API 호출하는 부분
    private ArrayList<String> getAutocomplete(CharSequence constraint) {
        Log.i(TAG, "Starting autocomplete query for: " + constraint);

        //API 쿼리 날리고, 쿼리 완료됐을 때 결과 값 갖고 있는 PendingResult 가져오기
        Task<AutocompletePredictionBufferResponse> results =
                mGeoDataClient.getAutocompletePredictions(constraint.toString(), mBounds,
                        mPlaceFilter);

        //api 결과 받아오기 위해 최대 60초 기다린다.
        try {
            Tasks.await(results, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        try {
            //result 결과를 가져오고
            AutocompletePredictionBufferResponse autocompletePredictions = results.getResult();
            //데이터를 특정형식으로 고정시켜서 자동완성 리스트에 담는다.
            ArrayList<AutocompletePrediction> autoCompleteList = DataBufferUtils.freezeAndClose(autocompletePredictions);

            ArrayList<String> auto_result = new ArrayList<>();
            mPredictionItem = new ArrayList<>();

            for (AutocompletePrediction prediction : autoCompleteList) {
                auto_result.add(prediction.getFullText(null).toString().substring(5));
                mPredictionItem.add(prediction);
            }

            return auto_result;

        } catch (RuntimeExecutionException e) {

            Toast.makeText(getContext(), "Error contacting API: " + e.toString(),
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error getting autocomplete prediction API call", e);
            return null;
        }
    }

}
