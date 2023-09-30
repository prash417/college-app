package com.example.jce.Splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.jce.R;
import com.example.jce.Student.StudentHome.StudentHomeActivity;
import com.example.jce.Teacher.TeacherHome.TeacherHomeActivity;
import com.example.jce.Users.Login.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    //declaration

    FirebaseAuth fAuth;
    FirebaseFirestore firestore;
    String userID;

    private static int splash_timeout = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        //firebase connection
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //handler to splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fAuth = FirebaseAuth.getInstance();

                //check if user is registered
                if (fAuth.getCurrentUser() != null) {

                    //get instance of current user
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    //check if email is verified
                    if (user.isEmailVerified()) {
                        userID = fAuth.getCurrentUser().getUid();
                        DocumentReference df = firestore.collection("users").document(userID);
                        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if (documentSnapshot.getString("isStudent") !=null){
                                    startActivity(new Intent(getApplicationContext(), StudentHomeActivity.class));
                                    finish();
                                }if (documentSnapshot.getString("isTeacher") !=null){
                                    startActivity(new Intent(getApplicationContext(), TeacherHomeActivity.class));
                                    finish();
                                }
                            }
                        });
                    } else {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        }, splash_timeout);
    }
}