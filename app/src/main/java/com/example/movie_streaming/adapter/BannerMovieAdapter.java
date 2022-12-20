package com.example.movie_streaming.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.movie_streaming.MovieDetail;
import com.example.movie_streaming.R;
import com.example.movie_streaming.model.Movie;

import java.util.List;

public class BannerMovieAdapter extends PagerAdapter {
    private final List<Movie> movies;
    private final Context context;

    public BannerMovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.banner_movie, null);
        ImageView imgBanner = view.findViewById(R.id.img_banner);
        Glide.with(context).load(movies.get(position).getImg()).into(imgBanner);
        container.addView(view);
        view.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, MovieDetail.class);
            intent.putExtra("movie", movies.get(position));
            context.startActivity(intent);
        });
        return view;
    }
}
