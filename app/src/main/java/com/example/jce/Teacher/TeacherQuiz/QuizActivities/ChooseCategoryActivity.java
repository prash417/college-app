package com.example.jce.Teacher.TeacherQuiz.QuizActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jce.Teacher.TeacherQuiz.AdapterClasses.CategoryAdapter;
import com.example.jce.Teacher.TeacherQuiz.ModelClasses.CategoryModel;
import com.example.jce.R;
import com.example.jce.Teacher.TeacherHome.TeacherHomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChooseCategoryActivity extends AppCompatActivity {

    //declaration
    FirebaseDatabase database;
    FirebaseStorage storage;
    CircleImageView Category_Image;
    EditText CategoryName;
    Button CategorySaveButton;
    Dialog dialog;
    Uri imageUri;
    String enteredCategory;
    TextView CategoryNameMsg;
    int i = 0;
    ProgressDialog progressDialog;
    ImageView AddCategoryBtn;

    ArrayList<CategoryModel> categoryModelArrayList;
    CategoryAdapter categoryAdapter;
    RecyclerView categoryRecycler;
    ImageView backBtn;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        //find id
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        AddCategoryBtn = findViewById(R.id.AddCategoryBtn);
        categoryRecycler = findViewById(R.id.categoryRecycler);
        backBtn = findViewById(R.id.backBtn);

        categoryModelArrayList = new ArrayList<>();

        dialog = new Dialog(ChooseCategoryActivity.this);
        dialog.setContentView(R.layout.category_add_dialog);

        if (dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
        }

        progressDialog = new ProgressDialog(ChooseCategoryActivity.this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please Wait");

        CategorySaveButton = dialog.findViewById(R.id.CategorySaveButton);
        Category_Image = dialog.findViewById(R.id.Category_Image);
        CategoryName = dialog.findViewById(R.id.CategoryName);
        CategoryNameMsg = dialog.findViewById(R.id.CategoryNameMsg);

        GridLayoutManager layoutManager = new GridLayoutManager(ChooseCategoryActivity.this,2);
        categoryRecycler.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(ChooseCategoryActivity.this,categoryModelArrayList);
        categoryRecycler.setAdapter(categoryAdapter);


        //fetch Categories
        fetchCategories();

        //AddCategoryBtn
        AddCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        //jump to phone memory
        Category_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        //text watcher on CategoryName
        CategoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enteredCategory = CategoryName.getText().toString().trim();
                if (TextUtils.isEmpty(enteredCategory)){
                    CategoryNameMsg.setVisibility(View.VISIBLE);
                    CategoryNameMsg.setText("Category Name Is Required!");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(CategoryName);
                    CategoryName.setBackgroundResource(R.drawable.custom_edittext_wrong);
                }else {
                    CategoryNameMsg.setVisibility(View.GONE);
                    CategoryName.setBackgroundResource(R.drawable.custom_edittext_right);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //save Category
        CategorySaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredCategory = CategoryName.getText().toString().trim();
                if (imageUri ==null){
                    Toast.makeText(ChooseCategoryActivity.this, "Upload Category Image ", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(enteredCategory)) {
                    CategoryNameMsg.setVisibility(View.VISIBLE);
                    CategoryNameMsg.setText("Category Name Is Required!");
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(CategoryName);
                    CategoryName.setBackgroundResource(R.drawable.custom_edittext_wrong);
                }else {
                    progressDialog.show();
                    CategoryName.setBackgroundResource(R.drawable.custom_edittext_right);
                    UploadData(enteredCategory);
                }
            }
        });

        //back button is pressed
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

//    fetchCategories
    private void fetchCategories() {
        database.getReference().child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    categoryModelArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        CategoryModel model = dataSnapshot.getValue(CategoryModel.class);
//                        categoryModelArrayList.add(model);

                        categoryModelArrayList.add(new CategoryModel(
                                dataSnapshot.child("categoryName").getValue().toString(),
                                dataSnapshot.child("categoryImage").getValue().toString(),
                                dataSnapshot.getKey(),
                                Integer.parseInt(dataSnapshot.child("setNum").getValue().toString())
                        ));

                    }
                    categoryAdapter.notifyDataSetChanged();
                    YoYo.with(Techniques.Pulse)
                            .duration(1000)
                            .playOn(categoryRecycler);
                }else {
                    Toast.makeText(ChooseCategoryActivity.this, "Categories Not Exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Toast.makeText(ChooseCategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //upload Category
    private void UploadData(String enteredCategory) {
        final StorageReference reference = storage.getReference().child("Category")
                .child(String.valueOf(System.currentTimeMillis()));
        reference.putFile(this.imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        CategoryModel categoryModel = new CategoryModel();
                        categoryModel.setCategoryName(enteredCategory);
                        categoryModel.setSetNum(0);
                        categoryModel.setCategoryImage(uri.toString());

                        database.getReference().child("Categories").child(enteredCategory)
                                .setValue(categoryModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(ChooseCategoryActivity.this, "Category Saved", Toast.LENGTH_SHORT).show();
                                        categoryModelArrayList.clear();
                                        categoryAdapter.notifyDataSetChanged();
                                        fetchCategories();
                                        closeFunction();
                                        progressDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ChooseCategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                });
            }
        });
    }

    //reset img and link
    private void closeFunction() {
        CategoryNameMsg.setVisibility(View.GONE);
        CategoryName.setText("");
        imageUri = null;
        Glide.with(getApplicationContext())
                .load(imageUri)
                .placeholder(R.drawable.add_photo_icon)
                .into(Category_Image);
        dialog.dismiss();
    }

    //fetch img
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (data != null){
                imageUri = data.getData();
                Glide.with(getApplicationContext())
                        .load(imageUri)
                        .placeholder(R.drawable.add_photo_icon)
                        .into(Category_Image);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), TeacherHomeActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        YoYo.with(Techniques.Pulse)
                .duration(1000)
                .playOn(categoryRecycler);
    }
}