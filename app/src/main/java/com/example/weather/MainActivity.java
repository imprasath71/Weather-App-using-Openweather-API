package com.example.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);
    }

    public void checkWeather(View view) {
        try {
            DownloadTask task = new DownloadTask();
            String s = editText.getText().toString();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q="+ s +"&appid=53d08e5561471fca638b993b03dca864");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),":"+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result.append(current);
                    data = reader.read();
                }

                return result.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {
                if(!TextUtils.isEmpty(s)){
                    JSONObject jsonObject = new JSONObject(s);

                    String weatherInfo = jsonObject.getString("weather");

                    JSONArray arr = new JSONArray(weatherInfo);

                    StringBuilder message = new StringBuilder();
                    if(arr.length()>0){
                        for (int i=0; i < arr.length(); i++) {
                            JSONObject jsonPart = arr.getJSONObject(i);

                            String main = jsonPart.getString("main");
                            String description = jsonPart.getString("description");

                            if (!main.equals("") && !description.equals("")) {
                                message.append(main).append(": ").append(description).append("\r\n");
                            }
                        }
                    }

                    if (!message.toString().equals("")) {
                        resultTextView.setText(message.toString());
                    }
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),":"+e.getMessage(),Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }

        }
    }
}
