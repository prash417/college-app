package com.example.jce.Teacher.TeacherQuiz.QuizActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.Teacher.TeacherQuiz.AdapterClasses.GrideAdapter;
import com.example.jce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class SetsActivity extends AppCompatActivity {

    //declaration
    FirebaseDatabase database;

    GrideAdapter adapter;
    int a=1;
    String key;

    GridView gridView;

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        //find id
        gridView = findViewById(R.id.gridView);
        backBtn = findViewById(R.id.backBtn);

        database = FirebaseDatabase.getInstance();
        key = getIntent().getStringExtra("key");

        YoYo.with(Techniques.Pulse)
                .duration(1000)
                .playOn(gridView);


        adapter = new GrideAdapter(getIntent().getIntExtra("sets", 0),
                getIntent().getStringExtra("CategoryName"), key, new GrideAdapter.GridListener() {
            @Override
            public void addSets() {
                database.getReference().child("Categories").child(getIntent().getStringExtra("CategoryName"))
                        .child("setNum").setValue(getIntent().getIntExtra("sets",0)+a++
                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()){
                                  adapter.sets++;
                                  adapter.notifyDataSetChanged();
                              }else {
                                  Toast.makeText(SetsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                              }
                            }
                        });
            }
        });

        gridView.setAdapter(adapter);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        YoYo.with(Techniques.Pulse)
                .duration(1000)
                .playOn(gridView);
    }
}