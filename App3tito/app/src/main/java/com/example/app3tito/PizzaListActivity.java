package com.example.app3tito;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class PizzaListActivity extends AppCompatActivity {
    private static final String LOG_TAG = PizzaListActivity.class.getName();
    private FirebaseUser user;
    private boolean viewRow = true;
    private FrameLayout redCircle;
    private TextView countTextView;
    public int cartItems = 0;
    private int gridNumber = 1;
    private CollectionReference mKosar;
    private RecyclerView mRecyclerView;
    private ArrayList<Pizzak> mEtelAdtok;
    private PizzakAdapter mAdapter;
    private SharedPreferences preferences;
    private FirebaseFirestore mFirestore;
    private CollectionReference mEtelek;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etel_list);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }


        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                this, gridNumber));
        mEtelAdtok = new ArrayList<>();
        mAdapter = new PizzakAdapter(this, mEtelAdtok);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mEtelek = mFirestore.collection("Etelek").document(user.getUid())
                .collection("user_etelek");
        mKosar = FirebaseFirestore.getInstance().collection("kosar");

        queryData();

        Toolbar menusav = findViewById(R.id.toolbar);
        setSupportActionBar(menusav);
        updateAlertIcon();


        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortadapter = ArrayAdapter.createFromResource(this, R.array.sort_spinner, android.R.layout.simple_spinner_item);
        sortadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortadapter);

        Spinner limitSpinner = findViewById(R.id.limit);
        ArrayAdapter<CharSequence> limitadapter = ArrayAdapter.createFromResource(this, R.array.limit, android.R.layout.simple_spinner_item);
        limitadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        limitSpinner.setAdapter(limitadapter);

        Button sortButton = findViewById(R.id.sort_button);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                szures();
            }
        });


    }


    private void initializeData() {
        String[] etelList = getResources()
                .getStringArray(R.array.etel_nevek);
        String[] etelar = getResources()
                .getStringArray(R.array.etel_arak);
        TypedArray etelkepek =
                getResources().obtainTypedArray(R.array.etel_kepek);
        TypedArray etelRate = getResources().obtainTypedArray(R.array.etel_ertekelesek);

        for (int i = 0; i < etelList.length; i++) {
            mEtelek.add(new Pizzak(etelList[i], etelar[i], etelRate.getFloat(i, 0),
                    etelkepek.getResourceId(i, 0)));
        }

        etelkepek.recycle();
    }

    private void queryData() {
        mEtelAdtok.clear();
        mEtelek.orderBy("ar", Query.Direction.ASCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Pizzak etel = document.toObject(Pizzak.class);
                mEtelAdtok.add(etel);
            }

            if (mEtelAdtok.size() == 0) {
                initializeData();
                queryData();
            }

            mAdapter.notifyDataSetChanged();
        });
    }

    private void szures(){
        String sortBy = "ar"; // Alapértelmezett rendezés ár szerint
        Query.Direction direction = Query.Direction.ASCENDING; // Alapértelmezett rendezési irány: növekvő

        // Ellenőrizzük, melyik elem van kiválasztva a spinnerben
        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        String selectedSortOption = sortSpinner.getSelectedItem().toString();

        Spinner limitSpinner= findViewById(R.id.limit);
        String selectedLimitOption = limitSpinner.getSelectedItem().toString();

        // Ellenőrzés a kiválasztott érték alapján és beállítjuk a rendezési sorrendet
        if (selectedSortOption.equals("Csökkenő ár")) {
            direction = Query.Direction.DESCENDING;
        }else if(selectedSortOption.equals("Növekvő ár")){
            direction = Query.Direction.ASCENDING;
        }

        // Végrehajtjuk a Firestore lekérdezést a megadott rendezési sorrend alapján
        mEtelek.orderBy(sortBy, direction)
                .limit(Integer.parseInt(selectedLimitOption))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mEtelAdtok.clear(); // Töröljük az előző adatokat
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Pizzak etel = document.toObject(Pizzak.class);
                        mEtelAdtok.add(etel);
                    }

                    if (mEtelAdtok.size() == 0) {
                        initializeData();
                        queryData();
                    }

                    mAdapter.notifyDataSetChanged();
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.appetito_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.log_out_button) {

            //kijelentjkezés
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.cart) {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivity(intent);
            return true;
        }else if (item.getItemId() == R.id.home) {
            Intent intent = new Intent(this, PizzaListActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mKosar.document(userId).collection("user_kosar")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        cartItems = queryDocumentSnapshots.size(); // Az adott felhasználó kosarában található elemek száma
                        if (0 < cartItems) {
                            countTextView.setText(String.valueOf(cartItems));
                        } else {
                            countTextView.setText("");
                        }
                        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(LOG_TAG, "Hiba az elemek lekérdezésekor: " + e.getMessage());
                    });
        }
    }


}