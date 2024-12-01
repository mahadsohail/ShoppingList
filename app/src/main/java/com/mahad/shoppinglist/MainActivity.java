package com.mahad.shoppinglist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> showAddItemDialog());

        // Setup FirebaseRecyclerOptions
        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(FirebaseDatabase.getInstance().getReference("items"), Item.class)
                .build();

        adapter = new ItemAdapter(options);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setTitle("Add Item");

        // Inflate the dialog layout
        final View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        EditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        EditText priceEditText = dialogView.findViewById(R.id.priceEditText);

        builder.setPositiveButton("Save", (dialog, which) -> {
            // Get user input
            String name = nameEditText.getText().toString().trim();
            String quantityStr = quantityEditText.getText().toString().trim();
            String priceStr = priceEditText.getText().toString().trim();

            // Validate inputs
            if (name.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(MainActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse the quantity and price
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);

            // Create a new item object
            Item newItem = new Item(name, quantity, price);

            // Add item to Firebase Database
            String itemId = FirebaseDatabase.getInstance().getReference("items").push().getKey();
            if (itemId != null) {
                FirebaseDatabase.getInstance().getReference("items").child(itemId).setValue(newItem)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                                Log.d("MainActivity", "Item added to Firebase with ID: " + itemId);

                                // Refresh the adapter's data
                                adapter.notifyDataSetChanged();  // Force RecyclerView to refresh its data
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }


}
