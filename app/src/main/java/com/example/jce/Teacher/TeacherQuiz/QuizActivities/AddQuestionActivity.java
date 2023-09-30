package com.example.jce.Teacher.TeacherQuiz.QuizActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.jce.Teacher.TeacherQuiz.ModelClasses.QuestionModel;
import com.example.jce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class AddQuestionActivity extends AppCompatActivity {

    int set;
    String CategoryName;
    FirebaseDatabase database;

    Button UploadQuestion;
    RadioGroup optionContainer;
    LinearLayout AnswerContainer;
    RadioButton optionA,optionB,optionC,optionD;
    EditText option1,option2,option3,option4,inputQuestion;

    ImageView backBtn;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        //find id
        UploadQuestion = findViewById(R.id.UploadQuestion);
        optionContainer = findViewById(R.id.optionContainer);
        AnswerContainer = findViewById(R.id.AnswerContainer);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        inputQuestion = findViewById(R.id.inputQuestion);
        backBtn = findViewById(R.id.backBtn);

        set = getIntent().getIntExtra("setNum",-1);
        CategoryName = getIntent().getStringExtra("category");

        database = FirebaseDatabase.getInstance();

        if (set == -1){
            finish();
            return;
        }

        //upload question
        UploadQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int correct = -1;


                for (int i=0;i<optionContainer.getChildCount();i++){
                    EditText Answer = (EditText) AnswerContainer.getChildAt(i);
                    if (Answer.getText().toString().isEmpty()){
                        Answer.setError("Answer Is Required!");
                        return;
                    }

                    RadioButton radioButton = (RadioButton) optionContainer.getChildAt(i);
                    if (radioButton.isChecked()){
                        correct = i;
                        break;
                    }
                }
                if (correct == -1){
                    Toast.makeText(AddQuestionActivity.this, "Mark The Correct Option", Toast.LENGTH_SHORT).show();
                    return;
                }


                QuestionModel questionModel = new QuestionModel();
                questionModel.setQuestion(inputQuestion.getText().toString());
                questionModel.setOptionA(((EditText) AnswerContainer.getChildAt(0)).getText().toString());
                questionModel.setOptionB(((EditText) AnswerContainer.getChildAt(1)).getText().toString());
                questionModel.setOptionC(((EditText) AnswerContainer.getChildAt(2)).getText().toString());
                questionModel.setOptionD(((EditText) AnswerContainer.getChildAt(3)).getText().toString());
                questionModel.setCorrectAnswer(((EditText) AnswerContainer.getChildAt(correct)).getText().toString());
                questionModel.setSetNum(set);

                database.getReference().child("Sets").child(CategoryName).child("Questions")
                        .push()
                        .setValue(questionModel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AddQuestionActivity.this, "Question Added", Toast.LENGTH_SHORT).show();
                                inputQuestion.setText(null);
                                option1.setText(null);
                                option2.setText(null);
                                option3.setText(null);
                                option4.setText(null);
                            }
                        });
            }
        });


        //on back pressed
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}