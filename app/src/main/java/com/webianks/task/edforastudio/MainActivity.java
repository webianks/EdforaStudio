package com.webianks.task.edforastudio;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SongsAdapter.ClickListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private RelativeLayout bottomPlayer;
    private RelativeLayout mainPlayer;

    private long enqueue;
    private DownloadManager dm;
    //bottom_bar ui elements

    private ImageView bottomThumbnail;
    private TextView songTitle;
    private TextView bottomArtists;
    private ImageView playPause;

    //main_player_ui_elements
    private ImageView mainThumbnail;
    private TextView mainSongTitle;
    private TextView mainArtists;
    private ImageView mainPlayPause;
    private ImageView stopMain;

    private SongService player;
    boolean serviceBound = false;

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.webianks.task.edforastudio.PlayNewAudio";
    public static final String Broadcast_PAUSE_RESUME_AUDIO = "com.webianks.task.edforastudio.PauseResumeAudio";
    public static final String Broadcast_STOP_AUDIO = "com.webianks.task.edforastudio.StopAudio";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        getListFromTheNetwork();

        registerReceiver(downloadReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }


    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SongService.LocalBinder binder = (SongService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            //Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void init() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setEnabled(true);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        bottomPlayer = (RelativeLayout) findViewById(R.id.bottom_player);
        mainPlayer = (RelativeLayout) findViewById(R.id.main_player);

        bottomThumbnail = (ImageView) findViewById(R.id.thumbnail);
        songTitle = (TextView) findViewById(R.id.song_title);
        bottomArtists = (TextView) findViewById(R.id.artists);
        playPause = (ImageView) findViewById(R.id.play_pause);

        mainThumbnail = (ImageView) findViewById(R.id.main_cover);
        mainSongTitle = (TextView) findViewById(R.id.song_title_main);
        mainArtists = (TextView) findViewById(R.id.artists_main);
        mainPlayPause = (ImageView) findViewById(R.id.play_pause_main);
        stopMain = (ImageView) findViewById(R.id.stop_main);

        mainPlayPause.setOnClickListener(this);
        stopMain.setOnClickListener(this);


        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //hide upper layout slowly and make main_layout visible
                bottomPlayer.setAlpha(1 - slideOffset);
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

        playPause.setImageResource(R.drawable.ic_pause_circle_filled);
        mainPlayPause.setImageResource(R.drawable.ic_pause_circle_filled_large);

        mainPlayPause.setTag("pause");

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

        playAudio(url);

    }

    @Override
    public void downloadClicked(String url) {
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        enqueue = dm.enqueue(request);
    }


    private void playAudio(String media) {
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, SongService.class);
            playerIntent.putExtra("media", media);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver

            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            broadcastIntent.putExtra("media", media);
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }

        unregisterReceiver(downloadReceiver);
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.stop_main) {

            Intent broadcastIntent = new Intent(Broadcast_STOP_AUDIO);
            sendBroadcast(broadcastIntent);

            mainPlayPause.setImageResource(R.drawable.ic_play_circle_filled_large);
            mainPlayPause.setTag("play");

        } else if (view.getId() == R.id.play_pause_main) {

            Intent broadcastIntent = new Intent(Broadcast_PAUSE_RESUME_AUDIO);
            sendBroadcast(broadcastIntent);

            String tag = mainPlayPause.getTag().toString();

            switch (tag) {
                case "play":
                    mainPlayPause.setImageResource(R.drawable.ic_pause_circle_filled_large);
                    mainPlayPause.setTag("pause");
                    break;
                case "pause":
                    mainPlayPause.setImageResource(R.drawable.ic_play_circle_filled_large);
                    mainPlayPause.setTag("play");
                    break;

            }

        }
    }

    BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                Toast.makeText(context, "Download complete.", Toast.LENGTH_LONG).show();
            }
        }
    };



}
