package com.example.warehouseinventoryapp;

import com.example.warehouseinventoryapp.provider.Item;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    List<Item> data = new ArrayList<>();

    public RecyclerAdapter() {}

    public void setData(List<Item> data){
        this.data = data;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false); //CardView inflated as RecyclerView list item
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.itemId.setText(String.valueOf(data.get(position).getItemID()));
        viewHolder.itemName.setText(data.get(position).getItemName());
        viewHolder.itemQuantity.setText(data.get(position).getItemQuantity());
        viewHolder.itemCost.setText(data.get(position).getItemCost());
        viewHolder.itemDescription.setText(data.get(position).getItemDescription());
        viewHolder.itemFrozen.setText(data.get(position).getItemFrozen());

        //a class declared in a method (so called local or anonymous class can only access the method's local variables if they are declared final (1.8 or are effectively final)
        //this has to do with Java closures
        // see https://docs.oracle.com/javase/tutorial/java/javaOO/localclasses.html and https://docs.oracle.com/javase/tutorial/java/javaOO/anonymousclasses.html
        final int fPosition = position;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() { //set back to itemView for students
            @Override
            public void onClick(View v) {

                Snackbar.make(v, "Item at position " + fPosition + " was clicked!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public TextView itemId;
        public TextView itemName;
        public TextView itemQuantity;
        public TextView itemCost;
        public TextView itemDescription;
        public TextView itemFrozen;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemId = itemView.findViewById(R.id.carddata_itemId);
            itemName = itemView.findViewById(R.id.carddata_itemName);
            itemQuantity = itemView.findViewById(R.id.carddata_quantity);
            itemCost = itemView.findViewById(R.id.carddata_cost);
            itemDescription = itemView.findViewById(R.id.carddata_description);
            itemFrozen = itemView.findViewById(R.id.carddata_frozenItem);
        }
    }


}
