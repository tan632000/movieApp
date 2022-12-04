package com.example.movie_streaming.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.movie_streaming.R;
import com.example.movie_streaming.model.AllCategory;
import com.example.movie_streaming.model.CategoryItem;

public class MainRecycleAdapter extends RecyclerView.Adapter<MainRecycleAdapter.MainViewHolder> {

    Context context;
    List<AllCategory> listCategory;
    final private ListItemClickListener mOnClickListener;

    public MainRecycleAdapter(Context context, List<AllCategory> listCategory, ListItemClickListener mOnClickListener) {
        this.context = context;
        this.listCategory = listCategory;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public MainViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_all_category, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder( MainViewHolder holder, int position) {
        AllCategory allCategory = listCategory.get(position);
        holder.txtTitle.setText(allCategory.getCateTitle());
        setItemRecycle(holder.rcvItem, allCategory.getListCategoryItem());
    }

    private void setItemRecycle(RecyclerView rcvItem, List<CategoryItem> listCategoryItem) {
        CategoryItemAdapter categoryItemAdapter = new CategoryItemAdapter(context,listCategoryItem);
        rcvItem.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        rcvItem.setAdapter(categoryItemAdapter);
    }

    @Override
    public int getItemCount() {
        return listCategory.size();
    }

    public interface ListItemClickListener{
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
