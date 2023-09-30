package com.example.jce.Student.StudentFragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.R;
import com.example.jce.Users.Login.LoginActivity;
import com.example.jce.Users.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class Student_Account_Fragment extends Fragment {

    //declaration
    Button logout,editProfile,changePassd,changeuserprofile;
    TextView name,email,phono,usn,branch;
    ImageView profileImage;
    LottieAnimationView animationView;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String userID,fuserEmail,fuserpassd,fusername,fuserphone,fuserusn,fuserbranch,femail;
    StorageReference storageReference;
    String uri,fUSN;

    Dialog cPassddialog,eProfileDialog;

    private List<String> usnList = new ArrayList<>();
    private List<String> RegusnList = new ArrayList<>();
    private List<String> RegEmailList = new ArrayList<>();

    //pattern for password
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[a-zA-Z])" +      //Any Letter
                    "(?=.*[@#$%^&+=])" +    //At Least 1 Special Character
                    "(?=\\S+$)" +           //No White Spaces
                    ".{4,}" +               //At Least 4 Characters
                    "$");

    Boolean nameValue = false;
    Boolean phoneValue = false;
    Boolean usnValue = false;
    Boolean branchValue = false;
    Boolean emailValue = false;
    View SAView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SAView = inflater.inflate(R.layout.fragment_student__account_, container, false);

        //find id
        logout =SAView.findViewById(R.id.logout);
        changePassd = SAView.findViewById(R.id.ChangePassword);
        name = SAView.findViewById(R.id.name);
        email = SAView.findViewById(R.id.email);
        phono = SAView.findViewById(R.id.phone);
        usn = SAView.findViewById(R.id.UserUSN);
        branch = SAView.findViewById(R.id.Userbranch);
        profileImage = SAView.findViewById(R.id.profile_image);
        editProfile = SAView.findViewById(R.id.editProfile);
        changeuserprofile = SAView.findViewById(R.id.changeUserImg);
        animationView = SAView.findViewById(R.id.animationView);


        //start animation
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();


        //fetch data  ony if user is logged  in
        if (FirebaseAuth.getInstance().getCurrentUser() !=null) {
            firebaseAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
            userID = firebaseAuth.getCurrentUser().getUid();
            storageReference = FirebaseStorage.getInstance().getReference();
            fetchuserdetails();
            storeuserprofile();
        }

        //logout current user
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Logged Out!", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });

        //change user profile fetch from memory
        changeuserprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationView.playAnimation();
                animationView.setVisibility(View.VISIBLE);
                //open gallery
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,1000);
            }
        });

        //change password
        changePassd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassdemailsent();
            }
        });

        //edit profile
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetch regEmail
                firestore.collection("users").addSnapshotListener
                        (new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    RegEmailList.clear();
                                    for (DocumentSnapshot snapshot : value) {
                                        RegEmailList.add(snapshot.getString("useremail"));
                                    }
                                }
                            }
                        });
                editProfilepopup();
            }
        });

        return SAView;
    }


    //edit profile page
    private void editProfilepopup() {

        eProfileDialog = new Dialog(getContext());
        eProfileDialog.setContentView(R.layout.student_edit_profile_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            eProfileDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.custom_box_bg));
        }


        EditText name = eProfileDialog.findViewById(R.id.username);
        EditText phone = eProfileDialog.findViewById(R.id.number);
        EditText usn = eProfileDialog.findViewById(R.id.eUsn);
        EditText branch = eProfileDialog.findViewById(R.id.branch);
        EditText email = eProfileDialog.findViewById(R.id.useremail);
        Button saveData = eProfileDialog.findViewById(R.id.saveData);
        Button cancelBtn = eProfileDialog.findViewById(R.id.cancelbtn);

        TextView errorUSNMsg = eProfileDialog.findViewById(R.id.errorUSNMsg);
        TextView errorBranchMsg = eProfileDialog.findViewById(R.id.errorBranchMsg);
        TextView errorMobileMsg = eProfileDialog.findViewById(R.id.errorMobileMsg);
        TextView errorNameMsg = eProfileDialog.findViewById(R.id.errorNameMsg);
        TextView errorEmailMsg = eProfileDialog.findViewById(R.id.errorEmailMsg);
        TextView errorCheckBoxMsg = eProfileDialog.findViewById(R.id.errorCheckBoxMsg);

        errorUSNMsg.setText("USN Is Required!");
        errorBranchMsg.setText("Branch Is Required!");
        errorNameMsg.setText("Name Is Required!");
        errorCheckBoxMsg.setText("Select Any One Check Box");

        CheckBox emailchange = eProfileDialog.findViewById(R.id.teacher);
        CheckBox datachange = eProfileDialog.findViewById(R.id.student);
        LinearLayout emailChangelayout = eProfileDialog.findViewById(R.id.emaillayout);
        LinearLayout dataChangelayout = eProfileDialog.findViewById(R.id.userDataLayout);
        LinearLayout checkBoxs = eProfileDialog.findViewById(R.id.checkBoxs);


        //teacher is checked
        emailchange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //check any account is selected
                if (!(datachange.isChecked() || emailchange.isChecked())){
                    errorCheckBoxMsg.setVisibility(View.VISIBLE);
                }else {
                    errorCheckBoxMsg.setVisibility(View.GONE);
                }

                if (emailchange.isChecked()){
                    datachange.setChecked(false);
                    errorCheckBoxMsg.setVisibility(View.GONE);
                    dataChangelayout.setVisibility(View.GONE);
                    emailChangelayout.setVisibility(View.VISIBLE);

                }else {
                    errorCheckBoxMsg.setVisibility(View.VISIBLE);
                    dataChangelayout.setVisibility(View.GONE);
                    emailChangelayout.setVisibility(View.GONE);
                }
            }
        });

        //student is checked
        datachange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //check any account is selected
                if (!(datachange.isChecked() || emailchange.isChecked())){
                    errorCheckBoxMsg.setVisibility(View.VISIBLE);
                }else {
                    errorCheckBoxMsg.setVisibility(View.GONE);
                }

                if (datachange.isChecked()){
                    emailchange.setChecked(false);
                    errorCheckBoxMsg.setVisibility(View.GONE);
                    dataChangelayout.setVisibility(View.VISIBLE);
                    emailChangelayout.setVisibility(View.GONE);
                }else {
                    errorCheckBoxMsg.setVisibility(View.VISIBLE);
                    dataChangelayout.setVisibility(View.GONE);
                    emailChangelayout.setVisibility(View.GONE);
                }
            }
        });

        //name text watcher
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fName = name.getText().toString().trim();
                if (TextUtils.isEmpty(fName)){
                    errorNameMsg.setVisibility(View.VISIBLE);
                    name.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    nameValue = false;
                }else {
                    errorNameMsg.setVisibility(View.GONE);
                    name.setBackgroundResource(R.drawable.custom_edittext_right);
                    nameValue = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //phone text watcher
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fPhone = phone.getText().toString().trim();

                if (TextUtils.isEmpty(fPhone)){
                    errorMobileMsg.setVisibility(View.VISIBLE);
                    errorMobileMsg.setText("Phone Number Is Required!");
                    phone.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    phoneValue = false;
                }
                else if (fPhone.length()<10){
                    errorMobileMsg.setVisibility(View.VISIBLE);
                    errorMobileMsg.setText("Please Enter Correct Number");
                    phone.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    phoneValue = false;
                }else {
                    errorMobileMsg.setVisibility(View.GONE);
                    phone.setBackgroundResource(R.drawable.custom_edittext_right);
                    phoneValue = true;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //branch text watcher
        branch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fBranch = branch.getText().toString().trim();
                if (TextUtils.isEmpty(fBranch)){
                    errorBranchMsg.setVisibility(View.VISIBLE);
                    branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    branchValue = false;
                }else {
                    errorBranchMsg.setVisibility(View.GONE);
                    branch.setBackgroundResource(R.drawable.custom_edittext_right);

                    firestore.collection(fBranch+"USN").addSnapshotListener
                            (new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        usnList.clear();
                                        for (DocumentSnapshot snapshot : value) {
                                            usnList.add(snapshot.getString("SUsn"));
                                        }
                                        if (usnList.isEmpty()){
                                            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(getContext(), usnList.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                    //fetch regusn
                    firestore.collection("users").addSnapshotListener
                            (new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        RegusnList.clear();
                                        for (DocumentSnapshot snapshot : value) {
                                            RegusnList.add(snapshot.getString("usn"));
                                        }
                                    }
                                }
                            });

                    branchValue = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //usn text watcher
        usn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fUSN = usn.getText().toString().trim();
                if (TextUtils.isEmpty(fUSN)){
                    errorUSNMsg.setVisibility(View.VISIBLE);
                    errorUSNMsg.setText("USN Is Required!");
                    usn.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    usnValue = false;
                }
                else if (!usnList.contains(fUSN)) {
                    errorUSNMsg.setVisibility(View.VISIBLE);
                    errorUSNMsg.setText("USN Is Not Registered! Contact College To Register Your USN");
                    usn.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    usnValue = false;
                }
                else if (RegusnList.contains(fUSN)) {
                    errorUSNMsg.setVisibility(View.VISIBLE);
                    errorUSNMsg.setText("USN Is Already Registered!");
                    usn.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    usnValue = false;
                }else {
                    errorUSNMsg.setVisibility(View.GONE);
                    usn.setBackgroundResource(R.drawable.custom_edittext_right);
                    usnValue = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //email text watcher
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fEmail = email.getText().toString().trim();
                if (TextUtils.isEmpty(fEmail)){
                    errorEmailMsg.setVisibility(View.VISIBLE);
                    errorEmailMsg.setText("Email Is Required!");
                    email.setBackgroundResource(R.drawable.custom_edittext_wrong);
                   emailValue = false;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(fEmail).matches()){
                    errorEmailMsg.setVisibility(View.VISIBLE);
                    errorEmailMsg.setText("Please Enter valid Email!");
                    email.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    emailValue = false;
                }
                else if (fuserEmail.equals(fEmail)) {
                    errorEmailMsg.setVisibility(View.VISIBLE);
                    errorEmailMsg.setText("Same Email Can't Be Used!");
                    email.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    emailValue = false;
                }
                else if (RegEmailList.contains(fEmail)) {
                    errorEmailMsg.setVisibility(View.VISIBLE);
                    errorEmailMsg.setText("Email Is Already Registered!");
                    email.setBackgroundResource(R.drawable.custom_edittext_wrong);
                    emailValue = false;
                }
                else {
                    errorEmailMsg.setVisibility(View.GONE);
                    email.setBackgroundResource(R.drawable.custom_edittext_right);
                    emailValue = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //save data to firebase
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = name.getText().toString().trim();
                String fphone = phone.getText().toString().trim();
                String fbranch = branch.getText().toString().trim();
                String fusn = usn.getText().toString().trim();

                //check any account is selected
                if (!(datachange.isChecked() || emailchange.isChecked())){
                    errorCheckBoxMsg.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(checkBoxs);
                }
                if (emailchange.isChecked()){
                    femail = email.getText().toString().trim();
                    //email validation
                    if (emailValue != true){
                        errorEmailMsg.setVisibility(View.VISIBLE);
                        errorEmailMsg.setText("Email Is Required!");
                        email.setBackgroundResource(R.drawable.custom_edittext_wrong);
                        YoYo.with(Techniques.Shake)
                                .duration(1000)
                                .playOn(email);
                    }
                    else {
                        FirebaseUser cuser = firebaseAuth.getCurrentUser();
                        cuser.updateEmail(femail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getContext(), "Email Updated! verify and login", Toast.LENGTH_SHORT).show();
                                    userID = firebaseAuth.getCurrentUser().getUid();
                                    if (userID != null){
                                        DocumentReference documentReference = firestore.collection("users").document(userID);
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("username", fusername);
                                        user.put("useremail", femail);
                                        user.put("userphno", fuserphone);
                                        user.put("userpassd", fuserpassd);
                                        user.put("usn", fuserusn);
                                        user.put("branch", fuserbranch);
                                        user.put("uri", uri);
                                        user.put("userId", userID);
                                        user.put("isStudent", "1");
                                        documentReference.set(user);
                                        firebaseAuth.signOut();
                                        startActivity(new Intent(getContext(), LoginActivity.class));
                                        getActivity().finish();
                                    }

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                firebaseAuth.signOut();
                                Toast.makeText(getContext(), "By Login Verify Your Identity", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getContext(), LoginActivity.class));
                                getActivity().finish();
                            }
                        });
                    }
                }

                if (datachange.isChecked()){
                    //name validation
                    if (nameValue != true){
                        errorNameMsg.setVisibility(View.VISIBLE);
                        name.setBackgroundResource(R.drawable.custom_edittext_wrong);
                        YoYo.with(Techniques.Shake)
                                .duration(1000)
                                .playOn(name);
                    }

                    //phone validation
                    else if (phoneValue != true){
                        errorMobileMsg.setVisibility(View.VISIBLE);
                        errorMobileMsg.setText("Phone Number Is Required!");
                        phone.setBackgroundResource(R.drawable.custom_edittext_wrong);
                        YoYo.with(Techniques.Shake)
                                .duration(1000)
                                .playOn(phone);
                    }
                    else if (branchValue != true){
                        errorBranchMsg.setVisibility(View.VISIBLE);
                        branch.setBackgroundResource(R.drawable.custom_edittext_wrong);
                        YoYo.with(Techniques.Shake)
                                .duration(1000)
                                .playOn(branch);
                    }
                    else if (usnValue != true) {
                        errorUSNMsg.setVisibility(View.VISIBLE);
                        usn.setBackgroundResource(R.drawable.custom_edittext_wrong);
                        YoYo.with(Techniques.Shake)
                                .duration(1000)
                                .playOn(usn);
                    }
                    else {
                        Toast.makeText(getContext(), "Data Updated!", Toast.LENGTH_SHORT).show();
                        userID = firebaseAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = firestore.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("username", fname);
                        user.put("useremail", fuserEmail);
                        user.put("userphno", fphone);
                        user.put("userpassd", fuserpassd);
                        user.put("usn", fusn);
                        user.put("branch", fbranch);
                        user.put("uri", uri);
                        user.put("userId", userID);
                        user.put("isStudent", "1");
                        documentReference.set(user);

                        eProfileDialog.dismiss();
                    }
                }
            }
        });

//        dismis dialog box
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eProfileDialog.dismiss();
            }
        });

        eProfileDialog.setCancelable(false);
        eProfileDialog.show();
    }

    //edit password
    private void changePassdemailsent() {
        cPassddialog = new Dialog(getContext());
        cPassddialog.setContentView(R.layout.success_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            cPassddialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.custom_box_bg));
        }

        TextView custom_message = cPassddialog.findViewById(R.id.custom_message_text);
        firebaseAuth.sendPasswordResetEmail(fuserEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        custom_message.setText("Reset email sent!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        custom_message.setText("User is not registered!");
                    }
                });


        cPassddialog.setCancelable(true);
        cPassddialog.show();
    }

    //store user profile
    private void storeuserprofile() {
        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                animationView.pauseAnimation();
                animationView.setVisibility(View.GONE);
                if (getActivity() != null) {
                    Glide.with(getContext())
                            .load(uri)
                            .placeholder(R.drawable.person_icon)
                            .into(profileImage);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                animationView.pauseAnimation();
                animationView.setVisibility(View.GONE);
                profileImage.setImageResource(R.drawable.person_icon);
            }
        });
    }
    //fetch user details
    private void fetchuserdetails() {
        DocumentReference df = firestore.collection("users").document(userID);
        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.getString("username"));
                email.setText(value.getString("useremail"));
                phono.setText(value.getString("userphno"));
                usn.setText(value.getString("usn"));
                branch.setText(value.getString("branch"));


                fuserEmail = value.getString("useremail");
                fuserpassd = value.getString("userpassd");
                fuserusn = value.getString("usn");
                fuserbranch = value.getString("branch");
                fusername = value.getString("username");
                fuserphone = value.getString("userphno");
                uri = value.getString("uri");
            }
        });
    }

    //on start fetch user profile
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE);
        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                Uri imageuri = data.getData();

                uploadimagetofirebase(imageuri);
            }
        }
    }

    //upload image to firebase
    private void uploadimagetofirebase(Uri imageuri) {
        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE);
        //upload image to firbase storage
        StorageReference fileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(getContext(), "Profile Changed!", Toast.LENGTH_SHORT).show();
                        fetchuserdetails();
                        storeData(uri);
                        if (getActivity() != null) {
                            Glide.with(getContext())
                                    .load(uri)
                                    .placeholder(R.drawable.person_icon)
                                    .into(profileImage);
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                animationView.pauseAnimation();
                animationView.setVisibility(View.GONE);
            }
        });
    }

    private void storeData(Uri uri) {
        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firestore.collection("users").document(userID);
        Map<String, Object> user = new HashMap<>();
        user.put("username", fusername);
        user.put("useremail", fuserEmail);
        user.put("userphno", fuserphone);
        user.put("userpassd", fuserpassd);
        user.put("usn", fuserusn);
        user.put("branch", fuserbranch);
        user.put("uri",uri);
        user.put("userId", userID);
        user.put("isStudent", "1");
        documentReference.set(user);
    }

    //on start fetch image
    @Override
    public void onStart() {
        super.onStart();
        //store user profile
        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                animationView.pauseAnimation();
                animationView.setVisibility(View.GONE);
                if (getActivity() != null) {
                    Glide.with(getContext())
                            .load(uri)
                            .placeholder(R.drawable.person_icon)
                            .into(profileImage);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                animationView.pauseAnimation();
                animationView.setVisibility(View.GONE);
                profileImage.setImageResource(R.drawable.person_icon);
            }
        });
    }
}