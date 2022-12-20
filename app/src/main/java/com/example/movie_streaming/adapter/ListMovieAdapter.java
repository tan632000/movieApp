package com.example.movie_streaming.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movie_streaming.R;
import com.example.movie_streaming.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class ListMovieAdapter extends RecyclerView.Adapter<ListMovieAdapter.FavoriteViewHolder> implements Filterable {

    final private ListItemClickListener mOnClickListener;
    List<Movie> listItem, listItemOld;
    private final Context context;

    public ListMovieAdapter(Context context, List<Movie> listItem, ListItemClickListener mOnClickListener) {
        this.context = context;
        this.listItem = listItem;
        this.mOnClickListener = mOnClickListener;
        this.listItemOld = listItem;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        Movie movie = listItem.get(position);
        holder.txtTitle.setText(movie.getName());
        holder.txtType.setText(movie.getType());
        Glide.with(context).load(movie.getImg()).into(holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String keyword = constraint.toString().toLowerCase();
                if (keyword.isEmpty()) {
                    listItem = listItemOld;
                } else {
                    List<Movie> list = new ArrayList<>();
                    for (Movie movie : listItemOld) {
                        if (movie.getName().toLowerCase().contains(keyword))
                            list.add(movie);
                    }
                    listItem = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listItem;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listItem = (List<Movie>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ListItemClickListener {
        void onFavoriteItemClick(int clickedItemIndex);
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTitle, txtType;
        ImageView imgPoster;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtTitle = itemView.findViewById(R.id.tv_title_fav);
            txtType = itemView.findViewById(R.id.tv_type_fav);
            imgPoster = itemView.findViewById(R.id.iv_poster_fav);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onFavoriteItemClick(clickedPosition);
        }
    }
}
