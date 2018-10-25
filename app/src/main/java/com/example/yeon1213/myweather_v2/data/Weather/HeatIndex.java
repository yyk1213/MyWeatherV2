package com.example.yeon1213.myweather_v2.data.Weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HeatIndex {
    @SerializedName("grid")
    @Expose
    private Grid grid;
    @SerializedName("current")
    @Expose
    private Current current;
    @SerializedName("forecast")
    @Expose
    private Forecast forecast;

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

}
