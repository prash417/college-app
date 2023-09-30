package com.example.jce.Notes;

import androidx.annotation.NonNull;
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

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NotesRetrieveActivity extends AppCompatActivity {

    //declaration
    String Branch,semNo;
    RecyclerView NotesRecycler;
    ImageView backBtn;

    NotesAdapter notesAdapter;
    ArrayList<NotesModel> notesModelArrayList;


    FirebaseDatabase firebaseDatabase;

    EditText Search;
    TextView notesText;
    ArrayList<NotesModel> filtereList;

    ImageView hideSearchView,ShowSearchView;

    LottieAnimationView animationView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_retrieve);

        Branch = getIntent().getStringExtra("Branch");
        semNo = getIntent().getStringExtra("SemNo");

        //search notes id
        Search = findViewById(R.id.Search);
        hideSearchView = findViewById(R.id.hideSearchView);
        ShowSearchView = findViewById(R.id.ShowSearchView);
        animationView = findViewById(R.id.animationView);
        notesText = findViewById(R.id.notesText);


        //search box Visibility
        ShowSearchView.setOnClickListener(v -> {
                Search.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceInRight)
                    .duration(1000)
                    .playOn(Search);
                hideSearchView.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.GONE);
                notesText.setVisibility(View.GONE);
                ShowSearchView.setVisibility(View.GONE);
        });

        //hide box Visibility
        hideSearchView.setOnClickListener(v -> {
            Search.setVisibility(View.GONE);
            hideSearchView.setVisibility(View.GONE);
            notesText.setVisibility(View.VISIBLE);
            backBtn.setVisibility(View.VISIBLE);
            ShowSearchView.setVisibility(View.VISIBLE);
        });


        //find id
        NotesRecycler = findViewById(R.id.NotesRecycler);
        backBtn = findViewById(R.id.backBtn);

        NotesRecycler.setHasFixedSize(true);
        NotesRecycler.setLayoutManager(new LinearLayoutManager(this));
        notesModelArrayList = new ArrayList<>();
        notesAdapter = new NotesAdapter(notesModelArrayList,this);
        NotesRecycler.setAdapter(notesAdapter);

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

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference().child(Branch).child("Notes").child(semNo);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NotesModel notesModel = dataSnapshot.getValue(NotesModel.class);
                    notesModelArrayList.add(notesModel);
                }

                if (notesModelArrayList.isEmpty()){
                    animationView.setVisibility(View.GONE);
                    animationView.pauseAnimation();
                    Toast.makeText(NotesRetrieveActivity.this, "No Data Found!", Toast.LENGTH_SHORT).show();
                }else {
                    animationView.setVisibility(View.GONE);
                    animationView.pauseAnimation();
                    notesAdapter.notifyDataSetChanged();
                    YoYo.with(Techniques.Pulse)
                            .duration(1000)
                            .playOn(NotesRecycler);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //back button pressed
        backBtn.setOnClickListener(v -> onBackPressed());

    }
    //on back pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), NotesUploadActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        YoYo.with(Techniques.Pulse)
                .duration(1000)
                .playOn(NotesRecycler);
    }
}