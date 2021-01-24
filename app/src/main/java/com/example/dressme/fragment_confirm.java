package com.example.dressme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;


public class fragment_confirm extends Fragment {

    public RecyclerView MyRecyclerView ;
    CommandAdapter recyclerViewAdapter ;

    Dialog dialog;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth ;
    public String userID;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirm_layout, container,false);
    }
    public void setUpRecyclerView(){
        MyRecyclerView = getView().findViewById(R.id.confirm_Recyclerview);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }
        //Query
        Query query = fStore.collection("users").document(userID)
                .collection("commandes").whereEqualTo("etat", "confirme");

        //RecyclerOptions
        FirestoreRecyclerOptions<CommandModel> options = new FirestoreRecyclerOptions.Builder<CommandModel>()
                .setLifecycleOwner(this)
                .setQuery(query, CommandModel.class)
                .build();
        dialog = new Dialog(getContext());
        recyclerViewAdapter = new CommandAdapter(options);

        MyRecyclerView.setAdapter(recyclerViewAdapter);
        MyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerViewAdapter.setOnItemClickListener(new CommandAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                CommandModel command = snapshot.toObject(CommandModel.class);
                final String id = snapshot.getId();
                final String idtailor = command.getTailorID();
                final String idCommandTail = command.getPostIdUser();
                String e = command.getEtat();
                Log.d("confirm", "button clicked: "+ e +" id: " + id);
                dialog.setContentView(R.layout.confirm_price);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView close = dialog.findViewById(R.id.closep);
                Button y = dialog.findViewById(R.id.yes);
                Button n = dialog.findViewById(R.id.no);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "You canceled", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                y.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentReference docRef= fStore.collection("users")
                                .document(userID).collection("commandes").document(id);
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("etat", "encours");
                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "confirmed", Toast.LENGTH_SHORT).show();
                            }
                        });

                        DocumentReference docRef1= fStore.collection("tailors")
                                .document(idtailor).collection("commandes").document(idCommandTail);

                        Map<String, Object> edited1 = new HashMap<>();
                        edited1.put("etat", "encours");
                        docRef1.update(edited1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "confirmed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();

                    }
                });
                n.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentReference docRef= fStore.collection("users")
                                .document(userID).collection("commandes").document(id);
                        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                        DocumentReference docRef1= fStore.collection("tailors")
                                .document(idtailor).collection("commandes").document(idCommandTail);

                        Map<String, Object> edited1 = new HashMap<>();
                        edited1.put("etat", "rejected");
                        docRef1.update(edited1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView();
    }

    public fragment_confirm(){

    }


}
