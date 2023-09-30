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

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.myViewHolder> {

    ArrayList<SubjectModel> subjectModelArrayList;

    private OnItemClickListener listener;

    public void filteredList(ArrayList<SubjectModel> filtereList) {
        subjectModelArrayList = filtereList;
        notifyDataSetChanged();
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener clickListener){
        listener = clickListener;
    }

    public SubjectAdapter(ArrayList<SubjectModel> subjectModelArrayList) {
        this.subjectModelArrayList = subjectModelArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_view,parent,false);


        return new myViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.subjectCode.setText(subjectModelArrayList.get(position).getSubCode());
        holder.SubName.setText(subjectModelArrayList.get(position).getSubName());
        holder.BranchName.setText(subjectModelArrayList.get(position).getSBranch());
    }

    @Override
    public int getItemCount() {
        return subjectModelArrayList.size();
    }

    static class myViewHolder extends RecyclerView.ViewHolder{

        TextView subjectCode,SubName,BranchName;
        Button deleteBtn;
        FirebaseFirestore firestore;
        public myViewHolder(@NonNull View itemView,OnItemClickListener listener) {
            super(itemView);

            subjectCode = itemView.findViewById(R.id.SubCode);
            SubName = itemView.findViewById(R.id.SubName);
            BranchName = itemView.findViewById(R.id.BranchName);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            firestore = FirebaseFirestore.getInstance();

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                    String sCode = subjectCode.getText().toString().trim();
                    String subBranch = BranchName.getText().toString().trim();
                    firestore.collection(subBranch).whereEqualTo("SubCode",sCode).get()
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
