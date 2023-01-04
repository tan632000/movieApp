package com.example.movie_streaming;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie_streaming.adapter.ListMovieAdapter;
import com.example.movie_streaming.model.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchMovie extends AppCompatActivity implements ListMovieAdapter.ListItemClickListener {

    private final String TAG = "SearchMovie";
    private FirebaseFirestore fireStore;
    private ListMovieAdapter adapter;
    private List<String> categories;
    private List<String> movieIds;
    private RecyclerView rcvFav;
    private List<Movie> movies;
    private FirebaseUser user;
    private Spinner spinner;

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
        spinner = findViewById(R.id.search_type);

        ImageView imgBack = findViewById(R.id.imgBack_frg);
        rcvFav = findViewById(R.id.recycle_fav);
        rcvFav.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        String type = getIntent().getStringExtra("type");
        if (type.equals("favorite")) {
            handleFavoritePage();
        } else {
            setSpinnerSearchCategory(null);
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
        fireStore.collection("favorite").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Object movieId;
                Object userUid;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    movieId = document.getData().get("movieId");
                    userUid = document.getData().get("userUid");
                    if (userUid != null && userUid.equals(user.getUid()) && movieId != null) {
                        movieIds.add(movieId.toString());
                    }
                }
                setSpinnerSearchCategory(movieIds);
            } else {
                Log.d(TAG, "Error getting list favorite: ", task.getException());
            }
        });
    }

    private void setSpinnerSearchCategory(List<String> movieIds) {
        categories = new ArrayList<>();
        categories.add("Thể loại");
        fireStore.collection("category").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Object category;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    category = document.getData().get("type");
                    if (category != null) {
                        categories.add(category.toString());
                    }
                }
                Log.d(TAG, "List category" + categories.toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchMovie.this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            } else {
                Log.d(TAG, "Error getting list category: ", task.getException());
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String category = adapterView.getItemAtPosition(i).toString();
                if (category.equals("Thể loại")) category = null;
                getListMovie(category, movieIds);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void getListMovie(String category, List<String> movieIds) {
        movies = new ArrayList<>();
        fireStore.collection("movie").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Movie movie;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (movieIds == null || movieIds.contains(document.getId())) {
                        movie = document.toObject(Movie.class);
                        if (category == null || movie.getType().equals(category)) {
                            movie.setId(document.getId());
                            movies.add(movie);
                        }
                    }
                }
                Log.d(TAG, "List movie" + movies.toString());
                adapter = new ListMovieAdapter(SearchMovie.this, movies, SearchMovie.this);
                rcvFav.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "Error getting list movie: ", task.getException());
            }
        });
    }

    @Override
    public void onFavoriteItemClick(int clickedItemIndex) {
        Intent intent = new Intent(SearchMovie.this, MovieDetail.class);
        intent.putExtra("movie", movies.get(clickedItemIndex));
        startActivity(intent);
    }


}