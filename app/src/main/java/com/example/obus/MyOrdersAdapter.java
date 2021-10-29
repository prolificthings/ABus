package com.example.obus;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MyOrdersAdapter extends FirebaseRecyclerAdapter<Order,MyOrdersAdapter.viewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MyOrdersAdapter(@NonNull FirebaseRecyclerOptions<Order> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull Order model) {
        holder.img.setImageURI(Uri.parse(model.getItm_uri()));
        holder.name.setText(model.getItm_name());
        holder.quantity.setText(model.getItm_qnt());
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item,parent,false);
        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name,quantity;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.myOrdImg);
            name = itemView.findViewById(R.id.myOrdName);
            quantity = itemView.findViewById(R.id.myOrdQnt);
        }
    }
}
