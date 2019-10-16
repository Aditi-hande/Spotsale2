package com.example.ecommerce.spotsale2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.google.firestore.v1beta1.StructuredQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderDisplayAdapter extends RecyclerView.Adapter<OrderDisplayAdapter.ViewHolder> {



    private List<Product> products;

    public OrderDisplayAdapter(List<Product> products) {
        this.products = products;
        //this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_catalog, parent, false);
        return new OrderDisplayAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDisplayAdapter.ViewHolder holder, int position) {

        Log.d("Product[" + position + "]", products.get(position).getCat_id().toString());

        Picasso.get().load(products.get(position).getImageUrl())
                .placeholder(R.drawable.ic_img_placeholder)
                .error(R.drawable.ic_img_error)
                .into(holder.imageView);
        holder.textView.setText(products.get(position).getName());
        holder.textView.setText(String.valueOf(products.get(position).getCost()));
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.disp_order_row_image);
            textView = (TextView) itemView.findViewById(R.id.disp_order_row_name);
            textView = (TextView) itemView.findViewById(R.id.disp_order_row_cost);

            //deleteButton = (ImageButton) itemView.findViewById(R.id.cart_row_delbtn);
        }

        public void bind(final Product product) {
            /*deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(product, itemView);
                }
            });
        }*/
        }
    }}