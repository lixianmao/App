package com.unique.dalian.voicephoto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import helper.Declare;
import helper.JSONHelper;
import helper.MyAdapter;

public class SeeActivity extends Activity {

    private GridView gridView;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see);

        gridView = (GridView) findViewById(R.id.see_grid);
        list = getList();
        if (list != null && list.size() > 0)
            gridView.setAdapter(new MyAdapter(this, list));
        gridView.setOnItemSelectedListener(itemSelectedListener);
        gridView.setOnItemClickListener(itemClickListener);
    }

    private List<String> getList() {
        JSONHelper jsonHelper = new JSONHelper(this);
        JSONArray array = jsonHelper.getArray();
        if (array == null)
            return null;

        List<String> list = new ArrayList<String>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = jsonHelper.getObject(array, i);
            try {
                String path = (String) object.get(Declare.PHOTO_PATH);
                list.add(path);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return list;
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), ShowActivity.class);
            intent.putExtra("path", list.get(position));
            intent.putExtra("position", position);
            startActivity(intent);
        }
    };

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.e("item selected", position + "");
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
