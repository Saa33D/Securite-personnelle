package com.example.securitpersonnelle;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {

    private final List<Alert> alertList;
    private final Context context;

    public AlertAdapter(Context context, List<Alert> alertList) {
        this.context = context;
        this.alertList = alertList;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alert, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        Alert alert = alertList.get(position);
        holder.txtDate.setText("📅 " + alert.getDate());
        holder.txtCoords.setText("📍 " + alert.getLatitude() + ", " + alert.getLongitude());

        if (alert.getPhotoUri() != null) {
            holder.imgAlert.setImageURI(Uri.parse(alert.getPhotoUri()));
        } else {
            holder.imgAlert.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    static class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtCoords;
        ImageView imgAlert;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtCoords = itemView.findViewById(R.id.txt_coords);
            imgAlert = itemView.findViewById(R.id.img_alert);
        }
    }
}
