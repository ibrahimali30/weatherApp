package com.ibrahim.a84jsonandweatherapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = (TextView) findViewById(R.id.textView);
        city = (EditText) findViewById(R.id.editText);
        mListView = findViewById(R.id.listview);

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
//                Log.d(TAG, "onCreate: "+matcher.group(1));
            }

//            String ss = "{" ;
//            for (int i = list.size()-1 ; i>=0 ; i--){
//                ss = ss + "\""+list.get(i)+"\""+",";
//
//            }
//            Log.d(TAG, "onCreate:size "+list.size());
//
//            InputFilter filter = new InputFilter() {
//                public CharSequence filter(CharSequence source, int start, int end,
//                                           Spanned dest, int dstart, int dend) {
//                    for (int i = start; i < end; i++) {
//                        if (!Character.isLetterOrDigit(source.charAt(i))) {
//                            return "";
//                        }
//                    }
//                    return null;
//                }
//            };
//            city.setFilters(new InputFilter[] { filter });



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
                    showresult();
                }
            });









        } catch (IOException ex) {
            ex.printStackTrace();
        }



//        String s = Resources.getSystem().openRawResource(R.raw.city).toString();






//        task.onPostExecute(html);
//        String main =task.getMain();

    }

    public void showresult(){

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(city.getWindowToken(),0);

        String city1 = null;
        try {
            city1 = URLEncoder.encode(city.getText().toString(),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        DownloadTask task = new DownloadTask();
        String jsonString=null;
        try {
            jsonString = task.execute("http://api.openweathermap.org/data/2.5/weather?q="+city1+"&appid=360097c40e4cf8c957c8dd47addcd42f" ).get();
            Log.i("============",jsonString);

            displayWeatherDetails(jsonString);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void displayWeatherDetails(String jsonString) {
        Log.d(TAG, "displayWeatherDetails: ");
    }
}
