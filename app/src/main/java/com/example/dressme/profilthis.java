package com.example.dressme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class profilthis extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String Extra_ID = "TailorID";
    public static final String Extra_pID = "PostID";
    public static final String Extra_tID = "TailleurID";
    public static final String Extra_username = "TailleurName";


    boolean test ;
    RecyclerView Recyclerview;

    postAdapter2 mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FloatingActionButton addcommandBtn;
    ImageButton like;
    TextView username, bio, mail, tel;
    CircleImageView ProfileImage;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userid;
    FirebaseFirestore fStore;
    StorageReference storageReference;

    private static final String TAG = "ProfileTailor";
    String t;
    String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_profilthis);
        Intent intent = getIntent();

        final String userID = intent.getStringExtra(Extra_ID);

        addcommandBtn = findViewById(R.id.addcommand);
        ProfileImage = findViewById(R.id.tailor_client_image);
        username = findViewById(R.id.tailor_client_username);
        bio = findViewById(R.id.bio_tail_client);
        mail = findViewById(R.id.tailor_maill);
        tel = findViewById(R.id.tailor_tell);
        like = findViewById(R.id.like);
        storageReference = FirebaseStorage.getInstance().getReference("ProfilePictures");
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        userid = user.getUid();

        drawerLayout = findViewById(R.id.drawerLayoutClient);
        navigationView = findViewById(R.id.nav_viewClient);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //navig drawer menu
        navigationView.bringToFront();
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        final DocumentReference documentReference = fStore.collection("tailors").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    t = documentSnapshot.getString("username");
                    username.setText(documentSnapshot.getString("username"));
                    bio.setText(documentSnapshot.getString("description"));
                    mail.setText(documentSnapshot.getString("email"));
                    tel.setText(documentSnapshot.getString("phone"));
                    imgUrl = documentSnapshot.getString("imageUrl");
                    Glide.with(profilthis.this).load(imgUrl).into(ProfileImage);

                } else {
                    Log.d(TAG, "Document do not exist");
                }
            }
        });


        CollectionReference collectionReference2 = fStore.collection("likes");

        Query userQuery = collectionReference2.whereEqualTo("tailor", userID).whereEqualTo("liker", userid);
        userQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    like.setImageResource(R.drawable.ic_liked);
                    test= true;
                } else {
                    like.setImageResource(R.drawable.ic_dislike);
                    test= false;                }
            }
        });

        addcommandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent donnee = new Intent(profilthis.this, AddCommand.class);
                donnee.putExtra(Extra_tID, userID);
                donnee.putExtra(Extra_username, t);
                startActivity(donnee);
            }
        });


        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference collectionReference2 = fStore.collection("likes");

                Query userQuery = collectionReference2.whereEqualTo("tailor", userID).whereEqualTo("liker", userid);
                userQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            test= true;
                        } else {
                            test= false;
                        }
                    }
                });

                if ( test == false) {
                    CollectionReference collectionReference = fStore.collection("likes");
                    final DocumentReference docRef = collectionReference.document();
                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            Map<String, Object> hashMap = new HashMap<>();
                            hashMap.put("liker", userid);
                            hashMap.put("tailor", userID);
                            hashMap.put("imageUrl", imgUrl);
                            hashMap.put("name", t);
                            docRef.set(hashMap);
                            like.setImageResource(R.drawable.ic_liked);
                            test= true;
                            Toast.makeText(profilthis.this, "added to favourites", Toast.LENGTH_SHORT).show();

                        }
                    });
                } else {
                    CollectionReference collectionReference = fStore.collection("likes");
                    Query query = collectionReference.whereEqualTo("tailor", userID).whereEqualTo("liker", userid);
                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    documentSnapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            like.setImageResource(R.drawable.ic_dislike);
                                            test = false;
                                            Toast.makeText(profilthis.this, "removed from favourites", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                        }
                                    });
                                    ;
                                }
                            }
                        }
                    });

                }
            }
        });

        final DocumentReference documentReference1 = fStore.collection("tailors").document(userID);
        //Query
        Query query = documentReference1.collection("posts");

        //RecyclerOptions
        FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setLifecycleOwner(this)
                .setQuery(query, PostModel.class)
                .build();

        mAdapter = new

                postAdapter2(options);

        Recyclerview =

                findViewById(R.id.RecyclerVieww);

         mLayoutManager = new GridLayoutManager(this , 2);
        // mLayoutManager = new LinearLayoutManager(this);

        Recyclerview.setLayoutManager(mLayoutManager);
        Recyclerview.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new postAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {

                Log.d("Item_click", "Clicked an item" + snapshot.getId());
                Intent details = new Intent(profilthis.this, postthis.class);
                details.putExtra(Extra_pID, snapshot.getId());
                details.putExtra(Extra_tID, snapshot.getString("publisher"));
                details.putExtra(Extra_username, t);
                startActivity(details);

            }
        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_explore:
                Intent intent2 = new Intent(getApplicationContext(), HomePage.class);
                startActivity(intent2);
                break;
            case R.id.nav_favourites:
                Intent intent4 = new Intent(getApplicationContext(), Favourites.class);
                startActivity(intent4);
                break;
            case R.id.nav_Cart:
                Intent intent3 = new Intent(getApplicationContext(), cart_client.class);
                startActivity(intent3);
                break;
            case R.id.nav_settingsClient:
                Intent intent1 = new Intent(getApplicationContext(), SettingsClient.class);
                startActivity(intent1);
                break;
            case R.id.nav_logoutClient:
                FirebaseAuth.getInstance().signOut();
                Intent intentlogin = new Intent(this, Login.class);
                startActivity(intentlogin);
                finish();

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout1 = findViewById(R.id.drawerLayoutClient);
        if (drawerLayout1.isDrawerOpen(GravityCompat.START)) {
            drawerLayout1.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}