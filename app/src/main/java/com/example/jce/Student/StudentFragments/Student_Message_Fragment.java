package com.example.jce.Student.StudentFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
import com.example.jce.Student.StudentHome.StudentHomeActivity;
import com.example.jce.Student.StudentMessage.StudentTeacherAdapter;
import com.example.jce.Student.StudentMessage.StudentTeacherModel;
import com.example.jce.Teacher.TeacherHome.TeacherHomeActivity;
import com.example.jce.Teacher.TeacherMessage.TeacherStudentsModel;
import com.example.jce.Users.Login.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Student_Message_Fragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<StudentTeacherModel> studentTeacherModelArrayList;
    StudentTeacherAdapter studentTeacherAdapter;
    FirebaseFirestore firestore;

    String userID;
    FirebaseAuth fAuth;

    EditText Search;
    ArrayList<StudentTeacherModel> filtereList;

    ImageView hideSearchView,ShowSearchView;
    TextView stdText;
    View SMView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SMView = inflater.inflate(R.layout.fragment_student__message_, container, false);


        //find id
        Search =SMView.findViewById(R.id.Search);
        hideSearchView =SMView.findViewById(R.id.hideSearchView);
        ShowSearchView =SMView.findViewById(R.id.ShowSearchView);
        stdText =SMView.findViewById(R.id.stdText);

        //search box Visibility
        ShowSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.BounceInRight)
                        .duration(1000)
                        .playOn(Search);
                hideSearchView.setVisibility(View.VISIBLE);
                stdText.setVisibility(View.GONE);
                ShowSearchView.setVisibility(View.GONE);
            }
        });

        //hide box Visibility
        hideSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search.setVisibility(View.GONE);
                hideSearchView.setVisibility(View.GONE);
                stdText.setVisibility(View.VISIBLE);
                ShowSearchView.setVisibility(View.VISIBLE);
            }
        });

        recyclerView =SMView.findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentTeacherModelArrayList = new ArrayList<>();
        studentTeacherAdapter = new StudentTeacherAdapter(getContext(), studentTeacherModelArrayList);
        recyclerView.setAdapter(studentTeacherAdapter);


        //search specific student
        Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtereList = new ArrayList<>();
                for (StudentTeacherModel item:studentTeacherModelArrayList){
                    if (item.getUsername().toLowerCase().contains(s.toString().toLowerCase())){
                        filtereList.add(item);
                    }
                }
                if (filtereList.isEmpty()){
                    Toast.makeText(getContext(), "No Teachers Found!", Toast.LENGTH_SHORT).show();
                    studentTeacherAdapter.filteredList(filtereList);
                }else {
                    studentTeacherAdapter.filteredList(filtereList);
                    YoYo.with(Techniques.Pulse)
                            .duration(1000)
                            .playOn(recyclerView);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        return SMView;
    }

    //on start fetch all teachers
    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() !=null){
                firestore = FirebaseFirestore.getInstance();
                fAuth = FirebaseAuth.getInstance();
                userID = fAuth.getCurrentUser().getUid();
                DocumentReference df = firestore.collection("users").document(userID);
                df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.getString("isStudent") != null) {
                            firestore.collection("users").whereEqualTo("isTeacher", "1").get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            studentTeacherModelArrayList.clear();
                                            List<DocumentSnapshot> studentList = queryDocumentSnapshots.getDocuments();
                                            for (DocumentSnapshot documentSnapshot : studentList) {
                                                StudentTeacherModel obj = documentSnapshot.toObject(StudentTeacherModel.class);
                                                studentTeacherModelArrayList.add(obj);
                                            }
                                            studentTeacherAdapter.notifyDataSetChanged();
                                            YoYo.with(Techniques.Pulse)
                                                    .duration(1000)
                                                    .playOn(recyclerView);
                                        }
                                    });
                        }
                        if (documentSnapshot.getString("isTeacher") != null) {
                            firestore.collection("users").whereEqualTo("isStudent", "1").get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            studentTeacherModelArrayList.clear();
                                            List<DocumentSnapshot> studentList = queryDocumentSnapshots.getDocuments();
                                            for (DocumentSnapshot documentSnapshot : studentList) {
                                                StudentTeacherModel obj = documentSnapshot.toObject(StudentTeacherModel.class);
                                                studentTeacherModelArrayList.add(obj);
                                            }
                                            studentTeacherAdapter.notifyDataSetChanged();
                                            YoYo.with(Techniques.Pulse)
                                                    .duration(1000)
                                                    .playOn(recyclerView);
                                        }
                                    });
                        }
                    }
                });
            YoYo.with(Techniques.Pulse)
                    .duration(1000)
                    .playOn(recyclerView);
        }
        else {
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        }
    }
}