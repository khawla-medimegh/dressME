package com.example.dressme;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilTailor extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {
    public static final String Extra_postID = "PostID";
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FloatingActionButton addPostBtn;
    TextView username, bio, custom, mail, tel;
    CircleImageView ProfileImage;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userID;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    RecyclerView Recyclerview;
    postAdapter2 mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    private static final String TAG = "ProfileTailor";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_tailor);
        addPostBtn = findViewById(R.id.addPost);
        ProfileImage = findViewById(R.id.tailor_image);
        username = findViewById(R.id.tailor_username);
        bio = findViewById(R.id.bio);
        mail = findViewById(R.id.tailor_mail);
        tel = findViewById(R.id.tailor_tel);
        custom= findViewById(R.id.custom);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        Recyclerview = findViewById(R.id.recycler_viewPosts);

        storageReference = FirebaseStorage.getInstance().getReference("ProfilePictures");
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }
        //toolbar
        setSupportActionBar(toolbar);
        //navig drawer menu
        navigationView.bringToFront();
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        if (user != null) {
            updateNavHeader();
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final DocumentReference documentReference = fStore.collection("tailors").document(userID);

            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        username.setText(documentSnapshot.getString("username"));
                        bio.setText(documentSnapshot.getString("description"));
                        mail.setText(documentSnapshot.getString("email"));
                        tel.setText(documentSnapshot.getString("phone"));
                        String imgUrl = documentSnapshot.getString("imageUrl");
                        Glide.with(ProfilTailor.this).load(imgUrl).into(ProfileImage);

                    } else {
                        Log.d(TAG, "Document do not exist");
                    }
                }
            });

        }

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddPost.class));
            }
        });
        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), customHP.class));
            }
        });


        //Query
        Query query = fStore.collection("tailors").document(userID).collection("posts");

        //RecyclerOptions
        FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setLifecycleOwner(this)
                .setQuery(query, PostModel.class)
                .build();

        mAdapter = new postAdapter2(options);

        Recyclerview = findViewById(R.id.recycler_viewPosts);

        mLayoutManager = new GridLayoutManager(this , 2);
        // mLayoutManager = new LinearLayoutManager(this);

        Recyclerview.setLayoutManager(mLayoutManager);
        Recyclerview.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new postAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                Log.d("Item_click", "Clicked an item" + snapshot.getId());
                PostModel post = snapshot.toObject(PostModel.class);
                String id = snapshot.getId();
                Intent details = new Intent(ProfilTailor.this, Postthis_tailor.class);
                details.putExtra(Extra_postID, id );
                startActivity(details);
            }
        });
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mAdapter.getItemCount();
                int lastVisiblePosition = ((LinearLayoutManager)mLayoutManager).findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mLayoutManager.scrollToPosition(positionStart);
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                break;
            case R.id.nav_activity:
                Intent intent1 = new Intent(getApplicationContext(), Activite.class);
                startActivity(intent1);
                break;
            case R.id.nav_settings:
                Intent intent2 = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent2);
                break;
            case R.id.nav_logout:
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
        DrawerLayout drawerLayout1 = findViewById(R.id.drawerLayout);
        if (drawerLayout1.isDrawerOpen(GravityCompat.START)) {
            drawerLayout1.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public void updateNavHeader() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final DocumentReference documentReference = fStore.collection("tailors").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    final CircleImageView headerImage = findViewById(R.id.header_image);
                    TextView headerUser = findViewById(R.id.header_username);
                    View headerView = navigationView.getHeaderView(0);

                    headerUser.setText(documentSnapshot.getString("username"));
                    String imgUrl = documentSnapshot.getString("imageUrl");
                    Glide.with(headerView).load(imgUrl).into(headerImage);
                }
            });
        }
    }

}