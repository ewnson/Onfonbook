package com.example.www.contactsmanager;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Collections;
import java.util.List;

public class AdapterContacts extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<Contacts> data= Collections.emptyList();
    Contacts current;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterContacts(Context context, List<Contacts> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.container_contacts, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        Contacts current=data.get(position);
        myHolder.textContactsName.setText(current.name);
        myHolder.textPnumber.setText("Phone Number: " + current.pnumber);
        myHolder.textEmail.setText("Email: " + current.email);
        myHolder.textPnumber.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textContactsName;
        TextView textPnumber;
        TextView textEmail;
        TextView textPrice;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textContactsName= (TextView) itemView.findViewById(R.id.textContactsName);
            textPnumber = (TextView) itemView.findViewById(R.id.textPnumber);
            textEmail = (TextView) itemView.findViewById(R.id.textEmail);
            itemView.setOnClickListener(this);
        }

        // Click event for all items
        @Override
        public void onClick(View v) {

            Toast.makeText(context, "You clicked an item", Toast.LENGTH_SHORT).show();

        }

    }

}
