package com.optimalcities.android.sunshine.app;

import android.graphics.Bitmap;

/**
 * Created by obelix on 02/01/2017.
 */

public class WeatherInfo {

    public static WeatherInfo weatherInfo = new WeatherInfo();

    public String highTemp;

    public String lowTemp;

    public String weatherDesc;

    public Bitmap weatherIcon;

    public WeatherInfo(){

    }

    public static WeatherInfo getCurrentWeatherObject(){
        return weatherInfo;
    }
}
