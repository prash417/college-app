package com.example.jce.Student.StudentFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jce.AboutUs.AboutUsActivity;
import com.example.jce.Marks.StudentMarksDisplayActivity;
import com.example.jce.Notes.NotesUploadActivity;
import com.example.jce.Notes.StudentNotesRetrieveActivity;
import com.example.jce.QuestionPaper.QPUploadActivity;
import com.example.jce.QuestionPaper.StudentQpRetrieveActivity;
import com.example.jce.R;
import com.example.jce.Student.StudentCourse.StudentCourseActivity;
import com.example.jce.Student.StudentQuiz.StudentQuizActivities.StudentChooseCategoryActivity;
import com.example.jce.Teacher.TeacherHome.TeacherHomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class Student_Home_Fragment extends Fragment {
    //declaration

    FirebaseAuth fAuth;
    FirebaseFirestore fireStore;
    String userID,enteredBranch;

    CardView resultCardView,notesCardView,questionCardView,QuizView,AboutCardView,CourseCardView;
    ImageView TeacherPage;
    TextView username;
    View SHView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SHView = inflater.inflate(R.layout.fragment_student__home_, container, false);

        //find id
        resultCardView = SHView.findViewById(R.id.resultCardView);
        notesCardView = SHView.findViewById(R.id.notesCardView);
        questionCardView = SHView.findViewById(R.id.questionCardView);
        AboutCardView = SHView.findViewById(R.id.AboutCardView);
        CourseCardView = SHView.findViewById(R.id.CourseCardView);
        QuizView = SHView.findViewById(R.id.QuizView);
        TeacherPage = SHView.findViewById(R.id.TeacherPage);
        username = SHView.findViewById(R.id.username);

        //if user is logged in then only fetch data
        if (FirebaseAuth.getInstance().getCurrentUser() !=null) {
            //Firebase instance created
            fAuth = FirebaseAuth.getInstance();
            fireStore = FirebaseFirestore.getInstance();
            userID = fAuth.getCurrentUser().getUid();
            fetchuserdetails();
        }

        //Firebase instance created
        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        //jump to teacher Home
        TeacherPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TeacherHomeActivity.class));
                getActivity().finish();
            }
        });

        //resultCardView on click
        resultCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID = fAuth.getCurrentUser().getUid();
                DocumentReference df = fireStore.collection("users").document(userID);
                df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.getString("isStudent") !=null){
                            startActivity(new Intent(getContext(), StudentMarksDisplayActivity.class));
                            getActivity().finish();
                        }if (documentSnapshot.getString("isTeacher") !=null){
                            Toast.makeText(getContext(),"Only Student Can Open It!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //notesCardView on click
        notesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID = fAuth.getCurrentUser().getUid();
                DocumentReference df = fireStore.collection("users").document(userID);
                df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                            Intent intent = new Intent(getContext(), StudentNotesRetrieveActivity.class);
                            intent.putExtra("Branch", enteredBranch);
                            startActivity(intent);

                    }
                });
            }
        });

//        questionCardView on click
        questionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID = fAuth.getCurrentUser().getUid();
                DocumentReference df = fireStore.collection("users").document(userID);
                df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Intent intent = new Intent(getContext(), StudentQpRetrieveActivity.class);
                            intent.putExtra("Branch", enteredBranch);
                            startActivity(intent);
                    }
                });
            }
        });

        //QuizView on click
        QuizView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), StudentChooseCategoryActivity.class));
                getActivity().finish();
            }
        });

        //AboutCardView is clicked
        AboutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AboutUsActivity.class));
                getActivity().finish();
            }
        });

        //Course CardView is clicked
        CourseCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), StudentCourseActivity.class));
                getActivity().finish();
            }
        });

        return SHView;
    }

    //fetch user profile
    private void fetchuserdetails() {
        DocumentReference df = fireStore.collection("users").document(userID);
        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username.setText(value.getString("username"));
                enteredBranch = value.getString("branch");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference df = fireStore.collection("users").document(userID);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.getString("isStudent") !=null){
                    TeacherPage.setVisibility(View.GONE);
                }if (documentSnapshot.getString("isTeacher") !=null){
                    TeacherPage.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}