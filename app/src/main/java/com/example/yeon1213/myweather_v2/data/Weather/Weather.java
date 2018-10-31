package com.example.yeon1213.myweather_v2.data.Weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {

    @SerializedName("hourly")
    @Expose
    private List<Hourly> hourly = null;

    public List<Hourly> getHourly() {
        return hourly;
    }

    public void setHourly(List<Hourly> hourly) {
        this.hourly = hourly;
    }

    @SerializedName("wIndex")
    @Expose
    private WIndex wIndex;

    public WIndex getWIndex() {
        return wIndex;
    }

    public void setWIndex(WIndex wIndex) {
        this.wIndex = wIndex;
    }

    @SerializedName("minutely")
    @Expose
    private List<Minutely> minutely = null;

    public List<Minutely> getMinutely() {
        return minutely;
    }

    public void setMinutely(List<Minutely> minutely) {
        this.minutely = minutely;
    }


    class Pressure {

        @SerializedName("surface")
        @Expose
        private String surface;
        @SerializedName("seaLevel")
        @Expose
        private String seaLevel;

        public String getSurface() {
            return surface;
        }

        public void setSurface(String surface) {
            this.surface = surface;
        }

        public String getSeaLevel() {
            return seaLevel;
        }

        public void setSeaLevel(String seaLevel) {
            this.seaLevel = seaLevel;
        }
    }

    class Rain {

        @SerializedName("sinceMidnight")
        @Expose
        private String sinceMidnight;
        @SerializedName("last10min")
        @Expose
        private String last10min;
        @SerializedName("last15min")
        @Expose
        private String last15min;
        @SerializedName("last30min")
        @Expose
        private String last30min;
        @SerializedName("last1hour")
        @Expose
        private String last1hour;
        @SerializedName("last6hour")
        @Expose
        private String last6hour;
        @SerializedName("last12hour")
        @Expose
        private String last12hour;
        @SerializedName("sinceOntime")
        @Expose
        private String sinceOntime;
        @SerializedName("last24hour")
        @Expose
        private String last24hour;

        public String getSinceMidnight() {
            return sinceMidnight;
        }

        public void setSinceMidnight(String sinceMidnight) {
            this.sinceMidnight = sinceMidnight;
        }

        public String getLast10min() {
            return last10min;
        }

        public void setLast10min(String last10min) {
            this.last10min = last10min;
        }

        public String getLast15min() {
            return last15min;
        }

        public void setLast15min(String last15min) {
            this.last15min = last15min;
        }

        public String getLast30min() {
            return last30min;
        }

        public void setLast30min(String last30min) {
            this.last30min = last30min;
        }

        public String getLast1hour() {
            return last1hour;
        }

        public void setLast1hour(String last1hour) {
            this.last1hour = last1hour;
        }

        public String getLast6hour() {
            return last6hour;
        }

        public void setLast6hour(String last6hour) {
            this.last6hour = last6hour;
        }

        public String getLast12hour() {
            return last12hour;
        }

        public void setLast12hour(String last12hour) {
            this.last12hour = last12hour;
        }

        public String getSinceOntime() {
            return sinceOntime;
        }

        public void setSinceOntime(String sinceOntime) {
            this.sinceOntime = sinceOntime;
        }

        public String getLast24hour() {
            return last24hour;
        }

        public void setLast24hour(String last24hour) {
            this.last24hour = last24hour;
        }
    }
}
