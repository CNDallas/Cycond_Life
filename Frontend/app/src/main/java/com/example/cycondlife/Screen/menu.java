package com.example.cycondlife.Screen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.cycondlife.Communication.Callback_handler;
import com.example.cycondlife.Communication.Chat;
import com.example.cycondlife.Game.Character;
import com.example.cycondlife.Game.Game;
import com.example.cycondlife.R;


import org.json.JSONArray;
import org.json.JSONObject;

public class menu extends AppCompatActivity {
    public Callback_handler callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //button object creations
        Button stats = findViewById(R.id.stats);
        Button inventory = findViewById(R.id.inventorylist);
        Button shop = findViewById(R.id.shop);
        Button friends = findViewById(R.id.friends);
        Button map = findViewById(R.id.map);
        Button dev_menu = findViewById(R.id.dev_menu);
        if(Game.monster_map.size()==0)
        {
            pull_monster_map();
        }

        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openStats= new Intent(menu.this, stats_menu.class);
                startActivity(openStats);
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Cycond Life", "Attempt to open map from main menu");
                //Toast.makeText(getApplicationContext(), "Opening map...", Toast.LENGTH_SHORT);
                Intent openMap = new Intent(menu.this, MapsActivity.class);
                startActivity(openMap);
            }
        });

        dev_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Cycond Life", "Attempt to open dev menu");
                Intent openDevMenu = new Intent(menu.this, com.example.cycondlife.Screen.dev_menu.class);
                startActivity(openDevMenu);
            }
        });

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openFriends= new Intent(menu.this, Chat.class);
                startActivity(openFriends);
            }
        });


    }
    private void pull_monster_map()
    {
        callback = new Callback_handler() {
            @Override
            public void get_response(JSONArray a) {
                try {
                    JSONArray response = a;
                    for(int i=0;i<response.length();i++) {
                        JSONObject mon = response.getJSONObject(i);
                        Game.add_monster(new Character(mon.getInt("id"), mon.getInt("type"), mon.getDouble("latitude"), mon.getDouble("longitude")));
                        Game.num_monsters++;
                    }
                }
                catch (Exception e)
                {
                    Log.i("Cycond Life", "convert error");
                    Log.i("Cycond Life", e.getLocalizedMessage());
                }
                }

            @Override
            public void get_object_response(JSONObject o) {
                return ;
            }
        };
        getResponse(Request.Method.GET, "", null, callback);
                // Initialize a new JsonArrayRequest instance


                // Add JsonArrayRequest to the RequestQueue


    }
    /* public void setActivityBackgroundColor(int color) {      //Can possibly be used to change background color of main menu
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }   */
    public void getResponse(int method, String url, JSONObject jsonValue, final Callback_handler callback)
    {
        Context c= getApplicationContext();
        final RequestQueue requestQueue = Volley.newRequestQueue(c);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                "http://cs309-sd-6.misc.iastate.edu:8080/api/monsters/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Do something with response
                        //mTextView.setText(response.toString());

                        // Process the JSON
                        Log.i("Cycond test", "request succsessful");
                        callback.get_response(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when error occurred
                        Log.i("Cycond Life", "monster request error");
                        Log.i("Cycond Life", error.getLocalizedMessage());
                    }

    });
        requestQueue.add(jsonArrayRequest);
    }
    @Override
    protected void onPause()
    {
        super.onPause();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
    }
}