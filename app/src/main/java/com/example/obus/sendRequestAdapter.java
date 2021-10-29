package com.example.obus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class sendRequestAdapter extends RecyclerView.Adapter<sendRequestAdapter.viewHolder> implements Filterable{
private ArrayList<Items> itemList;
private ArrayList<Items> backup;
Context context;

    public sendRequestAdapter(ArrayList<Items> list, Context context) {
        itemList = list;
        this.context = context;
        backup = new ArrayList<>(itemList);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_request_item,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final Items temp = itemList.get(position);

        holder.img.setImageURI(Uri.parse(itemList.get(position).getItemUri()));
        holder.name.setText(itemList.get(position).getItemName());

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,PlaceRequestActivity.class);
                intent.putExtra("image", temp.getItemUri());
                intent.putExtra("name", temp.getItemName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Items> filterData = new ArrayList<>();

            if (charSequence.toString().isEmpty())
                filterData.addAll(backup);

            else{
                for (Items obj : backup){
                    if (obj.getItemName().toLowerCase().contains(charSequence.toString().toLowerCase()))
                        filterData.add(obj);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filterData;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            itemList.clear();
            itemList.addAll((ArrayList<Items>)results.values);
            notifyDataSetChanged();
        }
    };

    public class viewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView name;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.itemImg);
            name = itemView.findViewById(R.id.itemName);
        }
    }
}
