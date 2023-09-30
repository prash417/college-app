package com.example.jce.AboutUs.OurHistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jce.R;

import java.util.ArrayList;

public class OurHistoryAdapter extends RecyclerView.Adapter<OurHistoryAdapter.myViewHolder>{


    private Context context;
    ArrayList<OurHistoryModel> ourHistoryModelArrayList;


    public OurHistoryAdapter(Context context, ArrayList<OurHistoryModel> ourHistoryModelArrayList) {
        this.context = context;
        this.ourHistoryModelArrayList = ourHistoryModelArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View OurHistoryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.aboutuslayout,parent,false);

        return new myViewHolder(OurHistoryView);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.label.setText(ourHistoryModelArrayList.get(position).getLabel().replace("LB","\n"));
        holder.Info.setText(ourHistoryModelArrayList.get(position).getInfo().replace("LB","\n"));

    }

    @Override
    public int getItemCount() {
        return ourHistoryModelArrayList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        //declaration
        TextView label,Info;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            label = itemView.findViewById(R.id.label);
            Info = itemView.findViewById(R.id.Info);

        }
    }
}
