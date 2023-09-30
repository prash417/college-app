package com.example.jce.Marks;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MarksAdapter extends RecyclerView.Adapter<MarksAdapter.myViewHolder> {

    ArrayList<MarksModel> marksArrayList;


    private OnItemClickListener listener;


    public interface OnItemClickListener{
        void onItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener clickListener){
        listener = clickListener;
    }

    public MarksAdapter(ArrayList<MarksModel> marksArrayList) {
        this.marksArrayList = marksArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.marks_view,parent,false);

        return new myViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.StudentSem.setText(marksArrayList.get(position).getSemNo());
        holder.StudentBranch.setText(marksArrayList.get(position).getSBranch());
        holder.SubCode.setText(marksArrayList.get(position).getSubCode());
        holder.SubName.setText(marksArrayList.get(position).getSubName());
        holder.SubMarks.setText(marksArrayList.get(position).getsMarks()+"/"
                +marksArrayList.get(position).getsOutOfMarks());
        holder.IANumber.setText(marksArrayList.get(position).getIANo());
        holder.stdUSN = marksArrayList.get(position).getSUsn();
    }

    @Override
    public int getItemCount() {
        return marksArrayList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder{

        TextView StudentSem,StudentBranch,SubCode,SubName,SubMarks,IANumber;
        String stdUSN;
        Button deleteBtn;
        FirebaseFirestore firestore;
        FirebaseAuth fAuth;
        String userID;
        public myViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            StudentSem = itemView.findViewById(R.id.StudentSem);
            StudentBranch = itemView.findViewById(R.id.StudentBranch);
            SubCode = itemView.findViewById(R.id.SubCode);
            SubName = itemView.findViewById(R.id.SubName);
            SubMarks = itemView.findViewById(R.id.SubMarks);
            IANumber = itemView.findViewById(R.id.IANumber);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            //Firebase instance created
            fAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();

            //get current user instance
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            userID = fAuth.getCurrentUser().getUid();
            DocumentReference df = firestore.collection("users").document(userID);
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    if (documentSnapshot.getString("isStudent") !=null){
                       deleteBtn.setVisibility(View.GONE);
                    }if (documentSnapshot.getString("isTeacher") !=null){
                        deleteBtn.setVisibility(View.VISIBLE);
                    }
                }
            });

            //delete marks btn
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                    firestore.collection("marks").whereEqualTo("SUsn",stdUSN).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()){
                                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                                        String documentId = snapshot.getId();
                                        firestore.collection("marks").document(documentId)
                                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(itemView.getContext(), "Marks Deleted!", Toast.LENGTH_SHORT).show();
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
