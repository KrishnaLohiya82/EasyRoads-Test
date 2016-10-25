package com.example.shri.myapplication;

/**
 * Created by Shri on 22/10/2016.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlaceJsonParser {

    public List<HashMap<String,String>> parse(JSONObject jObject){

        JSONArray jPlaces = null;
        try {
            jPlaces = jObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jPlaces);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jPlaces){
        int placesCount = jPlaces.length();
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> place = null;

        for(int i=0; i<placesCount;i++){
            try {
                place = getPlace((JSONObject)jPlaces.get(i));
                placesList.add(place);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject jPlace){

        HashMap<String, String> place = new HashMap<String, String>();
        String placeName = "-NA-";
        String vicinity="-NA-";
        String latitude="";
        String longitude="";
        String reference="";
        String formatted_phone="-NA-";
        String website="-NA-";
        String rating="-NA-";
        String url="-NA-";

        try {
            if(!jPlace.isNull("name")){
                placeName = jPlace.getString("name");
            }

            if(!jPlace.isNull("vicinity")){
                vicinity = jPlace.getString("vicinity");
            }

            if(!jPlace.isNull("formatted_phone_number")){
                formatted_phone = jPlace.getString("formatted_phone_number");
            }

            if(!jPlace.isNull("website")){
                website = jPlace.getString("website");
            }

            if(!jPlace.isNull("rating")){
                rating = jPlace.getString("rating");
            }

            if(!jPlace.isNull("url")){
                url = jPlace.getString("url");
            }


            latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = jPlace.getString("reference");

            place.put("place_name", placeName);
            place.put("vicinity", vicinity);
            place.put("lat", latitude);
            place.put("lng", longitude);
            place.put("reference", reference);
            place.put("formatted_phone", formatted_phone);
            place.put("website", website);
            place.put("rating", rating);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }
}