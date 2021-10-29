package com.example.obus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AcceptRequestAdapter extends FirebaseRecyclerAdapter<Order,AcceptRequestAdapter.viewHolder> {



    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    Context context;


    public AcceptRequestAdapter(@NonNull FirebaseRecyclerOptions<Order> options, Context context ) {
        super(options);

//        myOptions = new ArrayList<>();

        this.context = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull Order model) {
        holder.img.setImageURI(Uri.parse(model.getItm_uri()));
        holder.state.setText(model.getItm_state());
        holder.dist.setText(model.getItm_dist()+", ");
        holder.qnt.setText(model.getItm_qnt());
        holder.name.setText(model.getItm_name());
        holder.num.setText(model.getPhne());
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accept_req_item,parent,false);
        return new viewHolder(view);
    }


    public class viewHolder extends RecyclerView.ViewHolder{
        private static final int REQUEST_CALL = 1;
        ImageView img, call;
        TextView name, state, dist, qnt, num;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.accReqImg);
            name = itemView.findViewById(R.id.accReqName);
            state = itemView.findViewById(R.id.accReqState);
            dist = itemView.findViewById(R.id.accReqDist);
            qnt = itemView.findViewById(R.id.accReqQnt);
            call = itemView.findViewById(R.id.call);
            num = itemView.findViewById(R.id.accReqNum);

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBtn();
                }

                private void callBtn() {
                    String number = num.getText().toString();
                    if (num.length()>0) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                        } else {
                            String dial = "tel:" + number;
                            context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));

                        }
                    }
                }
            });
        }
    }
}
