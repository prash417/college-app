package com.example.jce.Teacher.TeacherFragment;

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

import com.example.jce.AboutUs.AboutUsActivity;
import com.example.jce.Notes.NotesUploadActivity;
import com.example.jce.Notification.NotificationActivity;
import com.example.jce.QuestionPaper.QPUploadActivity;
import com.example.jce.Teacher.TeacherQuiz.QuizActivities.ChooseCategoryActivity;
import com.example.jce.R;
import com.example.jce.Student.StudentHome.StudentHomeActivity;
import com.example.jce.Teacher.AddSubUSN.TeacherAddSubUSNActivity;
import com.example.jce.Teacher.TeacherMarks.MarksActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class Teacher_Home_Fragment extends Fragment {

    //declaration
    CardView marksCardView,AddCardView,notesCardView,questionCardView,QuizView,AboutCardView;

    ImageView StudentPage,Notification;

    FirebaseAuth fAuth;
    FirebaseFirestore fireStore;
    String userID;
    TextView username;
    View THView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        THView =  inflater.inflate(R.layout.fragment_teacher__home_, container, false);

        //find id
        marksCardView = THView.findViewById(R.id.marksCardView);
        AddCardView = THView.findViewById(R.id.AddCardView);
        notesCardView = THView.findViewById(R.id.notesCardView);
        questionCardView = THView.findViewById(R.id.questionCardView);
        AboutCardView = THView.findViewById(R.id.AboutCardView);
        QuizView = THView.findViewById(R.id.QuizView);
        StudentPage = THView.findViewById(R.id.StudentPage);
        username = THView.findViewById(R.id.username);
        Notification = THView.findViewById(R.id.Notification);


        //if user is logged in then only fetch data
        if (FirebaseAuth.getInstance().getCurrentUser() !=null) {
            //Firebase instance created
            fAuth = FirebaseAuth.getInstance();
            fireStore = FirebaseFirestore.getInstance();
            userID = fAuth.getCurrentUser().getUid();
            fetchuserdetails();
        }

        //jump to student page
        StudentPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), StudentHomeActivity.class));
                getActivity().finish();
            }
        });

        //marks card view
        marksCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MarksActivity.class));
                getActivity().finish();
            }
        });

        //Add card view
        AddCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TeacherAddSubUSNActivity.class));
                getActivity().finish();
            }
        });

        //notes card view
        notesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotesUploadActivity.class));
                getActivity().finish();
            }
        });

        //question card view
        questionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), QPUploadActivity.class));
                getActivity().finish();
            }
        });

        //notification is clicked
        Notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
                getActivity().finish();
            }
        });

        //quiz card view is clicked
        QuizView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChooseCategoryActivity.class));
                getActivity().finish();
            }
        });

//        AboutCardView is clicked
        AboutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AboutUsActivity.class));
                getActivity().finish();
            }
        });
        return THView;
    }
    //fetch user profile
    private void fetchuserdetails() {
        DocumentReference df = fireStore.collection("users").document(userID);
        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username.setText(value.getString("username"));
            }
        });
    }
}