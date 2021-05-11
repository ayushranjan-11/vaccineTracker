package com.example.vaccinetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        jsonObject();

        Intent intent= new Intent(this,ForegroundService.class);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

    }

    public void jsonObject() {


        MediaPlayer player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);

        final TextView vaccinetextView=(TextView) findViewById(R.id.vaccine_name);
        final TextView TotalCount=(TextView)findViewById(R.id.totalCount);

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String url="https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByDistrict?district_id=294&date=";
        String finalurl=url.concat(date);


        requestQueue =Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.GET,
                finalurl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("onSuccess","The availability is "+response.getString("sessions"));

                    JSONArray jsonArray = response.getJSONArray("sessions");

                    int totalCount=jsonArray.length();

                    TotalCount.append("Centers available for vaccination: "+String.valueOf(totalCount));

                    for(int i=0;i<totalCount;i++) {

                        JSONObject centers=jsonArray.getJSONObject(i);

                        String name=centers.getString("name");
                        String vaccine_name=centers.getString("vaccine");
                        int availableSlot=centers.getInt("available_capacity");
                        int ageLimit=centers.getInt("min_age_limit");
                        int areapincode=centers.getInt("pincode");

                        if(ageLimit<45) {
                            vaccinetextView.append("Vaccine Center: "+name + ", "+"\n"+"Vaccine name: "+vaccine_name+", "+"\n"+"Available Slots: "+String.valueOf(availableSlot)+"\n"+"Min Age limit: "+String.valueOf(ageLimit)+"\n"+"Pincode: "+String.valueOf(areapincode)+"\n\n");
                            player.setLooping(true);
                            player.start();

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}

