package com.example.lostandfoundapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;

    public ItemAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            @SuppressLint("Range") String itemName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_ITEM_NAME));
            @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_PHONE));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_DESCRIPTION));
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_DATE));
            @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_LOCATION));
            @SuppressLint("Range") boolean isFound = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COLUMN_IS_FOUND)) == 1;

            holder.itemName.setText(itemName);
            holder.phone.setText(phone);
            holder.description.setText(description);
            holder.date.setText(date);
            holder.location.setText(location);
            holder.isFound.setText(isFound ? "Found" : "Lost");
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, phone, description, date, location, isFound;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            phone = itemView.findViewById(R.id.phone);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            location = itemView.findViewById(R.id.location);
            isFound = itemView.findViewById(R.id.isFound);
        }
    }
}
