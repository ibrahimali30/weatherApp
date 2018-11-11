package com.ibrahim.a84jsonandweatherapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    EditText city;
    TextView textview;
    ListView mListView;
    ArrayAdapter mArrayAdapter;
    ImageView mImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = (TextView) findViewById(R.id.textView);
        city = (EditText) findViewById(R.id.editText);
        mListView = findViewById(R.id.listview);
        mImageView = findViewById(R.id.weatherStatusImageView);

        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.city);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
//            Log.d(TAG, "onCreate: "+json);

            Pattern pattern = Pattern.compile("\"name\": \"(.*?)\",");
            Matcher matcher = pattern.matcher(json);

            final ArrayList<String> list = new ArrayList<>();

            while (matcher.find()){

                list.add(matcher.group(1));

            }



            mArrayAdapter = new ArrayAdapter(this , android.R.layout.simple_list_item_1 , list);

            mListView.setAdapter(mArrayAdapter);

            city.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    mArrayAdapter.getFilter().filter(charSequence);
                    mListView.setVisibility(View.VISIBLE);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //selecting listview item
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    city.setText(mArrayAdapter.getItem(i).toString());
                    mListView.setVisibility(View.INVISIBLE);
                    //download data and parse json
                    showresult();
                }
            });


        } catch (IOException ex) {
            ex.printStackTrace();
        }



    }

    public void showresult(){
        Log.d(TAG, "showresult: called");
        //hide input method
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(city.getWindowToken(),0);

        String city = null;
        try {
            city = URLEncoder.encode(this.city.getText().toString(),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        DownloadTask task = new DownloadTask();
        String jsonString=null;
        try {
            jsonString = task.execute("http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=360097c40e4cf8c957c8dd47addcd42f" ).get();

            //parse json string and display results
            displayWeatherDetails(jsonString);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void displayWeatherDetails(String jsonString)  {
        Log.d(TAG, "displayWeatherDetails: called");

        try {
            JSONObject mainObject = new JSONObject(jsonString);

            JSONObject weather = mainObject.getJSONArray("weather").getJSONObject(0);
            JSONObject wind = mainObject.getJSONObject("wind");
            JSONObject main = mainObject.getJSONObject("main");
            JSONObject coord = mainObject.getJSONObject("coord");

            String mainTemp = weather.getString("main");
            String description = weather.getString("description");
            double maxTempInCel = main.getDouble("temp_max")-273.15;
            double minTempInCel = main.getDouble("temp_min")-273.15;
            String windSpeed = wind.getString("speed");
            String lat = coord.getString("lat");
            String lon = coord.getString("lon");

            int id = getResources().getIdentifier("com.ibrahim.a84jsonandweatherapp:drawable/icon" + weather.getString("icon"), null, null);
            mImageView.setImageResource(id);

            textview.setText("main      : " + mainTemp+"\n\n"
                    +"description : "+ description+"\n\n"
                    +"temp_max   : "+maxTempInCel+"\n\n"
                    +"temp_min    : "+minTempInCel+"\n\n"
                    +"wind speed : "+windSpeed+"\n\n"
                    +"latitude     : "+lat+"\n\n"
                    +"longitude   : "+lon+"\n\n"
            );


            Log.d(TAG, "displayWeatherDetails: "+weather);

        } catch (JSONException e) {
            Log.d(TAG, "displayWeatherDetails: error while parsing "+e.getMessage());
            e.printStackTrace();
        }

    }
}
