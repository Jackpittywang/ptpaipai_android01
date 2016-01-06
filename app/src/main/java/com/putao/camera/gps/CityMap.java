package com.putao.camera.gps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.util.StringHelper;

/**
 * Created by jidongdong on 15/1/16.
 */
public class CityMap {
    private static CityMap cityMap;
    private static HashMap<String, CityPositon> cityListMap;

    public static CityMap getInstance() {
        if (cityMap == null) {
            cityMap = new CityMap();
        }
        return cityMap;
    }

    /**
     * init city list
     */
    public void init() {
        if (cityListMap != null) {
            cityListMap.clear();
        } else {
            cityListMap = new HashMap<String, CityPositon>();
        }
        ReadCityListFromTXT();
    }

    /**
     * return all city
     *
     * @return
     */
    public ArrayList<String> getAllCity() {
        ArrayList<String> list = new ArrayList<String>();
        if (cityListMap != null) {
            Iterator<String> iterator = cityListMap.keySet().iterator();
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
        }
        return list;
    }

    /**
     * return city list by key
     *
     * @param key
     * @return
     */
    public ArrayList<String> getCityListByKey(String key) {
        ArrayList<String> list = new ArrayList<String>();
        Iterator<String> iterator = cityListMap.keySet().iterator();
        while (iterator.hasNext()) {
            String city = iterator.next();
            if (city.contains(key)) {
                list.add(city);
            }
        }
        return list;
    }

    /**
     * get the city by location (longitude,latitude)
     *
     * @param longitude
     * @param latitude
     * @return
     */
    public String getCityByLocation(String longitude, String latitude) {
        if (cityListMap == null)
            return null;
        String city = null;
        Iterator<String> iterator = cityListMap.keySet().iterator();
        while (iterator.hasNext()) {
            city = iterator.next();
            CityPositon pos = cityListMap.get(city);
            if (getNumPrePoint(pos.longitude) == getNumPrePoint(longitude) && getNumPrePoint(pos.latitude) == getNumPrePoint(latitude)) {
                break;
            }
        }
        return city;
    }

    /**
     * return the num's Integer part
     *
     * @param numString
     * @return
     */
    private int getNumPrePoint(String numString) {
        if (StringHelper.isEmpty(numString))
            return 0;
        numString = numString.trim();
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(numString);
        boolean bool = m.find();
        if (bool) {
            return Integer.valueOf(m.group());
        } else {
            return 0;
        }
    }

    /**
     * get the location by city name
     *
     * @param city
     * @return
     */
    public CityPositon getLocationByCity(String city) {
        if (StringHelper.isEmpty(city) || cityListMap == null) {
            return null;
        }
        return (cityListMap.containsKey(city)) ? cityListMap.get(city) : null;
    }

    /**
     * Load city list from txt file
     */
    private void ReadCityListFromTXT() {
        InputStream inputStream = MainApplication.getInstance().getResources().openRawResource(R.raw.citylist);
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String city = line.split("=")[0];
                String location = line.split("=")[1];
                CityPositon pos = new CityPositon(location.split(",")[0], location.split(",")[1]);
                cityListMap.put(city, pos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * city location  struct
     */
    public class CityPositon {
        public String longitude;
        public String latitude;

        public CityPositon(String Longitude, String Latitude) {
            longitude = Longitude;
            latitude = Latitude;
        }
    }
}
