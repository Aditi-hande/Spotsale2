package com.example.ecommerce.spotsale2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {


    public interface OnItemClickListener {
        void onItemClick(String categories);
    }
    private final OnItemClickListener listener;

   public void onItemClick(){}

    private static ArrayList<String> categories;

    public CategoryAdapter(ArrayList<String> categories,OnItemClickListener listener) {
        this.categories = categories;
        this.listener = listener;

    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_category, parent, false);
        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.nameView.setText(categories.get(position));
        holder.bind(categories.get(position),listener);
    }

    @Override
    public int getItemCount() {
        int size=categories.size();
        return size;
        //Log.v("size",String.valueOf(categories.size()));
    }




        public static class ViewHolder extends RecyclerView.ViewHolder {


            TextView nameView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameView = (TextView) itemView.findViewById(R.id.row_name);
            }

            public void bind( final String category,final OnItemClickListener listener) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(category);
                    }
                });
            }
        }

}