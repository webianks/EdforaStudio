package com.webianks.task.edforastudio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

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
        recyclerView.setAdapter(songsAdapter);
        progressBar.setVisibility(View.GONE);
    }
}
