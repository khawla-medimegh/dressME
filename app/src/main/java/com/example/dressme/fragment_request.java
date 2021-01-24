package com.example.dressme;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class fragment_request  extends Fragment {
    public RecyclerView MyRecyclerView ;
    CommandAdapter3 recyclerViewAdapter ;


    FirebaseFirestore fStore;
    FirebaseAuth fAuth ;
    public String userID;
    FirebaseUser user;
    Dialog dialog ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_layout, container,false);
    }
    public void setUpRecyclerView(){
        MyRecyclerView = getView().findViewById(R.id.request_Recyclerview);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }
        //Query
        Query query = fStore.collection("tailors").document(userID)
                .collection("commandes").whereEqualTo("etat", "nonConfirme");

        //RecyclerOptions
        FirestoreRecyclerOptions<CommandModel> options = new FirestoreRecyclerOptions.Builder<CommandModel>()
                .setLifecycleOwner(this)
                .setQuery(query, CommandModel.class)
                .build();

        recyclerViewAdapter = new CommandAdapter3(options);
        dialog = new Dialog(getContext());

        MyRecyclerView.setAdapter(recyclerViewAdapter);
        MyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerViewAdapter.setOnItemClickListener(new CommandAdapter3.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                CommandModel command = snapshot.toObject(CommandModel.class);
                final String id = snapshot.getId();
                final String idclient = command.getUserID();
                final String idCommanduser = command.getPostIdUser();
                String e = command.getEtat();
                Log.d("confirm", "button clicked: "+ e +" id: " + id);
                dialog.setContentView(R.layout.price_layout_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView close = dialog.findViewById(R.id.close);
                Button confirm = dialog.findViewById(R.id.button);
                final EditText prix = dialog.findViewById(R.id.textInputEditText);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(),"You canceled",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String p= prix.getText().toString();

                        DocumentReference docRef= fStore.collection("tailors")
                                .document(userID).collection("commandes").document(id);
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("etat", "waiting for client to confirm");
                        edited.put("price", p);
                        docRef.set(edited, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                            }
                        });
                        DocumentReference docRef1= fStore.collection("users")
                                .document(idclient).collection("commandes").document(idCommanduser);

                        Map<String, Object> edited1 = new HashMap<>();
                        edited1.put("etat", "confirme");
                        edited1.put("price", p);
                        docRef1.set(edited1, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Wait for your client to confirm!", Toast.LENGTH_SHORT).show();
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

    public fragment_request(){

    }

}