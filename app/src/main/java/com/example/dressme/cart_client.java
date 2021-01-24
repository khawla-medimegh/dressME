package com.example.dressme;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;


public class cart_client extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userID;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_cart);
        final ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabBar);
        TabItem tabconfirm = findViewById(R.id.tab_confirm);
        TabItem tabprogress = findViewById(R.id.tab_progress);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        if (user != null){
            userID = user.getUid();}
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        drawerLayout = findViewById(R.id.drawerLayoutClient);
        navigationView = findViewById(R.id.nav_viewClient);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //navig drawer menu
        navigationView.bringToFront();
        toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        if (user != null) {
            updateNavHeader();
        }
        viewPager.setAdapter(pagerAdapter);
         tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
             @Override
             public void onTabSelected(TabLayout.Tab tab) {
                 viewPager.setCurrentItem(tab.getPosition());
             }

             @Override
             public void onTabUnselected(TabLayout.Tab tab) {

             }

             @Override
             public void onTabReselected(TabLayout.Tab tab) {

             }
         });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch ( menuItem.getItemId()){
            case R.id.nav_explore:
                Intent intent2 = new Intent(getApplicationContext(), HomePage.class);
                startActivity(intent2);
                break;
            case R.id.nav_favourites :
                Intent intent1 = new Intent(getApplicationContext(), Favourites.class);
                startActivity(intent1);
                break;
            case R.id.nav_Cart :
                break;
            case R.id.nav_settingsClient:
                Intent intent3 = new Intent(getApplicationContext(), SettingsClient.class);
                startActivity(intent3);
                break;
            case R.id.nav_logoutClient :
                FirebaseAuth.getInstance().signOut();
                Intent intentlogin = new Intent(this,Login.class);
                startActivity(intentlogin);
                finish();

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout1 = findViewById(R.id.drawerLayoutClient);
        if (drawerLayout1.isDrawerOpen(GravityCompat.START)){
            drawerLayout1.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void updateNavHeader() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final DocumentReference documentReference = fStore.collection("users").document(userID);
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