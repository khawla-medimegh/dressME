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
import android.widget.TextView;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class fragment_progress extends Fragment {

    public RecyclerView MyRecyclerView;
    CommandAdapter2 recyclerViewAdapter;
    Dialog dialog;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    public String userID;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress_layout, container, false);
    }

    public void setUpRecyclerView() {
        MyRecyclerView = getView().findViewById(R.id.progress_Recyclerview);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }
        //Query
        Query query = fStore.collection("users").document(userID)
                .collection("commandes").whereNotEqualTo("etat", "confirme");

        //RecyclerOptions
        FirestoreRecyclerOptions<CommandModel> options = new FirestoreRecyclerOptions.Builder<CommandModel>()
                .setLifecycleOwner(this)
                .setQuery(query, CommandModel.class)
                .build();
        dialog = new Dialog(getContext());
        recyclerViewAdapter = new CommandAdapter2(options);

        MyRecyclerView.setAdapter(recyclerViewAdapter);
        MyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewAdapter.setOnItemClickListener(new CommandAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                CommandModel command = snapshot.toObject(CommandModel.class);
                final String id = snapshot.getId();
                final String idtail = command.getTailorID();
                final String idCommandtail = command.getPostIdUser();
                String e = command.getEtat();
                Log.d("confirm", "button clicked: " + e + " id: " + id);
                    dialog.setContentView(R.layout.confirm_ready);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    ImageView close = dialog.findViewById(R.id.closeReadyc);
                    Button confirm = dialog.findViewById(R.id.buttonReadyc);
                    final TextView time = dialog.findViewById(R.id.Timec);
                    final TextView date = dialog.findViewById(R.id.Datec);
                    final TextView address = dialog.findViewById(R.id.addressc);
                DocumentReference docRef1 = fStore.collection("tailors")
                        .document(idtail).collection("commandes").document(idCommandtail);
                docRef1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                        if (documentSnapshot.exists()) {
                            time.setText(documentSnapshot.getString("time"));
                            date.setText(documentSnapshot.getString("date"));
                            address.setText(documentSnapshot.getString("address"));
                        }

                    }
                });

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "You canceled", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            DocumentReference docRef = fStore.collection("users")
                                    .document(userID).collection("commandes").document(id);

                            docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Done !", Toast.LENGTH_SHORT).show();
                                }
                            });

                            DocumentReference docRef1 = fStore.collection("tailors")
                                    .document(idtail).collection("commandes").document(idCommandtail);
                            Map<String, Object> edited1 = new HashMap<>();
                            edited1.put("etat", "pretConfirme");
                            docRef1.set(edited1, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Great experience!", Toast.LENGTH_SHORT).show();
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

    public fragment_progress() {

    }
}
