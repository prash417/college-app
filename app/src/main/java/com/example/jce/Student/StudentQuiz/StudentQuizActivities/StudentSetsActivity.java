package com.example.jce.Student.StudentQuiz.StudentQuizActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
import com.example.jce.Student.StudentQuiz.StudentQuizAdapter.StudentGrideAdapter;
import com.google.firebase.database.FirebaseDatabase;

public class StudentSetsActivity extends AppCompatActivity {

    //declaration
    FirebaseDatabase database;

    StudentGrideAdapter studentGrideAdapter;

    String key;

    GridView gridView;

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sets);

        //find id
        gridView = findViewById(R.id.gridView);
        backBtn = findViewById(R.id.backBtn);

        database = FirebaseDatabase.getInstance();
        key = getIntent().getStringExtra("key");



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        studentGrideAdapter = new StudentGrideAdapter(getIntent().getIntExtra("sets",0),
                getIntent().getStringExtra("CategoryName"));
        gridView.setAdapter(studentGrideAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        YoYo.with(Techniques.Pulse)
                .duration(1000)
                .playOn(gridView);
    }
}