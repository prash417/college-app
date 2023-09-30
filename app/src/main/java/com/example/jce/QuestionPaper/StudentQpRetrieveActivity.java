package com.example.jce.QuestionPaper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.Notes.NotesAdapter;
import com.example.jce.Notes.NotesModel;
import com.example.jce.Notes.StudentNotesRetrieveActivity;
import com.example.jce.R;
import com.example.jce.Student.StudentHome.StudentHomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class StudentQpRetrieveActivity extends AppCompatActivity {

    //declaration
    AutoCompleteTextView filled_exposed;
    final List<String> semNoList = new ArrayList<>();
    HashSet<String> semNoHashSet = new HashSet<>();
    ArrayAdapter<String> semNoAdapter;
    FirebaseFirestore firestore;

    String selSemNo,FBranch;

    RecyclerView NotesRecycler;
    ImageView backBtn;

    NotesAdapter notesAdapter;
    ArrayList<NotesModel> notesModelArrayList;


    FirebaseDatabase firebaseDatabase;

    LottieAnimationView animationView;

    EditText Search;
    TextView QPText;
    ArrayList<NotesModel> filtereList;

    ImageView hideSearchView,ShowSearchView;

    Boolean SemNoValue = false;

    TextView semestermsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_qp_retrieve);


        //find id
        filled_exposed = findViewById(R.id.filled_exposed);
        animationView = findViewById(R.id.animationView);
        semestermsg = findViewById(R.id.semestermsg);
        firestore = FirebaseFirestore.getInstance();

        //search notes id
        Search = findViewById(R.id.Search);
        hideSearchView = findViewById(R.id.hideSearchView);
        ShowSearchView = findViewById(R.id.ShowSearchView);
        QPText = findViewById(R.id.QPText);


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


        //search box Visibility
        ShowSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SemNoValue !=true){
                    semestermsg.setVisibility(View.VISIBLE);
                    semestermsg.setText("Sem Number Is Required!");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(filled_exposed);
                    filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                }else {
                    Search.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceInRight)
                            .duration(1000)
                            .playOn(Search);
                    hideSearchView.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.GONE);
                    QPText.setVisibility(View.GONE);
                    ShowSearchView.setVisibility(View.GONE);
                    semestermsg.setVisibility(View.GONE);
                    filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }
        });

        //hide box Visibility
        hideSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search.setVisibility(View.GONE);
                hideSearchView.setVisibility(View.GONE);
                QPText.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);
                ShowSearchView.setVisibility(View.VISIBLE);
            }
        });

        NotesRecycler = findViewById(R.id.NotesRecycler);
        backBtn = findViewById(R.id.backBtn);

        NotesRecycler.setHasFixedSize(true);
        NotesRecycler.setLayoutManager(new LinearLayoutManager(this));
        notesModelArrayList = new ArrayList<>();
        notesAdapter = new NotesAdapter(notesModelArrayList,this);
        NotesRecycler.setAdapter(notesAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();

        //search specific student
        Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtereList = new ArrayList<>();
                for (NotesModel item:notesModelArrayList){
                    if (item.getFilename().toLowerCase().contains(s.toString().toLowerCase())){
                        filtereList.add(item);
                    }
                }
                if (filtereList.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Notes Not Found!", Toast.LENGTH_SHORT).show();
                    notesAdapter.filteredList(filtereList);
                }else {
                    notesAdapter.filteredList(filtereList);
                    YoYo.with(Techniques.Pulse)
                            .duration(1000)
                            .playOn(NotesRecycler);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        FBranch = getIntent().getStringExtra("Branch");

        //fetch sem no
        firestore.collection(FBranch+"USN").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                semNoAdapter = new ArrayAdapter<String>(StudentQpRetrieveActivity.this,R.layout.list_items,semNoList);
                //setting adapter
                filled_exposed.setAdapter(semNoAdapter);

            }
        });

        //text watcher on sem no
        filled_exposed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                animationView.setVisibility(View.VISIBLE);
                animationView.playAnimation();
                notesModelArrayList.clear();
                selSemNo = filled_exposed.getText().toString().trim();
                if (TextUtils.isEmpty(selSemNo)){
                    semestermsg.setVisibility(View.VISIBLE);
                    semestermsg.setText("Sem Number Is Required!");
                    filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    animationView.setVisibility(View.GONE);
                    animationView.pauseAnimation();
                }else {
                    DatabaseReference reference = firebaseDatabase.getReference().child(FBranch).child("QP").child(selSemNo);

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                NotesModel notesModel = dataSnapshot.getValue(NotesModel.class);
                                notesModelArrayList.clear();
                                notesModelArrayList.add(notesModel);
                            }

                            if (notesModelArrayList.isEmpty()){
                                animationView.setVisibility(View.GONE);
                                animationView.pauseAnimation();
                                Toast.makeText(StudentQpRetrieveActivity.this, "No Data Found!", Toast.LENGTH_SHORT).show();
                                notesModelArrayList.clear();
                                NotesRecycler.setVisibility(View.GONE);
                            }else {
                                animationView.setVisibility(View.GONE);
                                animationView.pauseAnimation();
                                notesAdapter.notifyDataSetChanged();
                                NotesRecycler.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.Pulse)
                                        .duration(1000)
                                        .playOn(NotesRecycler);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    //on back pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), StudentHomeActivity.class));
        finish();
    }
}