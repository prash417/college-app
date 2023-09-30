package com.example.jce.Student.StudentHome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jce.R;
import com.example.jce.Student.StudentFragments.Student_Account_Fragment;
import com.example.jce.Student.StudentFragments.Student_Home_Fragment;
import com.example.jce.Student.StudentFragments.Student_Message_Fragment;
import com.example.jce.Student.StudentFragments.Student_Notification_Fragment;
import com.example.jce.Teacher.TeacherFragment.Teacher_Account_Fragment;
import com.example.jce.Teacher.TeacherFragment.Teacher_Message_Fragment;
import com.example.jce.Users.Login.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentHomeActivity extends AppCompatActivity {

    //declaration
    FirebaseAuth fAuth;
    FirebaseFirestore fireStore;
    String userID;

    LinearLayout Home_fragment,Message_fragment,Notification_fragment,Account_fragment;
    ImageView Home_Image,Message_Image,Notification_Image,Account_Image;
    TextView Home_text,Message_text,Notification_text,Account_text;
    private int selectedTab = 1;

    private long backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        //find id
        Home_fragment = findViewById(R.id.Home_fragment);
        Message_fragment = findViewById(R.id.Message_fragment);
        Notification_fragment = findViewById(R.id.Notification_fragment);
        Account_fragment = findViewById(R.id.Account_fragment);


        Home_Image = findViewById(R.id.Home_Image);
        Message_Image = findViewById(R.id.Message_Image);
        Notification_Image = findViewById(R.id.Notification_Image);
        Account_Image = findViewById(R.id.Account_Image);


        Home_text = findViewById(R.id.Home_text);
        Message_text = findViewById(R.id.Message_text);
        Notification_text = findViewById(R.id.Notification_text);
        Account_text = findViewById(R.id.Account_text);

        //Firebase instance created
        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.FragmentContainer, Student_Home_Fragment.class,null)
                .commit();

        //Home Fragment
        Home_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab !=1){

                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.FragmentContainer, Student_Home_Fragment.class,null)
                            .commit();

                    //set Visibility of text view
                    Home_text.setVisibility(View.VISIBLE);
                    Message_text.setVisibility(View.GONE);
                    Notification_text.setVisibility(View.GONE);
                    Account_text.setVisibility(View.GONE);

                    //set Images
                    Home_Image.setImageResource(R.drawable.home_icon);
                    Message_Image.setImageResource(R.drawable.message_icon1);
                    Notification_Image.setImageResource(R.drawable.notifications_icon1);
                    Account_Image.setImageResource(R.drawable.account_icon1);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,0.0f, Animation.RELATIVE_TO_SELF,0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    Home_fragment.startAnimation(scaleAnimation);

                    selectedTab = 1;
                }
            }
        });

        //Message Fragment
        Message_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab !=2){

                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference df = fireStore.collection("users").document(userID);
                    df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.getString("isStudent") !=null){
                                getSupportFragmentManager().beginTransaction()
                                        .setReorderingAllowed(true)
                                        .replace(R.id.FragmentContainer, Student_Message_Fragment.class,null)
                                        .commit();
                            }if (documentSnapshot.getString("isTeacher") !=null){
                                getSupportFragmentManager().beginTransaction()
                                        .setReorderingAllowed(true)
                                        .replace(R.id.FragmentContainer, Teacher_Message_Fragment.class,null)
                                        .commit();

                            }
                        }
                    });

                    //set Visibility of text view
                    Home_text.setVisibility(View.GONE);
                    Message_text.setVisibility(View.VISIBLE);
                    Notification_text.setVisibility(View.GONE);
                    Account_text.setVisibility(View.GONE);

                    //set Images
                    Home_Image.setImageResource(R.drawable.home_icon1);
                    Message_Image.setImageResource(R.drawable.message_icon);
                    Notification_Image.setImageResource(R.drawable.notifications_icon1);
                    Account_Image.setImageResource(R.drawable.account_icon1);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f,Animation.RELATIVE_TO_SELF,0.0f, Animation.RELATIVE_TO_SELF,0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    Message_fragment.startAnimation(scaleAnimation);

                    selectedTab = 2;
                }
            }
        });

        //Help Fragment
        Notification_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab !=3){


                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.FragmentContainer, Student_Notification_Fragment.class,null)
                            .commit();

                    //set Visibility of text view
                    Home_text.setVisibility(View.GONE);
                    Message_text.setVisibility(View.GONE);
                    Notification_text.setVisibility(View.VISIBLE);
                    Account_text.setVisibility(View.GONE);

                    //set Images
                    Home_Image.setImageResource(R.drawable.home_icon1);
                    Message_Image.setImageResource(R.drawable.message_icon1);
                    Notification_Image.setImageResource(R.drawable.notifications_icon);
                    Account_Image.setImageResource(R.drawable.account_icon1);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f,Animation.RELATIVE_TO_SELF,0.0f, Animation.RELATIVE_TO_SELF,0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    Notification_fragment.startAnimation(scaleAnimation);

                    selectedTab = 3;
                }
            }
        });

        //account fragment
        Account_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab !=4){

                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference df = fireStore.collection("users").document(userID);
                    df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.getString("isStudent") !=null){
                                getSupportFragmentManager().beginTransaction()
                                        .setReorderingAllowed(true)
                                        .replace(R.id.FragmentContainer, Student_Account_Fragment.class,null)
                                        .commit();
                            }if (documentSnapshot.getString("isTeacher") !=null){
                                getSupportFragmentManager().beginTransaction()
                                        .setReorderingAllowed(true)
                                        .replace(R.id.FragmentContainer, Teacher_Account_Fragment.class,null)
                                        .commit();

                            }
                        }
                    });

                    //set Visibility of text view
                    Home_text.setVisibility(View.GONE);
                    Message_text.setVisibility(View.GONE);
                    Notification_text.setVisibility(View.GONE);
                    Account_text.setVisibility(View.VISIBLE);

                    //set Images
                    Home_Image.setImageResource(R.drawable.home_icon1);
                    Message_Image.setImageResource(R.drawable.message_icon1);
                    Notification_Image.setImageResource(R.drawable.notifications_icon1);
                    Account_Image.setImageResource(R.drawable.account_icon);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f,Animation.RELATIVE_TO_SELF,0.0f, Animation.RELATIVE_TO_SELF,0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    Account_fragment.startAnimation(scaleAnimation);

                    selectedTab = 4;
                }
            }
        });
    }
    //on start check user is logged in if not send him to login activity class
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() !=null){
            return;
        }else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }


    //on back pressed close app
    @Override
    public void onBackPressed() {

        if (backPressed + 2000 > System.currentTimeMillis()){
            System.exit(1);
        }else {
            Toast.makeText(this, "Press Again To Exit!", Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();

    }
}