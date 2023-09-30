package com.example.jce.Marks;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
import com.example.jce.Student.StudentHome.StudentHomeActivity;
import com.example.jce.Teacher.TeacherMarks.MarksActivity;
import com.example.jce.Users.Login.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class StudentMarksDisplayActivity extends AppCompatActivity {

    //declaration
    RecyclerView marksRecycler;
    ArrayList<MarksModel> marksList;
    MarksAdapter marksAdapter;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String userID,selUSN;

    String fBranch,selSemNo,selIANo;

    TextView studentUSN,studentName,studentPer;

    ImageView backBtn;

    AutoCompleteTextView filled_exposed,IA_filled_exposed;
    private List<String> semNoList = new ArrayList<>();
    HashSet<String> semNoHashSet = new HashSet<>();
    ArrayAdapter<String> semNoAdapter;

    Boolean SemNoValue = false;
    TextView semestermsg,IAmsg;


    Boolean selIANoValue = false;

    private List<String> IANoList = new ArrayList<>();

    HashSet<String> IANoHashSet = new HashSet<>();
    ArrayAdapter<String> IAAdapter;
    Button fetchMBtn;
    LottieAnimationView animationView;

    int sMarksSum=0;
    int OutOfMarks=0;
    float perDisplay = 0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_marks_display);
        //find id
        studentUSN = findViewById(R.id.studentUSN);
        studentName = findViewById(R.id.studentName);
        backBtn = findViewById(R.id.backBtn);
        studentPer = findViewById(R.id.studentPer);
        animationView = findViewById(R.id.animationView);


        filled_exposed = findViewById(R.id.filled_exposed);
        IA_filled_exposed = findViewById(R.id.IA_filled_exposed);
        semestermsg = findViewById(R.id.semestermsg);
        IAmsg = findViewById(R.id.IAmsg);
        fetchMBtn = findViewById(R.id.fetchMBtn);
        firestore = FirebaseFirestore.getInstance();

        //fetch data  ony if user is logged  in
        if (FirebaseAuth.getInstance().getCurrentUser() !=null) {
            firebaseAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
            userID = firebaseAuth.getCurrentUser().getUid();
            fetchuserdetails();
        }


        //semester number drop down
        filled_exposed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selSemNo = parent.getItemAtPosition(position).toString();
                if (TextUtils.isEmpty(selSemNo)){
                    SemNoValue = false;
                    semestermsg.setVisibility(View.VISIBLE);
                    semestermsg.setText("Sem Number Is Required!");
                    filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                }else {
                    SemNoValue = true;
                    semestermsg.setVisibility(View.GONE);
                    filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }
        });

        //IA Number drop down
        IA_filled_exposed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selIANo = parent.getItemAtPosition(position).toString();
                if (TextUtils.isEmpty(selIANo)){
                    selIANoValue = false;
                    IAmsg.setVisibility(View.VISIBLE);
                    IAmsg.setText("Select IA number");
                    IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                }
                else {
                    selIANoValue = true;
                    IAmsg.setVisibility(View.GONE);
                    IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }
        });

        //text watcher on IA_filled_exposed
        IA_filled_exposed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchMBtn.setVisibility(View.VISIBLE);
                IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //        fetch Marks to Firestore
        fetchMBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationView.setVisibility(View.VISIBLE);
                animationView.playAnimation();
                if (SemNoValue != true) {
                    semestermsg.setVisibility(View.VISIBLE);
                    semestermsg.setText("Sem Number Is Required!");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(filled_exposed);
                    filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    animationView.setVisibility(View.GONE);
                    animationView.pauseAnimation();
                }else if (selIANoValue != true) {
                    IAmsg.setVisibility(View.VISIBLE);
                    IAmsg.setText("IA Number Is Required!");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(IA_filled_exposed);
                    IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    animationView.setVisibility(View.GONE);
                    animationView.pauseAnimation();
                }else {
                    animationView.setVisibility(View.GONE);
                    animationView.pauseAnimation();
                    semestermsg.setVisibility(View.GONE);
                    IAmsg.setVisibility(View.GONE);
                    IA_filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
                    filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
                    fetchMarks();
                }
            }
        });

        //marks recycler view
        marksRecycler = findViewById(R.id.marksRecycler);
        marksRecycler.setLayoutManager(new LinearLayoutManager(this));
        marksList = new ArrayList<>();
        marksAdapter = new MarksAdapter(marksList);
        marksRecycler.setAdapter(marksAdapter);

        //back button pressed
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });
    }
    //fetch marks
    private void fetchMarks() {
         selUSN = studentUSN.getText().toString().trim();
        //fetch data
        marksList.clear();

        firestore.collection("marks")
                .whereEqualTo("SemNo",selSemNo)
                .whereEqualTo("SUsn",selUSN)
                .whereEqualTo("IANo",selIANo).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d:list){
                            MarksModel obj=d.toObject(MarksModel.class);
                            marksList.add(obj);
                        }
                        marksAdapter.notifyDataSetChanged();
                        fetchMBtn.setVisibility(View.GONE);
                        YoYo.with(Techniques.Pulse)
                                .duration(1000)
                                .playOn(marksRecycler);

                        //check if subjectModelArrayList is empty
                        if (marksList.isEmpty()){
                            Toast.makeText(StudentMarksDisplayActivity.this, "No Data Found!", Toast.LENGTH_SHORT).show();
                            fetchMBtn.setVisibility(View.VISIBLE);
                            studentPer.setVisibility(View.GONE);

                        }
                    }
                });

        //fetch  per sub Marks
        firestore.collection("marks").whereEqualTo("SemNo",selSemNo)
                .whereEqualTo("SUsn",selUSN).whereEqualTo("IANo",selIANo)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentSnapshot snapshot : value){
                    sMarksSum=sMarksSum+Integer.parseInt(snapshot.getString("sMarks"));
                }
            }
        });

        //fetch  out of Marks
        firestore.collection("marks").whereEqualTo("SemNo",selSemNo)
                .whereEqualTo("SUsn",selUSN).whereEqualTo("IANo",selIANo)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot snapshot : value){
                    OutOfMarks=OutOfMarks+Integer.parseInt(snapshot.getString("sOutOfMarks"));
                }
                if (TextUtils.isEmpty(String.valueOf(sMarksSum)) && TextUtils.isEmpty(String.valueOf(OutOfMarks))){
                    studentPer.setVisibility(View.GONE);
                }else {
                    try {
                        studentPer.setVisibility(View.VISIBLE);
                        perDisplay = (sMarksSum * 100) / OutOfMarks;
                        studentPer.setText(String.valueOf(perDisplay)+"%");
                    }catch (ArithmeticException e){
                        studentPer.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    //fetch user details
    private void fetchuserdetails() {
        DocumentReference df = firestore.collection("users").document(userID);
        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                studentName.setText(value.getString("username"));
                studentUSN.setText(value.getString("usn"));
                fBranch = value.getString("branch");

                //fetch sem no
                firestore.collection(fBranch+"USN").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        semNoList.clear();
                        for (DocumentSnapshot snapshot : value){
                            semNoList.add(snapshot.getString("SemNo"));
                        }
                        semNoHashSet.addAll(semNoList);
                        semNoList.clear();
                        semNoList.addAll(semNoHashSet);
                        semNoAdapter = new ArrayAdapter<String>(StudentMarksDisplayActivity.this,R.layout.list_items,semNoList);
                        //setting adapter
                        filled_exposed.setAdapter(semNoAdapter);
                    }
                });


                //fetch IA no
                firestore.collection("marks").whereEqualTo("SBranch",fBranch)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                IANoList.clear();
                                for (DocumentSnapshot snapshot : value){
                                    IANoList.add(snapshot.getString("IANo"));
                                }
                                IANoHashSet.addAll(IANoList);
                                IANoList.clear();
                                IANoList.addAll(IANoHashSet);
                                IAAdapter = new ArrayAdapter<String>(StudentMarksDisplayActivity.this,R.layout.list_items,IANoList);
                                //setting adapter
                                IA_filled_exposed.setAdapter(IAAdapter);
                            }
                        });

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