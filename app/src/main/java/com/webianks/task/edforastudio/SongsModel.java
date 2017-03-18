package com.webianks.task.edforastudio;

/**
 * Created by R Ankit on 18-03-2017.
 */

public class SongsModel {


    /**
     * song : Afreen Afreen
     * url : http://hck.re/Rh8KTk
     * artists : Rahat Fateh Ali Khan, Momina Mustehsan
     * cover_image : http://hck.re/kWWxUI
     */

    private String song;
    private String url;
    private String artists;
    private String cover_image;

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }
}
