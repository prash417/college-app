package com.example.jce.Student.StudentFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.Notification.NotificationAdapter;
import com.example.jce.Notification.NotificationModel;
import com.example.jce.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Student_Notification_Fragment extends Fragment {

    RecyclerView NotificationRecycler;

    ArrayList<NotificationModel> notificationModelArrayList;
    NotificationAdapter notificationAdapter;

    FirebaseAuth fAuth;
    FirebaseFirestore fireStore;
    ImageView backBtn;
    String userID;

    View NView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        NView =  inflater.inflate(R.layout.fragment_student__notification_, container, false);

        //find id
        backBtn = NView.findViewById(R.id.backBtn);
        NotificationRecycler = NView.findViewById(R.id.NotificationRecycler);


        //Firebase instance created
        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        //fetch notification Function
        fetchNotification();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        NotificationRecycler.setLayoutManager(linearLayoutManager);
        notificationModelArrayList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(),notificationModelArrayList);
        NotificationRecycler.setAdapter(notificationAdapter);


        return NView;
    }

    //fetch notification method
    private void fetchNotification() {
        fireStore.collection("Notification").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        notificationModelArrayList.clear();
                        List<DocumentSnapshot> NotificationList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot:NotificationList){
                            NotificationModel  obj = documentSnapshot.toObject(NotificationModel.class);
                            notificationModelArrayList.add(obj);
                        }
                        notificationAdapter.notifyDataSetChanged();
                        YoYo.with(Techniques.BounceInLeft)
                                .duration(1000)
                                .playOn(NotificationRecycler);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        notificationModelArrayList.clear();
        fireStore.collection("Notification").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        notificationModelArrayList.clear();
                        List<DocumentSnapshot> NotificationList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot:NotificationList){
                            NotificationModel  obj = documentSnapshot.toObject(NotificationModel.class);
                            notificationModelArrayList.add(obj);
                        }
                        notificationAdapter.notifyDataSetChanged();
                        YoYo.with(Techniques.BounceInLeft)
                                .duration(1000)
                                .playOn(NotificationRecycler);
                    }
                });

        YoYo.with(Techniques.Pulse)
                .duration(1000)
                .playOn(NotificationRecycler);
    }
}