package com.example.movie_streaming;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movie_streaming.model.Favorite;
import com.example.movie_streaming.model.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MovieDetail extends AppCompatActivity implements View.OnClickListener {

    private Movie movie;
    private ImageView imgAdd;
    private FirebaseUser user;
    private boolean isAddFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

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
//        checkFavorite();

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
                    Toast.makeText(MovieDetail.this, "Phim đã tồn tại trong mục ưa thích", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void addToFavorite(FirebaseUser user) {
        if (movie != null) {
            FirebaseFirestore.getInstance().collection("favorite").add(new Favorite(movie.getId(), user.getUid())).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MovieDetail.this, "Đã thêm vào mục yêu thích", Toast.LENGTH_LONG).show();
                        isAddFavorite = true;
                        imgAdd.setImageResource(R.drawable.ic_baseline_add_red);
                    } else {
                        Toast.makeText(MovieDetail.this, "Thất bại", Toast.LENGTH_LONG).show();
                        isAddFavorite = false;
                        imgAdd.setImageResource(R.drawable.ic_baseline_add_24);
                    }
                }
            });
        }
    }

//    private void checkFavorite() {
//        Favorite currenFavorite;
//        if (movie != null) {
//            currenFavorite = new Favorite(movie.getId(), movie.getName(), movie.getImg(), movie.getType(), movie.getVideo(), user.getUid());
//        } else if (bannerItem != null) {
//            currenFavorite = new Favorite(null, bannerItem.getName(), bannerItem.getImg(), bannerItem.getType(), bannerItem.getVideo(), user.getUid());
//        } else {
//            currenFavorite = favoriteItem;
//        }
//        reference = FirebaseDatabase.getInstance().getReference("favorite");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                for (DataSnapshot data : snapshot.getChildren()) {
//
//                    Favorite favorite = data.getValue(Favorite.class);
//
//                    if (favorite.getUserUid() != null && favorite.getUserUid().compareTo(currenFavorite.getUserUid()) == 0) {
//                        if (String.valueOf(favorite.getId()).compareTo(String.valueOf(currenFavorite.getId())) == 0) {
//                            imgAdd.setImageResource(R.drawable.ic_baseline_add_red);
//                            IS_ADD = ADD;
//                            favoriteUid = data.getKey();
//                            break;
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void removeFavorite(FirebaseUser user) {
//        Favorite currenFavorite;
//        if (movie != null) {
//            currenFavorite = new Favorite(movie.getId(), movie.getName(), movie.getImg(), movie.getType(), movie.getVideo(), user.getUid());
//        } else if (bannerItem != null) {
//            currenFavorite = new Favorite(null, bannerItem.getName(), bannerItem.getImg(), bannerItem.getType(), bannerItem.getVideo(), user.getUid());
//        } else {
//            currenFavorite = favoriteItem;
//        }
//
//        reference = FirebaseDatabase.getInstance().getReference("favorite");
//        reference.child(favoriteUid).removeValue();
//
//        FirebaseFirestore.getInstance().collection("favorite").document(favoriteUid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(MovieDetail.this, "Đã xóa khỏi mục ưa thích", Toast.LENGTH_LONG).show();
//                    IS_ADD = UnADD;
//                    imgAdd.setImageResource(R.drawable.ic_baseline_add_24);
//                } else {
//                    Toast.makeText(MovieDetail.this, "Thất bại", Toast.LENGTH_LONG).show();
//                    IS_ADD = ADD;
//                    imgAdd.setImageResource(R.drawable.ic_baseline_add_red);
//                }
//            }
//        });
//    }
//

}
