package com.example.jce.Notification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
import com.example.jce.Student.StudentHome.StudentHomeActivity;
import com.example.jce.Teacher.TeacherHome.TeacherHomeActivity;
import com.example.jce.Teacher.TeacherMessage.TeacherStudentAdapter;
import com.example.jce.Teacher.TeacherMessage.TeacherStudentsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationActivity extends AppCompatActivity {

    //declaration
    FirebaseAuth fAuth;
    FirebaseFirestore fireStore;
    String userID,enteredMSG,displayName,SenderName,currenttime,sentdate,TimeStamp;
    ImageView backBtn,choosenImg;
    EditText MessageInput;
    CircleImageView ChooseIMG,ChooseFile,CloseBTN,sendbtn;
    RelativeLayout SelIMGorFile,PDF_Display;
    Uri imageuri,fileuri,fetchedFileUri;
    Boolean imageuriValue = false;
    Boolean ChooseImgValue = false;
    Boolean ChooseFileValue = false;
    Boolean FileValue = false;
    TextView PDF_Name;
    StorageReference storageReference;
    RecyclerView NotificationRecycler;

    ArrayList<NotificationModel> notificationModelArrayList;
    NotificationAdapter notificationAdapter;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //find id
        backBtn = findViewById(R.id.backBtn);
        MessageInput = findViewById(R.id.MessageInput);
        ChooseIMG = findViewById(R.id.ChooseIMG);
        ChooseFile = findViewById(R.id.ChooseFile);
        SelIMGorFile = findViewById(R.id.SelIMGorFile);
        choosenImg = findViewById(R.id.choosenImg);
        PDF_Display = findViewById(R.id.PDF_Display);
        CloseBTN = findViewById(R.id.CloseBTN);
        sendbtn = findViewById(R.id.sendbtn);
        PDF_Name = findViewById(R.id.PDF_Name);
        NotificationRecycler = findViewById(R.id.NotificationRecycler);


        //Firebase instance created
        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //fetch notification Function
        fetchNotification();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        NotificationRecycler.setLayoutManager(linearLayoutManager);
        notificationModelArrayList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getApplicationContext(),notificationModelArrayList);
        NotificationRecycler.setAdapter(notificationAdapter);

        calendar=Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");


        //back btn pressed
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        //text watcher on MessageInput
        MessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enteredMSG = MessageInput.getText().toString().trim();
                if (TextUtils.isEmpty(enteredMSG)){
                    ChooseIMG.setVisibility(View.VISIBLE);
                    ChooseFile.setVisibility(View.VISIBLE);
                }else {
                    ChooseIMG.setVisibility(View.GONE);
                    ChooseFile.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //choose IMG is clicked
        ChooseIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImgValue = true;
                ChooseFileValue = false;
                SelIMGorFile.setVisibility(View.VISIBLE);
                choosenImg.setVisibility(View.VISIBLE);
                PDF_Display.setVisibility(View.GONE);
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,1000);
            }
        });

        //choose File is clicked
        ChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImgValue = false;
                ChooseFileValue = true;
                SelIMGorFile.setVisibility(View.VISIBLE);
                choosenImg.setVisibility(View.GONE);
                PDF_Display.setVisibility(View.VISIBLE);


                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select PDF Files"),101);

            }
        });

        //close icon is clicked
        CloseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              closeFunction();
            }
        });

        //send button is clicked
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredMSG = MessageInput.getText().toString().trim();
                //if image is clicked
                if (ChooseImgValue == true){
                    if (imageuriValue !=true){
                        Toast.makeText(NotificationActivity.this, "Choose An Image First!", Toast.LENGTH_SHORT).show();
                    }if (TextUtils.isEmpty(enteredMSG)){
                        Toast.makeText(NotificationActivity.this, "Enter Message First!", Toast.LENGTH_SHORT).show();
                    }else {
                        sendNotificationWithImg(imageuri,enteredMSG);
                        closeFunction();
                    }
                }

                //if File is clicked
                if (ChooseFileValue == true){
                    if (FileValue !=true){
                        Toast.makeText(NotificationActivity.this, "Choose An File First!", Toast.LENGTH_SHORT).show();
                    }if (TextUtils.isEmpty(enteredMSG)){
                        Toast.makeText(NotificationActivity.this, "Enter Message First!", Toast.LENGTH_SHORT).show();
                    }else {
                        sendNotificationWithfile(fileuri,enteredMSG,displayName);
                        closeFunction();
                    }
                }

                if (ChooseFileValue != true && ChooseImgValue != true){
                    if (TextUtils.isEmpty(enteredMSG)){
                        Toast.makeText(NotificationActivity.this, "Enter Message First!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        sendNotification(enteredMSG);
                        closeFunction();
                    }
                }
            }
        });
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
                    }
                });
    }

    //send notification
    private void sendNotification(String enteredMSG) {

        fetchDateTime();

        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fireStore.collection("Notification")
                .document(String.valueOf(System.currentTimeMillis()));
        Map<String, Object> SimpleUser = new HashMap<>();
        SimpleUser.put("Message", enteredMSG);
        SimpleUser.put("ImgUri", null);
        SimpleUser.put("FileUri", null);
        SimpleUser.put("FileName", null);
        SimpleUser.put("Sender", SenderName);
        SimpleUser.put("sentTime", currenttime);
        SimpleUser.put("sentDate", sentdate);
        SimpleUser.put("TimeStamp", TimeStamp);
        documentReference.set(SimpleUser);

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
                    }
                });

    }

    //fetch date and time notification sent
    private void fetchDateTime() {
        Date date= new Date();
        currenttime = simpleDateFormat.format(calendar.getTime());
        Date DateAndTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sentdate = dateFormat.format(DateAndTime);
        TimeStamp = String.valueOf(System.currentTimeMillis());
    }

    //store notification with file
    private void sendNotificationWithfile(Uri fileuri, String enteredMSG, String displayName) {
        StorageReference reference = storageReference.child("Notification/"
                +System.currentTimeMillis() +"/"+displayName);
        reference.putFile(fileuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        fetchedFileUri = uriTask.getResult();
                        storeDataWithFile(fetchedFileUri,enteredMSG,displayName );

                    }
                });
    }

    //store data with file
    private void storeDataWithFile(Uri fetchedFileUri, String enteredMSG, String displayName) {
        fetchDateTime();

        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fireStore.collection("Notification").document(String.valueOf(System.currentTimeMillis()));
        Map<String, Object> FileUser = new HashMap<>();
        FileUser.put("Message", enteredMSG);
        FileUser.put("FileUri", fetchedFileUri);
        FileUser.put("FileName", displayName);
        FileUser.put("ImgUri", null);
        FileUser.put("Sender", SenderName);
        FileUser.put("Sender", SenderName);
        FileUser.put("sentTime", currenttime);
        FileUser.put("sentDate", sentdate);
        FileUser.put("TimeStamp", TimeStamp);
        documentReference.set(FileUser);

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
                    }
                });
    }

    //send notification with image
    private void sendNotificationWithImg(Uri imageuri, String enteredMSG) {

        //upload image to firbase storage
        StorageReference fileRef = storageReference.child("Notification/"+System.currentTimeMillis()+"/Notification.jpg");
        fileRef.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        storeDataWithImg(uri,enteredMSG);
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .placeholder(R.drawable.person_icon)
                                    .into(choosenImg);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //store data along with image uri
    private void storeDataWithImg(Uri uri, String enteredMSG) {
        fetchDateTime();

        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fireStore.collection("Notification").document(String.valueOf(System.currentTimeMillis()));
        Map<String, Object> ImgUser = new HashMap<>();
        ImgUser.put("Message", enteredMSG);
        ImgUser.put("ImgUri", uri);
        ImgUser.put("FileUri", null);
        ImgUser.put("FileName", null);
        ImgUser.put("Sender", SenderName);
        ImgUser.put("Sender", SenderName);
        ImgUser.put("sentTime", currenttime);
        ImgUser.put("sentDate", sentdate);
        ImgUser.put("TimeStamp", TimeStamp);
        documentReference.set(ImgUser);

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
                    }
                });
    }

    //close other layouts if opened
    private void closeFunction() {
        SelIMGorFile.setVisibility(View.GONE);
        choosenImg.setVisibility(View.GONE);
        PDF_Display.setVisibility(View.GONE);

        imageuriValue = false;
        FileValue = false;

        imageuri = null;
        fileuri = null;
        MessageInput.setText(null);
        PDF_Name.setText(null);
        Glide.with(getApplicationContext())
                .load(imageuri)
                .placeholder(R.drawable.add_photo_icon)
                .into(choosenImg);
    }

    //    onBackPressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        userID = fAuth.getCurrentUser().getUid();
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

    //fetch image and file from memory
    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ChooseImgValue == true) {
            if (requestCode == 1000) {
                if (resultCode == Activity.RESULT_OK) {
                    imageuri = data.getData();
                    if (imageuri != null) {
                            Glide.with(getApplicationContext())
                                    .load(imageuri)
                                    .placeholder(R.drawable.add_photo_icon)
                                    .into(choosenImg);
                        imageuriValue = true;
                    } else {
                        imageuriValue = false;
                    }
                }
            }
        }
        if (ChooseFileValue == true){
            if (requestCode == 101 && resultCode == RESULT_OK && data !=null && data.getData() !=null){
                fileuri = data.getData();

                //to fetch pdf file name
                String uriString = fileuri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                displayName = null;

                if (uriString.startsWith("content://")){
                    Cursor cursor = null;
                    try {
                        cursor = this.getContentResolver().query(fileuri,null,null,null,null);
                        if (cursor !=null && cursor.moveToFirst()){
                            FileValue = true;
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    }finally {
                        cursor.close();
                    }
                }else if (uriString.startsWith("file://")){
                    FileValue = true;
                    displayName = myFile.getName();
                }
                PDF_Name.setText(displayName);

            }
        }
    }

    //on start fetch details
    @Override
    protected void onStart() {
        super.onStart();
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference df = fireStore.collection("users").document(userID);
        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                SenderName = value.getString("username");
            }
        });

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
    }
}
