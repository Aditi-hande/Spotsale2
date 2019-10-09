package com.example.ecommerce.spotsale2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Product product, View view);
    }
    private final CartAdapter.OnItemClickListener listener;


    private ArrayList<Product> products;

    public CartAdapter(ArrayList<Product> products, CartAdapter.OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_cart, parent, false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.d("Product[" + position + "]", products.get(position).getCat_id().toString());

        Picasso.get().load(products.get(position).getImageUrl())
                .placeholder(R.drawable.ic_img_placeholder)
                .error(R.drawable.ic_img_error)
                .into(holder.imageView);
        holder.textView.setText(products.get(position).getName());
        holder.bind(products.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.cart_row_image);
            textView = (TextView) itemView.findViewById(R.id.cart_row_name);
            deleteButton = (ImageButton) itemView.findViewById(R.id.cart_row_delbtn);
        }

        public void bind(final Product product, final CartAdapter.OnItemClickListener listener) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(product, itemView);
                }
            });
        }
    }
}
