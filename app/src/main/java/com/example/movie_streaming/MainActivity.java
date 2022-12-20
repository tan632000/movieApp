package com.example.movie_streaming;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.movie_streaming.adapter.BannerMovieAdapter;
import com.example.movie_streaming.adapter.MainActivityAdapter;
import com.example.movie_streaming.model.Category;
import com.example.movie_streaming.model.Movie;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements MainActivityAdapter.ListItemClickListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    private TextView txtName, txtUsername;
    private FirebaseFirestore fireStore;
    private List<Category> categories;
    private DrawerLayout drawerLayout;
    private CircleImageView imgUser;
    private TabLayout tabIndicator;
    private ViewPager viewPager;
    private List<Movie> movies;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        checkAndRequestPermissions();

        fireStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        initUI();
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.INTERNET
        };
        List<String> listPermissionNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(permission);
            }
        }
        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[0]), 1);

        }
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        tabIndicator = findViewById(R.id.tab_indicator);

        NavigationView navigationView = findViewById(R.id.nav_user);
        CircleImageView imgAvatar = findViewById(R.id.imgAvatar);
        txtUsername = navigationView.getHeaderView(0).findViewById(R.id.txtUserName);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txtNavName);
        imgUser = navigationView.getHeaderView(0).findViewById(R.id.img_user);

        getListMovie();

        navigationView.setNavigationItemSelectedListener(this);
        imgAvatar.setOnClickListener(this);
        showUserInformation();
    }

    private void getListMovie() {
        movies = new ArrayList<>();
        fireStore.collection("movie").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Movie movie;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    movie = document.toObject(Movie.class);
                    movie.setId(document.getId());
                    movies.add(movie);
                }
                Log.d(TAG, "List movie" + movies.toString());
                setBannerAdapter(movies.subList(0, 5));
                getListMovieByCategory();
            } else {
                Log.d(TAG, "Error getting list movie: ", task.getException());
            }
        });
    }

    protected void getListMovieByCategory() {
        categories = new ArrayList<>();
        fireStore.collection("category").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Category category;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    category = new Category(document.getId(), document.getData().get("type").toString());
                    categories.add(category);
                }
                Log.d(TAG, "List category" + categories.toString());

                List<Map<String, Object>> categoryWithMovies = new ArrayList<>();
                Map<String, Object> categoryWithMovie;
                List<Movie> temps;
                for (Category item : categories) {
                    temps = new ArrayList<>();
                    for (Movie temp : movies) {
                        if (temp.getType().equals(item.getType())) {
                            temps.add(temp);
                        }
                    }
                    if (!temps.isEmpty()) {
                        categoryWithMovie = new HashMap<>();
                        categoryWithMovie.put("category", item.getType());
                        categoryWithMovie.put("movies", temps);
                        categoryWithMovies.add(categoryWithMovie);
                    }
                }
                setCategoryWithMoviesAdapter(categoryWithMovies);

            } else {
                Log.d(TAG, "Error getting list category: ", task.getException());
            }
        });
    }

    private void setCategoryWithMoviesAdapter(List<Map<String, Object>> categoryWithMovies) {
        MainActivityAdapter mainActivityAdapter = new MainActivityAdapter(this, categoryWithMovies, this);
        mainActivityAdapter.notifyDataSetChanged();
        RecyclerView categoryWithMoviesRecycle = findViewById(R.id.rcv_allcate);
        categoryWithMoviesRecycle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        categoryWithMoviesRecycle.setAdapter(mainActivityAdapter);
    }

    private void setBannerAdapter(List<Movie> movies) {
        viewPager = findViewById(R.id.bannerViewPager);
        BannerMovieAdapter bannerMovieAdapter = new BannerMovieAdapter(MainActivity.this, movies);
        bannerMovieAdapter.notifyDataSetChanged();
        viewPager.setAdapter(bannerMovieAdapter);
        tabIndicator.setupWithViewPager(viewPager);

        Timer autoSlider = new Timer();
        autoSlider.schedule(new AutoSlider(movies), 4000, 6000);
        tabIndicator.setupWithViewPager(viewPager, true);
    }

    private void showUserInformation() {
        if (user == null)
            return;
        if (user.getDisplayName() == null) {
            txtName.setVisibility(View.GONE);
        } else {
            txtName.setVisibility(View.VISIBLE);
            txtName.setText(user.getDisplayName());
        }
        txtUsername.setText(user.getEmail());
        Glide.with(MainActivity.this).load(user.getPhotoUrl()).error(R.drawable.user1).into(imgUser);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgAvatar) {
            showNavigationBar();
        }
    }

    private void showNavigationBar() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = new Intent(this, ListMovie.class);
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_favorite:
                intent.putExtra("type", "favorite");
                drawerLayout.closeDrawer(GravityCompat.END);
                startActivity(intent);
                break;
            case R.id.nav_search:
                intent.putExtra("type", "search");
                drawerLayout.closeDrawer(GravityCompat.END);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginScreen.class));
                finishAffinity();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCategoryItemClick(int clickedItemIndex) {
    }

    public class AutoSlider extends TimerTask {
        List<Movie> list;

        public AutoSlider(List<Movie> list) {
            this.list = list;
        }

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(() -> {
                if (viewPager.getCurrentItem() < list.size() - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                } else {
                    viewPager.setCurrentItem(0);
                }
            });
        }
    }
}