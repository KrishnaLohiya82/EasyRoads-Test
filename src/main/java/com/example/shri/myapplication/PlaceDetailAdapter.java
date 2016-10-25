package com.example.shri.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shri on 25/10/2016.
 */

public class PlaceDetailAdapter extends BaseAdapter {

    private List<HashMap<String, String>> _hashMapPlaces= new ArrayList<>();
    private Context _context;

    public PlaceDetailAdapter(Context pContext){
        this._hashMapPlaces = MainActivity.mTop3;

        this._context =pContext;
    }

    @Override
    public int getCount() {
        return _hashMapPlaces.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.place_details, parent, false);

        TextView mTVHeader = (TextView) view.findViewById(R.id.Header);
        mTVHeader.setText(_hashMapPlaces.get(position).get("place_name"));

        TextView mTVAddress = (TextView) view.findViewById(R.id.Address);
        mTVAddress.setText(_hashMapPlaces.get(position).get("vicinity"));

        TextView mTVNumber = (TextView) view.findViewById(R.id.number);
        mTVNumber.setText(_hashMapPlaces.get(position).get("formatted_phone"));

        TextView mTVRating = (TextView) view.findViewById(R.id.Rating);
        mTVRating.setText(_hashMapPlaces.get(position).get("rating"));

        TextView mTVUrl = (TextView) view.findViewById(R.id.url);
        mTVUrl.setText(_hashMapPlaces.get(position).get("website"));

        return view;
    }
}
