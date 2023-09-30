package com.example.jce.Teacher.AddSubUSN;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
import com.example.jce.Teacher.TeacherHome.TeacherHomeActivity;
import com.example.jce.Users.Login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TeacherAddSubUSNActivity extends AppCompatActivity {


    //    declaration
    ImageView backBtn;

    String[] semNo = {"Sem No", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapter;
    String selSemNo;

    Button saveBtn, fetchBtn;
    EditText Branch, subCode, subName, studentUsn, studentName;
    FirebaseFirestore firestore;
    Boolean value = false;

    CheckBox subjectCheck, studentCheck;
    RelativeLayout subjects, stdUSN, checkboxes;

    TextView checkBoxMessage, BMessage, semesterMessage, subjectCodeMessage, subjectNameMessage, studentUSNMessage, studentNameMessage;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_add_sub_usnactivity);

        //find id
        backBtn = findViewById(R.id.backBtn);
        autoCompleteTextView = findViewById(R.id.filled_exposed);
        saveBtn = findViewById(R.id.saveBtn);
        Branch = findViewById(R.id.Branch);
        subName = findViewById(R.id.subName);
        subCode = findViewById(R.id.subCode);
        fetchBtn = findViewById(R.id.fetchBtn);
        subjectCheck = findViewById(R.id.subjectCheck);
        studentCheck = findViewById(R.id.studentCheck);
        subjects = findViewById(R.id.subjects);
        stdUSN = findViewById(R.id.stdUSN);
        studentUsn = findViewById(R.id.studentUsn);
        studentName = findViewById(R.id.studentName);
        studentNameMessage = findViewById(R.id.studentNameMessage);
        checkboxes = findViewById(R.id.checkboxes);
        firestore = FirebaseFirestore.getInstance();

//        error text views
        checkBoxMessage = findViewById(R.id.checkBoxMessage);
        BMessage = findViewById(R.id.BMessage);
        semesterMessage = findViewById(R.id.semesterMessage);
        subjectCodeMessage = findViewById(R.id.subjectCodeMessage);
        subjectNameMessage = findViewById(R.id.subjectNameMessage);
        studentUSNMessage = findViewById(R.id.studentUSNMessage);


        //creating String array adapter to add list of semester number
        adapter = new ArrayAdapter<String>(this, R.layout.list_items, semNo);
        //setting adapter
        autoCompleteTextView.setAdapter(adapter);

        //semester number drop down
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selSemNo = parent.getItemAtPosition(position).toString();
                if (selSemNo.equals("Sem No")) {
                    value = false;
                    semesterMessage.setVisibility(View.VISIBLE);
                    semesterMessage.setText("Sem Number Is Required!");
                    autoCompleteTextView.setBackgroundResource(R.drawable.custom_edittext_wrong);
                } else {
                    value = true;
                    semesterMessage.setVisibility(View.GONE);
                    autoCompleteTextView.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }
        });

        //back btn pressed
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TeacherHomeActivity.class));
                finish();
            }
        });

        //on pressed of save btn
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check any check box  is selected
                if (!(studentCheck.isChecked() || subjectCheck.isChecked())) {
                    checkBoxMessage.setVisibility(View.VISIBLE);
                    checkBoxMessage.setText("Select Any One Check Box");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(checkboxes);
                } else {
                    checkBoxMessage.setVisibility(View.GONE);
                    saveSubCodeNName();
                }
            }
        });

        //on fetch btn pressed
        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check any check box  is selected
                if (!(studentCheck.isChecked() || subjectCheck.isChecked())) {
                    checkBoxMessage.setVisibility(View.VISIBLE);
                    checkBoxMessage.setText("Select Any One Check Box");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(checkboxes);
                } else {
                    checkBoxMessage.setVisibility(View.GONE);
                    fetchSCodeName();
                }
            }
        });

        //subject is checked
        subjectCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!(studentCheck.isChecked() || subjectCheck.isChecked())) {
                    checkBoxMessage.setVisibility(View.VISIBLE);
                    checkBoxMessage.setText("Select Any One Check Box");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(checkboxes);
                    BMessage.setVisibility(View.GONE);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);

                    semesterMessage.setVisibility(View.GONE);
                    autoCompleteTextView.setBackgroundResource(R.drawable.custom_edittext_right);

                    subjectCodeMessage.setVisibility(View.GONE);
                    subCode.setBackgroundResource(R.drawable.custom_edittext_right);

                    subjectNameMessage.setVisibility(View.GONE);
                    subName.setBackgroundResource(R.drawable.custom_edittext_right);

                    studentNameMessage.setVisibility(View.GONE);
                    studentName.setBackgroundResource(R.drawable.custom_edittext_right);
                }
                if (subjectCheck.isChecked()) {
                    BMessage.setVisibility(View.GONE);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);

                    semesterMessage.setVisibility(View.GONE);
                    autoCompleteTextView.setBackgroundResource(R.drawable.custom_edittext_right);

                    subjectCodeMessage.setVisibility(View.GONE);
                    subCode.setBackgroundResource(R.drawable.custom_edittext_right);

                    subjectNameMessage.setVisibility(View.GONE);
                    subName.setBackgroundResource(R.drawable.custom_edittext_right);

                    studentNameMessage.setVisibility(View.GONE);
                    studentName.setBackgroundResource(R.drawable.custom_edittext_right);

                    studentCheck.setChecked(false);
                    checkBoxMessage.setVisibility(View.GONE);
                    stdUSN.setVisibility(View.GONE);
                    subjects.setVisibility(View.VISIBLE);

                } else {
                    stdUSN.setVisibility(View.GONE);
                    subjects.setVisibility(View.GONE);
                }
            }
        });

        //student is checcked
        studentCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!(studentCheck.isChecked() || subjectCheck.isChecked())) {
                    checkBoxMessage.setVisibility(View.VISIBLE);
                    checkBoxMessage.setText("Select Any One Check Box");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(checkboxes);
                    BMessage.setVisibility(View.GONE);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);

                    semesterMessage.setVisibility(View.GONE);
                    autoCompleteTextView.setBackgroundResource(R.drawable.custom_edittext_right);

                    studentUSNMessage.setVisibility(View.GONE);
                    studentUsn.setBackgroundResource(R.drawable.custom_edittext_right);

                    studentNameMessage.setVisibility(View.GONE);
                    studentName.setBackgroundResource(R.drawable.custom_edittext_right);
                }
                if (studentCheck.isChecked()) {
                    BMessage.setVisibility(View.GONE);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);

                    semesterMessage.setVisibility(View.GONE);
                    autoCompleteTextView.setBackgroundResource(R.drawable.custom_edittext_right);

                    studentUSNMessage.setVisibility(View.GONE);
                    studentUsn.setBackgroundResource(R.drawable.custom_edittext_right);

                    studentNameMessage.setVisibility(View.GONE);
                    studentName.setBackgroundResource(R.drawable.custom_edittext_right);

                    subjectCheck.setChecked(false);
                    checkBoxMessage.setVisibility(View.GONE);
                    stdUSN.setVisibility(View.VISIBLE);
                    subjects.setVisibility(View.GONE);
                } else {
                    stdUSN.setVisibility(View.GONE);
                    subjects.setVisibility(View.GONE);
                }
            }
        });

//        on branch edit text changed
        Branch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fBranch = Branch.getText().toString().trim();

                if (TextUtils.isEmpty(fBranch)) {
                    BMessage.setVisibility(View.VISIBLE);
                    BMessage.setText("Branch Name Is Required!");
                    Branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
                } else {
                    BMessage.setVisibility(View.GONE);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        on sub code edit text changed
        subCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fsubcode = subCode.getText().toString().trim();

                if (TextUtils.isEmpty(fsubcode)) {
                    subjectCodeMessage.setVisibility(View.VISIBLE);
                    subjectCodeMessage.setText("Subject Code Is Required!");
                    subCode.setBackgroundResource(R.drawable.custom_edittext_wrong);
                } else {
                    subjectCodeMessage.setVisibility(View.GONE);
                    subCode.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        on sub name edit text changed
        subName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fsubname = subName.getText().toString().trim();

                if (TextUtils.isEmpty(fsubname)) {
                    subjectNameMessage.setVisibility(View.VISIBLE);
                    subjectNameMessage.setText("Subject Name Is Required!");
                    subName.setBackgroundResource(R.drawable.custom_edittext_wrong);
                } else {
                    subjectNameMessage.setVisibility(View.GONE);
                    subName.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        on student usn edit text changed
        studentUsn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fstdUSN = studentUsn.getText().toString().trim();

                if (TextUtils.isEmpty(fstdUSN)) {
                    studentUSNMessage.setVisibility(View.VISIBLE);
                    studentUSNMessage.setText("Student USN Is Required!");
                    studentUsn.setBackgroundResource(R.drawable.custom_edittext_wrong);
                } else {
                    studentUSNMessage.setVisibility(View.GONE);
                    studentUsn.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        on student name edit text changed
        studentName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fstdName = studentName.getText().toString().trim();

                if (TextUtils.isEmpty(fstdName)) {
                    studentNameMessage.setVisibility(View.VISIBLE);
                    studentNameMessage.setText("Student Name Is Required!");
                    studentName.setBackgroundResource(R.drawable.custom_edittext_wrong);
                } else {
                    studentNameMessage.setVisibility(View.GONE);
                    studentName.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void fetchSCodeName() {

        subjectCodeMessage.setVisibility(View.GONE);
        subCode.setBackgroundResource(R.drawable.custom_edittext_right);

        subjectNameMessage.setVisibility(View.GONE);
        subName.setBackgroundResource(R.drawable.custom_edittext_right);

        studentUSNMessage.setVisibility(View.GONE);
        studentUsn.setBackgroundResource(R.drawable.custom_edittext_right);

        studentNameMessage.setVisibility(View.GONE);
        studentName.setBackgroundResource(R.drawable.custom_edittext_right);
        //if subjectCheck is checked
        if (subjectCheck.isChecked()) {
            String bName = Branch.getText().toString().trim();

            if (TextUtils.isEmpty(bName)) {
                BMessage.setVisibility(View.VISIBLE);
                BMessage.setText("Branch Name Is Required!");
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(Branch);
                Branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
            } else if (value != true) {
                semesterMessage.setVisibility(View.VISIBLE);
                semesterMessage.setText("Sem Number Is Required!");
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(autoCompleteTextView);
                autoCompleteTextView.setBackgroundResource(R.drawable.custom_edittext_wrong);
            } else {
                subjectCodeMessage.setVisibility(View.GONE);
                subCode.setBackgroundResource(R.drawable.custom_edittext_right);

                subjectNameMessage.setVisibility(View.GONE);
                subName.setBackgroundResource(R.drawable.custom_edittext_right);

                studentUSNMessage.setVisibility(View.GONE);
                studentUsn.setBackgroundResource(R.drawable.custom_edittext_right);

                studentNameMessage.setVisibility(View.GONE);
                studentName.setBackgroundResource(R.drawable.custom_edittext_right);

                Intent intent = new Intent(getApplicationContext(), AllSubjectDisplayActivity.class);
                intent.putExtra("fBranch", bName);
                intent.putExtra("fsemno", selSemNo);
                startActivity(intent);

            }
        }

        //fetch student data
        if (studentCheck.isChecked()) {
            String bName = Branch.getText().toString().trim();

            if (TextUtils.isEmpty(bName)) {
                BMessage.setVisibility(View.VISIBLE);
                BMessage.setText("Branch Name Is Required!");
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(Branch);
                Branch.setBackgroundResource(R.drawable.custom_edittext_wrong);

            } else if (value != true) {
                semesterMessage.setVisibility(View.VISIBLE);
                semesterMessage.setText("Sem Number Is Required!");
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(autoCompleteTextView);
                autoCompleteTextView.setBackgroundResource(R.drawable.custom_edittext_wrong);
            } else {
                studentUSNMessage.setVisibility(View.GONE);
                studentUsn.setBackgroundResource(R.drawable.custom_edittext_right);

                subjectCodeMessage.setVisibility(View.GONE);
                subCode.setBackgroundResource(R.drawable.custom_edittext_right);

                subjectNameMessage.setVisibility(View.GONE);
                subName.setBackgroundResource(R.drawable.custom_edittext_right);


                Intent intent = new Intent(getApplicationContext(), AllStudentDisplayActivity.class);
                intent.putExtra("fBranch", bName);
                intent.putExtra("fsemno", selSemNo);
                startActivity(intent);

            }
        }

    }

    private void saveSubCodeNName() {

//        if subjectCheck is checked
        if (subjectCheck.isChecked()) {
            String bName = Branch.getText().toString().trim();
            String sName = subName.getText().toString().trim();
            String sCode = subCode.getText().toString().trim();

            if (TextUtils.isEmpty(bName)) {
                BMessage.setVisibility(View.VISIBLE);
                BMessage.setText("Branch Name Is Required!");
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(Branch);
                Branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
            } else if (value != true) {
                semesterMessage.setVisibility(View.VISIBLE);
                semesterMessage.setText("Sem Number Is Required!");
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(autoCompleteTextView);
                autoCompleteTextView.setBackgroundResource(R.drawable.custom_edittext_wrong);
            } else if (TextUtils.isEmpty(sName)) {
                subjectNameMessage.setVisibility(View.VISIBLE);
                subjectNameMessage.setText("Subject Name Is Required!");
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(subName);
                subName.setBackgroundResource(R.drawable.custom_edittext_wrong);
            } else if (TextUtils.isEmpty(sCode)) {
                subjectCodeMessage.setVisibility(View.VISIBLE);
                subjectCodeMessage.setText("Subject Code Is Required!");
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(subCode);
                subCode.setBackgroundResource(R.drawable.custom_edittext_wrong);
            } else {
                Toast.makeText(TeacherAddSubUSNActivity.this, "Subject Saved!", Toast.LENGTH_SHORT).show();
                DocumentReference documentReference = firestore.collection(bName + "subCode").document(bName + sName);
                Map<String, Object> user = new HashMap<>();
                user.put("SubName", sName);
                user.put("SubCode", sCode);
                user.put("SemNo", selSemNo);
                user.put("SBranch", bName);
                documentReference.set(user);
                subName.setText("");
                subCode.setText("");
                subjectCodeMessage.setVisibility(View.GONE);
                subCode.setBackgroundResource(R.drawable.custom_edittext_right);

                subjectNameMessage.setVisibility(View.GONE);
                subName.setBackgroundResource(R.drawable.custom_edittext_right);

                studentUSNMessage.setVisibility(View.GONE);
                studentUsn.setBackgroundResource(R.drawable.custom_edittext_right);

            }
        }

//        if subjectCheck is checked
        if (studentCheck.isChecked()) {
            String bName = Branch.getText().toString().trim();
            String SUsn = studentUsn.getText().toString().trim();
            String SName = studentName.getText().toString().trim();

            if (TextUtils.isEmpty(bName)) {
                BMessage.setVisibility(View.VISIBLE);
                BMessage.setText("Branch Name Is Required!");
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(Branch);
                Branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
            } else if (value != true) {
                semesterMessage.setVisibility(View.VISIBLE);
                semesterMessage.setText("Sem Number Is Required!");
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(autoCompleteTextView);
                autoCompleteTextView.setBackgroundResource(R.drawable.custom_edittext_wrong);
            } else if (TextUtils.isEmpty(SUsn)) {
                studentUSNMessage.setVisibility(View.VISIBLE);
                studentUSNMessage.setText("Student USN Is Required!");
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(studentUsn);
                studentUsn.setBackgroundResource(R.drawable.custom_edittext_wrong);

            } else if (TextUtils.isEmpty(SName)) {
                studentNameMessage.setVisibility(View.VISIBLE);
                studentNameMessage.setText("Student Name Is Required!");
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(studentName);
                studentName.setBackgroundResource(R.drawable.custom_edittext_wrong);

            } else {
                Toast.makeText(TeacherAddSubUSNActivity.this, "USN Saved!", Toast.LENGTH_SHORT).show();
                DocumentReference documentReference = firestore.collection(bName + "USN").document(SUsn);
                Map<String, Object> user = new HashMap<>();
                user.put("SemNo", selSemNo);
                user.put("SBranch", bName);
                user.put("SUsn", SUsn);
                user.put("SName", SName);
                documentReference.set(user);
                studentUsn.setText("");
                studentName.setText("");
                studentUSNMessage.setVisibility(View.GONE);
                studentUsn.setBackgroundResource(R.drawable.custom_edittext_right);

                subjectCodeMessage.setVisibility(View.GONE);
                subCode.setBackgroundResource(R.drawable.custom_edittext_right);

                subjectNameMessage.setVisibility(View.GONE);
                subName.setBackgroundResource(R.drawable.custom_edittext_right);

                studentNameMessage.setVisibility(View.GONE);
                studentName.setBackgroundResource(R.drawable.custom_edittext_right);
            }
        }
    }

    //on back pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), TeacherHomeActivity.class));
        finish();
    }
}