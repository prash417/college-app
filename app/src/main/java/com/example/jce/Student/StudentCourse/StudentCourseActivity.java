package com.example.jce.Student.StudentCourse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.AboutUs.AboutUsActivity;
import com.example.jce.AboutUs.OurHistory.OurHistoryAdapter;
import com.example.jce.AboutUs.OurHistory.OurHistoryModel;
import com.example.jce.R;
import com.example.jce.Student.StudentHome.StudentHomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudentCourseActivity extends AppCompatActivity {


    //declaration
    FirebaseAuth fAuth;
    FirebaseFirestore fireStore;
    String userID;
    ImageView backBtn,Under_Graduate_Courses_ExpandBtn,Post_Graduate_Courses_ExpandBtn,Research_Centers_ExpandBtn;
    RecyclerView Under_Graduate_Courses_Recycler,Post_Graduate_Courses_Recycler,Research_Centers_Recycler;
    ArrayList<StudentCourseModel> studentCourseModelArrayList;
    StudentCourseAdapter studentCourseAdapter;
    TextView JGILink;
    Boolean Under_Graduate_Courses_Value = true;
    Boolean Post_Graduate_Courses_Value = true;
    Boolean Research_Centers_Value = true;
    RelativeLayout Under_Graduate_CoursesLayout,Post_Graduate_Courses_Layout,Research_Centers_Layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_course);

        //find id
        backBtn = findViewById(R.id.backBtn);
        Under_Graduate_Courses_ExpandBtn = findViewById(R.id.Under_Graduate_Courses_ExpandBtn);
        Post_Graduate_Courses_ExpandBtn = findViewById(R.id.Post_Graduate_Courses_ExpandBtn);
        Research_Centers_ExpandBtn = findViewById(R.id.Research_Centers_ExpandBtn);
        Under_Graduate_Courses_Recycler = findViewById(R.id.Under_Graduate_Courses_Recycler);
        Post_Graduate_Courses_Recycler = findViewById(R.id.Post_Graduate_Courses_Recycler);
        Research_Centers_Recycler = findViewById(R.id.Research_Centers_Recycler);
        Under_Graduate_CoursesLayout = findViewById(R.id.Under_Graduate_CoursesLayout);
        Post_Graduate_Courses_Layout = findViewById(R.id.Post_Graduate_Courses_Layout);
        Research_Centers_Layout = findViewById(R.id.Research_Centers_Layout);
        JGILink = findViewById(R.id.JGILink);
        //Firebase instance created
        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        //back button is pressed
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //        Under_Graduate_Courses_ExpandBtn is clicked
        Under_Graduate_Courses_ExpandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Under_Graduate_Courses_Value == true){
                    Under_Graduate_Courses_Recycler.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceInDown)
                            .duration(2000)
                            .playOn(Under_Graduate_Courses_Recycler);
                    Post_Graduate_Courses_Recycler.setVisibility(View.GONE);
                    Research_Centers_Recycler.setVisibility(View.GONE);
                    fetchUnderGraduateCourses();
                }else {
                    Under_Graduate_Courses_Recycler.setVisibility(View.GONE);
                    Post_Graduate_Courses_Recycler.setVisibility(View.GONE);
                    Research_Centers_Recycler.setVisibility(View.GONE);
                    Under_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                    Post_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                    Research_Centers_ExpandBtn.setImageResource(R.drawable.down_icon);
                    Under_Graduate_Courses_Value = true;
                }

            }
        });

//        Under_Graduate_CoursesLayout clicked
        Under_Graduate_CoursesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Under_Graduate_Courses_ExpandBtn.callOnClick();
            }
        });

        //        Post_Graduate_Courses_ExpandBtn is clicked
        Post_Graduate_Courses_ExpandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Post_Graduate_Courses_Value == true){
                    Under_Graduate_Courses_Recycler.setVisibility(View.GONE);
                    Post_Graduate_Courses_Recycler.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceInDown)
                            .duration(2000)
                            .playOn(Post_Graduate_Courses_Recycler);
                    Research_Centers_Recycler.setVisibility(View.GONE);
                    fetchPostGraduateCourses();
                }else {
                    Under_Graduate_Courses_Recycler.setVisibility(View.GONE);
                    Post_Graduate_Courses_Recycler.setVisibility(View.GONE);
                    Research_Centers_Recycler.setVisibility(View.GONE);
                    Under_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                    Post_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                    Research_Centers_ExpandBtn.setImageResource(R.drawable.down_icon);
                    Post_Graduate_Courses_Value = true;
                }
            }
        });

//        Post_Graduate_Courses_Layout is clicked
        Post_Graduate_Courses_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post_Graduate_Courses_ExpandBtn.callOnClick();
            }
        });

        //        Research_Centers_ExpandBtn is clicked
        Research_Centers_ExpandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Research_Centers_Value == true){
                    Under_Graduate_Courses_Recycler.setVisibility(View.GONE);
                    Post_Graduate_Courses_Recycler.setVisibility(View.GONE);
                    Research_Centers_Recycler.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceInDown)
                            .duration(2000)
                            .playOn(Research_Centers_Recycler);
                    fetchResearchCenters();
                }else {
                    Under_Graduate_Courses_Recycler.setVisibility(View.GONE);
                    Post_Graduate_Courses_Recycler.setVisibility(View.GONE);
                    Research_Centers_Recycler.setVisibility(View.GONE);
                    Under_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                    Post_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                    Research_Centers_ExpandBtn.setImageResource(R.drawable.down_icon);
                    Research_Centers_Value = true;
                }

            }
        });

//        Research_Centers_Layout is clicked
        Research_Centers_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Research_Centers_ExpandBtn.callOnClick();
            }
        });

        //jump to JGI Website
        JGILink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.jce.ac.in/"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    //fetchResearchCenters
    private void fetchResearchCenters() {
        Research_Centers_Recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        studentCourseModelArrayList = new ArrayList<>();
        studentCourseAdapter = new StudentCourseAdapter(getApplicationContext(), studentCourseModelArrayList);
        Research_Centers_Recycler.setAdapter(studentCourseAdapter);

        fireStore.collection("Research Centers").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        studentCourseModelArrayList.clear();
                        List<DocumentSnapshot> ourHistory= queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot:ourHistory){
                            StudentCourseModel obj = documentSnapshot.toObject(StudentCourseModel.class);
                            studentCourseModelArrayList.add(obj);
                        }
                        studentCourseAdapter.notifyDataSetChanged();
                        if (studentCourseModelArrayList.isEmpty()){
                            Toast.makeText(StudentCourseActivity.this, "No Data Found!", Toast.LENGTH_SHORT).show();
                            Under_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                            Post_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                            Research_Centers_ExpandBtn.setImageResource(R.drawable.down_icon);
                        }else {
                            Under_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                            Post_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                            Research_Centers_ExpandBtn.setImageResource(R.drawable.up_icon);
                            studentCourseAdapter.notifyDataSetChanged();
                            Research_Centers_Value = false;
                        }
                    }
                });
    }

    //fetchPostGraduateCourses
    private void fetchPostGraduateCourses() {
        Post_Graduate_Courses_Recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        studentCourseModelArrayList = new ArrayList<>();
        studentCourseAdapter = new StudentCourseAdapter(getApplicationContext(), studentCourseModelArrayList);
        Post_Graduate_Courses_Recycler.setAdapter(studentCourseAdapter);

        fireStore.collection("Post Graduate Courses").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        studentCourseModelArrayList.clear();
                        List<DocumentSnapshot> ourHistory= queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot:ourHistory){
                            StudentCourseModel obj = documentSnapshot.toObject(StudentCourseModel.class);
                            studentCourseModelArrayList.add(obj);
                        }
                        studentCourseAdapter.notifyDataSetChanged();
                        if (studentCourseModelArrayList.isEmpty()){
                            Toast.makeText(StudentCourseActivity.this, "No Data Found!", Toast.LENGTH_SHORT).show();
                            Under_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                            Post_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                            Research_Centers_ExpandBtn.setImageResource(R.drawable.down_icon);
                        }else {
                            Under_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                            Post_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.up_icon);
                            Research_Centers_ExpandBtn.setImageResource(R.drawable.down_icon);
                            studentCourseAdapter.notifyDataSetChanged();
                            Post_Graduate_Courses_Value = false;
                        }
                    }
                });
    }

    // fetchUnderGraduateCourses
    private void fetchUnderGraduateCourses() {
        Under_Graduate_Courses_Recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        studentCourseModelArrayList = new ArrayList<>();
        studentCourseAdapter = new StudentCourseAdapter(getApplicationContext(), studentCourseModelArrayList);
        Under_Graduate_Courses_Recycler.setAdapter(studentCourseAdapter);

        fireStore.collection("Under Graduate Courses").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        studentCourseModelArrayList.clear();
                        List<DocumentSnapshot> ourHistory= queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot:ourHistory){
                            StudentCourseModel obj = documentSnapshot.toObject(StudentCourseModel.class);
                            studentCourseModelArrayList.add(obj);
                        }
                        studentCourseAdapter.notifyDataSetChanged();
                        if (studentCourseModelArrayList.isEmpty()){
                            Toast.makeText(StudentCourseActivity.this, "No Data Found!", Toast.LENGTH_SHORT).show();
                            Under_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                            Post_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                            Research_Centers_ExpandBtn.setImageResource(R.drawable.down_icon);
                        }else {
                            Under_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.up_icon);
                            Post_Graduate_Courses_ExpandBtn.setImageResource(R.drawable.down_icon);
                            Research_Centers_ExpandBtn.setImageResource(R.drawable.down_icon);
                            studentCourseAdapter.notifyDataSetChanged();
                            Under_Graduate_Courses_Value = false;
                        }
                    }
                });
    }

    //on back pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), StudentHomeActivity.class));
        finish();
    }
}