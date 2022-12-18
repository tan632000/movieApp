package com.example.movie_streaming;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.movie_streaming.adapter.BannerMovieAdapter;
import com.example.movie_streaming.adapter.MainRecycleAdapter;
import com.example.movie_streaming.model.BannerMovie;
import com.example.movie_streaming.model.Category;
import com.example.movie_streaming.model.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements MainRecycleAdapter.ListItemClickListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    private List<BannerMovie> listHomeBanner, listTvShowBanner, listMovieBanner, listKidBanner;
    private TabLayout tabIndicater, tabCategory;
    private TextView txtName, txtUsername;
    private FirebaseFirestore fireStore;
    private List<Category> categories;
    private DrawerLayout drawerLayout;
    private CircleImageView imgUser;
    private ViewPager viewPager;
    private List<Movie> movies;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), 1);

        }
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        CircleImageView imgAvatar = findViewById(R.id.imgAvatar);

        NavigationView navigationView = findViewById(R.id.nav_user);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txtNavName);
        txtUsername = navigationView.getHeaderView(0).findViewById(R.id.txtUserName);
        tabIndicater = findViewById(R.id.tab_indicator);
        tabCategory = findViewById(R.id.tabCategory);
        imgUser = navigationView.getHeaderView(0).findViewById(R.id.img_user);

        listHomeBanner = new ArrayList<>();
        listTvShowBanner = new ArrayList<>();
        listMovieBanner = new ArrayList<>();
        listKidBanner = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("banners");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    BannerMovie uploadBanner = postSnapshot.getValue(BannerMovie.class);
                    if (uploadBanner.getType().equals("Home"))
                        listHomeBanner.add(uploadBanner);
                    if (uploadBanner.getType().equals("Tv Show"))
                        listTvShowBanner.add(uploadBanner);
                    if (uploadBanner.getType().equals("Movie"))
                        listMovieBanner.add(uploadBanner);
                    if (uploadBanner.getType().equals("Kids"))
                        listKidBanner.add(uploadBanner);
                }

                setBannerAdapter(listHomeBanner);

                tabCategory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        switch (tab.getPosition()) {
                            case 0:
                                setBannerAdapter(listHomeBanner);
                                break;
                            case 1:
                                setBannerAdapter(listTvShowBanner);
                                break;
                            case 2:
                                setBannerAdapter(listMovieBanner);
                                break;
                            case 3:
                                setBannerAdapter(listKidBanner);
                                break;
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        getListMovie();

        navigationView.setNavigationItemSelectedListener(this);
        imgAvatar.setOnClickListener(this);
        showUserInformation();
    }

    protected void getListMovie() {
        categories = new ArrayList<>();
        fireStore.collection("category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Category category;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        category = new Category(document.getId(), document.getData().get("type").toString());
                        categories.add(category);
                    }
                    Log.d(TAG, "List category" + categories.toString());
                    getListMovieByCategory();
                } else {
                    Log.d(TAG, "Error getting list category: ", task.getException());
                }
            }
        });
    }

    private void getListMovieByCategory() {
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

                List<Map<String, Object>> categoryWithMovies = new ArrayList<>();
                Map<String, Object> categoryWithMovie;
                List<Movie> temps;
                for (Category category : categories) {
                    temps = new ArrayList<>();
                    for (Movie temp : movies) {
                        if (temp.getType().equals(category.getType())) {
                            temps.add(temp);
                        }
                    }
                    if (!temps.isEmpty()) {
                        categoryWithMovie = new HashMap<>();
                        categoryWithMovie.put("category", category.getType());
                        categoryWithMovie.put("movies", temps);
                        categoryWithMovies.add(categoryWithMovie);
                    }
                }

                setCategoryWithMoviesAdapter(categoryWithMovies);
            } else {
                Log.d(TAG, "Error getting list movie: ", task.getException());
            }
        });
    }

    private void setCategoryWithMoviesAdapter(List<Map<String, Object>> categoryWithMovies) {
        RecyclerView categoryWithMoviesRecycle = findViewById(R.id.rcv_allcate);
        categoryWithMoviesRecycle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        MainRecycleAdapter mainRecycleAdapter = new MainRecycleAdapter(this, categoryWithMovies, this);
        mainRecycleAdapter.notifyDataSetChanged();
        categoryWithMoviesRecycle.setAdapter(mainRecycleAdapter);
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

    private void setBannerAdapter(List<BannerMovie> listBannerMovie) {
        viewPager = findViewById(R.id.bannerViewPager);
        BannerMovieAdapter bannerMovieAdapter = new BannerMovieAdapter(MainActivity.this, listBannerMovie);
        bannerMovieAdapter.notifyDataSetChanged();
        viewPager.setAdapter(bannerMovieAdapter);
        tabIndicater.setupWithViewPager(viewPager);

        Timer autoSlider = new Timer();
        autoSlider.schedule(new AutoSlider(listBannerMovie), 4000, 6000);
        tabIndicater.setupWithViewPager(viewPager, true);
    }

    @Override
    public void onCategoryItemClick(int clickedItemIndex) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgAvatar:
                showNavigationBar();
                break;
        }
    }

    private void showNavigationBar() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_fav:
                startActivity(new Intent(this, FavoriteMovie.class));
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
            case R.id.nav_search:
                Intent intent = new Intent(this, FavoriteMovie.class);
                intent.putExtra("nav_search", "nav_search");
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginScreen.class));
                finishAffinity();
        }
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_layout, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    public class AutoSlider extends TimerTask {

        List<BannerMovie> list;

        public AutoSlider(List<BannerMovie> list) {
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