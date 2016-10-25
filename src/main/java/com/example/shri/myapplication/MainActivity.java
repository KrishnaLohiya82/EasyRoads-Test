package com.example.shri.myapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.FloatProperty;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback {

    GoogleMap _googleMap;
    Spinner _sprPlaceType;

    public Context _context;

    String[] _placeType = null;
    String[] _placeTypeName = null;

    double _latitude = 0;
    double _longitude = 0;

    Button btn_top3;

    HashMap<String, String> _markerPlaceLink = new HashMap<String, String>();

    public static   List<HashMap<String, String>> _hashMapPlaces = new ArrayList<HashMap<String, String>>();

    public static List<HashMap<String, String>> mTop3 = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        _placeType = getResources().getStringArray(R.array.place_type);

        _placeTypeName = getResources().getStringArray(R.array.place_type_name);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, _placeTypeName);

        _sprPlaceType = (Spinner) findViewById(R.id.spr_place_type);

        _sprPlaceType.setAdapter(adapter);

         Button btnFind;

        btnFind = (Button) findViewById(R.id.btn_find);
        btn_top3 = (Button) findViewById(R.id.btn_top3);

         if (googleServicesAvailable()) {

            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            fragment.getMapAsync(this);


            btnFind.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (isNetworkAvailable()) {
                        int selectedPosition = _sprPlaceType.getSelectedItemPosition();
                        String type = _placeType[selectedPosition];

                        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                        sb.append("location=" + _latitude + "," + _longitude);
                        sb.append("&radius=3000");
                        sb.append("&types=" + type);
                        sb.append("&sensor=true");
                        sb.append("&key=AIzaSyDq9-TlFJFWC6g9Y2VHMKiA_Q_AgDydJ4g");

                        PlacesTask placesTask = new PlacesTask();

                        placesTask.execute(sb.toString());

                    }else {
                        Toast.makeText(getApplicationContext(), "Check the internet connectivity", Toast.LENGTH_SHORT).show();
                    }
                }
            });

             btn_top3.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Collections.sort(_hashMapPlaces, new Comparator<HashMap<String, String>>() {
                         @Override
                         public int compare(final HashMap<String, String> map1, final HashMap<String, String> map2) {

                             String map1Vle = map1.get("rating");
                             String map2Vle = map2.get("rating");

                             if(map1Vle.equalsIgnoreCase("-NA-")){
                                 map1Vle = "0.0";
                             }

                             if(map2Vle.equalsIgnoreCase("-NA-")){
                                 map2Vle = "0.0";
                             }

                             double map1Value = Double.parseDouble(map1Vle);
                             double map2Value =  Double.parseDouble(map2Vle);

                             if(map1Value==map2Value)
                                 return 0;
                             else if(map1Value > map2Value)
                                 return -1;
                             else
                                 return 1;
                         }
                     });

                     for( int i= 0 ; i < _hashMapPlaces.size();i++) {
                         if (i < 3) {
                             mTop3.add(_hashMapPlaces.get(i));
                         }
                     }


                     Intent mIntent = new Intent(getApplicationContext(),DetailActivity.class);

                     startActivity(mIntent);

                  }
             });

        }
    }




    public boolean isNetworkAvailable() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mActiveNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        boolean result = mActiveNetworkInfo != null && mActiveNetworkInfo.isConnected();
        return result;
    }

    public boolean googleServicesAvailable(){
        GoogleApiAvailability mAPI = GoogleApiAvailability.getInstance();
        int isAvailable = mAPI.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if (mAPI.isUserResolvableError(isAvailable)){
            Dialog mDailog = mAPI.getErrorDialog(this, isAvailable,0 );
            mDailog.show();
        }else {
            Toast.makeText(_context, "Cannot connect to play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
    _googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        _googleMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            onLocationChanged(location);
        }

        locationManager.requestLocationUpdates(provider, 20000, 0, this);


    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJsonParser placeJsonParser = new PlaceJsonParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            _googleMap.clear();

            for (int i = 0; i < list.size(); i++) {
                MarkerOptions mMarkerOptions = new MarkerOptions();

                HashMap<String, String> mHashMapPlace = list.get(i);

                double lat = Double.parseDouble(mHashMapPlace.get("lat"));
                double lng = Double.parseDouble(mHashMapPlace.get("lng"));
                String name = mHashMapPlace.get("place_name");
                String vicinity = mHashMapPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);
                mMarkerOptions.position(latLng);
                mMarkerOptions.title(name + " : " + vicinity);

                Marker mMarker = _googleMap.addMarker(mMarkerOptions);
                _markerPlaceLink.put(mMarker.getId(), mHashMapPlace.get("reference"));

                }
            _hashMapPlaces = list;
            if (list.size() > 2){
                btn_top3.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        _latitude = location.getLatitude();
        _longitude = location.getLongitude();
        LatLng latLng = new LatLng(_latitude, _longitude);

        _googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        _googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

}
