package com.example.jce.Marks;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
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
import java.util.List;

public class TeacherMarksDisplayActivity extends AppCompatActivity {

    //declaration
    RecyclerView marksRecycler;
    ArrayList<MarksModel> marksList;
    MarksAdapter marksAdapter;
    FirebaseFirestore firestore;

    String fBranch,selSemNo,selUSNNo,selIANo;

    TextView studentUSN,studentName,studentPer;

    ImageView backBtn;

    int sMarksSum=0;
    int OutOfMarks=0;
    float perDisplay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_marks_display);
        //find id
        studentUSN = findViewById(R.id.studentUSN);
        studentName = findViewById(R.id.studentName);
        studentPer = findViewById(R.id.studentPer);
        backBtn = findViewById(R.id.backBtn);
        firestore = FirebaseFirestore.getInstance();

        //marks recycler view
        marksRecycler = findViewById(R.id.marksRecycler);
        marksRecycler.setLayoutManager(new LinearLayoutManager(this));
        marksList = new ArrayList<>();
        marksAdapter = new MarksAdapter(marksList);
        marksRecycler.setAdapter(marksAdapter);
        //to remove student marks
        marksAdapter.setOnItemClickListener(new MarksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                marksList.remove(position);
                marksAdapter.notifyItemRemoved(position);
            }
        });

        //back button pressed
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });
    }
    //on start of activity
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() !=null){
            fBranch = getIntent().getStringExtra("fBranch");
            selSemNo = getIntent().getStringExtra("fsemno");
            selUSNNo = getIntent().getStringExtra("fusn");
            studentUSN.setText(selUSNNo);
            selIANo = getIntent().getStringExtra("fIANo");
        }
        else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        //fetch data
        marksList.clear();
        firestore.collection("marks")
                .whereEqualTo("SemNo",selSemNo)
                .whereEqualTo("SUsn",selUSNNo)
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
                        YoYo.with(Techniques.Pulse)
                                .duration(1000)
                                .playOn(marksRecycler);

                        //check if subjectModelArrayList is empty
                        if (marksList.isEmpty()){
                            Toast.makeText(TeacherMarksDisplayActivity.this, "No Data Found!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //fetch  per sub Marks
        firestore.collection("marks").whereEqualTo("SemNo",selSemNo)
                .whereEqualTo("SUsn",selUSNNo).whereEqualTo("IANo",selIANo)
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
                .whereEqualTo("SUsn",selUSNNo).whereEqualTo("IANo",selIANo)
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

        DocumentReference df = firestore.collection(fBranch+"USN").document(selUSNNo);
        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                studentName.setText(value.getString("SName"));
            }
        });
    }

    //on back pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MarksActivity.class));
        finish();
    }
}