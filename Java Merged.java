//AddItemActivity.java

package com.example.lostandfoundapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class AddItemActivity extends AppCompatActivity {

    private EditText editTextItemName, editTextPhone, editTextDescription, editTextDate, editTextLocation;
    private Button buttonSave;
    private RadioGroup radioGroupPostType;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        editTextItemName = findViewById(R.id.editTextItemName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDate = findViewById(R.id.editTextDate);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonSave = findViewById(R.id.buttonSave);
        radioGroupPostType = findViewById(R.id.radioGroupPostType);

        dbHelper = new DataBaseHelper(this);

        buttonSave.setOnClickListener(v -> saveItem());
    }

    private void saveItem() {
        String itemName = editTextItemName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        boolean isFound = radioGroupPostType.getCheckedRadioButtonId() == R.id.radioFound;

        if (itemName.isEmpty() || phone.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDate(date)) {
            Toast.makeText(this, "Please enter a valid date (YYYY-MM-DD)", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.COLUMN_ITEM_NAME, itemName);
        values.put(DataBaseHelper.COLUMN_PHONE, phone);
        values.put(DataBaseHelper.COLUMN_DESCRIPTION, description);
        values.put(DataBaseHelper.COLUMN_DATE, date);
        values.put(DataBaseHelper.COLUMN_LOCATION, location);
        values.put(DataBaseHelper.COLUMN_IS_FOUND, isFound ? 1 : 0);

        try {
            long newRowId = db.insert(DataBaseHelper.TABLE_ITEMS, null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error saving item", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidDate(String date) {
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
        return Pattern.matches(datePattern, date);
    }
}

// DataBaseHelper.java
package com.example.lostandfoundapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lostandfound.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_IS_FOUND = "is_found";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_IS_FOUND + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // Method to get all items from the database
    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ITEMS, null, null, null, null, null, null);
    }
}

//Item.java
package com.example.lostandfoundapp;

public class Item {
    private String itemName;
    private String phone;
    private String description;
    private String date;
    private String location;
    private boolean isFound;

    public Item(String itemName, String phone, String description, String date, String location, boolean isFound) {
        this.itemName = itemName;
        this.phone = phone;
        this.description = description;
        this.date = date;
        this.location = location;
        this.isFound = isFound;
    }

    public String getItemName() {
        return itemName;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public boolean isFound() {
        return isFound;
    }
}

//ItemAdpater.java
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

//ItemDetailActivity.java
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

//MainActivity.java
package com.example.lostandfoundapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton buttonAddItem, buttonShowItems, buttonShowMap;
    private DataBaseHelper dbHelper;
    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DataBaseHelper(this);
        Cursor cursor = dbHelper.getAllItems();
        itemAdapter = new ItemAdapter(this, cursor);

        buttonAddItem = findViewById(R.id.buttonAddItem);
        buttonShowItems = findViewById(R.id.buttonShowItems);
        buttonShowMap = findViewById(R.id.buttonShowMap);

        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        buttonShowItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowItemsActivity.class);
                startActivity(intent);
            }
        });

        buttonShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }
}

//MapActvity.java
package com.example.lostandfoundapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Log.d(TAG, "API Key: " + getString(R.string.google_maps_key));

        dbHelper = new DataBaseHelper(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "SupportMapFragment is null");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d(TAG, "Map is ready");

        // Retrieve all items from the database
        Cursor cursor = dbHelper.getAllItems();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_LOCATION));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_DESCRIPTION));
                Log.d(TAG, "Item: " + description + ", Location: " + location);

                // Split the location string into latitude and longitude
                String[] latLng = location.split(", ");
                if (latLng.length == 2) {
                    try {
                        double latitude = Double.parseDouble(latLng[0]);
                        double longitude = Double.parseDouble(latLng[1]);

                        // Add a marker for each item
                        LatLng itemLocation = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(itemLocation).title(description));
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid location format: " + location, e);
                    }
                } else {
                    Log.e(TAG, "Location string does not contain valid latitude and longitude: " + location);
                }
            }
            cursor.close();
        } else {
            Log.e(TAG, "Cursor is null or empty");
        }

        // Move the camera to the first marker if available
        if (mMap.getCameraPosition().zoom == 0) {
            LatLng defaultLocation = new LatLng(-34, 151); // Sydney
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
        }
    }
}

//ShowItemsActivity.java
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





