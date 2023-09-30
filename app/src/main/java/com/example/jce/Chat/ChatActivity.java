package com.example.jce.Chat;

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
import com.example.jce.Notification.NotificationActivity;
import com.example.jce.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    //declaration
    RecyclerView messageRecycler;

    String UserUri;
    CircleImageView UserImage,sendbtn,ChooseIMG,ChooseFile,CloseBTN;
    TextView uName;
    EditText MessageInput;
    String enteredMessage;
    FirebaseDatabase firebaseDatabase;

    Intent intent;
    String ReceiverName,ReceiverUID,senderUID;
    private FirebaseAuth firebaseAuth;
    String senderroom,recieverroom,displayName;

    String currenttime,sentdate;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    MessagesAdapter messagesAdapter;
    ArrayList<Messages> messagesArrayList;

    RelativeLayout SelIMGorFile,PDF_Display;
    Uri imageuri,fileuri,fetchedFileUri;
    Boolean imageuriValue = false;
    Boolean ChooseImgValue = false;
    Boolean ChooseFileValue = false;
    Boolean FileValue = false;
    TextView PDF_Name;
    StorageReference storageReference;
    ImageView choosenImg;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //find id
        UserImage = findViewById(R.id.UserImage);
        uName = findViewById(R.id.uName);
        sendbtn = findViewById(R.id.sendbtn);
        MessageInput = findViewById(R.id.MessageInput);

        ChooseIMG = findViewById(R.id.ChooseIMG);
        ChooseFile = findViewById(R.id.ChooseFile);
        SelIMGorFile = findViewById(R.id.SelIMGorFile);
        choosenImg = findViewById(R.id.choosenImg);
        PDF_Display = findViewById(R.id.PDF_Display);
        CloseBTN = findViewById(R.id.CloseBTN);
        sendbtn = findViewById(R.id.sendbtn);
        PDF_Name = findViewById(R.id.PDF_Name);
        intent = getIntent();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        calendar=Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");

        senderUID = firebaseAuth.getUid();
        ReceiverUID = getIntent().getStringExtra("receiverUID");

        //create rooms to send and get message
        senderroom=senderUID+ReceiverUID;
        recieverroom=ReceiverUID+senderUID;

        ReceiverName = getIntent().getStringExtra("userName");
        uName.setText(ReceiverName);


        //text watcher on MessageInput
        MessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enteredMessage = MessageInput.getText().toString().trim();
                if (TextUtils.isEmpty(enteredMessage)){
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


        //fetch messages
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("chats").child(senderroom).child("Messages");
        messagesAdapter = new MessagesAdapter(ChatActivity.this,messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Messages  messages = snapshot1.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        messagesArrayList = new ArrayList<>();
        messageRecycler = findViewById(R.id.messageRecycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageRecycler.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(ChatActivity.this,messagesArrayList);
        messageRecycler.setAdapter(messagesAdapter);


        //send message
        sendbtn.setOnClickListener(v -> {

            enteredMessage = MessageInput.getText().toString().trim();
            //if image is clicked
            if (ChooseImgValue == true){
                if (imageuriValue !=true){
                    Toast.makeText(ChatActivity.this, "Choose An Image First!", Toast.LENGTH_SHORT).show();
                }if (TextUtils.isEmpty(enteredMessage)){
                    Toast.makeText(ChatActivity.this, "Enter Message First!", Toast.LENGTH_SHORT).show();
                }else {
                    sendNotificationWithImg(imageuri,enteredMessage);
                    closeFunction();
                }
            }

            //if File is clicked
            if (ChooseFileValue == true){
                if (FileValue !=true){
                    Toast.makeText(ChatActivity.this, "Choose An File First!", Toast.LENGTH_SHORT).show();
                }if (TextUtils.isEmpty(enteredMessage)){
                    Toast.makeText(ChatActivity.this, "Enter Message First!", Toast.LENGTH_SHORT).show();
                }else {
                    sendNotificationWithfile(fileuri,enteredMessage,displayName);
                    closeFunction();
                }
            }

            if (ChooseFileValue != true && ChooseImgValue != true){
                if (TextUtils.isEmpty(enteredMessage)){
                    Toast.makeText(ChatActivity.this, "Enter Message First!", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendNotification(enteredMessage);
                    closeFunction();
                }
            }
        });
    }

    private void sendNotification(String enteredMessage) {
        Date date= new Date();
        currenttime = simpleDateFormat.format(calendar.getTime());
        Date DateAndTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sentdate = dateFormat.format(DateAndTime);
        Messages messages = new Messages(enteredMessage,firebaseAuth.getUid(),null,null,null,date.getTime(),currenttime,sentdate);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("chats")
                .child(senderroom)
                .child("Messages")
                .push().setValue(messages)
                .addOnCompleteListener(task -> firebaseDatabase.getReference()
                        .child("chats")
                        .child(recieverroom)
                        .child("Messages")
                        .push().setValue(messages)
                        .addOnCompleteListener(task1 -> {
                        }));
        closeFunction();
        MessageInput.setText(null);
    }

    private void sendNotificationWithfile(Uri fileuri, String enteredMessage, String displayName) {
        StorageReference reference = storageReference.child("Chats/"
                +senderroom +"/Messages/"+System.currentTimeMillis()+"/"+displayName);
        reference.putFile(fileuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        fetchedFileUri = uriTask.getResult();
                        storeDataWithFile(fetchedFileUri,enteredMessage,displayName );

                    }
                });
    }

    private void storeDataWithFile(Uri fetchedFileUri, String enteredMessage, String displayName) {

        Date date= new Date();
        currenttime = simpleDateFormat.format(calendar.getTime());
        Date DateAndTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sentdate = dateFormat.format(DateAndTime);
        Messages messages = new Messages(enteredMessage,firebaseAuth.getUid(),displayName,String.valueOf(fetchedFileUri),null,date.getTime(),currenttime,sentdate);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("chats")
                .child(senderroom)
                .child("Messages")
                .push().setValue(messages)
                .addOnCompleteListener(task -> firebaseDatabase.getReference()
                        .child("chats")
                        .child(recieverroom)
                        .child("Messages")
                        .push().setValue(messages)
                        .addOnCompleteListener(task1 -> {
                        }));
        closeFunction();
        MessageInput.setText(null);
    }

    private void sendNotificationWithImg(Uri imageuri, String enteredMessage) {
        //upload image to firbase storage

        StorageReference reference = storageReference.child("Chats/"
                +senderroom +"/Messages/"+System.currentTimeMillis()+"/Message.jpg");

        reference.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        storeDataWithImg(uri,enteredMessage);
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

    private void storeDataWithImg(Uri uri, String enteredMessage) {
        Date date= new Date();
        currenttime = simpleDateFormat.format(calendar.getTime());
        Date DateAndTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sentdate = dateFormat.format(DateAndTime);
        Messages messages = new Messages(enteredMessage,firebaseAuth.getUid(),null,
                null,String.valueOf(uri),date.getTime(),currenttime,sentdate);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("chats")
                .child(senderroom)
                .child("Messages")
                .push().setValue(messages)
                .addOnCompleteListener(task -> firebaseDatabase.getReference()
                        .child("chats")
                        .child(recieverroom)
                        .child("Messages")
                        .push().setValue(messages)
                        .addOnCompleteListener(task1 -> {
                        }));
        closeFunction();
        MessageInput.setText(null);
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

    //on start fetch user profile and notify dataset changed
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onStart() {
        super.onStart();

        UserUri = getIntent().getStringExtra("UserUri");
        Glide.with(getApplicationContext())
                .load(UserUri)
                .placeholder(R.drawable.cardview_person_icon)
                .into(UserImage);

        messagesAdapter.notifyDataSetChanged();
        YoYo.with(Techniques.BounceInLeft)
                .duration(1000)
                .playOn(messageRecycler);
    }

    //if message messagesAdapter is not equal to null then notifyDataSetChanged
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onStop() {
        super.onStop();
        if (messagesAdapter!=null){
            messagesAdapter.notifyDataSetChanged();
        }
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
}