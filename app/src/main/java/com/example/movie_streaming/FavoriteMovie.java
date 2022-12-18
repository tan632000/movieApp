package com.example.movie_streaming;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie_streaming.adapter.FavoriteAdapter;
import com.example.movie_streaming.model.CategoryItem;
import com.example.movie_streaming.model.Favorite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavoriteMovie extends AppCompatActivity implements FavoriteAdapter.ListItemClickListener {

    ImageView imgBack;
    FavoriteAdapter adapter;
    RecyclerView rcvFav;
    List<Favorite> mdata;
    FirebaseUser user;
    DatabaseReference reference;
    LinearLayout layout;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movie);
        initUI();
        swipe();
    }

    @SuppressLint("ResourceAsColor")
    private void initUI() {
        searchView = findViewById(R.id.search_view);
        layout = findViewById(R.id.layout_main_fav);
        mdata = new ArrayList<>();
        imgBack = findViewById(R.id.imgBack_frg);
        user = FirebaseAuth.getInstance().getCurrentUser();
        rcvFav = findViewById(R.id.recycle_fav);
        rcvFav.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        if (getIntent().getStringExtra("nav_search") != null) {
            //search page -> get all movies
            handleSearchPage();
        } else {
            //favorite page -> get favorite movies
            handleFavoritePage();
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void handleFavoritePage() {
        reference = FirebaseDatabase.getInstance().getReference("favorite");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (mdata.size() != 0) {
                    mdata.clear();
                }
                for (DataSnapshot data : snapshot.getChildren()) {
                    Favorite favorite = data.getValue(Favorite.class);
                    if (favorite.getUserUid() != null && favorite.getUserUid().compareTo(user.getUid()) == 0) {
                        mdata.add(favorite);
                        adapter = new FavoriteAdapter(FavoriteMovie.this, mdata, FavoriteMovie.this::onFavoriteItemClick);
                        rcvFav.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        swipe();
    }

    private void handleSearchPage() {
        reference = FirebaseDatabase.getInstance().getReference("movies");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (mdata.size() != 0) {
                    mdata.clear();
                }
                for (DataSnapshot data : snapshot.getChildren()) {
                    CategoryItem movie = data.getValue(CategoryItem.class);
                    Favorite favorite = null;
                    mdata.add(favorite);
                    adapter = new FavoriteAdapter(FavoriteMovie.this, mdata, FavoriteMovie.this::onFavoriteItemClick);
                    rcvFav.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void swipe() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            //Ham onMovee xu ly codee drag va drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder dragged, RecyclerView.ViewHolder target) {
                int positionDragged = dragged.getAdapterPosition();
                int positionTarget = target.getAdapterPosition();
                Collections.swap(mdata, positionDragged, positionTarget);
                adapter.notifyItemMoved(positionDragged, positionTarget);
                return false;
            }

            //trượt xóa
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int deleteIndex = viewHolder.getAdapterPosition();
                Favorite item = mdata.get(deleteIndex);
                removeFavorite(user, item);
            }
        });
        itemTouchHelper.attachToRecyclerView(rcvFav);
    }

    private void removeFavorite(FirebaseUser user, Favorite item) {
        Favorite currenFavorite = new Favorite();

//        currenFavorite = new Favorite(item.getId(), item.getName(), item.getImg(), item.getType(), item.getVideo(), item.getUserUid());

        reference = FirebaseDatabase.getInstance().getReference("favorite");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {

                    Favorite favorite = data.getValue(Favorite.class);

                    if (favorite.getUserUid().compareTo(currenFavorite.getUserUid()) == 0) {
                        if (String.valueOf(favorite.getId()).compareTo(String.valueOf(currenFavorite.getId())) == 0) {
                            reference.child(data.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Snackbar snackbar = Snackbar.make(layout, " Deleted from Favorite list", Snackbar.LENGTH_SHORT);
                                        snackbar.setAction("UNDO", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                reference.push().setValue(item);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                        snackbar.setActionTextColor(Color.RED);
                                        snackbar.show();
                                    } else {

                                    }
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    public void onFavoriteItemClick(int clickedItemIndex) {
        Intent intent = new Intent(FavoriteMovie.this, MovieDetail.class);
        intent.putExtra("favorite_item", mdata.get(clickedItemIndex));
        startActivity(intent);
    }

}