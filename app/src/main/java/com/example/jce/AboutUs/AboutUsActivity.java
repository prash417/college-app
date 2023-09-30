
package com.example.jce.AboutUs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.AboutUs.OurHistory.OurHistoryAdapter;
import com.example.jce.AboutUs.OurHistory.OurHistoryModel;
import com.example.jce.R;
import com.example.jce.Student.StudentHome.StudentHomeActivity;
import com.example.jce.Student.StudentMessage.StudentTeacherAdapter;
import com.example.jce.Student.StudentMessage.StudentTeacherModel;
import com.example.jce.Teacher.TeacherHome.TeacherHomeActivity;
import com.example.jce.Teacher.TeacherMessage.TeacherStudentsModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AboutUsActivity extends AppCompatActivity {

    //declaration
    FirebaseAuth fAuth;
    FirebaseFirestore fireStore;
    String userID;
    ImageView backBtn,OurHistoryExpandBtn,VisionMissionExpandBtn,AdministrationExpandBtn;
    RecyclerView OurHistoryRecycler,VisionMissionRecycler,AdministrationRecycler;
    ArrayList<OurHistoryModel> ourHistoryModelArrayList;
    OurHistoryAdapter ourHistoryAdapter;
    TextView JGILink;
    Boolean OurHistoryValue = true;
    Boolean VisionMissionValue = true;
    Boolean AdministrationValue = true;
    RelativeLayout OurHistoryLayout,VisionMissionLayout,AdministrationLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        //find id
        //Firebase instance created
        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        backBtn = findViewById(R.id.backBtn);
        OurHistoryExpandBtn = findViewById(R.id.OurHistoryExpandBtn);
        VisionMissionExpandBtn = findViewById(R.id.VisionMissionExpandBtn);
        AdministrationExpandBtn = findViewById(R.id.AdministrationExpandBtn);
        OurHistoryRecycler = findViewById(R.id.OurHistoryRecycler);
        VisionMissionRecycler = findViewById(R.id.VisionMissionRecycler);
        AdministrationRecycler = findViewById(R.id.AdministrationRecycler);
        JGILink = findViewById(R.id.JGILink);
        OurHistoryLayout = findViewById(R.id.OurHistoryLayout);
        VisionMissionLayout = findViewById(R.id.VisionMissionLayout);
        AdministrationLayout = findViewById(R.id.AdministrationLayout);



        //back button is pressed
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        OurHistoryExpandBtn is clicked
        OurHistoryExpandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (OurHistoryValue == true){
                    OurHistoryRecycler.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceInDown)
                            .duration(2000)
                            .playOn(OurHistoryRecycler);
                    VisionMissionRecycler.setVisibility(View.GONE);
                    AdministrationRecycler.setVisibility(View.GONE);
                    fetchOurHistory();
                }else {
                    OurHistoryRecycler.setVisibility(View.GONE);
                    VisionMissionRecycler.setVisibility(View.GONE);
                    AdministrationRecycler.setVisibility(View.GONE);
                    OurHistoryExpandBtn.setImageResource(R.drawable.down_icon);
                    VisionMissionExpandBtn.setImageResource(R.drawable.down_icon);
                    AdministrationExpandBtn.setImageResource(R.drawable.down_icon);
                    OurHistoryValue = true;
                }

            }
        });

//        OurHistoryLayout clicked
        OurHistoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OurHistoryExpandBtn.callOnClick();
            }
        });

        //        VisionMissionExpandBtn is clicked
        VisionMissionExpandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (VisionMissionValue == true){
                    OurHistoryRecycler.setVisibility(View.GONE);
                    VisionMissionRecycler.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceInDown)
                            .duration(2000)
                            .playOn(VisionMissionRecycler);
                    AdministrationRecycler.setVisibility(View.GONE);
                    fetchVisionMission();
                }else {
                    OurHistoryRecycler.setVisibility(View.GONE);
                    VisionMissionRecycler.setVisibility(View.GONE);
                    AdministrationRecycler.setVisibility(View.GONE);
                    OurHistoryExpandBtn.setImageResource(R.drawable.down_icon);
                    VisionMissionExpandBtn.setImageResource(R.drawable.down_icon);
                    AdministrationExpandBtn.setImageResource(R.drawable.down_icon);
                    VisionMissionValue = true;
                }
            }
        });

//        VisionMissionLayout is clicked
        VisionMissionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisionMissionExpandBtn.callOnClick();
            }
        });

        //        AdministrationExpandBtn is clicked
        AdministrationExpandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AdministrationValue == true){
                    OurHistoryRecycler.setVisibility(View.GONE);
                    VisionMissionRecycler.setVisibility(View.GONE);
                    AdministrationRecycler.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceInDown)
                            .duration(2000)
                            .playOn(AdministrationRecycler);
                    fetchAdministration();
                }else {
                    OurHistoryRecycler.setVisibility(View.GONE);
                    VisionMissionRecycler.setVisibility(View.GONE);
                    AdministrationRecycler.setVisibility(View.GONE);
                    OurHistoryExpandBtn.setImageResource(R.drawable.down_icon);
                    VisionMissionExpandBtn.setImageResource(R.drawable.down_icon);
                    AdministrationExpandBtn.setImageResource(R.drawable.down_icon);
                    AdministrationValue = true;
                }

            }
        });

//        AdministrationLayout is clicked
        AdministrationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdministrationExpandBtn.callOnClick();
            }
        });

        //jump to JGI Website
        JGILink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.jce.ac.in/"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

//    fetchAdministration
    private void fetchAdministration() {
        AdministrationRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ourHistoryModelArrayList = new ArrayList<>();
        ourHistoryAdapter = new OurHistoryAdapter(getApplicationContext(), ourHistoryModelArrayList);
        AdministrationRecycler.setAdapter(ourHistoryAdapter);

        fireStore.collection("Administration").orderBy("number").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ourHistoryModelArrayList.clear();
                        List<DocumentSnapshot>  ourHistory= queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot:ourHistory){
                            OurHistoryModel obj = documentSnapshot.toObject(OurHistoryModel.class);
                            ourHistoryModelArrayList.add(obj);
                        }
                        ourHistoryAdapter.notifyDataSetChanged();
                        if (ourHistoryModelArrayList.isEmpty()){
                            Toast.makeText(AboutUsActivity.this, "No Data Found!", Toast.LENGTH_SHORT).show();
                            OurHistoryExpandBtn.setImageResource(R.drawable.down_icon);
                            VisionMissionExpandBtn.setImageResource(R.drawable.down_icon);
                            AdministrationExpandBtn.setImageResource(R.drawable.down_icon);
                        }else {
                            OurHistoryExpandBtn.setImageResource(R.drawable.down_icon);
                            VisionMissionExpandBtn.setImageResource(R.drawable.down_icon);
                            AdministrationExpandBtn.setImageResource(R.drawable.up_icon);
                            ourHistoryAdapter.notifyDataSetChanged();
                            AdministrationValue = false;
                        }
                    }
                });
    }

    //fetchVisionMission value
    private void fetchVisionMission() {
        VisionMissionRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ourHistoryModelArrayList = new ArrayList<>();
        ourHistoryAdapter = new OurHistoryAdapter(getApplicationContext(), ourHistoryModelArrayList);
        VisionMissionRecycler.setAdapter(ourHistoryAdapter);

        fireStore.collection("VisionMission").orderBy("number").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ourHistoryModelArrayList.clear();
                        List<DocumentSnapshot>  ourHistory= queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot:ourHistory){
                            OurHistoryModel obj = documentSnapshot.toObject(OurHistoryModel.class);
                            ourHistoryModelArrayList.add(obj);
                        }
                        ourHistoryAdapter.notifyDataSetChanged();
                        if (ourHistoryModelArrayList.isEmpty()){
                            Toast.makeText(AboutUsActivity.this, "No Data Found!", Toast.LENGTH_SHORT).show();
                            OurHistoryExpandBtn.setImageResource(R.drawable.down_icon);
                            VisionMissionExpandBtn.setImageResource(R.drawable.down_icon);
                            AdministrationExpandBtn.setImageResource(R.drawable.down_icon);
                        }else {
                            OurHistoryExpandBtn.setImageResource(R.drawable.down_icon);
                            VisionMissionExpandBtn.setImageResource(R.drawable.up_icon);
                            AdministrationExpandBtn.setImageResource(R.drawable.down_icon);
                            ourHistoryAdapter.notifyDataSetChanged();
                            VisionMissionValue = false;

                        }
                    }
                });
    }

    //fetch our History
    private void fetchOurHistory() {

        OurHistoryRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ourHistoryModelArrayList = new ArrayList<>();
        ourHistoryAdapter = new OurHistoryAdapter(getApplicationContext(), ourHistoryModelArrayList);
        OurHistoryRecycler.setAdapter(ourHistoryAdapter);

        fireStore.collection("OurHistory").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ourHistoryModelArrayList.clear();
                        List<DocumentSnapshot>  ourHistory= queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot:ourHistory){
                            OurHistoryModel obj = documentSnapshot.toObject(OurHistoryModel.class);
                            ourHistoryModelArrayList.add(obj);
                        }
                        if (ourHistoryModelArrayList.isEmpty()){
                            Toast.makeText(AboutUsActivity.this, "No Data Found!", Toast.LENGTH_SHORT).show();
                            OurHistoryExpandBtn.setImageResource(R.drawable.down_icon);
                            VisionMissionExpandBtn.setImageResource(R.drawable.down_icon);
                            AdministrationExpandBtn.setImageResource(R.drawable.down_icon);
                        }else {
                            OurHistoryExpandBtn.setImageResource(R.drawable.up_icon);
                            VisionMissionExpandBtn.setImageResource(R.drawable.down_icon);
                            AdministrationExpandBtn.setImageResource(R.drawable.down_icon);
                            ourHistoryAdapter.notifyDataSetChanged();
                            OurHistoryValue = false;
                        }
                    }
                });
    }

    //on back pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DocumentReference df = fireStore.collection("users").document(userID);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.getString("isStudent") !=null){
                    startActivity(new Intent(getApplicationContext(), StudentHomeActivity.class));
                    finish();
                }if (documentSnapshot.getString("isTeacher") !=null){
                    startActivity(new Intent(getApplicationContext(), TeacherHomeActivity.class));
                    finish();
                }
            }
        });
    }

}