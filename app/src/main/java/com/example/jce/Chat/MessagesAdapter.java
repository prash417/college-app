package com.example.jce.Chat;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jce.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {


    Context context;
    ArrayList<Messages> messagesArrayList;

    int ITEM_SEND=1;
    int ITEM_RECIEVE=2;


    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND){
            View  view = LayoutInflater.from(context).inflate(R.layout.senderchatlayout,parent,false);
            return new SenderViewHolder(view);
        }else {
            View  view = LayoutInflater.from(context).inflate(R.layout.recieverchatlayout,parent,false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages messages = messagesArrayList.get(position);
        if (holder.getClass() == SenderViewHolder.class){
            SenderViewHolder viewHolder = (SenderViewHolder)holder;
            viewHolder.Message.setText(messages.getMessage());
            viewHolder.Time.setText(messages.getCurrenttime());
            viewHolder.Date.setText(messages.getDate());

            if (TextUtils.isEmpty(messages.getFileName())){
                viewHolder.pdf_display.setVisibility(View.GONE);
            }
            else {
                viewHolder.pdf_display.setVisibility(View.VISIBLE);
                viewHolder.PDF_Name.setText(messages.getFileName());
            }

            if (TextUtils.isEmpty(messages.getImgUri())){
                viewHolder.img_display.setVisibility(View.GONE);
            }
            else {
                viewHolder.img_display.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(messages.getImgUri())
                        .placeholder(R.drawable.add_photo_icon)
                        .into(viewHolder.Message_img);
            }

            viewHolder.PDF_Name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile(context,messages.getFileName(),
                            ".pdf",DIRECTORY_DOWNLOADS,
                            messages.getFileUri());
                }
            });

            viewHolder.Message_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile(context,"Notification",
                            ".jpg",DIRECTORY_DOWNLOADS,
                            messages.getImgUri());
                }
            });
        }
        else {
            RecieverViewHolder viewHolder = (RecieverViewHolder)holder;
            viewHolder.Message.setText(messages.getMessage());
            viewHolder.Time.setText(messages.getCurrenttime());
            viewHolder.Date.setText(messages.getDate());

            if (TextUtils.isEmpty(messages.getFileName())){
                viewHolder.pdf_display.setVisibility(View.GONE);
            }
            else {
                viewHolder.pdf_display.setVisibility(View.VISIBLE);
                viewHolder.PDF_Name.setText(messages.getFileName());
            }

            if (TextUtils.isEmpty(messages.getImgUri())){
                viewHolder.img_display.setVisibility(View.GONE);
            }
            else {
                viewHolder.img_display.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(messages.getImgUri())
                        .placeholder(R.drawable.add_photo_icon)
                        .into(viewHolder.Message_img);
            }

            viewHolder.PDF_Name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile(context,messages.getFileName(),
                            ".pdf",DIRECTORY_DOWNLOADS,
                            messages.getFileUri());
                }
            });

            viewHolder.Message_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile(context,"Notification",
                            ".jpg",DIRECTORY_DOWNLOADS,
                            messages.getImgUri());
                }
            });
        }

    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url)
    {
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


    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderID())){
            return ITEM_SEND;
        }else {
            return  ITEM_RECIEVE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }


    class SenderViewHolder extends RecyclerView.ViewHolder
    {

        TextView Message,Time,Date,PDF_Name;
        RelativeLayout pdf_display,img_display;
        ImageView Message_img;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            Message = itemView.findViewById(R.id.Message);
            Time = itemView.findViewById(R.id.Time);
            Date = itemView.findViewById(R.id.Date);
            PDF_Name = itemView.findViewById(R.id.PDF_Name);
            img_display = itemView.findViewById(R.id.img_display);
            pdf_display = itemView.findViewById(R.id.pdf_display);
            Message_img = itemView.findViewById(R.id.Message_img);

        }
    }


    class RecieverViewHolder extends RecyclerView.ViewHolder
    {

        TextView Message,Time,Date,PDF_Name;
        RelativeLayout pdf_display,img_display;
        ImageView Message_img;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);

            Message = itemView.findViewById(R.id.Message);
            Time = itemView.findViewById(R.id.Time);
            Date = itemView.findViewById(R.id.Date);
            PDF_Name = itemView.findViewById(R.id.PDF_Name);
            pdf_display = itemView.findViewById(R.id.pdf_display);
            img_display = itemView.findViewById(R.id.img_display);
            Message_img = itemView.findViewById(R.id.Message_img);

        }
    }
}
