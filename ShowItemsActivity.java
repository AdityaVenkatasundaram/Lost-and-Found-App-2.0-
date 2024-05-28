package com.example.lostandfoundapp;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShowItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DataBaseHelper(this);
        Cursor cursor = dbHelper.getAllItems();
        itemAdapter = new ItemAdapter(this, cursor);
        recyclerView.setAdapter(itemAdapter);
    }

    private void loadItemsFromDatabase() {
        Cursor cursor = dbHelper.getAllItems();
        if (cursor != null) {
            itemAdapter.swapCursor(cursor);
        }
    }
}
