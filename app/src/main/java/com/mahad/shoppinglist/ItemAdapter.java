package com.mahad.shoppinglist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class ItemAdapter extends FirebaseRecyclerAdapter<Item, ItemAdapter.ItemViewHolder> {

    public ItemAdapter(@NonNull FirebaseRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Item model) {
        Log.d("ItemAdapter", "Binding data: " + model.getName());
        holder.nameTextView.setText(model.getName());
        holder.quantityTextView.setText("Quantity: " + model.getQuantity());
        holder.priceTextView.setText("Price: Rs" + model.getPrice());

        // Handle delete button
        holder.deleteButton.setOnClickListener(v -> {
            String key = getRef(position).getKey();
            if (key != null) {
                FirebaseDatabase.getInstance().getReference("items").child(key).removeValue();
            }
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
}
