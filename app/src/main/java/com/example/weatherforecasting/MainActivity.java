package com.example.weatherforecasting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
ProgressBar progressBar;
RelativeLayout RL;
TextView CityName, Temp, Condition;
RecyclerView RvWeather;
TextInputLayout textInputLayout;
TextInputEditText EditCity;
ImageView back,searchIMV, IVicon;
ArrayList<RvModel>RvModelArrayList;
WeatherRVAdapter Adapter;
LocationManager locationManager;
int PERMISSION_CODE = 1;
String cityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.PbLoading);
        RL = findViewById(R.id.Loading);
        CityName = findViewById(R.id.CityName);
        Temp = findViewById(R.id.tvTemp);
        Condition = findViewById(R.id.tvCondition);
        RvWeather = findViewById(R.id.RvWeather);
        back = findViewById(R.id.back);
        searchIMV = findViewById(R.id.searchImageView);
        IVicon = findViewById(R.id.IVicon);
        RvModelArrayList = new ArrayList<>();
        Adapter = new WeatherRVAdapter(this, RvModelArrayList);
        RvWeather.setAdapter(Adapter);
        EditCity = findViewById(R.id.EditCity);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(), location.getLatitude());
        getWeatherInfo(cityName);
        searchIMV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = EditCity.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter a city", Toast.LENGTH_SHORT).show();
                }else{
                    CityName.setText(city);
                    getWeatherInfo(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0 &&grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private  String getCityName(double Longitude, double Latitude){
        String cityName = "Not Found:";
        Geocoder gd = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> address = gd.getFromLocation(Latitude, Longitude,10);
            for (Address adr: address){
                if (adr != null){
                    String city = adr.getLocality();
                    if (city != null &&!city.equals("")){
                        cityName = city;
                    }else{
                        Toast.makeText(this, "City Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    private  void getWeatherInfo(String cityName){
        String url ="http://api.weatherapi.com/v1/current.json?key=1dbd8e6db17f4e2ca0380936220212&q="+cityName+"&aqi=yes";
        CityName.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                RL.setVisibility(View.VISIBLE);
                RvModelArrayList.clear();

                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    Temp.setText(temperature+"°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http".concat(conditionIcon)).into(IVicon);
                    Condition.setText(condition);
                    if (isDay==1){
                        Picasso.get().load("https://www.pinterest.com/pin/985231158869692/").into(back);
                    }else{
                        Picasso.get().load("https://www.pinterest.com/pin/777363585699589931/").into(back);
                    }


                    JSONObject forecastJson = response.getJSONObject("forecast");
                    JSONObject forecastArray = forecastJson.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastArray.getJSONArray("hour");

                    for (int i = 0; i<hourArray.length(); i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");

                        RvModelArrayList.add(new RvModel(time, temper, img, wind));
                    }
                    Adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please Enter Valid City Name", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}