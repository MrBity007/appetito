package com.example.app3tito;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private int last = -1;
    private ArrayList<Pizzak> etelAdatok;
    private ArrayList<Pizzak> mindenEtelAdat;

    ShoppingCartAdapter(Context context, ArrayList<Pizzak> itemsData) {
        this.etelAdatok = itemsData;
        this.mindenEtelAdat = itemsData;
        this.mContext = context;
    }



    @Override
    public ShoppingCartAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.cart_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ShoppingCartAdapter.ViewHolder holder, int position) {
        Pizzak currentItem = etelAdatok.get(position);
        holder.bindTo(currentItem);

        if(holder.getAdapterPosition() > last) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.move);
            holder.itemView.startAnimation(animation);
            last = holder.getAdapterPosition();
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

            Tnev = itemView.findViewById(R.id.etelNev2);
            Tkep = itemView.findViewById(R.id.itemImage);
            Tcsilag = itemView.findViewById(R.id.etelCsillag2);
            T_ar = itemView.findViewById(R.id.etelAr);
        }

        void bindTo(Pizzak currentItem) {
            Tnev.setText(currentItem.getName());
            T_ar.setText(currentItem.getAr());
            Tcsilag.setRating(currentItem.getCsillag());
            Glide.with(mContext).load(currentItem.getKep()).into(Tkep);
        }
    }





}
