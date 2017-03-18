package com.webianks.task.edforastudio;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by R Ankit on 18-03-2017.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.VH> {

    private Context context;
    private List<SongsModel> songsModelList;
    private String TAG = SongsAdapter.class.getSimpleName();
    private ClickListener clickListener;

    public SongsAdapter(Context context, List<SongsModel> songsModelList) {
        this.context = context;
        this.songsModelList = songsModelList;
    }

    @Override
    public SongsAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.single_song_list_item, parent, false);
        SongsAdapter.VH viewHolder = new SongsAdapter.VH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SongsAdapter.VH holder, int position) {

        holder.song.setText(songsModelList.get(position).getSong());
        holder.artists.setText(songsModelList.get(position).getArtists());

        Glide.with(context)
                .load(songsModelList.get(position).getCover_image())
                .centerCrop()
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return (songsModelList == null) ? 0 : songsModelList.size();
    }

    public class VH extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView thumbnail;
        TextView song;
        TextView artists;


        public VH(View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            song = (TextView) itemView.findViewById(R.id.song_title);
            artists = (TextView) itemView.findViewById(R.id.artists);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {

                String title = songsModelList.get(getAdapterPosition()).getSong();
                String artists = songsModelList.get(getAdapterPosition()).getArtists();
                String url = songsModelList.get(getAdapterPosition()).getUrl();
                String thumbnail = songsModelList.get(getAdapterPosition()).getCover_image();

                clickListener.itemClicked(title, artists, url,thumbnail);
            }
        }
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void itemClicked(String title, String artists, String url,String thumbnail);
    }
}
