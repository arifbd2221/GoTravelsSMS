package com.mangosoft.gotravelssms.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.mangosoft.gotravelssms.R;
import com.mangosoft.gotravelssms.model.Person;

import java.util.List;

public class ContactList extends RecyclerView.Adapter<ContactList.MyViewHolder>{

    List<Person> contacts;
    Context context;


    public ContactList(List<Person> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.name.setText(contacts.get(position).getName());
        holder.phone.setText(contacts.get(position).getPhone());



        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle("Are you sure to delete?");
        builder.setMessage("This process can not be undone.If you are sure then tap on confirm");

        String positiveText = "Confirm";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss alert dialog, update preferences with game score and restart play fragment

                        contacts.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,contacts.size());
                        Log.d("myTag", "positive button clicked");
                        dialog.dismiss();
                    }
                });

        String negativeText = "Cancel";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss dialog, start counter again
                        dialog.dismiss();
                        Log.d("myTag", "negative button clicked");
                    }
                });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,phone;
        ImageView delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            delete = itemView.findViewById(R.id.delete);


        }
    }

}
