package com.example.shri.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Created by Shri on 25/10/2016.
 */
public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_listview);
        ListView mListView =  (ListView) findViewById(R.id.lv_details);
        PlaceDetailAdapter mPlaceDetailAdapter = new PlaceDetailAdapter(getApplicationContext());
        mListView.setAdapter(mPlaceDetailAdapter);
    }

}
