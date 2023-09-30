package com.example.jce.Users.Register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
import com.example.jce.Users.Login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    //declaration
    LinearLayout checkboxes,studentInfo,teacherInfo;
    CheckBox teacher,student,passVisibility;
    TextView errorCheckBoxMsg,errorEmailMsg,errorNameMsg,errorNumberMsg,errorBranchMsg,
            errorStudentUSNMsg,errorUCNMsg,errorPassMsg,alreadyText;
    EditText email,username,number,branch,StudentUSN,ucn,password;
    Button registerButton;
    LottieAnimationView loading;

    //pattern for password
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[a-zA-Z])" +      //Any Letter
                    "(?=.*[@#$%^&+=])" +    //At Least 1 Special Character
                    "(?=\\S+$)" +           //No White Spaces
                    ".{4,}" +               //At Least 4 Characters
                    "$");

    private List<String> USNList = new ArrayList<>();
    private List<String> RegUSNList = new ArrayList<>();

    FirebaseAuth fAuth;
    FirebaseFirestore FireStore;
    String userID;

    Boolean emailValue = false;
    Boolean usernameValue = false;
    Boolean numberValue = false;
    Boolean branchValue = false;
    Boolean StudentUSNValue = false;
    Boolean ucnValue = false;
    Boolean passwordValue = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //find id

        //layout id
        checkboxes = findViewById(R.id.checkboxes);
        studentInfo = findViewById(R.id.studentInfo);
        teacherInfo = findViewById(R.id.teacherInfo);

        //check box id
        teacher = findViewById(R.id.teacher);
        student = findViewById(R.id.student);
        passVisibility = findViewById(R.id.passVisibility);

        //Edit box find id
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        number = findViewById(R.id.number);
        branch = findViewById(R.id.branch);
        StudentUSN = findViewById(R.id.StudentUSN);
        ucn = findViewById(R.id.ucn);
        password = findViewById(R.id.password);

        //error message id
        errorCheckBoxMsg = findViewById(R.id.errorCheckBoxMsg);
        errorEmailMsg = findViewById(R.id.errorEmailMsg);
        errorNameMsg = findViewById(R.id.errorNameMsg);
        errorNumberMsg = findViewById(R.id.errorNumberMsg);
        errorBranchMsg = findViewById(R.id.errorBranchMsg);
        errorStudentUSNMsg = findViewById(R.id.errorStudentUSNMsg);
        errorUCNMsg = findViewById(R.id.errorUCNMsg);
        errorPassMsg = findViewById(R.id.errorPassMsg);

        //Have Account text
        alreadyText = findViewById(R.id.alreadyText);

        //animation id
        loading = findViewById(R.id.loading);

        //Button id
        registerButton = findViewById(R.id.registerButton);

        //Firebase instance created
        fAuth = FirebaseAuth.getInstance();
        FireStore = FirebaseFirestore.getInstance();

        //email text watcher
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fEmail = email.getText().toString().trim();
                if (TextUtils.isEmpty(fEmail)) {
                    errorEmailMsg.setVisibility(View.VISIBLE);
                    errorEmailMsg.setText("Email Is Required!");
                    email.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    emailValue = false;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(fEmail).matches()) {
                    errorEmailMsg.setVisibility(View.VISIBLE);
                    errorEmailMsg.setText("Please Enter Valid Email!");
                    email.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    emailValue = false;
                }
                else {
                    errorEmailMsg.setVisibility(View.GONE);
                    email.setBackgroundResource(R.drawable.custom_edittext_right);
                    emailValue = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //user name text watcher
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fName = username.getText().toString().trim();
                if (TextUtils.isEmpty(fName)) {
                    errorNameMsg.setVisibility(View.VISIBLE);
                    errorNameMsg.setText("User Name Is Required!");
                    username.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    usernameValue = false;
                }
                else {
                    errorNameMsg.setVisibility(View.GONE);
                    username.setBackgroundResource(R.drawable.custom_edittext_right);
                    usernameValue = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //number text watcher
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fPhone = number.getText().toString().trim();
                if (TextUtils.isEmpty(fPhone)) {
                    errorNumberMsg.setVisibility(View.VISIBLE);
                    errorNumberMsg.setText("Phone Number Is Required!");
                    number.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    numberValue = false;
                }
                else if (fPhone.length() < 10) {
                    errorNumberMsg.setVisibility(View.VISIBLE);
                    errorNumberMsg.setText("Please Enter Correct Number");
                    number.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    numberValue = false;
                }
                else {
                    errorNumberMsg.setVisibility(View.GONE);
                    number.setBackgroundResource(R.drawable.custom_edittext_right);
                    numberValue = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //branch text watcher
        branch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String FBranch = branch.getText().toString().trim();
                if (TextUtils.isEmpty(FBranch)) {
                    errorBranchMsg.setVisibility(View.VISIBLE);
                    errorBranchMsg.setText("Branch Is Required");
                    branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    USNList.clear();
                    RegUSNList.clear();
                    branchValue = false;
                } else {
                    errorBranchMsg.setVisibility(View.GONE);
                    branch.setBackgroundResource(R.drawable.custom_edittext_right);
                    //fetch usn
                    FireStore.collection(FBranch+"USN").addSnapshotListener
                            (new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        USNList.clear();
                                        for (DocumentSnapshot snapshot : value) {
                                            USNList.add(snapshot.getString("SUsn"));
                                        }
                                        if (USNList.isEmpty()){
                                            Toast.makeText(RegisterActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(RegisterActivity.this, USNList.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                    //fetch regUSN
                    FireStore.collection("users").addSnapshotListener
                            (new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        RegUSNList.clear();
                                        for (DocumentSnapshot snapshot : value) {
                                            RegUSNList.add(snapshot.getString("usn"));
                                        }
                                    }
                                }
                            });
                    branchValue = true;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //StudentUSN text watcher
        StudentUSN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String EnteredUsn = StudentUSN.getText().toString().trim();
                if (TextUtils.isEmpty(EnteredUsn)) {
                    errorStudentUSNMsg.setVisibility(View.VISIBLE);
                    errorStudentUSNMsg.setText("USN Is Required!");
                    StudentUSN.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    StudentUSNValue = false;
                }
                else if (!USNList.contains(EnteredUsn)) {
                    errorStudentUSNMsg.setVisibility(View.VISIBLE);
                    errorStudentUSNMsg.setText("USN Is Not Registered! Contact College To Register Your USN");
                    StudentUSN.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    StudentUSNValue = false;
                }
                else if (RegUSNList.contains(EnteredUsn)) {
                    errorStudentUSNMsg.setVisibility(View.VISIBLE);
                    errorStudentUSNMsg.setText("USN Is Already Registered!");
                    StudentUSN.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    StudentUSNValue = false;
                }
                else {
                    errorStudentUSNMsg.setVisibility(View.GONE);
                    StudentUSN.setBackgroundResource(R.drawable.custom_edittext_right);
                    StudentUSNValue = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //ucn text watcher
        ucn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fUCN = ucn.getText().toString().trim();
                if (TextUtils.isEmpty(fUCN)) {
                    errorUCNMsg.setVisibility(View.VISIBLE);
                    errorUCNMsg.setText("Enter Unique College Number");
                    ucn.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    ucnValue = false;
                }
                else if (!(fUCN.equals("JCEB2023"))) {
                    errorUCNMsg.setVisibility(View.VISIBLE);
                    errorUCNMsg.setText("Enter Correct UCN");
                    ucn.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    ucnValue = false;
                } else {
                    errorUCNMsg.setVisibility(View.GONE);
                    ucn.setBackgroundResource(R.drawable.custom_edittext_right);
                    ucnValue = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //password text watcher
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fPass = password.getText().toString().trim();
                if (TextUtils.isEmpty(fPass)) {
                    errorPassMsg.setVisibility(View.VISIBLE);
                    errorPassMsg.setText("Password Is Required");
                    password.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    passwordValue = false;
                }
                else if (fPass.length() <= 5) {
                    errorPassMsg.setVisibility(View.VISIBLE);
                    errorPassMsg.setText("Password Must Be 6 Characters");
                    password.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    passwordValue = false;
                }
                else if (!PASSWORD_PATTERN.matcher(fPass).matches()) {
                    errorPassMsg.setVisibility(View.VISIBLE);
                    errorPassMsg.setText("Please Follow THis EX:ab123#");
                    password.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    passwordValue = false;
                } else {
                    errorPassMsg.setVisibility(View.GONE);
                    password.setBackgroundResource(R.drawable.custom_edittext_right);
                    passwordValue = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //teacher is checked
        teacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //check any account is selected
                if (!(student.isChecked() || teacher.isChecked())) {
                    errorCheckBoxMsg.setVisibility(View.VISIBLE);
                    errorCheckBoxMsg.setText("Select An Account Type");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(checkboxes);
                } else {
                    errorCheckBoxMsg.setVisibility(View.GONE);
                }
                // if teacher is checked
                if (teacher.isChecked()) {
                    student.setChecked(false);
                    errorCheckBoxMsg.setVisibility(View.GONE);
                    studentInfo.setVisibility(View.GONE);
                    teacherInfo.setVisibility(View.VISIBLE);

                    //remove all error Messages
                    errorPassMsg.setVisibility(View.GONE);
                    errorEmailMsg.setVisibility(View.GONE);
                    errorNameMsg.setVisibility(View.GONE);
                    errorNumberMsg.setVisibility(View.GONE);
                    errorBranchMsg.setVisibility(View.GONE);
                    errorStudentUSNMsg.setVisibility(View.GONE);
                    errorUCNMsg.setVisibility(View.GONE);
                    errorPassMsg.setVisibility(View.GONE);

                    // reset edit box
                    password.setBackgroundResource(R.drawable.custom_edittext_right);
                    ucn.setBackgroundResource(R.drawable.custom_edittext_right);
                    StudentUSN.setBackgroundResource(R.drawable.custom_edittext_right);
                    branch.setBackgroundResource(R.drawable.custom_edittext_right);
                    number.setBackgroundResource(R.drawable.custom_edittext_right);
                    username.setBackgroundResource(R.drawable.custom_edittext_right);
                    email.setBackgroundResource(R.drawable.custom_edittext_right);

                } else {
                    errorCheckBoxMsg.setVisibility(View.VISIBLE);
                    errorCheckBoxMsg.setText("Select An Account Type");
                    studentInfo.setVisibility(View.GONE);
                    teacherInfo.setVisibility(View.GONE);

                    //remove all error Messages
                    errorPassMsg.setVisibility(View.GONE);
                    errorEmailMsg.setVisibility(View.GONE);
                    errorNameMsg.setVisibility(View.GONE);
                    errorNumberMsg.setVisibility(View.GONE);
                    errorBranchMsg.setVisibility(View.GONE);
                    errorStudentUSNMsg.setVisibility(View.GONE);
                    errorUCNMsg.setVisibility(View.GONE);
                    errorPassMsg.setVisibility(View.GONE);

                    //reset edit box
                    password.setBackgroundResource(R.drawable.custom_edittext_right);
                    ucn.setBackgroundResource(R.drawable.custom_edittext_right);
                    StudentUSN.setBackgroundResource(R.drawable.custom_edittext_right);
                    branch.setBackgroundResource(R.drawable.custom_edittext_right);
                    number.setBackgroundResource(R.drawable.custom_edittext_right);
                    username.setBackgroundResource(R.drawable.custom_edittext_right);
                    email.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }
        });

        //student is checked
        student.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //check any account is selected
                if (!(student.isChecked() || teacher.isChecked())) {
                    errorCheckBoxMsg.setVisibility(View.VISIBLE);
                    errorCheckBoxMsg.setText("Select An Account Type");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(checkboxes);
                } else {
                    errorCheckBoxMsg.setVisibility(View.GONE);
                }
                // if student  isChecked
                if (student.isChecked()) {
                    teacher.setChecked(false);
                    errorCheckBoxMsg.setVisibility(View.GONE);
                    studentInfo.setVisibility(View.VISIBLE);
                    teacherInfo.setVisibility(View.GONE);

                    //remove all error Messages
                    errorPassMsg.setVisibility(View.GONE);
                    errorEmailMsg.setVisibility(View.GONE);
                    errorNameMsg.setVisibility(View.GONE);
                    errorNumberMsg.setVisibility(View.GONE);
                    errorBranchMsg.setVisibility(View.GONE);
                    errorStudentUSNMsg.setVisibility(View.GONE);
                    errorUCNMsg.setVisibility(View.GONE);
                    errorPassMsg.setVisibility(View.GONE);

                    //reset edit box
                    password.setBackgroundResource(R.drawable.custom_edittext_right);
                    ucn.setBackgroundResource(R.drawable.custom_edittext_right);
                    StudentUSN.setBackgroundResource(R.drawable.custom_edittext_right);
                    branch.setBackgroundResource(R.drawable.custom_edittext_right);
                    number.setBackgroundResource(R.drawable.custom_edittext_right);
                    username.setBackgroundResource(R.drawable.custom_edittext_right);
                    email.setBackgroundResource(R.drawable.custom_edittext_right);
                } else {
                    errorCheckBoxMsg.setVisibility(View.VISIBLE);
                    errorCheckBoxMsg.setText("Select An Account Type");
                    studentInfo.setVisibility(View.GONE);
                    teacherInfo.setVisibility(View.GONE);

                    //remove all error Messages
                    errorPassMsg.setVisibility(View.GONE);
                    errorEmailMsg.setVisibility(View.GONE);
                    errorNameMsg.setVisibility(View.GONE);
                    errorNumberMsg.setVisibility(View.GONE);
                    errorBranchMsg.setVisibility(View.GONE);
                    errorStudentUSNMsg.setVisibility(View.GONE);
                    errorUCNMsg.setVisibility(View.GONE);
                    errorPassMsg.setVisibility(View.GONE);

                    //reset edit box
                    password.setBackgroundResource(R.drawable.custom_edittext_right);
                    ucn.setBackgroundResource(R.drawable.custom_edittext_right);
                    StudentUSN.setBackgroundResource(R.drawable.custom_edittext_right);
                    branch.setBackgroundResource(R.drawable.custom_edittext_right);
                    number.setBackgroundResource(R.drawable.custom_edittext_right);
                    username.setBackgroundResource(R.drawable.custom_edittext_right);
                    email.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                loading.playAnimation();
                String FEmail = email.getText().toString().trim();
                String FName = username.getText().toString().trim();
                String FPhone = number.getText().toString().trim();
                String FPass = password.getText().toString().trim();
                String SBranch = branch.getText().toString().trim();

                //check any account is selected
                if (!(student.isChecked() || teacher.isChecked())) {
                    errorCheckBoxMsg.setVisibility(View.VISIBLE);
                    errorCheckBoxMsg.setText("Select An Account Type");
                    loading.setVisibility(View.GONE);
                    loading.pauseAnimation();
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(checkboxes);
                }

                //if student checked
                if (student.isChecked()) {
                    String SEnteredUsn = StudentUSN.getText().toString().trim();
                    errorCheckBoxMsg.setVisibility(View.GONE);
                    RegisterAsStudent(FEmail,FName,FPhone,FPass,SBranch,SEnteredUsn);
                }

                //if teacher checked
                if (teacher.isChecked()) {
                    String fUCN = ucn.getText().toString().trim();
                    errorCheckBoxMsg.setVisibility(View.GONE);
                    RegisterAsTeacher(FEmail,FName,FPhone,FPass,SBranch,fUCN);
                }
            }
        });

        //Have Account
        alreadyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        //Password Visibility
        passVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //for show password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passVisibility.setBackgroundResource(R.drawable.show_pass);
                } else {
                    //for hide password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passVisibility.setBackgroundResource(R.drawable.hide_pass);
                }
            }
        });
    }

    //Register as teacher
    private void RegisterAsTeacher(String fEmail, String fName, String fPhone, String fPass, String sBranch, String fUCN) {

        //Email validation
        if (emailValue != true){
            errorEmailMsg.setVisibility(View.VISIBLE);
            errorEmailMsg.setText("Email Is Required!");
            email.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(email);
        }

        //Name validation
        if (usernameValue != true){
            errorNameMsg.setVisibility(View.VISIBLE);
            errorNameMsg.setText("User Name Is Required!");
            username.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(username);
        }

        //Number validation
        if (numberValue != true){
            errorNumberMsg.setVisibility(View.VISIBLE);
            errorNumberMsg.setText("Phone Number Is Required!");
            number.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(number);
        }

        //Branch Validation
        if (branchValue != true){
            errorBranchMsg.setVisibility(View.VISIBLE);
            errorBranchMsg.setText("Branch Is Required!");
            branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(branch);

        }

        //ucn validation
        if (ucnValue != true){
            errorUCNMsg.setVisibility(View.VISIBLE);
            errorUCNMsg.setText("Unique College Number Is Required!");
            ucn.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(ucn);
        }

        //Password Validation
        if (passwordValue != true){
            errorPassMsg.setVisibility(View.VISIBLE);
            errorPassMsg.setText("Password Is Required!");
            password.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(password);
        }

        //Register as student
        else {
            //Register User In Firebase
            fAuth.createUserWithEmailAndPassword(fEmail, fPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //send email verification link
                        FirebaseUser vuser = fAuth.getCurrentUser();
                        vuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(RegisterActivity.this, "Verification Email Sent!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "User Is Not Registered! Please Check Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(RegisterActivity.this, "Successfully Registered.", Toast.LENGTH_SHORT).show();
                        userID = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = FireStore.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("username", fName);
                        user.put("useremail", fEmail);
                        user.put("userphno", fPhone);
                        user.put("userpassd", fPass);
                        user.put("userId", userID);
                        user.put("branch", sBranch);
                        user.put("isTeacher", "1");
                        documentReference.set(user);

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference("users");

                        UserProfile userProfile = new UserProfile(fName, fAuth.getUid());
                        databaseReference
                                .child(fAuth.getUid()).setValue(userProfile);


                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        loading.setVisibility(View.GONE);
                        loading.pauseAnimation();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        loading.pauseAnimation();
                    }
                }
            });
        }
    }

    //Register as student
    private void RegisterAsStudent(String fEmail, String fName, String fPhone, String fPass, String sBranch, String sEnteredUsn) {

        //Email validation
        if (emailValue != true){
            errorEmailMsg.setVisibility(View.VISIBLE);
            errorEmailMsg.setText("Email Is Required!");
            email.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(email);
        }

        //Name validation
        if (usernameValue != true){
            errorNameMsg.setVisibility(View.VISIBLE);
            errorNameMsg.setText("User Name Is Required!");
            username.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(username);
        }

        //Number validation
        if (numberValue != true){
            errorNumberMsg.setVisibility(View.VISIBLE);
            errorNumberMsg.setText("Phone Number Is Required!");
            number.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(number);
        }

        //Branch Validation
        if (branchValue != true){
            errorBranchMsg.setVisibility(View.VISIBLE);
            errorBranchMsg.setText("Branch Is Required!");
            branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(branch);
        }

        //Student Usn Validation
        if ( StudentUSNValue != true){
            errorStudentUSNMsg.setVisibility(View.VISIBLE);
            errorStudentUSNMsg.setText("USN Is Required!");
            StudentUSN.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(StudentUSN);
        }

        //Password Validation
        if (passwordValue != true){
            errorPassMsg.setVisibility(View.VISIBLE);
            errorPassMsg.setText("Password Is Required!");
            password.setBackgroundResource(R.drawable.custom_edittext_wrong);
            loading.setVisibility(View.GONE);
            loading.pauseAnimation();
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(password);
        }

        //Register as student
        else {
            //Register User In Firebase
            fAuth.createUserWithEmailAndPassword(fEmail, fPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //send email verification link
                        FirebaseUser vuser = fAuth.getCurrentUser();
                        vuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(RegisterActivity.this, "Verification Email Sent!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "User Is Not Registered! Please Check Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(RegisterActivity.this, "Successfully Registered.", Toast.LENGTH_SHORT).show();
                        userID = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = FireStore.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("username", fName);
                        user.put("useremail", fEmail);
                        user.put("userphno", fPhone);
                        user.put("userpassd", fPass);
                        user.put("usn", sEnteredUsn);
                        user.put("branch", sBranch);
                        user.put("userId", userID);
                        user.put("isStudent", "1");
                        documentReference.set(user);

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference("users");

                        UserProfile userProfile = new UserProfile(fName, fAuth.getUid());
                        databaseReference
                                .child(fAuth.getUid()).setValue(userProfile);

                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        loading.setVisibility(View.GONE);
                        loading.pauseAnimation();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        loading.pauseAnimation();
                    }
                }
            });
        }
    }
}