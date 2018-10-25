package com.example.yeon1213.myweather_v2.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class AddressAutoCompleteAdapter extends ArrayAdapter implements Filterable{
    public static final String TAG = "AddressAutoCompAdapter";

    private GeoDataClient mGeoDataClient;
    private AutocompleteFilter mPlaceFilter;
    private ArrayList<String> mResultList;
    private LatLngBounds mBounds;
    private ArrayList<AutocompletePrediction> mPredictionItem;

    public AddressAutoCompleteAdapter(@NonNull Context context, GeoDataClient mGeoDataClient, LatLngBounds mBounds, AutocompleteFilter mPlaceFilter) {
        super(context,android.R.layout.simple_expandable_list_item_2,android.R.id.text1);
        this.mGeoDataClient = mGeoDataClient;
        this.mPlaceFilter = mPlaceFilter;
        this.mBounds = mBounds;
    }

    public AutocompletePrediction getPrediction_item(int position) {
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

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results=new FilterResults();//결과 값 담기

                //ArrayList<AutocompletePrediction> filterData=new ArrayList<>();
                ArrayList<String> filterData_String=new ArrayList<>();

                if(constraint !=null){
                    filterData_String=getAutocomplete(constraint);
                }

                results.values=filterData_String;
                if(filterData_String!=null){
                    results.count=filterData_String.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if(results!=null && results.count>0){
                    mResultList=(ArrayList<String>)results.values;
                            notifyDataSetChanged();
                }else{
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

    //자동완성
    private ArrayList<String> getAutocomplete(CharSequence constraint) {
        Log.i(TAG, "Starting autocomplete query for: " + constraint);

        // Submit the query to the autocomplete API and retrieve a PendingResult that will
        // contain the results when the query completes.
        Task<AutocompletePredictionBufferResponse> results =
                mGeoDataClient.getAutocompletePredictions(constraint.toString(), mBounds,
                        mPlaceFilter);

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(results, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        try {
            AutocompletePredictionBufferResponse autocompletePredictions = results.getResult();
            ArrayList<AutocompletePrediction> autoCompleteList=DataBufferUtils.freezeAndClose(autocompletePredictions);

            ArrayList<String> auto_result=new ArrayList<>();
            mPredictionItem =new ArrayList<>();

            for(AutocompletePrediction prediction:autoCompleteList){
                    auto_result.add(prediction.getFullText(null).toString().substring(5));
                    mPredictionItem.add(prediction);
            }

            return auto_result;

        } catch (RuntimeExecutionException e) {
            // If the query did not complete successfully return null
            Toast.makeText(getContext(), "Error contacting API: " + e.toString(),
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error getting autocomplete prediction API call", e);
            return null;
        }
    }

}
