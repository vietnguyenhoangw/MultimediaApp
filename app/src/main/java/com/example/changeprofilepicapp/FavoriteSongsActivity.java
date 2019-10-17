package com.example.changeprofilepicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.changeprofilepicapp.object.Datum;
import com.example.changeprofilepicapp.object.Song;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FavoriteSongsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SongAdapter songAdapter;
    ArrayList<Datum> arrayList;

    String url = "https://api.deezer.com/search?redirect_uri=http%253A%252F%252Fguardian.mashape.com%252Fcallback&q=post%20malone&index=25";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_songs);

        recyclerView = findViewById(R.id.rcv);
        arrayList = new ArrayList<>();

        getSongs();

        LinearLayoutManager linearLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayout);

        songAdapter = new SongAdapter(this, arrayList, R.layout.song_item);
        recyclerView.setAdapter(songAdapter);
    }

    private void getSongs() {

        RequestQueue requestQueue = Volley.newRequestQueue(FavoriteSongsActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new GsonBuilder().create();
                        Song song = gson.fromJson(response, Song.class);

                        arrayList.addAll(song.getData());
                        songAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(stringRequest);
    }
}
