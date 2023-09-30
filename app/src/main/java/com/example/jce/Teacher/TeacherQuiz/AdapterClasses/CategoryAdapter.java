package com.example.jce.Teacher.TeacherQuiz.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jce.Teacher.TeacherQuiz.ModelClasses.CategoryModel;
import com.example.jce.Teacher.TeacherQuiz.QuizActivities.SetsActivity;
import com.example.jce.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {


    Context context;
    ArrayList<CategoryModel> categoryModelArrayList;


    public CategoryAdapter(Context context, ArrayList<CategoryModel> categoryModelArrayList) {
        this.context = context;
        this.categoryModelArrayList = categoryModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View CView = LayoutInflater.from(context).inflate(R.layout.item_category,parent,false);

        return new ViewHolder(CView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel categoryModel = categoryModelArrayList.get(position);
        holder.CategoryName.setText(categoryModel.getCategoryName());
        Glide.with(context)
                .load(categoryModel.getCategoryImage())
                .placeholder(R.drawable.add_photo_icon)
                .into(holder.Category_Image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SetsActivity.class);
                intent.putExtra("CategoryName",categoryModel.getCategoryName());
                intent.putExtra("sets",categoryModel.getSetNum());
                intent.putExtra("key",categoryModel.getKey());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelArrayList.size();
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
