
package com.putao.camera.gps;

import java.util.Iterator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.util.Loger;
import com.putao.camera.util.SharedPreferencesHelper;

public class GpsService extends Service {
    private LocationManager lm;
    private final String Tag = "gps";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请打开GPS定位设置", Toast.LENGTH_SHORT).show();
            return;
        }
        String bestProvider = lm.getBestProvider(getCriteria(), true);
        Location location = lm.getLastKnownLocation(bestProvider);
        resetLocationInfo(location);
        lm.removeGpsStatusListener(listener);
        lm.addGpsStatusListener(listener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            resetLocationInfo(location);
            Loger.d(Tag + ":" + "time:" + location.getTime());
            Loger.d(Tag + ":" + "long:" + location.getLongitude());
            Loger.d(Tag + ":" + "lat:" + location.getLatitude());
            Loger.d(Tag + ":" + "alt:" + location.getAltitude());
        }

        public void onProviderEnabled(String provider) {
            Location location = lm.getLastKnownLocation(provider);
            resetLocationInfo(location);
        }

        public void onProviderDisabled(String provider) {
            resetLocationInfo(null);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
//                    Loger.d(Tag+":"+"GPS visible");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
//                    Loger.d(Tag+":"+"GPS out of service");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                    Loger.d(Tag+":"+"GPS pause service");
                    break;
            }
        }
    };
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
//                    Loger.d(Tag+":"+"first fix");
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//                    Loger.d(Tag+":"+"satellite status");
                    GpsStatus gpsStatus = lm.getGpsStatus(null);
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
//                    Loger.d(Tag+":"+"search:" + count + " satellite");
                    break;
                case GpsStatus.GPS_EVENT_STARTED:
//                    Loger.d(Tag+":"+"started loc");
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
//                    Loger.d(Tag+":"+"stop loc");
                    break;
            }
        }

        ;
    };

    /**
     * @param location
     */
    private void resetLocationInfo(Location location) {
        if (location != null) {
            Loger.d("long,lat:" + location.getLongitude() + "," + location.getLatitude());
            SharedPreferencesHelper.saveStringValue(this, PuTaoConstants.PREFERENC_LOCATION_LONGITUDE, location.getLongitude() + "");
            SharedPreferencesHelper.saveStringValue(this, PuTaoConstants.PREFERENC_LOCATION_LATITUDE, location.getLatitude() + "");
            Loger.d("city----->" + CityMap.getInstance().getCityByLocation(location.getLongitude() + "", location.getLatitude() + ""));
            Loger.d("distance------->" + GpsUtil.GetDistance(location.getLongitude(), location.getLatitude(), 101.62, 34.75));
        } else {
        }
    }

    /**
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);
        criteria.setBearingRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(locationListener);
    }
}
