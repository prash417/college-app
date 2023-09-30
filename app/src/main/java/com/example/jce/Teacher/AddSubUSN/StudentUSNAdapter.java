package com.example.jce.Teacher.AddSubUSN;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StudentUSNAdapter extends RecyclerView.Adapter<StudentUSNAdapter.myViewHolder> {

    ArrayList<StudentUSNModel> studentUSNModelArrayList;

    private SubjectAdapter.OnItemClickListener listener;

    public void filteredList(ArrayList<StudentUSNModel> filtereList) {
        studentUSNModelArrayList = filtereList;
        notifyDataSetChanged();
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }


    public void setOnItemClickListener(SubjectAdapter.OnItemClickListener clickListener){
        listener = clickListener;
    }


    public StudentUSNAdapter(ArrayList<StudentUSNModel> studentUSNModelArrayList) {
        this.studentUSNModelArrayList = studentUSNModelArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_usn_view,parent,false);


        return new myViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.BranchName.setText(studentUSNModelArrayList.get(position).getSBranch());
        holder.StudentUSN.setText(studentUSNModelArrayList.get(position).getSUsn());
        holder.semNoDisplay.setText(studentUSNModelArrayList.get(position).getSemNo());
        holder.StudentName.setText(studentUSNModelArrayList.get(position).getSName());

    }

    @Override
    public int getItemCount() {
        return studentUSNModelArrayList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView StudentUSN,semNoDisplay,BranchName,StudentName;
        Button deleteBtn;
        FirebaseFirestore firestore;
        public myViewHolder(@NonNull View itemView, SubjectAdapter.OnItemClickListener listener) {
            super(itemView);

            StudentUSN = itemView.findViewById(R.id.StudentUSN);
            semNoDisplay = itemView.findViewById(R.id.semNoDisplay);
            BranchName = itemView.findViewById(R.id.BranchName);
            StudentName = itemView.findViewById(R.id.StudentName);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            firestore = FirebaseFirestore.getInstance();

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                    String sCode = StudentUSN.getText().toString().trim();
                    String subBranch = BranchName.getText().toString().trim();
                    firestore.collection(subBranch+"USN").whereEqualTo("SUsn",StudentUSN).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()){
                                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                                        String documentId = snapshot.getId();
                                        firestore.collection(subBranch).document(documentId)
                                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(itemView.getContext(), "Subject Deleted!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(itemView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            });
                }
            });
        }
    }
}
