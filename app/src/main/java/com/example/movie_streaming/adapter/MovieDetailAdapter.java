package com.example.movie_streaming.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movie_streaming.MovieDetail;
import com.example.movie_streaming.R;
import com.example.movie_streaming.model.Movie;

import java.util.List;

public class MovieDetailAdapter extends RecyclerView.Adapter<MovieDetailAdapter.ItemViewHolder> {
    private final Context context;
    private final List<Movie> movies;

    public MovieDetailAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_category, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Movie categoryItem = movies.get(position);
        Glide.with(context).load(categoryItem.getImg()).into(holder.imgItem);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgItem;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imgItem = itemView.findViewById(R.id.img_item);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            Intent intent = new Intent(context, MovieDetail.class);
            intent.putExtra("movie", movies.get(clickedPosition));
            context.startActivity(intent);
        }
    }
}
