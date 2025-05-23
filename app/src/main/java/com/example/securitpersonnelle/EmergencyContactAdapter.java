package com.example.securitpersonnelle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ContactViewHolder> {

    private List<EmergencyContact> contactList;

    public EmergencyContactAdapter(List<EmergencyContact> contacts) {
        this.contactList = contacts;
    }

    public void setContacts(List<EmergencyContact> newContacts) {
        this.contactList = newContacts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        EmergencyContact contact = contactList.get(position);
        holder.contactName.setText(contact.getName());
        holder.contactPhone.setText(contact.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return contactList == null ? 0 : contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView contactName;
        TextView contactPhone;

        public ContactViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            contactPhone = itemView.findViewById(R.id.contact_phone);
        }
    }
}
