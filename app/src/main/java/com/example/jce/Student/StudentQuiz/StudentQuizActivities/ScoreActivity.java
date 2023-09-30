package com.example.jce.Student.StudentQuiz.StudentQuizActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jce.R;
import com.example.jce.Student.StudentHome.StudentHomeActivity;

public class ScoreActivity extends AppCompatActivity {

    //declaration
    TextView allQuestions,rightAnswer,wroAnswerAnswer,ResultProgressText,playedCategoryName;
    Button RetryButton,QuitButton;

    ProgressBar ResultProgress;
    int valueProgress,Correct,TotalQuestion,Wrong;
    String playedCategory;
    private ObjectAnimator progressAnimator;
    ImageView backBtn;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        //find id
        allQuestions = findViewById(R.id.allQuestions);
        rightAnswer = findViewById(R.id.rightAnswer);
        wroAnswerAnswer = findViewById(R.id.wroAnswerAnswer);
        ResultProgress = findViewById(R.id.ResultProgress);
        playedCategoryName = findViewById(R.id.playedCategoryName);
        ResultProgressText = findViewById(R.id.ResultProgressText);
        RetryButton = findViewById(R.id.RetryButton);
        QuitButton = findViewById(R.id.QuitButton);
        backBtn = findViewById(R.id.backBtn);

        //back button is clicked
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StudentChooseCategoryActivity.class));
                finish();
            }
        });

        //get score details
         Correct = getIntent().getIntExtra("CorrectAnswer",0);
          TotalQuestion = getIntent().getIntExtra("TotalQuestion",0);
        playedCategory = getIntent().getStringExtra("categoryName");
        playedCategoryName.setText(playedCategory);
          Wrong = TotalQuestion - Correct;
          valueProgress = (Correct * 100) / TotalQuestion;

          //update progress
          updateProgress();

        allQuestions.setText(String.valueOf(TotalQuestion));
        rightAnswer.setText(String.valueOf(Correct));
        wroAnswerAnswer.setText(String.valueOf(Wrong));


        //retry button
        RetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this, StudentChooseCategoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //quit from Quiz
        QuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StudentHomeActivity.class));
                finish();
            }
        });

    }

    private void updateProgress() {
        progressAnimator = ObjectAnimator.ofInt(ResultProgress, "progress", Correct);
        progressAnimator.setDuration(1000);
        progressAnimator.start();
        ResultProgress.setProgress(Correct);
        ResultProgress.setMax(TotalQuestion);
        ResultProgressText.setText(valueProgress+"%");
    }
}