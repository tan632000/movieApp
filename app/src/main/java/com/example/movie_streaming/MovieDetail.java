package com.example.movie_streaming;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movie_streaming.model.BannerMovie;
import com.example.movie_streaming.model.Favorite;
import com.example.movie_streaming.model.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class MovieDetail extends AppCompatActivity implements View.OnClickListener {
    Movie movie;
    BannerMovie bannerItem;
    Favorite favoriteItem;
    ImageView imgDetail, imgBack, imgAdd;
    TextView txtDetail;
    Button btnPlay;
    FirebaseUser user;
    DatabaseReference reference;
    boolean ADD = true;
    boolean UnADD = false;
    boolean IS_ADD = UnADD;
    String favoriteUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        user = FirebaseAuth.getInstance().getCurrentUser();
        favoriteItem = (Favorite) getIntent().getSerializableExtra("favorite_item");
        movie = (Movie) getIntent().getSerializableExtra("movie");
        bannerItem = (BannerMovie) getIntent().getSerializableExtra("banner");

        initUI();
    }

    private void initUI() {
        imgDetail = findViewById(R.id.imgDetail);
        txtDetail = findViewById(R.id.txtNameMovie);
        btnPlay = findViewById(R.id.btnPlay);
        imgBack = findViewById(R.id.imgBack);
        imgAdd = findViewById(R.id.imgAdd);
//        checkFavorite();

        if (movie != null) {
            Glide.with(this).load(movie.getImg()).into(imgDetail);
            txtDetail.setText(movie.getName());
        } else if (bannerItem != null) {
            Glide.with(this).load(bannerItem.getImg()).into(imgDetail);
            txtDetail.setText(bannerItem.getName());
        } else {
            Glide.with(this).load(favoriteItem.getImg()).into(imgDetail);
            txtDetail.setText(favoriteItem.getName());
        }

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieDetail.this, VideoPlayer.class);
                if (movie != null) {
                    intent.putExtra("url", movie.getVideo());
                    startActivity(intent);
                } else if (bannerItem != null) {
                    intent.putExtra("url", bannerItem.getVideo());
                    startActivity(intent);
                } else {
                    intent.putExtra("url", favoriteItem.getVideo());
                    startActivity(intent);
                }
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
                if (!IS_ADD) {
//                    addToFavorite(user);
                } else {
                    Toast.makeText(MovieDetail.this, "Phim đã tồn tại trong mục ưa thích", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void checkFavorite() {
        Favorite currenFavorite;
        if (movie != null) {
            currenFavorite = new Favorite(movie.getId(), movie.getName(), movie.getImg(), movie.getType(), movie.getVideo(), user.getUid());
        } else if (bannerItem != null) {
            currenFavorite = new Favorite(null, bannerItem.getName(), bannerItem.getImg(), bannerItem.getType(), bannerItem.getVideo(), user.getUid());
        } else {
            currenFavorite = favoriteItem;
        }
        reference = FirebaseDatabase.getInstance().getReference("favorite");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {

                    Favorite favorite = data.getValue(Favorite.class);

                    if (favorite.getUserUid() != null && favorite.getUserUid().compareTo(currenFavorite.getUserUid()) == 0) {
                        if (String.valueOf(favorite.getId()).compareTo(String.valueOf(currenFavorite.getId())) == 0) {
                            imgAdd.setImageResource(R.drawable.ic_baseline_add_red);
                            IS_ADD = ADD;
                            favoriteUid = data.getKey();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
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
//    private void addToFavorite(FirebaseUser user) {
//        Favorite favorite;
//        if (movie != null) {
//            favorite = new Favorite(movie.getId(), movie.getName(), movie.getImg(), movie.getType(), movie.getVideo(), user.getUid());
//        } else if (bannerItem != null) {
//            favorite = new Favorite(null, bannerItem.getName(), bannerItem.getImg(), bannerItem.getType(), bannerItem.getVideo(), user.getUid());
//        } else {
//            favorite = favoriteItem;
//        }
//
//        reference = FirebaseDatabase.getInstance().getReference("favorite");
//        reference.push().setValue(favorite).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(MovieDetail.this, "Đã thêm vào mục yêu thích", Toast.LENGTH_LONG).show();
//                    IS_ADD = ADD;
//                    imgAdd.setImageResource(R.drawable.ic_baseline_add_red);
//                } else {
//                    Toast.makeText(MovieDetail.this, "Thất bại", Toast.LENGTH_LONG).show();
//                    IS_ADD = UnADD;
//                    imgAdd.setImageResource(R.drawable.ic_baseline_add_24);
//                }
//            }
//        });
//
//        FirebaseFirestore.getInstance().collection("favorite").add(favorite);
//    }
}
