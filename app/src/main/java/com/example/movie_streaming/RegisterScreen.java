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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterScreen extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnFB, btnGG, btnBack;
    ImageView imgLogo;
    TextView txtTitle, txtDes;
    TextInputLayout edtUser, edtPass, edtName;
    Button btnSignUp;
    LinearLayout linear;
    ProgressBar progressBar;
    DatabaseReference reference;

    private static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_screen);
        initUI();
    }

    private void initUI() {
        View someView = (View) findViewById(R.id.layout_register);
        someView.setBackgroundColor(getResources().getColor(android.R.color.white));

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
                String userName = edtUser.getEditText().getText().toString();
                String password = edtPass.getEditText().getText().toString();
                String name = edtName.getEditText().getText().toString();
                String avatar = "https://firebasestorage.googleapis.com/v0/b/moviestream-f6661.appspot.com/o/avatars%2Fuser1.jpg?alt=media&token=776839ac-1a14-47d9-a6fc-bd0b7314cbe8";

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(userName, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterScreen.this, "Register Success!!", Toast.LENGTH_LONG).show();
                            FirebaseUser user = auth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .setPhotoUri(Uri.parse(avatar))
                                    .build();

                            FirebaseFirestore.getInstance().collection("user").add(new User(userName, null, name, null, avatar, user.getUid()));

                            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("a", "update success");
                                        Intent intent = new Intent(RegisterScreen.this, MainActivity.class);
                                        startActivity(intent);
                                        finishAffinity();
                                        progressBar.setVisibility(View.GONE);
                                    } else
                                        Log.d("a", "update fail");
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterScreen.this, "????ng k?? th???t b???i", Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
                break;
            case R.id.btn_reg_facebook:
                break;
            case R.id.btn_reg_google:
                break;
        }
    }

    private Boolean validateUser() {
        String val = edtUser.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            edtUser.setError("Nh???p email");
            return false;
        } else if (!LoginScreen.VALIDATE_MAIL.matcher(val).matches()) {
            edtUser.setError("Email kh??ng h???p l???");
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
            edtPass.setError("Xin h??y nh???p m???t kh???u");
            return false;
        } else if (val.length() >= 15) {
            edtPass.setError("M???t kh???u kh??ng ???????c qu?? 15 k?? t???");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            edtPass.setError("M???t kh???u kh??ng h???p l???");
            return false;
        } else {
            edtPass.setError(null);
            return true;
        }
    }

    private Boolean validateName() {
        String val = edtName.getEditText().getText().toString();
        if (val.isEmpty()) {
            edtName.setError("Xin h??y nh???p h??? t??n");
            return false;
        } else {
            edtName.setError(null);
            return true;
        }
    }
}