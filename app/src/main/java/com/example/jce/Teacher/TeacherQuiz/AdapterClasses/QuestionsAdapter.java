package com.example.jce.Teacher.TeacherQuiz.AdapterClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jce.Teacher.TeacherQuiz.ModelClasses.QuestionModel;
import com.example.jce.R;

import java.util.ArrayList;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.viewHolder> {

    Context context;
    ArrayList<QuestionModel> questionModelArrayList;
    String categoryName;
    DeleteListener listener;

    public QuestionsAdapter(Context context, ArrayList<QuestionModel> questionModelArrayList, String categoryName, DeleteListener listener) {
        this.context = context;
        this.questionModelArrayList = questionModelArrayList;
        this.categoryName = categoryName;
        this.listener = listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.item_questions,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {

        QuestionModel model = questionModelArrayList.get(position);
        holder.question.setText(model.getQuestion());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLongClick(position,model.getKey());
            }
        });

    }

    @Override
    public int getItemCount() {
        return questionModelArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView question;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.question);
        }
    }
    public interface DeleteListener{
        public void onLongClick(int position,String ID);
    }
}
