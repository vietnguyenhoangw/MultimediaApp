package com.example.changeprofilepicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.changeprofilepicapp.object.Datum;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.VH> {

    Context context;
    ArrayList<Datum> arrayList;
    int layout;

    public SongAdapter(Context context, ArrayList<Datum> arrayList, int layout) {
        this.context = context;
        this.arrayList = arrayList;
        this.layout = layout;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(layout, parent, false);

        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Datum datum = arrayList.get(position);

        holder.song.setText(datum.getTitle());
        holder.artists.setText(datum.getArtist().getName());
        holder.album.setText(datum.getAlbum().getTitle());

        Picasso.get()
                .load(datum.getAlbum().getCover())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class VH extends RecyclerView.ViewHolder {

        TextView song, artists, album;
        ImageView imageView;

        public VH(@NonNull View itemView) {
            super(itemView);

            song = itemView.findViewById(R.id.tvSongName);
            artists = itemView.findViewById(R.id.tvArtists);
            album = itemView.findViewById(R.id.tvAlbum);

            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
