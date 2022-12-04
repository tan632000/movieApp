package com.example.movie_streaming;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.movie_streaming.model.CategoryItem;
import com.example.movie_streaming.model.User;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.List;

public class RegisterScreen extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnFB, btnGG, btnBack;
    ImageView imgLogo;
    TextView txtTitle, txtDes;
    TextInputLayout edtUser, edtPass, edtName;
    Button btnSignUp;
    LinearLayout linear;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_screen);

        initUI();
    }

    private void initUI() {
        btnFB = findViewById(R.id.btn_reg_facebook);
        btnGG = findViewById(R.id.btn_reg_google);
        btnBack = findViewById(R.id.btnBack);
        imgLogo = findViewById(R.id.logoSignUp);
        txtTitle = findViewById(R.id.txtTitleSignUp);
        txtDes = findViewById(R.id.txtDesSignUp);
        edtUser = findViewById(R.id.username_signup);
        edtPass = findViewById(R.id.password_signup);
        edtName = findViewById(R.id.name_signup);
        btnSignUp = findViewById(R.id.btnRegister);
        linear = findViewById(R.id.linear_signup);

        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        Animation botAnim = AnimationUtils.loadAnimation(this, R.anim.bot_anim);
        Animation leftAnim = AnimationUtils.loadAnimation(this, R.anim.left_anim);

        btnBack.setAnimation(leftAnim);
        imgLogo.setAnimation(topAnim);
        txtTitle.setAnimation(topAnim);
        txtDes.setAnimation(topAnim);
        linear.setAnimation(botAnim);
        btnFB.setAnimation(botAnim);
        btnGG.setAnimation(botAnim);

        progressBar = (ProgressBar) findViewById(R.id.progres);
        Sprite wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);

        btnBack.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnFB.setOnClickListener(this);
        btnGG.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnRegister:
                if (!validateUser() | !validatePass() | !validateName()) {
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                hideKeyboard(RegisterScreen.this);

                //GET ALL VALUE
                String USER = edtUser.getEditText().getText().toString();
                String PASS = edtPass.getEditText().getText().toString();
                String NAME = edtName.getEditText().getText().toString();
                String IMG = "https://firebasestorage.googleapis.com/v0/b/moviestream-f6661.appspot.com/o/avatars%2Fuser1.jpg?alt=media&token=776839ac-1a14-47d9-a6fc-bd0b7314cbe8";

  //              List<CategoryItem> fav = null;

 //               User user = new User(USER, PASS, NAME, fav, IMG);

//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
//                reference.push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(Task<Void> task) {
//
//                        if (task.isSuccessful()) {
//                            Toast.makeText(RegisterScreen.this, "Register Success!!", Toast.LENGTH_LONG).show();
//                            progressBar.setVisibility(View.GONE);
//                        } else {
//                            Toast.makeText(RegisterScreen.this, "Fail", Toast.LENGTH_LONG).show();
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    }
//                });
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(USER, PASS).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterScreen.this, "Register Success!!", Toast.LENGTH_LONG).show();
                            FirebaseUser user = auth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(NAME)
                                    .setPhotoUri(Uri.parse(IMG))
                                    .build();

                            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("a", "update success");
                                        Intent intent = new Intent(RegisterScreen.this, MainActivity.class);
                                        startActivity(intent);
                                        finishAffinity();
                                        progressBar.setVisibility(View.GONE);
                                    }else
                                        Log.d("a", "update fail");
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterScreen.this, "Fail", Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
                //finish();
                break;
            case R.id.btn_reg_facebook:
                startActivity(new Intent(RegisterScreen.this, com.example.movie_streaming.RegisterFacebook.class));
                break;
            case R.id.btn_reg_google:
                startActivity(new Intent(RegisterScreen.this, com.example.movie_streaming.RegisterGmail.class));
                break;
        }
    }

    private Boolean validateUser() {
        String val = edtUser.getEditText().getText().toString().trim();
        //String noWhiteSpace = "\\A\\w{4,20}\\z";
        //String emailFormat = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,6}$";

        if (val.isEmpty()) {
            edtUser.setError("Nhập email");
            return false;
//        } else if (val.length() >= 15) {
//            edtUser.setError("không được quá 15 ký tự");
//            return false;
        } else if (!LoginScreen.VALIDATE_MAIL.matcher(val).matches()) {
            edtUser.setError("Email không hợp lệ");
            return false;
        } else {
            edtUser.setError(null);
            return true;
        }
    }

    private Boolean validatePass() {
        String val = edtPass.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            edtPass.setError("Nhập mật khẩu");
            return false;
        } else if (val.length() >= 15) {
            edtPass.setError("Không được quá 15 ký tự");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            edtPass.setError("Mật khẩu không hợp lệ");
            return false;
        } else {
            edtPass.setError(null);
            return true;
        }
    }

    private Boolean validateName() {
        String val = edtName.getEditText().getText().toString();

        if (val.isEmpty()) {
            edtName.setError("Điền họ tên");
            return false;
        } else {
            edtName.setError(null);
            return true;
        }
    }

    private static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}