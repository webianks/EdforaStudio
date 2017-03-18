package com.webianks.task.edforastudio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SongsAdapter.ClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MediaPlayer mMediaPlayer;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private RelativeLayout bottomPlayer;
    private RelativeLayout mainPlayer;

    //bottom_bar ui elements

    private ImageView bottomThumbnail;
    private TextView songTitle;
    private TextView bottomArtists;
    private ImageView playPause;

    //all bottom values
    private String title;
    private String artists;
    private String url;
    private String thumbnail;

    //main_player_ui_elements
    private ImageView mainThumbnail;
    private TextView mainSongTitle;
    private TextView mainArtists;
    private ImageView mainPlayPause;

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        getListFromTheNetwork();

    }

    private void init() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setEnabled(true);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                togglePlayPause();
            }
        });

        bottomPlayer = (RelativeLayout) findViewById(R.id.bottom_player);
        mainPlayer = (RelativeLayout) findViewById(R.id.main_player);

        bottomThumbnail = (ImageView) findViewById(R.id.thumbnail);
        songTitle = (TextView) findViewById(R.id.song_title);
        bottomArtists = (TextView) findViewById(R.id.artists);
        playPause = (ImageView) findViewById(R.id.play_pause);

        mainThumbnail = (ImageView) findViewById(R.id.main_cover);
        mainSongTitle = (TextView) findViewById(R.id.song_title_main);
        mainArtists = (TextView) findViewById(R.id.artists_main);
        //playPause = (ImageView) findViewById(R.id.play_pause);


        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //hide upper layout slowly and make main_layout visible
                bottomPlayer.setAlpha(1-slideOffset);
                mainPlayer.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });
        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

    }

    private void getListFromTheNetwork() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a jsonArray response from the provided URL.
        JsonArrayRequest songsListRequest = new JsonArrayRequest(Request.Method.GET, Constants.BASE_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        //Toast.makeText(MainActivity.this, "Response is: " + response.toString(), Toast.LENGTH_SHORT).show();
                        parseResponse(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "That didn't work!", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(songsListRequest);
    }

    private void parseResponse(JSONArray response) {

        List<SongsModel> songsModelList = new ArrayList<>();
        SongsModel songsModel;

        for (int i = 0; i < response.length(); i++) {

            try {

                JSONObject songObject = response.getJSONObject(i);

                String song_name = songObject.getString("song");
                String url = songObject.getString("url");
                String artists = songObject.getString("artists");
                String cover_image = songObject.getString("cover_image");

                songsModel = new SongsModel();
                songsModel.setSong(song_name);
                songsModel.setUrl(url);
                songsModel.setArtists(artists);
                songsModel.setCover_image(cover_image);

                songsModelList.add(songsModel);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        setSongsList(songsModelList);

    }

    private void setSongsList(List<SongsModel> songsModelList) {
        SongsAdapter songsAdapter = new SongsAdapter(this, songsModelList);
        songsAdapter.setClickListener(this);
        recyclerView.setAdapter(songsAdapter);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void itemClicked(String title, String artists, String url, String thumbnail) {


        this.title = title;
        this.artists = artists;
        this.thumbnail = thumbnail;
        this.url = url;

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }

        try {
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void togglePlayPause() {

        if (mMediaPlayer.isPlaying()) {

            mMediaPlayer.pause();
            playPause.setImageResource(R.drawable.ic_play_circle_filled);

        } else {

            mMediaPlayer.start();
            playPause.setImageResource(R.drawable.ic_pause_circle_filled);

            songTitle.setText(title);
            bottomArtists.setText(artists);

            mainSongTitle.setText(title);
            mainArtists.setText(artists);

            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            Glide.with(this)
                    .load(thumbnail)
                    .centerCrop()
                    .into(bottomThumbnail);

            Glide.with(this)
                    .load(thumbnail)
                    .centerCrop()
                    .into(mainThumbnail);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
