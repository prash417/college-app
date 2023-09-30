package com.example.jce.Student.StudentMessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jce.Chat.ChatActivity;
import com.example.jce.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentTeacherAdapter extends RecyclerView.Adapter<StudentTeacherAdapter.myViewHolder> {


    private Context context;
    ArrayList<StudentTeacherModel> studentTeacherModelArrayList;


    public StudentTeacherAdapter(Context context, ArrayList<StudentTeacherModel> studentTeacherModelArrayList) {
        this.context = context;
        this.studentTeacherModelArrayList = studentTeacherModelArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View sview = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_display,parent,false);

        return new myViewHolder(sview);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.teacherName.setText(studentTeacherModelArrayList.get(position).getUsername());
        Glide.with(context)
                .load(studentTeacherModelArrayList.get(position).getUri())
                .placeholder(R.drawable.person_icon)
                .into(holder.UserImage);

        holder.imaUri = studentTeacherModelArrayList.get(position).getUri();
        holder.rUID = studentTeacherModelArrayList.get(position).getUserId();

    }

    @Override
    public int getItemCount() {
        return studentTeacherModelArrayList.size();
    }

    public void filteredList(ArrayList<StudentTeacherModel> filtereList) {
        studentTeacherModelArrayList = filtereList;
        notifyDataSetChanged();
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        EditText teacherName;
        CircleImageView UserImage;
        String imaUri,teacherUsername,rUID;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            teacherName = itemView.findViewById(R.id.teacherFetchName);
            UserImage = itemView.findViewById(R.id.UserImage);

            //on click of list item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teacherUsername = teacherName.getText().toString().trim();
                    onclick(teacherUsername,imaUri,rUID);
                }
            });

            //on click of teacher Name
            teacherName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teacherUsername = teacherName.getText().toString().trim();
                    onclick(teacherUsername,imaUri,rUID);
                }
            });

            //on click of teacher Image
            UserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teacherUsername = teacherName.getText().toString().trim();
                    onclick(teacherUsername,imaUri,rUID);
                }
            });
        }
    }

    //on click function
    private void onclick(String teacherUsername, String imaUri, String rUID) {
        Intent intent = new Intent();
        intent.setClass(context, ChatActivity.class);
        intent.putExtra("userName",teacherUsername);
        intent.putExtra("UserUri",imaUri);
        intent.putExtra("receiverUID",rUID);
        ((Activity) context).startActivity(intent);
    }
}
