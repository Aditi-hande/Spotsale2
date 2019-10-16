package com.example.ecommerce.spotsale2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ecommerce.spotsale2.DatabaseClasses.Cart;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {


    public interface OnItemClickListener {
        void onItemClick(Cart carts);
    }
    private final OnItemClickListener listener;

    private
    ArrayList<Cart> carts;

    public OrderAdapter(ArrayList<Cart> carts,OnItemClickListener listener) {
        this.carts = carts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_order, parent, false);
        return new OrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        holder.nameView.setText(carts.get(position).getCart_id());
        holder.costView.setText(String.valueOf(carts.get(position).getTotal_sum()));
        holder.bind(carts.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView nameView,costView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.order_name);
            costView = (TextView) itemView.findViewById(R.id.order_cost);

        }

        public void bind(final Cart cart, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(cart);
                }
            });
        }
    }


}
