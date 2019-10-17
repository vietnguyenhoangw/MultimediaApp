package com.example.changeprofilepicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavoriteSongsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SongAdapter songAdapter;
    ArrayList<Datum> arrayList;

    static MediaPlayer mediaPlayer = new MediaPlayer();

    String url = "https://api.deezer.com/search?redirect_uri=http%253A%252F%252Fguardian.mashape.com%252Fcallback&q=post%20malone&index=25";

    ImageView play;

    CircleImageView imageView;
    TextView tvSongname, tvArtists;

    Animation rotateAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_songs);

        imageView = findViewById(R.id.image);
        tvSongname = findViewById(R.id.tvNameSong);
        tvArtists = findViewById(R.id.tvArtists);

        recyclerView = findViewById(R.id.rcv);
        arrayList = new ArrayList<>();

        getSongs();

        LinearLayoutManager linearLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayout);

        songAdapter = new SongAdapter(this, arrayList, R.layout.song_item);
        recyclerView.setAdapter(songAdapter);

        rotateAnimation = AnimationUtils.loadAnimation(FavoriteSongsActivity.this, R.anim.rotate_anim);

        songAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Datum datum = arrayList.get(position);

                tvSongname.setText(datum.getTitle());
                tvArtists.setText(datum.getArtist().getName());

                Picasso.get()
                        .load(datum.getAlbum().getCover())
                        .into(imageView);

                imageView.startAnimation(rotateAnimation);
            }
        });

        play = findViewById(R.id.playorpause);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();

                rotateAnimation.cancel();
            }
        });
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

    // chua sai duoc
    private void playFileInternet() {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });

        try {
            mediaPlayer.setDataSource("https://cdns-preview-c.dzcdn.net/stream/c-cb5f9905d5b5dd3c76e2b59ace395fd3-3.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
    }
}
