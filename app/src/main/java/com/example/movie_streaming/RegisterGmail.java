package com.example.movie_streaming;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterGmail extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnBack;
    ImageView imgLogo;
    TextView txtTitle;
    TextInputLayout edtUser, edtPass;
    Button btnSignup;
    LinearLayout linear;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_gmail);

        initUI();
    }

    private void initUI() {
        btnBack = findViewById(R.id.btnBackGG);
        imgLogo = findViewById(R.id.logoSignUpGG);
        txtTitle = findViewById(R.id.txtTitleSignUpGG);
        edtUser = findViewById(R.id.username_signupGG);
        edtPass = findViewById(R.id.password_signupGG);
        btnSignup = findViewById(R.id.btnRegisterGG);
        linear = findViewById(R.id.linear_signupGG);

        progressBar = (ProgressBar)findViewById(R.id.progres);
        Sprite wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);

        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        Animation botAnim = AnimationUtils.loadAnimation(this, R.anim.bot_anim);
        Animation leftAnim = AnimationUtils.loadAnimation(this,R.anim.left_anim);

        btnBack.setAnimation(leftAnim);
        imgLogo.setAnimation(topAnim);
        txtTitle.setAnimation(topAnim);
        linear.setAnimation(botAnim);

        btnBack.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBackGG:
                finish();
                break;
            case R.id.btnRegisterGG:
                createUser();
                break;
        }
    }

    private void createUser(){
        if(!validationUser() | !validattionPass()){
            return;
        }
        hideKeyboard(RegisterGmail.this);
        progressBar.setVisibility(View.VISIBLE);

        String strEmail = edtUser.getEditText().getText().toString().trim();
        String strPass = edtPass.getEditText().getText().toString().trim();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(strEmail, strPass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterGmail.this, "Success!!", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();

                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName("Pham Le Anh Tuyet")
                            .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/moviestream-f6661.appspot.com/o/avatars%2Fuser1.jpg?alt=media&token=776839ac-1a14-47d9-a6fc-bd0b7314cbe8"))
                            .build();

                    user.updateProfile(profileChangeRequest).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            Intent intent = new Intent(RegisterGmail.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterGmail.this, "Fail!!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
    private boolean validationUser() {
        String val = edtUser.getEditText().getText().toString();
        String space = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            edtUser.setError("Nhập tài khoản");
            return false;
        } else {
            edtUser.setError(null);
            return true;
        }
    }

    private boolean validattionPass() {
        String val = edtPass.getEditText().getText().toString();
        String space = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            edtPass.setError("Nhập mật khẩu");
            return false;
        } else if (val.length() >= 15) {
            edtPass.setError("không được quá 15 ký tự");
            return false;
        } else if (!val.matches(space)) {
            edtPass.setError("Không được chứa khoảng trắng");
            return false;
        } else {
            edtPass.setError(null);
            return true;
        }
    }

    private static void hideKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view == null){
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}