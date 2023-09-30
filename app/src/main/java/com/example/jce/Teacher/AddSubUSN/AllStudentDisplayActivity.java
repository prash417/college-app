package com.example.jce.Teacher.AddSubUSN;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.Notes.NotesModel;
import com.example.jce.R;
import com.example.jce.Users.Login.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllStudentDisplayActivity extends AppCompatActivity {

    //declaration
    ArrayList<StudentUSNModel> studentUSNModelArrayList;
    StudentUSNAdapter studentUSNAdapter;

    RecyclerView studentRecycler;

    FirebaseFirestore firestore;

    String fBranch,selSemNo;

    TextView BranchName;
    EditText Search;
    ArrayList<StudentUSNModel> filtereList;

    ImageView hideSearchView,ShowSearchView;

    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_student_display);
        //find id
        studentRecycler = findViewById(R.id.studentRecycler);
        BranchName = findViewById(R.id.BranchName);
        backBtn = findViewById(R.id.backBtn);
        firestore = FirebaseFirestore.getInstance();

        //search notes id
        Search = findViewById(R.id.Search);
        hideSearchView = findViewById(R.id.hideSearchView);
        ShowSearchView = findViewById(R.id.ShowSearchView);


        //search box Visibility
        ShowSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.BounceInRight)
                        .duration(1000)
                        .playOn(Search);
                hideSearchView.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.GONE);
                BranchName.setVisibility(View.GONE);
                ShowSearchView.setVisibility(View.GONE);
            }
        });

        //hide box Visibility
        hideSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search.setVisibility(View.GONE);
                hideSearchView.setVisibility(View.GONE);
                BranchName.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);
                ShowSearchView.setVisibility(View.VISIBLE);
            }
        });


        studentRecycler.setLayoutManager(new LinearLayoutManager(AllStudentDisplayActivity.this));
        studentUSNModelArrayList = new ArrayList<>();
        studentUSNAdapter = new StudentUSNAdapter(studentUSNModelArrayList);
        studentRecycler.setAdapter(studentUSNAdapter);

        //search specific student
        Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtereList = new ArrayList<>();
                for (StudentUSNModel item:studentUSNModelArrayList){
                    if (item.getSName().toLowerCase().contains(s.toString().toLowerCase())){
                        filtereList.add(item);
                    }
                }
                if (filtereList.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Students Not Found!", Toast.LENGTH_SHORT).show();
                    studentUSNAdapter.filteredList(filtereList);
                }else {
                    studentUSNAdapter.filteredList(filtereList);
                    YoYo.with(Techniques.Pulse)
                            .duration(1000)
                            .playOn(studentRecycler);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //on back button pressed
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });

        //to remove student usn
        studentUSNAdapter.setOnItemClickListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                studentUSNModelArrayList.remove(position);
                studentUSNAdapter.notifyItemRemoved(position);
                YoYo.with(Techniques.Pulse)
                        .duration(1000)
                        .playOn(studentRecycler);
            }
        });
    }

    //on start fetch all students
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() !=null){

            fBranch = getIntent().getStringExtra("fBranch");
            BranchName.setText(fBranch);
            selSemNo = getIntent().getStringExtra("fsemno");

            studentUSNModelArrayList.clear();
            firestore.collection(fBranch + "USN").whereEqualTo("SemNo", selSemNo).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                StudentUSNModel obj = d.toObject(StudentUSNModel.class);
                                studentUSNModelArrayList.add(obj);
                            }
                            studentUSNAdapter.notifyDataSetChanged();
                            YoYo.with(Techniques.Pulse)
                                    .duration(1000)
                                    .playOn(studentRecycler);

                            //check if subjectModelArrayList is empty
                            if (studentUSNModelArrayList.isEmpty()) {
                                Toast.makeText(AllStudentDisplayActivity.this, "No Data Found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            YoYo.with(Techniques.Pulse)
                    .duration(1000)
                    .playOn(studentRecycler);
        }
        else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    //on back pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),TeacherAddSubUSNActivity.class));
        finish();
    }
}