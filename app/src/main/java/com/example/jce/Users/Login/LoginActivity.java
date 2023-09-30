package com.example.jce.Users.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
import com.example.jce.Student.StudentHome.StudentHomeActivity;
import com.example.jce.Teacher.TeacherHome.TeacherHomeActivity;
import com.example.jce.Users.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    //declaration
    EditText uEmail,uPass;
    Button loginBtn;
    TextView newUser,forgotPass;
    LottieAnimationView loading;
    FirebaseAuth fAuth;
    FirebaseFirestore fireStore;
    String userID;
    CheckBox PassVisible;

    Dialog cPassDialog;

    TextView errorPassMsg,errorEmailMsg;

    Boolean uEmailValue = false;
    Boolean uPassValue = false;

    //pattern for password
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //find id
        uEmail = findViewById(R.id.email);
        uPass = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginButton);
        newUser = findViewById(R.id.signupText);
        loading = findViewById(R.id.animationView);
        forgotPass = findViewById(R.id.resetPass);
        PassVisible = findViewById(R.id.passVisibility);
        errorEmailMsg = findViewById(R.id.errorEmailMsg);
        errorPassMsg = findViewById(R.id.errorPassMsg);

        // User Email text watcher
        uEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fEmail = uEmail.getText().toString().trim();
                if (TextUtils.isEmpty(fEmail)){
                    errorEmailMsg.setVisibility(View.VISIBLE);
                    errorEmailMsg.setText("Email is Required!");
                    uEmail.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    uEmailValue = false;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(fEmail).matches()){
                    errorEmailMsg.setVisibility(View.VISIBLE);
                    errorEmailMsg.setText("Please Enter Valid Email!");
                    uEmail.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    uEmailValue = false;
                }else {
                    errorEmailMsg.setVisibility(View.GONE);
                    uEmail.setBackgroundResource(R.drawable.custom_edittext_right);
                    uEmailValue = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // User password text watcher
        uPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String FPass = uPass.getText().toString().trim();

                if (TextUtils.isEmpty(FPass)){
                    errorPassMsg.setVisibility(View.VISIBLE);
                    errorPassMsg.setText("Password Is Required!");
                    uPass.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    uPassValue = false;
                }
                else if (!PASSWORD_PATTERN.matcher(FPass).matches()){
                    errorPassMsg.setVisibility(View.VISIBLE);
                    errorPassMsg.setText("Please Set Strong Password!");
                    uPass.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    uPassValue = false;
                }else {
                    errorPassMsg.setVisibility(View.GONE);
                    uPass.setBackgroundResource(R.drawable.custom_edittext_right);
                    uPassValue = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Firebase instance created
        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        //login btn
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                loading.playAnimation();
                String FEmail = uEmail.getText().toString().trim();
                String FPassword = uPass.getText().toString().trim();
                LoginUser(FEmail,FPassword);
            }
        });

        //create user
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        //forgot password
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordEmailSent();
            }
        });

        //password visibility
        PassVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //for show password
                    uPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    PassVisible.setBackgroundResource(R.drawable.show_pass);
                }else {
                    //for hide password
                    uPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    PassVisible.setBackgroundResource(R.drawable.hide_pass);
                }
            }
        });
    }

    private void LoginUser(String fEmail, String fPassword) {

        //email validation
        if (uEmailValue != true){
            errorEmailMsg.setVisibility(View.VISIBLE);
            errorEmailMsg.setText("Email Is Required!");
            uEmail.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(uEmail);
        }

        //password validation
        if (uPassValue != true){
            errorPassMsg.setVisibility(View.VISIBLE);
            errorPassMsg.setText("Password Is Required!");
            uPass.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(uPass);
        }

        //student login
        else {
            //login user
            fAuth.signInWithEmailAndPassword(fEmail, fPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    //check for task successful
                    if (task.isSuccessful()) {

                        //get current user instance
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        //check for email is varified
                        if (user.isEmailVerified()) {
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference df = fireStore.collection("users").document(userID);
                            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    if (documentSnapshot.getString("isStudent") !=null){
                                        startActivity(new Intent(getApplicationContext(), StudentHomeActivity.class));
                                        finish();
                                        loading.setVisibility(View.GONE);
                                        loading.pauseAnimation();
                                    }if (documentSnapshot.getString("isTeacher") !=null){
                                        startActivity(new Intent(getApplicationContext(), TeacherHomeActivity.class));
                                        finish();
                                        loading.setVisibility(View.GONE);
                                        loading.pauseAnimation();
                                    }
                                }
                            });
                        }

                        //if email is not verified then Toast Message and send new verification link
                        else {
                            user.sendEmailVerification();
                            Toast.makeText(LoginActivity.this, "Please verify your email!", Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            loading.pauseAnimation();
                        }
                    }

                    //if is not successful then toast message
                    else {
                        Toast.makeText(LoginActivity.this, "Error Occurred! Please check your Credential's!", Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        loading.pauseAnimation();
                    }
                }
            });
        }
    }

    //    change passd email send
    private void changePasswordEmailSent() {

        cPassDialog = new Dialog(LoginActivity.this);
        cPassDialog.setContentView(R.layout.success_dialog);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cPassDialog.getWindow().setBackgroundDrawable(LoginActivity.this.getDrawable(R.drawable.custom_edittext_right));
        }

        loading.setVisibility(View.VISIBLE);
        loading.playAnimation();
        String fEmail = uEmail.getText().toString().trim();


        //email validation
        if (TextUtils.isEmpty(fEmail)){
            errorEmailMsg.setVisibility(View.VISIBLE);
            errorEmailMsg.setText("Email Is Required!");
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(uEmail);
            uEmail.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(fEmail).matches()){
            errorEmailMsg.setVisibility(View.VISIBLE);
            errorEmailMsg.setText("Please Enter Valid Email!");
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(uEmail);
            uEmail.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            return;
        }else {
            TextView custom_message = cPassDialog.findViewById(R.id.custom_message_text);
            fAuth.sendPasswordResetEmail(fEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            custom_message.setText("Reset Email Sent!");
                            loading.setVisibility(View.GONE);
                            loading.pauseAnimation();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            custom_message.setText("User Is Not Registered!");
                            loading.setVisibility(View.GONE);
                            loading.pauseAnimation();
                        }
                    });
        }
        cPassDialog.setCancelable(true);
        cPassDialog.show();
    }
}