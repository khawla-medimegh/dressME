package com.example.dressme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
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


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String Extra_ID = "TailorID";
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    RecyclerView Recyclerview;
    customizeAdapter mAdapter;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userID;
    RecyclerView.LayoutManager mLayoutManager;
    TextView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }
        drawerLayout = findViewById(R.id.drawerLayoutClient);
        navigationView = findViewById(R.id.nav_viewClient);
        toolbar = findViewById(R.id.toolbar);
        search = findViewById(R.id.search);
        setSupportActionBar(toolbar);
        //navig drawer menu
        navigationView.bringToFront();
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        fStore = FirebaseFirestore.getInstance();
        if (user != null) {
            updateNavHeader();
        }


        //Query
        Query query = fStore.collection("customize");

        //RecyclerOptions
        FirestoreRecyclerOptions<customizeModel> options = new FirestoreRecyclerOptions.Builder<customizeModel>()
                .setLifecycleOwner(this)
                .setQuery(query, customizeModel.class)
                .build();

        mAdapter = new customizeAdapter(options);

        Recyclerview = findViewById(R.id.RecyclerView);
        mLayoutManager = new LinearLayoutManager(this);

        Recyclerview.setLayoutManager(mLayoutManager);
        Recyclerview.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new customizeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                Log.d("Item_click", "Clicked an item" + snapshot.getId());
                Intent details = new Intent(HomePage.this, profilthis.class);
                details.putExtra(Extra_ID, snapshot.getId());
                startActivity(details);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchPage = new Intent(HomePage.this, searchPage.class);
                startActivity(searchPage);
            }
        });

    }

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
            case R.id.nav_explore:
                break;
            case R.id.nav_favourites:
                Intent intent1 = new Intent(getApplicationContext(), Favourites.class);
                startActivity(intent1);
                break;
            case R.id.nav_Cart:
                Intent intent3 = new Intent(getApplicationContext(), cart_client.class);
                startActivity(intent3);
                break;
            case R.id.nav_settingsClient:
                Intent intent2 = new Intent(getApplicationContext(), SettingsClient.class);
                startActivity(intent2);
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


    public void updateNavHeader() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            CollectionReference users= fStore.collection("users");
            final DocumentReference documentReference =  users.document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_viewClient);
                    final CircleImageView headerImage = findViewById(R.id.header_imageclient);
                    TextView headerUser = findViewById(R.id.header_usernameclient);
                    View headerView = navigationView.getHeaderView(0);
                    headerUser.setText(documentSnapshot.getString("username"));
                    String imgUrl = documentSnapshot.getString("imageUrl");
                    Glide.with(headerView).load(imgUrl).into(headerImage);
                }
            });
        }
    }
}