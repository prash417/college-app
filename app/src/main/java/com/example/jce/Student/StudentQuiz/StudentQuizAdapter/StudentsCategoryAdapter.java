package com.example.jce.Student.StudentQuiz.StudentQuizAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jce.R;
import com.example.jce.Student.StudentQuiz.StudentQuizActivities.StudentSetsActivity;
import com.example.jce.Student.StudentQuiz.StudentQuizModels.StudentCategoryModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentsCategoryAdapter extends RecyclerView.Adapter<StudentsCategoryAdapter.ViewHolder> {


    Context context;
    ArrayList<StudentCategoryModel> studentCategoryModelArrayList;


    public StudentsCategoryAdapter(Context context, ArrayList<StudentCategoryModel> studentCategoryModelArrayList) {
        this.context = context;
        this.studentCategoryModelArrayList = studentCategoryModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View CView = LayoutInflater.from(context).inflate(R.layout.item_category,parent,false);

        return new ViewHolder(CView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentCategoryModel studentCategoryModel = studentCategoryModelArrayList.get(position);
        holder.CategoryName.setText(studentCategoryModel.getCategoryName());
        Glide.with(context)
                .load(studentCategoryModel.getCategoryImage())
                .placeholder(R.drawable.add_photo_icon)
                .into(holder.Category_Image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StudentSetsActivity.class);
                intent.putExtra("CategoryName",studentCategoryModel.getCategoryName());
                intent.putExtra("sets",studentCategoryModel.getSetNum());
                intent.putExtra("key",studentCategoryModel.getKey());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentCategoryModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //declaration
        CircleImageView Category_Image;
        TextView CategoryName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //find id
            Category_Image = itemView.findViewById(R.id.Category_Image);
            CategoryName = itemView.findViewById(R.id.CategoryName);

        }
    }
}
