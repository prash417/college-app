package com.example.jce.Teacher.TeacherMarks;

import androidx.annotation.Nullable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.Marks.TeacherMarksDisplayActivity;
import com.example.jce.R;
import com.example.jce.Teacher.TeacherHome.TeacherHomeActivity;
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

public class MarksActivity extends AppCompatActivity {

    //declaration
    RelativeLayout checkboxes,IANumberLayout,subNameCodeMarks;
    CheckBox loadCheck,saveCheck;
    TextView errorCheckBoxMessage,errorBranchMsg,errorSemesterMsg,errorStudentUSNMsg,errorIAMsg,
            errorSubjectCodeMsg,errorSubjectNameMsg,errorMarksMsg;
    EditText Branch,Marks,outOf;
    AutoCompleteTextView sem_filled_exposed,usn_filled_exposed,IA_filled_exposed,subject_Code_filled_exposed,
            subject_name_filled_exposed;
    Button fetchMBtn,saveBtn;
    ImageView backBtn;
    String selSemNo,selUSNNo,selSubCode,selSubName,selIANo,OutOfMarksValue,FMarks;
    private List<String> usnList = new ArrayList<>();
    private List<String> subCodeList = new ArrayList<>();
    private List<String> subNameList = new ArrayList<>();
    private List<String> semNoList = new ArrayList<>();
    HashSet<String> semNoHashSet = new HashSet<>();
    HashSet<String> subNameHashSet = new HashSet<>();
    HashSet<String> subCodeHashSet = new HashSet<>();
    HashSet<String> usnHashSet = new HashSet<>();
    ArrayAdapter<String> usnAdapter;
    ArrayAdapter<String> subCodeAdapter;
    ArrayAdapter<String> subNameAdapter;
    ArrayAdapter<String> semNoAdapter;
    FirebaseFirestore FireStore;
    Boolean BooleanOutOfMarksValue = false;
    Boolean BooleanMarksValue = false;
    Boolean SemNoValue = false;
    Boolean selSubValue = false;
    Boolean selUSNValue = false;
    Boolean selSubNameValue = false;
    Boolean selIANoValue = false;
    Boolean branchValue = false;
    int OutOfMarks,RMarks;
    String[] IANo = {"One","Two","Three","Four","Five"};
    ArrayAdapter<String> IAAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);

         //find id

        //Layout id
        checkboxes = findViewById(R.id.checkboxes);
        IANumberLayout = findViewById(R.id.IANumberLayout);
        subNameCodeMarks = findViewById(R.id.subNameCodeMarks);

        //Check Box id
        loadCheck = findViewById(R.id.loadCheck);
        saveCheck = findViewById(R.id.saveCheck);

        //Error message id
        errorCheckBoxMessage = findViewById(R.id.errorCheckBoxMessage);
        errorBranchMsg = findViewById(R.id.errorBranchMsg);
        errorSemesterMsg = findViewById(R.id.errorSemesterMsg);
        errorStudentUSNMsg = findViewById(R.id.errorStudentUSNMsg);
        errorIAMsg = findViewById(R.id.errorIAMsg);
        errorSubjectCodeMsg = findViewById(R.id.errorSubjectCodeMsg);
        errorSubjectNameMsg = findViewById(R.id.errorSubjectNameMsg);
        errorMarksMsg = findViewById(R.id.errorMarksMsg);

        //Edit text id
        Branch = findViewById(R.id.Branch);
        Marks = findViewById(R.id.Marks);
        outOf = findViewById(R.id.outOf);

        //AutoCompleteTextView id
        sem_filled_exposed = findViewById(R.id.sem_filled_exposed);
        usn_filled_exposed = findViewById(R.id.usn_filled_exposed);
        IA_filled_exposed = findViewById(R.id.IA_filled_exposed);
        subject_Code_filled_exposed = findViewById(R.id.subject_Code_filled_exposed);
        subject_name_filled_exposed = findViewById(R.id.subject_name_filled_exposed);

        //Button id
        fetchMBtn = findViewById(R.id.fetchMBtn);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);

        FireStore = FirebaseFirestore.getInstance();

        //if loadCheck is checked
        loadCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!(loadCheck.isChecked() || saveCheck.isChecked())) {
                    errorCheckBoxMessage.setVisibility(View.VISIBLE);
                    IANumberLayout.setVisibility(View.GONE);
                    errorCheckBoxMessage.setText("Select Any One Check Box!");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(checkboxes);

                    //remove all error message and edit text background
                    errorBranchMsg.setVisibility(View.GONE);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSemesterMsg.setVisibility(View.GONE);
                    sem_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorStudentUSNMsg.setVisibility(View.GONE);
                    usn_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorIAMsg.setVisibility(View.GONE);
                    IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSubjectCodeMsg.setVisibility(View.GONE);
                    subject_Code_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSubjectNameMsg.setVisibility(View.GONE);
                    subject_name_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorMarksMsg.setVisibility(View.GONE);
                    Marks.setBackgroundResource(R.drawable.custom_edittext_right);

                }if (loadCheck.isChecked()){
                    saveCheck.setChecked(false);
                    errorCheckBoxMessage.setVisibility(View.GONE);
                    subNameCodeMarks.setVisibility(View.GONE);
                    IANumberLayout.setVisibility(View.VISIBLE);

                    //remove all error message and edit text background
                    errorBranchMsg.setVisibility(View.GONE);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSemesterMsg.setVisibility(View.GONE);
                    sem_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorStudentUSNMsg.setVisibility(View.GONE);
                    usn_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorIAMsg.setVisibility(View.GONE);
                    IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSubjectCodeMsg.setVisibility(View.GONE);
                    subject_Code_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSubjectNameMsg.setVisibility(View.GONE);
                    subject_name_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorMarksMsg.setVisibility(View.GONE);
                    Marks.setBackgroundResource(R.drawable.custom_edittext_right);


                }else {
                    subNameCodeMarks.setVisibility(View.GONE);
                    IANumberLayout.setVisibility(View.GONE);

                    //remove all error message and edit text background
                    errorBranchMsg.setVisibility(View.GONE);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSemesterMsg.setVisibility(View.GONE);
                    sem_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorStudentUSNMsg.setVisibility(View.GONE);
                    usn_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorIAMsg.setVisibility(View.GONE);
                    IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSubjectCodeMsg.setVisibility(View.GONE);
                    subject_Code_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSubjectNameMsg.setVisibility(View.GONE);
                    subject_name_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorMarksMsg.setVisibility(View.GONE);
                    Marks.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }
        });

        //if saveCheck is checked
        saveCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!(loadCheck.isChecked() || saveCheck.isChecked())) {
                    errorCheckBoxMessage.setVisibility(View.VISIBLE);
                    IANumberLayout.setVisibility(View.GONE);
                    errorCheckBoxMessage.setText("Select Any One Check Box!");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(checkboxes);

                    //remove all error message and edit text background
                    errorBranchMsg.setVisibility(View.GONE);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSemesterMsg.setVisibility(View.GONE);
                    sem_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorStudentUSNMsg.setVisibility(View.GONE);
                    usn_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorIAMsg.setVisibility(View.GONE);
                    IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSubjectCodeMsg.setVisibility(View.GONE);
                    subject_Code_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSubjectNameMsg.setVisibility(View.GONE);
                    subject_name_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorMarksMsg.setVisibility(View.GONE);
                    Marks.setBackgroundResource(R.drawable.custom_edittext_right);
                }if (saveCheck.isChecked()){
                    loadCheck.setChecked(false);
                    errorCheckBoxMessage.setVisibility(View.GONE);
                    subNameCodeMarks.setVisibility(View.VISIBLE);
                    IANumberLayout.setVisibility(View.VISIBLE);

                    //remove all error message and edit text background
                    errorBranchMsg.setVisibility(View.GONE);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSemesterMsg.setVisibility(View.GONE);
                    sem_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorStudentUSNMsg.setVisibility(View.GONE);
                    usn_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorIAMsg.setVisibility(View.GONE);
                    IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSubjectCodeMsg.setVisibility(View.GONE);
                    subject_Code_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSubjectNameMsg.setVisibility(View.GONE);
                    subject_name_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorMarksMsg.setVisibility(View.GONE);
                    Marks.setBackgroundResource(R.drawable.custom_edittext_right);

                }else {
                    subNameCodeMarks.setVisibility(View.GONE);
                    IANumberLayout.setVisibility(View.GONE);

                    //remove all error message and edit text background
                    errorBranchMsg.setVisibility(View.GONE);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSemesterMsg.setVisibility(View.GONE);
                    sem_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorStudentUSNMsg.setVisibility(View.GONE);
                    usn_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorIAMsg.setVisibility(View.GONE);
                    IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSubjectCodeMsg.setVisibility(View.GONE);
                    subject_Code_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorSubjectNameMsg.setVisibility(View.GONE);
                    subject_name_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);

                    errorMarksMsg.setVisibility(View.GONE);
                    Marks.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }
        });

        //IA Number drop down
        IA_filled_exposed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selIANo = parent.getItemAtPosition(position).toString();
                if (TextUtils.isEmpty(selIANo)){
                    errorIAMsg.setVisibility(View.VISIBLE);
                    errorIAMsg.setText("IA Is Required!");
                    IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    selIANoValue = false;
                }
                else {
                    errorIAMsg.setVisibility(View.GONE);
                    IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
                    selIANoValue = true;
                }
            }
        });

        //semester number drop down
        sem_filled_exposed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selSemNo = parent.getItemAtPosition(position).toString();
                if (TextUtils.isEmpty(selSemNo)){
                    errorSemesterMsg.setVisibility(View.VISIBLE);
                    errorSemesterMsg.setText("Sem Number Is Required!");
                    sem_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    SemNoValue = false;
                }else {
                    errorSemesterMsg.setVisibility(View.GONE);
                    sem_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
                    SemNoValue = true;
                }
            }
        });

        //USN drop down
        usn_filled_exposed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selUSNNo = parent.getItemAtPosition(position).toString();
                if (TextUtils.isEmpty(selUSNNo)){
                    errorStudentUSNMsg.setVisibility(View.VISIBLE);
                    errorStudentUSNMsg.setText("USN Is Required!");
                    usn_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    selUSNValue = false;
                }else {
                    errorStudentUSNMsg.setVisibility(View.GONE);
                    usn_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
                    selUSNValue = true;
                }
            }
        });

        //subject_filled_exposed drop down
        subject_name_filled_exposed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selSubName = parent.getItemAtPosition(position).toString();
                if (TextUtils.isEmpty(selSubName)){
                    errorSubjectNameMsg.setVisibility(View.VISIBLE);
                    errorSubjectNameMsg.setText("Subject Name Is Required!");
                    subject_name_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    selSubNameValue = false;
                }else {
                    errorSubjectNameMsg.setVisibility(View.GONE);
                    subject_name_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
                    selSubNameValue = true;
                }
            }
        });

        //subject_Code_filled_exposed drop down
        subject_Code_filled_exposed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selSubCode = parent.getItemAtPosition(position).toString();
                if (TextUtils.isEmpty(selSubCode)){
                    errorSubjectCodeMsg.setVisibility(View.VISIBLE);
                    errorSubjectCodeMsg.setText("Subject Code Is Required!");
                    subject_Code_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    selSubValue = false;
                }else {
                    errorSubjectCodeMsg.setVisibility(View.GONE);
                    subject_Code_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
                    selSubValue = true;
                }
            }
        });

        //fetch student usn and subcode based on Branch
        Branch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String FBranch = Branch.getText().toString().trim();

                if (TextUtils.isEmpty(FBranch)){
                    errorBranchMsg.setVisibility(View.VISIBLE);
                    errorBranchMsg.setText("Branch Name Is Required!");
                    Branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    branchValue = false;
                }else {
                    errorBranchMsg.setVisibility(View.GONE);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);
                    //fetch usn
                    FireStore.collection(FBranch+"USN").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            usnList.clear();
                            usnHashSet.clear();
                            for (DocumentSnapshot snapshot : value){
                                usnList.add(snapshot.getString("SUsn"));
                            }
                            usnHashSet.addAll(usnList);
                            usnList.clear();
                            usnList.addAll(usnHashSet);
                            usnAdapter = new ArrayAdapter<String>(MarksActivity.this,R.layout.list_items,usnList);
                            usn_filled_exposed.setAdapter(usnAdapter);

                        }
                    });

                    //fetch sub name
                    FireStore.collection(FBranch+"subCode").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            subNameList.clear();
                            subNameHashSet.clear();
                            for (DocumentSnapshot snapshot : value){
                                subNameList.add(snapshot.getString("SubName"));
                            }
                            subNameHashSet.addAll(subNameList);
                            subNameList.clear();
                            subNameList.addAll(subNameHashSet);
                            subNameAdapter = new ArrayAdapter<String>(MarksActivity.this,R.layout.list_items,subNameList);
                            subject_name_filled_exposed.setAdapter(subNameAdapter);
                        }
                    });

                    //fetch sem no
                    FireStore.collection(FBranch+"USN").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            semNoList.clear();
                            semNoHashSet.clear();
                            for (DocumentSnapshot snapshot : value){
                                semNoList.add(snapshot.getString("SemNo"));
                            }
                            semNoHashSet.addAll(semNoList);
                            semNoList.clear();
                            semNoList.addAll(semNoHashSet);
                            semNoAdapter = new ArrayAdapter<String>(MarksActivity.this,R.layout.list_items,semNoList);
                            //setting adapter
                            sem_filled_exposed.setAdapter(semNoAdapter);
                        }
                    });

                    //fetch subCodeList
                    FireStore.collection(FBranch+"subCode").addSnapshotListener
                            (new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            subCodeList.clear();
                            subCodeHashSet.clear();
                            for (DocumentSnapshot snapshot : value){
                                subCodeList.add(snapshot.getString("SubCode"));
                            }
                            subCodeHashSet.addAll(subCodeList);
                            subCodeList.clear();
                            subCodeList.addAll(subCodeHashSet);
                            subCodeAdapter = new ArrayAdapter<String>(MarksActivity.this,R.layout.list_items,subCodeList);
                            //setting adapter
                            subject_Code_filled_exposed.setAdapter(subCodeAdapter);
                        }
                    });

                    //creating String array adapter to add list of semester number
                    IAAdapter = new ArrayAdapter<String>(MarksActivity.this,R.layout.list_items,IANo);
                    //setting adapter
                    IA_filled_exposed.setAdapter(IAAdapter);

                    branchValue = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //        on marks edit box changed
        Marks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try
                {
                    FMarks = Marks.getText().toString().trim();
                    RMarks = Integer.parseInt(Marks.getText().toString().trim());
                    if (TextUtils.isEmpty(FMarks)){
                        errorMarksMsg.setVisibility(View.VISIBLE);
                        errorMarksMsg.setText("Marks Is Required!");
                        Marks.setBackgroundResource(R.drawable.custom_edittext_wrong);
                        BooleanMarksValue = false;
                    }
                    else if (RMarks > OutOfMarks){
                        errorMarksMsg.setVisibility(View.VISIBLE);
                        errorMarksMsg.setText("Marks Will Be "+OutOfMarks+" Max");
                        Marks.setBackgroundResource(R.drawable.custom_edittext_wrong);
                        BooleanMarksValue = false;
                    }else {
                        errorMarksMsg.setVisibility(View.GONE);
                        Marks.setBackgroundResource(R.drawable.custom_edittext_right);
                        BooleanMarksValue = true;
                    }
                }
                catch (NumberFormatException e)
                {
                    errorMarksMsg.setVisibility(View.VISIBLE);
                    errorMarksMsg.setText("Marks Is Required!");
                    Marks.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    BooleanMarksValue = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //out of marks
        outOf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try
                {
                    OutOfMarksValue = outOf.getText().toString().trim();
                    OutOfMarks = Integer.parseInt(outOf.getText().toString().trim());
                    if (TextUtils.isEmpty(OutOfMarksValue)){
                        Marks.setEnabled(false);
                        Marks.setAlpha(0.3f);
                        errorMarksMsg.setVisibility(View.VISIBLE);
                        errorMarksMsg.setText("Out Of Marks Is Required!");
                        outOf.setBackgroundResource(R.drawable.custom_edittext_wrong);
                        BooleanOutOfMarksValue = false;
                    }
                    else if (OutOfMarks > 100){
                        Marks.setEnabled(false);
                        Marks.setAlpha(0.3f);
                        errorMarksMsg.setVisibility(View.VISIBLE);
                        errorMarksMsg.setText("Out Of Marks Will Be 100 Max");
                        outOf.setBackgroundResource(R.drawable.custom_edittext_wrong);
                        BooleanOutOfMarksValue = false;
                    }else {
                        Marks.setEnabled(true);
                        Marks.setAlpha(1f);
                        errorMarksMsg.setVisibility(View.GONE);
                        outOf.setBackgroundResource(R.drawable.custom_edittext_right);
                        BooleanOutOfMarksValue = true;
                    }
                }
                catch (NumberFormatException e)
                {
                    Marks.setEnabled(false);
                    Marks.setAlpha(0.3f);
                    errorMarksMsg.setVisibility(View.VISIBLE);
                    errorMarksMsg.setText("Out Of Marks Is Required!");
                    outOf.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    BooleanOutOfMarksValue = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //on back arrow pressed
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //        save Marks to Firestore
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(loadCheck.isChecked() || saveCheck.isChecked())) {
                    errorCheckBoxMessage.setVisibility(View.VISIBLE);
                    errorCheckBoxMessage.setText("Select Any One Check Box");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(checkboxes);
                }else {
                    errorCheckBoxMessage.setVisibility(View.GONE);
                    saveMarks();
                }
            }
        });

        //        fetch Marks to Firestore
        fetchMBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(loadCheck.isChecked() || saveCheck.isChecked())) {
                    errorCheckBoxMessage.setVisibility(View.VISIBLE);
                    errorCheckBoxMessage.setText("Select Any One Check Box");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(checkboxes);
                }else {
                    errorCheckBoxMessage.setVisibility(View.GONE);
                    fetchMarks();
                }
            }
        });


    }

    //        fetch Marks to Firestore
    private void fetchMarks() {
        if (loadCheck.isChecked()) {
            String fBranch = Branch.getText().toString().trim();
            if (TextUtils.isEmpty(fBranch)) {
                errorBranchMsg.setVisibility(View.VISIBLE);
                errorBranchMsg.setText("Branch Name Is Required!");
                Branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(Branch);
            }
           else if (SemNoValue != true) {
                errorSemesterMsg.setVisibility(View.VISIBLE);
                errorSemesterMsg.setText("Sem Number Is Required!");
                sem_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(sem_filled_exposed);
            }
            else if (selUSNValue != true) {
                errorStudentUSNMsg.setVisibility(View.VISIBLE);
                errorStudentUSNMsg.setText("USN Is Required!");
                usn_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(usn_filled_exposed);
            }
            else if (selIANoValue != true) {
                errorIAMsg.setVisibility(View.VISIBLE);
                errorIAMsg.setText("IA Number Is Required!");
                IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(IA_filled_exposed);
            } else {
                errorMarksMsg.setVisibility(View.GONE);
                errorSubjectCodeMsg.setVisibility(View.GONE);
                errorSubjectNameMsg.setVisibility(View.GONE);
                Branch.setBackgroundResource(R.drawable.custom_edittext_right);
                Marks.setBackgroundResource(R.drawable.custom_edittext_right);
                sem_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
                usn_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
                IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);


                Intent intent = new Intent(getApplicationContext(), TeacherMarksDisplayActivity.class);
                intent.putExtra("fBranch", fBranch);
                intent.putExtra("fusn", selUSNNo);
                intent.putExtra("fsemno", selSemNo);
                intent.putExtra("fIANo", selIANo);
                startActivity(intent);

            }
        }
    }

    //    save student Marks
    private void saveMarks() {
        if (saveCheck.isChecked()){
            String fBranch = Branch.getText().toString().trim();
            //branch validation
            if (branchValue != true) {
                errorBranchMsg.setVisibility(View.VISIBLE);
                errorBranchMsg.setText("Branch Name Is Required!");
                Branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(Branch);
            }
            //sem validation
            else if (SemNoValue != true) {
                errorSemesterMsg.setVisibility(View.VISIBLE);
                errorSemesterMsg.setText("Sem Number Is Required!");
                sem_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(sem_filled_exposed);
            }
            //usn validation
            else if (selUSNValue != true) {
                errorStudentUSNMsg.setVisibility(View.VISIBLE);
                errorStudentUSNMsg.setText("USN Is Required! ");
                usn_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(usn_filled_exposed);
            }
            //IA validation
            else if (selIANoValue != true) {
                errorIAMsg.setVisibility(View.VISIBLE);
                errorIAMsg.setText("IA Is Required!");
                IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(IA_filled_exposed);
            }
            //subject code validation
            else if (selSubValue != true) {
                errorSubjectCodeMsg.setVisibility(View.VISIBLE);
                errorSubjectCodeMsg.setText("Subject Code Is Required!");
                subject_Code_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(subject_Code_filled_exposed);
            }
            //sub name validation
            else if (selSubNameValue != true) {
                errorSubjectNameMsg.setVisibility(View.VISIBLE);
                errorSubjectNameMsg.setText("Subject Name Is Required!");
                subject_name_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(subject_name_filled_exposed);
            }
            //subject Marks validation
            else if (BooleanMarksValue !=true){
                errorMarksMsg.setVisibility(View.VISIBLE);
                errorMarksMsg.setText("Marks Is Required!");
                Marks.setBackgroundResource(R.drawable.custom_edittext_wrong);
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(outOf);
            }
            //subject out of marks validation
            else if (BooleanOutOfMarksValue != true){
                Marks.setEnabled(false);
                Marks.setAlpha(0.3f);
                errorMarksMsg.setVisibility(View.VISIBLE);
                errorMarksMsg.setText("Out Of Marks Is Required!");
                outOf.setBackgroundResource(R.drawable.custom_edittext_wrong);
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .playOn(outOf);
            }
            //save marks
            else {
                Toast.makeText(MarksActivity.this, "Marks Saved!", Toast.LENGTH_SHORT).show();
                DocumentReference documentReference = FireStore.collection("marks").document(selUSNNo+selSubCode);
                Map<String, Object> user = new HashMap<>();
                user.put("SemNo", selSemNo);
                user.put("SBranch", fBranch);
                user.put("SUsn", selUSNNo);
                user.put("SubCode", selSubCode);
                user.put("sMarks", FMarks);
                user.put("sOutOfMarks", OutOfMarksValue);
                user.put("subName", selSubName);
                user.put("IANo", selIANo);
                documentReference.set(user);
                Marks.setText("");
            }
        }
    }


    //    onBackPressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), TeacherHomeActivity.class));
        finish();
    }
}