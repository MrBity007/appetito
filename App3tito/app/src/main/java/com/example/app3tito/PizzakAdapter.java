package com.example.app3tito;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PizzakAdapter extends RecyclerView.Adapter<PizzakAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private int last = -1;
    private ArrayList<Pizzak> etelAdatok;
    private ArrayList<Pizzak> mindenEtelAdat;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    private CollectionReference mKosar = FirebaseFirestore.getInstance().collection("kosar");

    PizzakAdapter(Context context, ArrayList<Pizzak> itemsData) {
        this.etelAdatok = itemsData;
        this.mindenEtelAdat = itemsData;
        this.mContext = context;
    }

    @Override
    public PizzakAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.etel_list, parent, false));
    }

    @Override
    public void onBindViewHolder(PizzakAdapter.ViewHolder holder, int position) {

        Pizzak currentItem = etelAdatok.get(position);
        holder.bindTo(currentItem);

        if(holder.getAdapterPosition() > last) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            last = holder.getAdapterPosition();
        }


        Button megveszemButton = holder.itemView.findViewById(R.id.megveszem);

        megveszemButton.setOnClickListener(v -> {
            addItemToCart(currentItem);

            ((PizzaListActivity) mContext).updateAlertIcon();
        });
    }

    private void addItemToCart(Pizzak etel) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            Map<String, Object> itemData = new HashMap<>();
            itemData.put("name", etel.getName());
            itemData.put("ar", etel.getAr());
            itemData.put("kep", etel.getKep());
            itemData.put("csillag", etel.getCsillag()); // Assuming "csillag" is the rating value


            String documentId = mKosar.document().getId(); // Generate a random document ID

            PizzakCart cartItem = new PizzakCart(documentId, etel.getName(), etel.getAr(), etel.getCsillag(), etel.getKep());

            mKosar.document(userId).collection("user_kosar").document(documentId).set(cartItem)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("EtelListActivity", "Item added to cart with ID: " + documentId);

                     })
                    .addOnFailureListener(e -> {
                        Log.w("EtelListActivity", "Failed to add item to cart");
                    });
        }
    }



    @Override
    public int getItemCount() {
        return etelAdatok.size();
    }

  @Override
    public Filter getFilter() {
        return shopingFilter;
    }

    private Filter shopingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Pizzak> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                results.count = mindenEtelAdat.size();
                results.values = mindenEtelAdat;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(Pizzak item : mindenEtelAdat) {
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            etelAdatok = (ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView Tnev;
        private TextView T_ar;
        private ImageView Tkep;
        private RatingBar Tcsilag;

        ViewHolder(View itemView) {
            super(itemView);


            Tnev = itemView.findViewById(R.id.etelNev);
            Tkep = itemView.findViewById(R.id.itemImage);
            Tcsilag = itemView.findViewById(R.id.etelCsillag);
            T_ar = itemView.findViewById(R.id.etelAr);

            itemView.findViewById(R.id.megveszem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((PizzaListActivity)mContext).updateAlertIcon();
                }
            });
        }

        void bindTo(Pizzak currentItem){
            Tnev.setText(currentItem.getName());
            T_ar.setText(currentItem.getAr());
            Tcsilag.setRating(currentItem.getCsillag());

            Glide.with(mContext).load(currentItem.getKep()).into(Tkep);
        }
    }
}
