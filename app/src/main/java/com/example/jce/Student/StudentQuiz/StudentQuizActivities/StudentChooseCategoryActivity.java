package com.example.jce.Student.StudentQuiz.StudentQuizActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
import com.example.jce.Student.StudentHome.StudentHomeActivity;
import com.example.jce.Student.StudentQuiz.StudentQuizModels.StudentCategoryModel;
import com.example.jce.Student.StudentQuiz.StudentQuizAdapter.StudentsCategoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentChooseCategoryActivity extends AppCompatActivity {

    //declaration
    FirebaseDatabase database;

    ArrayList<StudentCategoryModel> studentCategoryModelArrayList;
    StudentsCategoryAdapter studentsCategoryAdapter;
    RecyclerView categoryRecycler;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_choose_category);

        //find id
        database = FirebaseDatabase.getInstance();
        categoryRecycler = findViewById(R.id.categoryRecycler);
        backBtn = findViewById(R.id.backBtn);

        studentCategoryModelArrayList = new ArrayList<>();

        GridLayoutManager layoutManager = new GridLayoutManager(StudentChooseCategoryActivity.this,2);
        categoryRecycler.setLayoutManager(layoutManager);
        studentsCategoryAdapter = new StudentsCategoryAdapter(StudentChooseCategoryActivity.this,studentCategoryModelArrayList);
        categoryRecycler.setAdapter(studentsCategoryAdapter);

        //fetch Categories
        fetchCategories();

        //back button pressed
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //    fetchCategories
    private void fetchCategories() {
        database.getReference().child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    studentCategoryModelArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                        studentCategoryModelArrayList.add(new StudentCategoryModel(
                                dataSnapshot.child("categoryName").getValue().toString(),
                                dataSnapshot.child("categoryImage").getValue().toString(),
                                dataSnapshot.getKey(),
                                Integer.parseInt(dataSnapshot.child("setNum").getValue().toString())
                        ));

                    }
                    studentsCategoryAdapter.notifyDataSetChanged();
                    YoYo.with(Techniques.Pulse)
                            .duration(1000)
                            .playOn(categoryRecycler);
                }else {
                    Toast.makeText(StudentChooseCategoryActivity.this, "Categories Not Exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Toast.makeText(StudentChooseCategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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