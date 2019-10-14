package com.example.ecommerce.spotsale2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SellerProductAdapter extends RecyclerView.Adapter<SellerProductAdapter.ViewHolder> {



    public interface OnItemClickListener {
        void onItemClick(Product product);
    }
    private final OnItemClickListener listener;


    private ArrayList<Product> products;

    public SellerProductAdapter(ArrayList<Product> products, OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_catalog, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.d("Product[" + position + "]", products.get(position).getCat_id().toString());

        Picasso.get().load(products.get(position).getImageUrl())
                .placeholder(R.drawable.ic_img_placeholder)
                .error(R.drawable.ic_img_error)
                //.resizeDimen(R.dimen.catalog_image_width, R.dimen.catalog_image_height)
                .into(holder.imageView);
        holder.nameView.setText(products.get(position).getName());
        holder.costView.setText(String.valueOf(products.get(position).getCost()));
        holder.bind(products.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView, costView;
        EditText qtyView,sellingcostView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.row_image);
            nameView = (TextView) itemView.findViewById(R.id.row_name);
            costView = (TextView) itemView.findViewById(R.id.row_cost);
            qtyView=(EditText)itemView.findViewById(R.id.sell_qty);
            sellingcostView=(EditText)itemView.findViewById(R.id.sell_cost);

        }

        public void bind(final Product product, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(product);
                }
            });
        }
    }

}
