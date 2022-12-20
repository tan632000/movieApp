package com.example.movie_streaming;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movie_streaming.model.Favorite;
import com.example.movie_streaming.model.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MovieDetail extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MovieDetail";
    private Movie movie;
    private ImageView imgAdd;
    private Favorite favorite;
    private FirebaseUser user;
    private boolean isAddFavorite;
    private FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_movie_detail);

        fireStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        movie = (Movie) getIntent().getSerializableExtra("movie");

        initUI();
    }

    private void initUI() {
        ImageView imgDetail = findViewById(R.id.imgDetail);
        TextView txtDetail = findViewById(R.id.txtNameMovie);
        Button btnPlay = findViewById(R.id.btnPlay);
        ImageView imgBack = findViewById(R.id.imgBack);
        imgAdd = findViewById(R.id.imgAdd);
        checkFavorite();

        if (movie != null) {
            Glide.with(this).load(movie.getImg()).into(imgDetail);
            txtDetail.setText(movie.getName());
        }

        btnPlay.setOnClickListener(view -> {
            Intent intent = new Intent(MovieDetail.this, VideoPlayer.class);
            if (movie != null) {
                intent.putExtra("url", movie.getVideo());
                startActivity(intent);
            }
        });

        imgBack.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.imgAdd:
                if (!isAddFavorite) {
                    addToFavorite(user);
                } else {
                    removeFavorite();
                }
                break;
        }
    }

    private void checkFavorite() {
        fireStore.collection("favorite").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String movieId;
                String userUid;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    movieId = document.getData().get("movieId").toString();
                    userUid = document.getData().get("userUid").toString();
                    if (movieId.equals(movie.getId()) && userUid.equals(user.getUid())) {
                        favorite = new Favorite(document.getId(), movieId, userUid);
                        imgAdd.setImageResource(R.drawable.ic_baseline_add_red);
                        isAddFavorite = true;
                    }
                }
            } else {
                Log.d(TAG, "Error getting list favorite: ", task.getException());
            }
        });
    }

    private void addToFavorite(FirebaseUser user) {
        favorite = new Favorite(movie.getId(), user.getUid());
        fireStore.collection("favorite").add(favorite).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MovieDetail.this, "Đã thêm vào mục yêu thích", Toast.LENGTH_LONG).show();
                imgAdd.setImageResource(R.drawable.ic_baseline_add_red);
                favorite.setId(task.getResult().getId());
                isAddFavorite = true;
            } else {
                Toast.makeText(MovieDetail.this, "Thất bại", Toast.LENGTH_LONG).show();
                imgAdd.setImageResource(R.drawable.ic_baseline_add_24);
                isAddFavorite = false;
            }
        });
    }

    private void removeFavorite() {
        fireStore.collection("favorite").document(favorite.getId()).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MovieDetail.this, "Đã xóa khỏi mục yêu thích", Toast.LENGTH_LONG).show();
                imgAdd.setImageResource(R.drawable.ic_baseline_add_24);
                isAddFavorite = false;
            } else {
                Toast.makeText(MovieDetail.this, "Thất bại", Toast.LENGTH_LONG).show();
                imgAdd.setImageResource(R.drawable.ic_baseline_add_red);
                isAddFavorite = true;
            }
        });
    }

}
