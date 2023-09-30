package com.example.jce.Notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
import com.example.jce.Teacher.TeacherHome.TeacherHomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class NotesUploadActivity extends AppCompatActivity {

    //declaration
    EditText Branch,FetchedFile;
    TextView branchmsg,semestermsg,FileErrormsg;
    AutoCompleteTextView filled_exposed;

    ImageView backBtn;

    Boolean SemNoValue = false;
    Boolean FileValue = false;
    private List<String> semNoList = new ArrayList<>();
    HashSet<String> semNoHashSet = new HashSet<>();
    ArrayAdapter<String> semNoAdapter;
    FirebaseFirestore firestore;

    String selSemNo,enteredBranch,fetchedFileName,fusername;

    FirebaseAuth firebaseAuth;
    String userID;

    Button UploadFile,RetrieveFile;

    Uri uri;

    StorageReference storageReference;
    DatabaseReference databaseReference;

    LottieAnimationView Uploading;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_upload);

        //find id
        Branch = findViewById(R.id.Branch);
        FetchedFile = findViewById(R.id.FetchedFile);
        filled_exposed = findViewById(R.id.filled_exposed);
        branchmsg = findViewById(R.id.branchmsg);
        semestermsg = findViewById(R.id.semestermsg);
        FileErrormsg = findViewById(R.id.FileErrormsg);
        UploadFile = findViewById(R.id.UploadFile);
        RetrieveFile = findViewById(R.id.RetrieveFile);
        backBtn = findViewById(R.id.backBtn);
        Uploading = findViewById(R.id.Uploading);


        //if user is logged in then only fetch data
        if (FirebaseAuth.getInstance().getCurrentUser() !=null) {
            firebaseAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
            userID = firebaseAuth.getCurrentUser().getUid();
            storageReference = FirebaseStorage.getInstance().getReference();
            fetchuserdetails();
        }


        //back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //fetch student usn and subcode based on Branch
        Branch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String FBranch = Branch.getText().toString().trim();

                if (TextUtils.isEmpty(FBranch)){
                    branchmsg.setVisibility(View.VISIBLE);
                    branchmsg.setText("Branch Name Is Required!");
                    Branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
                }else {
                    branchmsg.setVisibility(View.GONE);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);

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
                            semNoAdapter = new ArrayAdapter<String>(NotesUploadActivity.this,R.layout.list_items,semNoList);
                            //setting adapter
                            filled_exposed.setAdapter(semNoAdapter);
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //text watcher for FetchedFile edit text
        FetchedFile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchedFileName = FetchedFile.getText().toString().trim();

                if (TextUtils.isEmpty(fetchedFileName)){
                    FileErrormsg.setVisibility(View.VISIBLE);
                    FileErrormsg.setText("File  Is Required!");
                    FetchedFile.setBackgroundResource(R.drawable.custom_edittext_wrong);
                }else {
                    FileErrormsg.setVisibility(View.GONE);
                    FetchedFile.setBackgroundResource(R.drawable.custom_edittext_right);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

        //on click of edit text
        FetchedFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });

        //upload file to firebase
        UploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uploading.setVisibility(View.VISIBLE);
                Uploading.playAnimation();
                enteredBranch = Branch.getText().toString().trim();

                if (TextUtils.isEmpty(enteredBranch)){
                    branchmsg.setVisibility(View.VISIBLE);
                    branchmsg.setText("Branch Name Is Required!");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(Branch);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    Uploading.setVisibility(View.GONE);
                    Uploading.pauseAnimation();
                }else if (SemNoValue !=true){
                    semestermsg.setVisibility(View.VISIBLE);
                    semestermsg.setText("Sem Number Is Required!");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(filled_exposed);
                    filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    Uploading.setVisibility(View.GONE);
                    Uploading.pauseAnimation();
                }else if (FileValue !=true){
                    FileErrormsg.setVisibility(View.VISIBLE);
                    FileErrormsg.setText("File  Is Required!");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(FetchedFile);
                    FetchedFile.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    Uploading.setVisibility(View.GONE);
                    Uploading.pauseAnimation();
                }else {
                    storageReference = FirebaseStorage.getInstance().getReference();
                    databaseReference = FirebaseDatabase.getInstance().getReference(enteredBranch);
                    uploadPDF(uri);
                }
            }
        });

//        RetrieveFile from firebase
        RetrieveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uploading.setVisibility(View.VISIBLE);
                Uploading.playAnimation();
                enteredBranch = Branch.getText().toString().trim();

                if (TextUtils.isEmpty(enteredBranch)){
                    branchmsg.setVisibility(View.VISIBLE);
                    branchmsg.setText("Branch Name Is Required!");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(Branch);
                    Branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    Uploading.setVisibility(View.GONE);
                    Uploading.pauseAnimation();
                }else if (SemNoValue !=true){
                    semestermsg.setVisibility(View.VISIBLE);
                    semestermsg.setText("Sem Number Is Required!");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(filled_exposed);
                    filled_exposed.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    Uploading.setVisibility(View.GONE);
                    Uploading.pauseAnimation();
                }else {
                    Uploading.setVisibility(View.GONE);
                    Uploading.pauseAnimation();
                    Branch.setBackgroundResource(R.drawable.custom_edittext_right);
                    filled_exposed.setBackgroundResource(R.drawable.custom_edittext_right);
                    Intent intent = new Intent(getApplicationContext(), NotesRetrieveActivity.class);
                    intent.putExtra("Branch", enteredBranch);
                    intent.putExtra("SemNo", selSemNo);
                    startActivity(intent);
                }
            }
        });

    }

    private void fetchuserdetails() {
        DocumentReference df = firestore.collection("users").document(userID);
        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                fusername = value.getString("username");
            }
        });
    }

    //upload pdf function
    private void uploadPDF(Uri uri) {
         StorageReference reference = storageReference.child(enteredBranch+"/Notes/"
                +selSemNo+"/"+System.currentTimeMillis() +".pdf");
        reference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri uri = uriTask.getResult();

                        NotesModel notesModel = new NotesModel(FetchedFile.getText().toString(),uri.toString(),fusername);
                        databaseReference.child("Notes")
                                .child(selSemNo)
                                .push()
                                .setValue(notesModel);
                        Toast.makeText(NotesUploadActivity.this, "Notes Uploaded", Toast.LENGTH_SHORT).show();
                        FetchedFile.setText(null);
                        uri = null;
                        FileValue = false;
                        FileErrormsg.setVisibility(View.GONE);
                        Uploading.setVisibility(View.GONE);
                        FetchedFile.setBackgroundResource(R.drawable.custom_edittext_right);
                        Uploading.pauseAnimation();
                    }
                });
    }

    //select pdf from storage
    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF Files"),101);
    }

    //fetch file and file name
    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data !=null && data.getData() !=null){
             uri = data.getData();

            //to fetch pdf file name
            String uriString = uri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;

            if (uriString.startsWith("content://")){
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(uri,null,null,null,null);
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
            FetchedFile.setText(displayName);

        }
    }

    //on back pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), TeacherHomeActivity.class));
        finish();
    }

}