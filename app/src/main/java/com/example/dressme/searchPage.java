package com.example.dressme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static androidx.core.view.MenuItemCompat.getActionView;

public class searchPage extends AppCompatActivity {
    public static final String Extra_ID = "TailorID";
    EditText search;
    ImageView sciseau;
    FirebaseFirestore fStore;
    RecyclerView Recyclerview;
    RecyclerView.LayoutManager mLayoutManager;
    tailorAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        fStore = FirebaseFirestore.getInstance();
        Recyclerview= findViewById(R.id.recycler_search);
        sciseau= findViewById(R.id.sciseau);

        Query query = fStore.collection("tailors");
        //RecyclerOptions
        FirestoreRecyclerOptions<tailor> options = new FirestoreRecyclerOptions.Builder<tailor>()
                .setLifecycleOwner(this)
                .setQuery(query, tailor.class)
                .build();

        mAdapter = new tailorAdapter(options);

        Recyclerview= findViewById(R.id.recycler_search);

        mLayoutManager = new LinearLayoutManager(this);

        Recyclerview.setLayoutManager(mLayoutManager);
        Recyclerview.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new tailorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                Log.d("Item_click", "Clicked an item" + snapshot.getId());
                Intent details = new Intent(searchPage.this, profilthis.class);
                details.putExtra(Extra_ID, snapshot.getId());
                startActivity(details);
            }
        });

        sciseau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search= findViewById(R.id.search_bar);
                String s= search.getText().toString();

                searchData(s);

            }
        });

    }

    private void searchData(String s) {
        ProgressDialog pd = new ProgressDialog( searchPage.this);
        pd.setTitle("searching...");
        pd.show();
        Query query = fStore.collection("tailors").whereEqualTo("search",s.toLowerCase());
        //RecyclerOptions
        FirestoreRecyclerOptions<tailor> options = new FirestoreRecyclerOptions.Builder<tailor>()
                .setLifecycleOwner(this)
                .setQuery(query, tailor.class)
                .build();

        mAdapter = new tailorAdapter(options);

        Recyclerview= findViewById(R.id.recycler_search);

        mLayoutManager = new LinearLayoutManager(this);
        pd.dismiss();
        Recyclerview.setLayoutManager(mLayoutManager);
        Recyclerview.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new tailorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                Log.d("Item_click", "Clicked an item" + snapshot.getId());
                Intent details = new Intent(searchPage.this, profilthis.class);
                details.putExtra(Extra_ID, snapshot.getId());
                startActivity(details);
            }
        });
    }

}