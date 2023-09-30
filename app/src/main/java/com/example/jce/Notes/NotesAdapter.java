package com.example.jce.Notes;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.DeadObjectException;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jce.Chat.ChatActivity;
import com.example.jce.R;

import java.util.ArrayList;
import java.util.HashSet;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.myViewHolder> {


    ArrayList<NotesModel> notesModelArrayList;
    Context context;


    public NotesAdapter(ArrayList<NotesModel> notesModelArrayList, Context context) {
        this.notesModelArrayList = notesModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.pdf_items,parent,false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.PDF_Name.setText(notesModelArrayList.get(position).getFilename());
        holder.uri = notesModelArrayList.get(position).getFileurl();
        holder.By.setText("Uploaded By: "+notesModelArrayList.get(position).getBy());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(holder.PDF_Name.getContext(),notesModelArrayList.get(position).getFilename(),".pdf",DIRECTORY_DOWNLOADS,notesModelArrayList.get(position).getFileurl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesModelArrayList.size();
    }

    public void filteredList(ArrayList<NotesModel> filtereList) {
        notesModelArrayList = filtereList;
        notifyDataSetChanged();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView PDF_Name,By;
        String uri;
            public myViewHolder(@NonNull View itemView) {
            super(itemView);
                PDF_Name = itemView.findViewById(R.id.PDF_Name);
                By = itemView.findViewById(R.id.By);
        }
    }

    //download file
    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {


        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Downloaded "+fileName);
        request.setDescription("Downloading file...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, fileName + fileExtension);

        DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
         downloadmanager.enqueue(request);
        Toast.makeText(context, "Downloading file...", Toast.LENGTH_SHORT).show();
    }
}
