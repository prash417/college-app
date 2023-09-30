package com.example.jce.Student.StudentQuiz.StudentQuizActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
import com.example.jce.Student.StudentQuiz.StudentQuizModels.StudentQuestionModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentQuestionActivity extends AppCompatActivity {

    //declaration
    TextView Timer, NumberIndicator, question,CategoryName;

    RelativeLayout optionContainer;
    Button option1, option2, option3, option4, btnNext;
    ImageView backBtn, TimerIcon;


    private ArrayList<StudentQuestionModel> studentQuestionModelArrayList;
    private int count = 0;
    private int position = 0;
    private int score = 0;
    CountDownTimer timer;

    FirebaseDatabase database;
    String categoryName, WatchTextTimer;
    private int set;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_question);

        //find id
        Timer = findViewById(R.id.Timer);
        CategoryName = findViewById(R.id.CategoryName);
        TimerIcon = findViewById(R.id.TimerIcon);
        NumberIndicator = findViewById(R.id.NumberIndicator);
        question = findViewById(R.id.question);
        backBtn = findViewById(R.id.backBtn);
        optionContainer = findViewById(R.id.optionContainer);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        btnNext = findViewById(R.id.btnNext);


        Timer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                WatchTextTimer = Timer.getText().toString().trim();
                if (Integer.parseInt(WatchTextTimer) <= 10) {
                    TimerIcon.setImageResource(R.drawable.timer_icon_red);
                    Timer.setTextColor(Color.parseColor("#FF0000"));

                        YoYo.with(Techniques.Pulse)
                                .duration(1000)
                                .playOn(Timer);

                        YoYo.with(Techniques.Pulse)
                                .duration(1000)
                                .playOn(TimerIcon);

                } else {
                    TimerIcon.setImageResource(R.drawable.timer_icon);
                    Timer.setTextColor(Color.parseColor("#FFFFFFFF"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        database = FirebaseDatabase.getInstance();
        categoryName = getIntent().getStringExtra("categoryName");
        CategoryName.setText(categoryName);
        set = getIntent().getIntExtra("setNum", 1);

        //fetch question and option from db
        studentQuestionModelArrayList = new ArrayList<>();
        database.getReference().child("Sets").child(categoryName).child("Questions")
                .orderByChild("setNum").equalTo(set)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            resetTimer();
                            timer.start();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                StudentQuestionModel model = dataSnapshot.getValue(StudentQuestionModel.class);
                                studentQuestionModelArrayList.add(model);
                            }
                            if (studentQuestionModelArrayList.size() > 0) {
                                for (int i = 0; i < 4; i++) {
                                    optionContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            checkAnswer((Button) v);
                                        }
                                    });
                                }

                                //creating play animation function
                                playAnimation(question, 0, studentQuestionModelArrayList.get(position).getQuestion());

                                //btn nxt is clicked
                                btnNext.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        btnNext.setEnabled(false);
                                        btnNext.setAlpha(0.3f);

                                        enableOption(true);
                                        position++;
                                        timer.cancel();
                                        resetTimer();
                                        timer.start();

                                        if (position == studentQuestionModelArrayList.size()) {
                                            timer.cancel();
                                            Intent intent = new Intent(StudentQuestionActivity.this, ScoreActivity.class);
                                            intent.putExtra("CorrectAnswer", score);
                                            intent.putExtra("TotalQuestion", studentQuestionModelArrayList.size());
                                            intent.putExtra("categoryName", categoryName);
                                            startActivity(intent);
                                            finish();

                                            return;
                                        }
                                        count = 0;
                                        playAnimation(question, 0, studentQuestionModelArrayList.get(position).getQuestion());
                                    }
                                });
                            } else {
                                Toast.makeText(StudentQuestionActivity.this, "Question Not Exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(StudentQuestionActivity.this, "Question Not Exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(StudentQuestionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //    reset timer
    private void resetTimer() {
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                Timer.setText(String.valueOf(l / 1000));
            }

            @Override
            public void onFinish() {
                Toast.makeText(StudentQuestionActivity.this, "Timer out", Toast.LENGTH_SHORT).show();
                btnNext.callOnClick();
            }
        };
    }

    //enable next btn
    private void enableOption(boolean enable) {
        for (int i = 0; i < 4; i++) {
            optionContainer.getChildAt(i).setEnabled(enable);
            if (enable) {
                optionContainer.getChildAt(i).setBackgroundResource(R.drawable.btn_option_back);

            }
        }
    }

    //    play animation of buttons
    private void playAnimation(View view, int value, String data) {

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {
                        if (value == 0 && count < 4) {
                            String option = "";
                            if (count == 0) {
                                option = studentQuestionModelArrayList.get(position).getOptionA();
                            } else if (count == 1) {
                                option = studentQuestionModelArrayList.get(position).getOptionB();
                            } else if (count == 2) {
                                option = studentQuestionModelArrayList.get(position).getOptionC();
                            } else if (count == 3) {
                                option = studentQuestionModelArrayList.get(position).getOptionD();
                            }
                            playAnimation(optionContainer.getChildAt(count), 0, option);
                            count++;
                        }
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        if (value == 0) {
                            try {
                                ((TextView) view).setText(data);
                                NumberIndicator.setText(position + 1 + "/" + studentQuestionModelArrayList.size());
                            } catch (Exception e) {
                                ((Button) view).setText(data);
                            }
                            view.setTag(data);
                            playAnimation(view, 1, data);

                        }
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {

                    }
                });
    }

    //check answer
    private void checkAnswer(Button selectedOption) {
        enableOption(false);

        btnNext.setEnabled(true);
        btnNext.setAlpha(1);

        if (selectedOption.getText().toString().equals(studentQuestionModelArrayList.get(position).getCorrectAnswer())) {
            score++;
            selectedOption.setBackgroundResource(R.drawable.custom_edittext_right);
        } else {
            selectedOption.setBackgroundResource(R.drawable.custom_edittext_wrong);

            Button correctOption = (Button) optionContainer.findViewWithTag(studentQuestionModelArrayList.get(position).getCorrectAnswer());
            correctOption.setBackgroundResource(R.drawable.custom_edittext_right);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (timer != null){
            timer.cancel();
            resetTimer();
        }else {
            return;
        }
    }
}