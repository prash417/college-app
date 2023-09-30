package com.example.jce.Teacher.TeacherFragment;

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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
import com.example.jce.Student.StudentMessage.StudentTeacherModel;
import com.example.jce.Teacher.TeacherMessage.TeacherStudentAdapter;
import com.example.jce.Teacher.TeacherMessage.TeacherStudentsModel;
import com.example.jce.Users.Login.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Teacher_Message_Fragment extends Fragment {

    //declaration
    RecyclerView recyclerView;
    ArrayList<TeacherStudentsModel> teacherStudentsModelArrayList;
    TeacherStudentAdapter teacherStudentAdapter;
    FirebaseFirestore firestore;

    EditText Search;
    ArrayList<TeacherStudentsModel> filtereList;

    ImageView hideSearchView,ShowSearchView;
    TextView stdText;

    View TMView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        TMView =  inflater.inflate(R.layout.fragment_teacher__message_, container, false);

        //find id
        Search =TMView.findViewById(R.id.Search);
        hideSearchView =TMView.findViewById(R.id.hideSearchView);
        ShowSearchView =TMView.findViewById(R.id.ShowSearchView);
        stdText =TMView.findViewById(R.id.stdText);


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


        //search specific student
        Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtereList = new ArrayList<>();
                for (TeacherStudentsModel item:teacherStudentsModelArrayList){
                    if (item.getUsername().toLowerCase().contains(s.toString().toLowerCase())){
                        filtereList.add(item);
                    }
                }
                if (filtereList.isEmpty()){
                    Toast.makeText(getContext(), "No Teachers Found!", Toast.LENGTH_SHORT).show();
                    teacherStudentAdapter.filteredList(filtereList);
                }else {
                    teacherStudentAdapter.filteredList(filtereList);
                    YoYo.with(Techniques.Pulse)
                            .duration(1000)
                            .playOn(recyclerView);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recyclerView =TMView.findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        teacherStudentsModelArrayList = new ArrayList<>();
        teacherStudentAdapter = new TeacherStudentAdapter(getContext(),teacherStudentsModelArrayList);
        recyclerView.setAdapter(teacherStudentAdapter);
        return TMView;
    }

    //on start fetch all students
    @Override
    public void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() !=null){
            teacherStudentsModelArrayList.clear();
            firestore = FirebaseFirestore.getInstance();
            firestore.collection("users").whereEqualTo("isStudent","1").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            teacherStudentsModelArrayList.clear();
                            List<DocumentSnapshot> studentList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot:studentList){
                                TeacherStudentsModel  obj = documentSnapshot.toObject(TeacherStudentsModel.class);
                                teacherStudentsModelArrayList.add(obj);
                            }
                            teacherStudentAdapter.notifyDataSetChanged();
                            YoYo.with(Techniques.Pulse)
                                    .duration(1000)
                                    .playOn(recyclerView);
                        }
                    });
        }else {
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        }
    }
}