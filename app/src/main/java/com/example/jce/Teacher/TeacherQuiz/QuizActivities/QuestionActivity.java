package com.example.jce.Teacher.TeacherQuiz.QuizActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.Teacher.TeacherQuiz.AdapterClasses.QuestionsAdapter;
import com.example.jce.Teacher.TeacherQuiz.ModelClasses.QuestionModel;
import com.example.jce.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuestionActivity extends AppCompatActivity {

    //declaration
    ImageView AddQuestionsBtn;
    ImageView backBtn;
    RecyclerView QuestionsRecycler;
    FirebaseDatabase database;
    ArrayList<QuestionModel> questionModelArrayList;
    QuestionsAdapter questionsAdapter;
    int setNum;
    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);


        //find id
        AddQuestionsBtn = findViewById(R.id.AddQuestionsBtn);
        backBtn = findViewById(R.id.backBtn);
        QuestionsRecycler = findViewById(R.id.QuestionsRecycler);

        database = FirebaseDatabase.getInstance();
        questionModelArrayList = new ArrayList<>();

        setNum = getIntent().getIntExtra("setNum",0);
        categoryName = getIntent().getStringExtra("categoryName");

        YoYo.with(Techniques.Pulse)
                .duration(1000)
                .playOn(QuestionsRecycler);


        LinearLayoutManager layoutManager = new LinearLayoutManager(QuestionActivity.this);
        QuestionsRecycler.setLayoutManager(layoutManager);
        questionsAdapter = new QuestionsAdapter(QuestionActivity.this, questionModelArrayList, categoryName, new QuestionsAdapter.DeleteListener() {
            @Override
            public void onLongClick(int position, String ID) {
                AlertDialog.Builder builder = new AlertDialog.Builder(QuestionActivity.this);
                builder.setTitle("Delete Question");
                builder.setMessage("Are You Sure,You Want To Delete This Question?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.getReference().child("Sets").child(categoryName).child("Questions")
                                .child(ID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(QuestionActivity.this, "Question Deleted", Toast.LENGTH_SHORT).show();
                                        onStart();
                                    }
                                });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        QuestionsRecycler.setAdapter(questionsAdapter);


        //add question page
        AddQuestionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionActivity.this, AddQuestionActivity.class);
                intent.putExtra("category",categoryName);
                intent.putExtra("setNum",setNum);
                startActivity(intent);
            }
        });


        //back button
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
        database.getReference().child("Sets").child(categoryName).child("Questions")
                .orderByChild("setNum").equalTo(setNum)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            questionModelArrayList.clear();
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                QuestionModel model = dataSnapshot.getValue(QuestionModel.class);
                                model.setKey(dataSnapshot.getKey());
                                questionModelArrayList.add(model);
                            }
                            questionsAdapter.notifyDataSetChanged();
                            YoYo.with(Techniques.Pulse)
                                    .duration(1000)
                                    .playOn(QuestionsRecycler);
                        }else {
                            Toast.makeText(QuestionActivity.this, "Question Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}