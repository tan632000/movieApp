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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.movie_streaming.R;
import com.example.movie_streaming.model.Favorite;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> implements Filterable {

    private Context context;
    List<Favorite> listItem, listItemOld;
    final private ListItemClickListener mOnClickListener;

    public FavoriteAdapter(Context context, List<Favorite> listItem, ListItemClickListener mOnClickListener) {
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
        Favorite favorite = listItem.get(position);

        holder.txtTitle.setText(favorite.getName());
        holder.txtType.setText(favorite.getType());
        Glide.with(context).load(favorite.getImg()).into(holder.imgPoster);
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
                if (keyword.isEmpty()){
                    listItem = listItemOld;
                }else{
                    List<Favorite> list = new ArrayList<>();
                    for (Favorite favorite : listItemOld){
                        if (favorite.getName().toLowerCase().contains(keyword))
                            list.add(favorite);
                    }
                    listItem = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listItem;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listItem = (List<Favorite>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ListItemClickListener{
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
