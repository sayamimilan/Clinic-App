package com.example.clinictest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class myadapter extends FirebaseRecyclerAdapter<DataBaseUser,myadapter.myviewholder> {
    private ArrayList<Booking> bookings;
    private static DatabaseReference databaseBookings = FirebaseDatabase.getInstance().getReference("bookings");

    public myadapter(@NonNull FirebaseRecyclerOptions<DataBaseUser> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final myviewholder holder, final int position, @NonNull final DataBaseUser model) {

//        Administrator activeuser;

        if (model.getStatus().equals("disabled")) {
            holder.Name.setText(model.getName());
            holder.UserName.setText(model.getUsername());
            holder.Role.setText(model.getRole());
            holder.Status.setText(model.getStatus());
            holder.Status.setTextColor(Color.RED);
        }else if (model.getStatus().equals("enabled")){
            holder.Name.setText(model.getName());
            holder.UserName.setText(model.getUsername());
            holder.Role.setText(model.getRole());
            holder.Status.setText(model.getStatus());
            holder.Status.setTextColor(Color.BLACK);
        }

        if(model.getRole().equals("Admin")) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {

//            WalkInClinic clinic = new WalkInClinic();
            @Override
            public void onClick(View view) {
                HashMap hashMap = new HashMap();
                if (model.getStatus().equals("enabled")) {
                    hashMap.put("status", "disabled");

                    FirebaseDatabase.getInstance().getReference("users")
                            .child(model.getId())
                            .updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(view.getContext(), "User disabled successfully", Toast.LENGTH_SHORT).show();

                            String clinicId = model.getClinicId();

                            if (clinicId != null && model.getRole().equals("Employee")) {
                                if (!clinicId.equals(""))
                                    disableClinic(clinicId);
                            }
                        }
                    }
                    );
                }else{
                    hashMap.put("status", "enabled");

                    FirebaseDatabase.getInstance().getReference("users")
                            .child(model.getId())
                            .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(view.getContext(), "User enabled successfully", Toast.LENGTH_SHORT).show();

                            String clinicId = model.getClinicId();

                            if (clinicId != null && model.getRole().equals("Employee")) {
                                if (!clinicId.equals(""))
                                    enableClinic(clinicId);
                            }
                        }
                    });

                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.Name.getContext());
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to permanently delete this user?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String id = model.getId();
                        String clinicId = model.getClinicId();
                        if (clinicId != null && model.getRole().equals("Employee")) {
                            if (!clinicId.equals("")){
                                deleteClinic(clinicId);
//                                deleteClinicBookings(clinicId);
                            }
                        }
                        if(id != null){
                            deleteUser(id);
                        }

//                        if (!FirebaseDatabase.getInstance().getReference("users").child(model.getRole()).equals("Admin")) {
//                            FirebaseDatabase.getInstance().getReference("users")
//                                    .child(model.getId()).removeValue();
//                        }
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
            }
        });

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new myviewholder(view);

    }

    class myviewholder extends RecyclerView.ViewHolder {

        TextView Name, UserName, Role, Status;
        ImageView  delete, edit;


        public myviewholder(@NonNull View itemView) {
            super(itemView);
            Name=(TextView)itemView.findViewById(R.id.tvName);
            UserName=(TextView)itemView.findViewById(R.id.tvUserName);
            Role=(TextView)itemView.findViewById(R.id.tvRole);
            Status=(TextView)itemView.findViewById(R.id.tvStatus);

            edit = (ImageView) itemView.findViewById(R.id.editBtn);
            delete = (ImageView) itemView.findViewById(R.id.deleteBtn);
        }
    }

//    public void updateBookings(){
//        bookings = new ArrayList<>();
//
//        databaseBookings.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                bookings.clear();
//                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                    Booking booking = postSnapshot.getValue(Booking.class);
//                    bookings.add(booking);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    public boolean deleteClinic(String id) {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("clinics").child(id);
    databaseReference.removeValue();
    return true;
}

    public boolean deleteUser(String id){
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(id);
        dR.removeValue();
        return true;
    }

    public boolean disableClinic(String id) {
        HashMap hashMap = new HashMap();
        hashMap.put("status", "disabled");
        FirebaseDatabase.getInstance().getReference("clinics").child(id).updateChildren(hashMap);
        return true;
    }

    public boolean enableClinic(String id) {
        HashMap hashMap = new HashMap();
        hashMap.put("status", "enabled");
        FirebaseDatabase.getInstance().getReference("clinics").child(id).updateChildren(hashMap);
        return true;
    }

}
