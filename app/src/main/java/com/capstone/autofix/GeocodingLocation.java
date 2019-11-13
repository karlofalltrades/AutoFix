package com.capstone.autofix;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodingLocation {
    private static final String TAG = "GeocodingLocation";

    public static void getAddressFromLocation(final String locationAddress, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                double latitude = 0.00;
                double longitude = 0.00;
                try {
                    List <Address> addressList = geocoder.getFromLocationName(locationAddress, 10);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
//                        StringBuilder sb = new StringBuilder();
//                        sb.append(address.getLatitude()).append(", ");
//                        sb.append(address.getLongitude());
//                        result = sb.toString();
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable to connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (latitude !=0.00 && longitude !=0.00) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putDouble("latitude", latitude);
                        bundle.putDouble("longitude", longitude);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}
