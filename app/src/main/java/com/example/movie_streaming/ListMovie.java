package com.example.movie_streaming;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie_streaming.adapter.ListMovieAdapter;
import com.example.movie_streaming.model.Favorite;
import com.example.movie_streaming.model.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListMovie extends AppCompatActivity implements ListMovieAdapter.ListItemClickListener {

    private final String TAG = "FavoriteMovie";
    private FirebaseFirestore fireStore;
    private List<Favorite> favorites;
    private ListMovieAdapter adapter;
    private List<String> movieIds;
    private RecyclerView rcvFav;
    private List<Movie> movies;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_favorite_movie);
        user = FirebaseAuth.getInstance().getCurrentUser();
        fireStore = FirebaseFirestore.getInstance();
        initUI();
    }

    @SuppressLint("ResourceAsColor")
    private void initUI() {
        SearchView searchView = findViewById(R.id.search_view);

        ImageView imgBack = findViewById(R.id.imgBack_frg);
        rcvFav = findViewById(R.id.recycle_fav);
        rcvFav.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        String type = getIntent().getStringExtra("type");
        if (type.equals("favorite")) {
            handleFavoritePage();
        } else {
            getListMovie(null);
        }

        imgBack.setOnClickListener(view -> finish());

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
        movieIds = new ArrayList<>();
        favorites = new ArrayList<>();
        fireStore.collection("favorite").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String movieId;
                String userUid;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    movieId = document.getData().get("movieId").toString();
                    userUid = document.getData().get("userUid").toString();
                    if (userUid.equals(user.getUid())) {
                        favorites.add(new Favorite(document.getId(), movieId, userUid));
                        movieIds.add(movieId);
                    }
                }
                getListMovie(movieIds);
            } else {
                Log.d(TAG, "Error getting list favorite: ", task.getException());
            }
        });
        swipe();
    }

    private void getListMovie(List<String> movieIds) {
        movies = new ArrayList<>();
        fireStore.collection("movie").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Movie movie;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (movieIds == null || movieIds.contains(document.getId())) {
                        movie = document.toObject(Movie.class);
                        movie.setId(document.getId());
                        movies.add(movie);
                    }
                }
                Log.d(TAG, "List movie" + movies.toString());
                adapter = new ListMovieAdapter(ListMovie.this, movies, ListMovie.this);
                rcvFav.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "Error getting list movie: ", task.getException());
            }
        });
    }

    private void swipe() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder dragged, RecyclerView.ViewHolder target) {
                int positionDragged = dragged.getAdapterPosition();
                int positionTarget = target.getAdapterPosition();
                Collections.swap(favorites, positionDragged, positionTarget);
                adapter.notifyItemMoved(positionDragged, positionTarget);
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int deleteIndex = viewHolder.getAdapterPosition();
                Favorite favorite = favorites.get(deleteIndex);
                removeFavorite(favorite.getId());
                movieIds.remove(favorite.getMovieId());
                getListMovie(movieIds);
            }
        });
        itemTouchHelper.attachToRecyclerView(rcvFav);
    }

    private void removeFavorite(String favoriteId) {
        fireStore.collection("favorite").document(favoriteId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ListMovie.this, "Đã xóa khỏi mục yêu thích", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ListMovie.this, "Thất bại", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onFavoriteItemClick(int clickedItemIndex) {
        Intent intent = new Intent(ListMovie.this, MovieDetail.class);
        intent.putExtra("movie", movies.get(clickedItemIndex));
        startActivity(intent);
    }

}