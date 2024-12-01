package com.mahad.shoppinglist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ItemAdapter extends FirebaseRecyclerAdapter<Item, ItemAdapter.ItemViewHolder> {

    public ItemAdapter(@NonNull FirebaseRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Item model) {
        holder.nameTextView.setText(model.getName());
        holder.quantityTextView.setText("Quantity: " + model.getQuantity());
        holder.priceTextView.setText("Price: Rs" + model.getPrice());

        // Handle delete button
        holder.deleteButton.setOnClickListener(v -> {
            String key = getRef(position).getKey();
            FirebaseDatabase.getInstance().getReference("items").child(key).removeValue()
                    .addOnCompleteListener(task -> reorderIds());
        });
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ItemViewHolder(view);
    }

    // ViewHolder class
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, quantityTextView, priceTextView;
        Button deleteButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    // Reorder IDs after an item is deleted
    private void reorderIds() {
        FirebaseDatabase.getInstance().getReference("items").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Process items and reorder their IDs
                        DataSnapshot snapshot = task.getResult();
                        int index = 1;  // Start from ID 1 for the first item
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Item item = child.getValue(Item.class);
                            if (item != null) {
                                item.setId(index++);  // Assign sequential IDs
                                FirebaseDatabase.getInstance().getReference("items")
                                        .child(Objects.requireNonNull(child.getKey()))
                                        .setValue(item)  // Update the item with new ID
                                        .addOnCompleteListener(updateTask -> {
                                            if (!updateTask.isSuccessful()) {
                                               // Toast.makeText(MainActivity.this, "Failed to update IDs", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    } else {
                       // Toast.makeText(MainActivity.this, "Failed to fetch items for reordering", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}

