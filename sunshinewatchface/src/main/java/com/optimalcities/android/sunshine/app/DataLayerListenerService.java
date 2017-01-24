package com.optimalcities.android.sunshine.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayerSample";
    public static final String DATASYNC_URI_WEATHER_INFO = "/today_weather";
    private static final String KEY_WEATHER_ICON = "today_weather_icon";
    private static final String KEY_HIGH_WEATHER_TEMP = "highest_temperature";
    private static final String KEY_LOW_WEATHER_TEMP = "lowest_temperature";
    private static final String KEY_WEATHER_DESC = "weather_desc";

    public DataLayerListenerService(){
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
        }

        final List events = FreezableUtils
                .freezeIterable(dataEvents);

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)

                .build();

        ConnectionResult connectionResult =
                googleApiClient.blockingConnect(1, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {

            if(connectionResult.getErrorCode() == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED){

                Toast.makeText(this, R.string.update_google_play_services,Toast.LENGTH_SHORT).show();
            }
            Log.e(TAG, "Failed to connect to GoogleApiCLient");
            return;
        }

        // Loop through the events and send a message
        // to the node that created the data item.
        for (Object event : events) {


            // DataItem changed
            DataItem item = ((DataEvent) event).getDataItem();
            if (item.getUri().getPath().compareTo(DATASYNC_URI_WEATHER_INFO) == 0) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                String highTemp = dataMap.getString(KEY_HIGH_WEATHER_TEMP);
                String lowTemp = dataMap.getString(KEY_LOW_WEATHER_TEMP);
                String weatherCondition = dataMap.getString(KEY_WEATHER_DESC);

                Asset asset = dataMap.getAsset(KEY_WEATHER_ICON);
                Bitmap bitmap = loadBitmapFromAsset(asset, googleApiClient);

                WeatherInfo weatherData = WeatherInfo.getCurrentWeatherObject();
                weatherData.highTemp = highTemp;
                weatherData.lowTemp = lowTemp;
                weatherData.weatherDesc = weatherCondition;
                weatherData.weatherIcon = bitmap;

            }
        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset, GoogleApiClient mGoogleApiClient ) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset Must not be null");
        }
        ConnectionResult result =
                mGoogleApiClient.blockingConnect(10000, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Unkown Asset");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

}