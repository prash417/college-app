package com.example.jce.Notification;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private Context context;
    ArrayList<NotificationModel> notificationModelArrayList;


    public NotificationAdapter(Context context, ArrayList<NotificationModel> notificationModelArrayList) {
        this.context = context;
        this.notificationModelArrayList = notificationModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View NView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_items,parent,false);

        return new MyViewHolder(NView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.Message.setText(notificationModelArrayList.get(position).getMessage());
        holder.By.setText("Sent By:"+notificationModelArrayList.get(position).getSender());
        holder.Date.setText(notificationModelArrayList.get(position).getSentDate());
        holder.Time.setText(notificationModelArrayList.get(position).getSentTime());

        if (TextUtils.isEmpty(notificationModelArrayList.get(position).getFileName())){
            holder.pdf_display.setVisibility(View.GONE);
        }
        else {
            holder.pdf_display.setVisibility(View.VISIBLE);
            holder.PDF_Name.setText(notificationModelArrayList.get(position).getFileName());
        }

        if (TextUtils.isEmpty(notificationModelArrayList.get(position).getImgUri())){
            holder.img_display.setVisibility(View.GONE);
        }
        else {
            holder.img_display.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(notificationModelArrayList.get(position).getImgUri())
                    .placeholder(R.drawable.add_photo_icon)
                    .into(holder.Message_img);
        }
        holder.PDF_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(context,notificationModelArrayList.get(position).getFileName(),
                        ".pdf",DIRECTORY_DOWNLOADS,
                        notificationModelArrayList.get(position).getFileUri());
            }
        });

        holder.Message_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(context,"Notification",
                        ".jpg",DIRECTORY_DOWNLOADS,
                        notificationModelArrayList.get(position).getImgUri());
            }
        });

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
    public int getItemCount() {
        return notificationModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Message,By,Date,Time,PDF_Name;
        RelativeLayout img_display,pdf_display;
        ImageView Message_img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Message = itemView.findViewById(R.id.Message);
            By = itemView.findViewById(R.id.By);
            Date = itemView.findViewById(R.id.Date);
            Time = itemView.findViewById(R.id.Time);
            PDF_Name = itemView.findViewById(R.id.PDF_Name);
            img_display = itemView.findViewById(R.id.img_display);
            pdf_display = itemView.findViewById(R.id.pdf_display);
            Message_img = itemView.findViewById(R.id.Message_img);

        }
    }

}
