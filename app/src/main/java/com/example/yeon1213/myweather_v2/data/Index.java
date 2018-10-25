package com.example.yeon1213.myweather_v2.data;

public class Index {
    private String livingName;
    private String livingExplanation;

    public Index(){}

    public Index(String living_name, String living_explanation) {
        this.livingName = living_name;
        this.livingExplanation =living_explanation;
    }

    public String getLivingName() {
        return livingName;
    }

    public String getLivingExplanation() {
        return livingExplanation;
    }

    public void setLivingName(String livingName) {
        this.livingName = livingName;
    }

    public void setLivingExplanation(String livingExplanation) {
        this.livingExplanation = livingExplanation;
    }
}
