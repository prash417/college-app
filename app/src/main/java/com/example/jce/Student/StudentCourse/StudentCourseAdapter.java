package com.example.jce.Student.StudentCourse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jce.AboutUs.OurHistory.OurHistoryAdapter;
import com.example.jce.AboutUs.OurHistory.OurHistoryModel;
import com.example.jce.R;

import java.util.ArrayList;

public class StudentCourseAdapter extends RecyclerView.Adapter<StudentCourseAdapter.myViewHolder>{

    private Context context;
    ArrayList<StudentCourseModel> studentCourseModelArrayList;


    public StudentCourseAdapter(Context context, ArrayList<StudentCourseModel> studentCourseModelArrayList) {
        this.context = context;
        this.studentCourseModelArrayList = studentCourseModelArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View CourseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.courseslayout,parent,false);

        return new myViewHolder(CourseView);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.label.setText(studentCourseModelArrayList.get(position).getLabel());
        holder.uri = studentCourseModelArrayList.get(position).getUri();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(studentCourseModelArrayList.get(position).getUri()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentCourseModelArrayList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        //declaration
        TextView label;
        String uri;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            label = itemView.findViewById(R.id.label);

        }
    }
}
