package com.example.movie_streaming.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie_streaming.R;
import com.example.movie_streaming.model.Movie;

import java.util.List;
import java.util.Map;

public class MainRecycleAdapter extends RecyclerView.Adapter<MainRecycleAdapter.MainViewHolder> {

    Context context;
    List<Map<String, Object>> categoryWithMovies;
    final private ListItemClickListener mOnClickListener;

    public MainRecycleAdapter(Context context, List<Map<String, Object>> categoryWithMovies, ListItemClickListener mOnClickListener) {
        this.context = context;
        this.categoryWithMovies = categoryWithMovies;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_all_category, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        Map<String, Object> item = categoryWithMovies.get(position);
        holder.txtTitle.setText(item.get("category").toString());
        setItemRecycle(holder.rcvItem, (List) item.get("movies"));
    }

    private void setItemRecycle(RecyclerView rcvItem, List<Movie> movies) {
        MovieAdapter movieAdapter = new MovieAdapter(context, movies);
        rcvItem.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        rcvItem.setAdapter(movieAdapter);
    }

    @Override
    public int getItemCount() {
        return categoryWithMovies.size();
    }

    public interface ListItemClickListener {
        void onCategoryItemClick(int clickedItemIndex);
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtTitle;
        RecyclerView rcvItem;

        public MainViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            rcvItem = itemView.findViewById(R.id.rcv_item);
        }

        @Override
        public void onClick(View view) {
            int clikedPosition = getAdapterPosition();
            mOnClickListener.onCategoryItemClick(clikedPosition);
        }
    }

}
