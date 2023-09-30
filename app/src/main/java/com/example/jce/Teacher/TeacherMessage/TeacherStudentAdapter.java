package com.example.jce.Teacher.TeacherMessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jce.Chat.ChatActivity;
import com.example.jce.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherStudentAdapter extends RecyclerView.Adapter<TeacherStudentAdapter.myViewHolder> {


    private Context context;
    ArrayList<TeacherStudentsModel> teacherStudentsModelArrayList;


    public TeacherStudentAdapter(Context context, ArrayList<TeacherStudentsModel> teacherStudentsModelArrayList) {
        this.context = context;
        this.teacherStudentsModelArrayList = teacherStudentsModelArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View sview = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_display,parent,false);

        return new myViewHolder(sview);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.username.setText(teacherStudentsModelArrayList.get(position).getUsername());
        holder.usn.setText(teacherStudentsModelArrayList.get(position).getUsn());
        holder.branch.setText("("+teacherStudentsModelArrayList.get(position).getBranch()+")");
        Glide.with(context)
                .load(teacherStudentsModelArrayList.get(position).getUri())
                .placeholder(R.drawable.person_icon)
                .into(holder.UserImage);
        holder.imgUri = teacherStudentsModelArrayList.get(position).getUri();
        holder.rUID = teacherStudentsModelArrayList.get(position).getUserId();
    }

    @Override
    public int getItemCount() {
        return teacherStudentsModelArrayList.size();
    }

    public void filteredList(ArrayList<TeacherStudentsModel> filtereList) {
        teacherStudentsModelArrayList = filtereList;
        notifyDataSetChanged();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        EditText username,usn,branch;
        CircleImageView UserImage;
        String imgUri,studentUsername,studentUsn,rUID;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.studentName);
            usn = itemView.findViewById(R.id.studentUSN);
            branch = itemView.findViewById(R.id.studentBranch);
            UserImage = itemView.findViewById(R.id.UserImage);

            //on click of list item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     studentUsername = username.getText().toString().trim();
                     studentUsn = usn.getText().toString().trim();
                     onclick(studentUsername,studentUsn,imgUri,rUID);

                }
            });

            //on click of student name
            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    studentUsername = username.getText().toString().trim();
                    studentUsn = usn.getText().toString().trim();
                    onclick(studentUsername,studentUsn,imgUri,rUID);
                }
            });

            //on click of student usn
            usn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    studentUsername = username.getText().toString().trim();
                    studentUsn = usn.getText().toString().trim();
                    onclick(studentUsername,studentUsn,imgUri,rUID);
                }
            });

            //on click of student Branch
            branch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    studentUsername = username.getText().toString().trim();
                    studentUsn = usn.getText().toString().trim();
                    onclick(studentUsername,studentUsn,imgUri,rUID);
                }
            });

            //on click of student image
            UserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    studentUsername = username.getText().toString().trim();
                    studentUsn = usn.getText().toString().trim();
                    onclick(studentUsername,studentUsn,imgUri,rUID);
                }
            });
        }
    }

    private void onclick(String studentUsername, String studentUsn, String imgUri, String rUID) {
        Intent intent = new Intent();
        intent.setClass(context, ChatActivity.class);
        intent.putExtra("userName",studentUsername);
        intent.putExtra("UserUri",imgUri);
        intent.putExtra("receiverUID",rUID);
        ((Activity) context).startActivity(intent);
    }
}
