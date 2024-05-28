package com.example.lostandfoundapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailActivity extends AppCompatActivity {

    private TextView textViewItemDetails;
    private Button buttonRemove;
    private DataBaseHelper dbHelper;
    private long itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        textViewItemDetails = findViewById(R.id.textViewItemDetails);
        buttonRemove = findViewById(R.id.buttonRemove);
        dbHelper = new DataBaseHelper(this);

        Intent intent = getIntent();
        itemId = intent.getLongExtra("ITEM_ID", -1);

        if (itemId != -1) {
            loadItemDetails(itemId);
        } else {
            Toast.makeText(this, "Error loading item details", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonRemove.setOnClickListener(v -> {
            deleteItem(itemId);
        });
    }

    private void loadItemDetails(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
                DataBaseHelper.COLUMN_ITEM_NAME,
                DataBaseHelper.COLUMN_PHONE,
                DataBaseHelper.COLUMN_DESCRIPTION,
                DataBaseHelper.COLUMN_DATE,
                DataBaseHelper.COLUMN_LOCATION,
                DataBaseHelper.COLUMN_IS_FOUND
        };

        Cursor cursor = null;
        try {
            cursor = db.query(
                    DataBaseHelper.TABLE_ITEMS,
                    columns,
                    DataBaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ITEM_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PHONE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DATE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_LOCATION));
                boolean isFound = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_IS_FOUND)) == 1;

                String details = "Name: " + itemName + "\n" +
                        "Phone: " + phone + "\n" +
                        "Description: " + description + "\n" +
                        "Date: " + date + "\n" +
                        "Location: " + location + "\n" +
                        "Status: " + (isFound ? "Found" : "Lost");

                textViewItemDetails.setText(details);

            } else {
                Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading item details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void deleteItem(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int rowsDeleted = db.delete(DataBaseHelper.TABLE_ITEMS, DataBaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            if (rowsDeleted > 0) {
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error deleting item", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
