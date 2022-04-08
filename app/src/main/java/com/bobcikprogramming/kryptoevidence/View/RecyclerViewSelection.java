package com.bobcikprogramming.kryptoevidence.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.CryptocurrencyEntity;
import com.bobcikprogramming.kryptoevidence.R;
import com.bobcikprogramming.kryptoevidence.Controller.RecyclerViewSelectionList;

import java.util.ArrayList;
import java.util.Locale;

public class RecyclerViewSelection extends RecyclerView.Adapter<RecyclerViewSelection.ViewHolder>{

    private ArrayList<CryptocurrencyEntity> dataList;
    private Context context;
    private View.OnClickListener myClickListener;

    public RecyclerViewSelection(Context context, ArrayList<CryptocurrencyEntity> dataList, View.OnClickListener myClickListener) {
        this.context = context;
        this.dataList = dataList;
        System.out.println(">>>>>>>>>>>>>>>>>>>>>velikost: "+dataList.size());
        this.myClickListener = myClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_crypto_selection, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppDatabase db = AppDatabase.getDbInstance(context);
        holder.tvLongName.setText(dataList.get(position).longName);
        holder.tvLongName.setSelected(true);
        holder.tvShortName.setText(dataList.get(position).shortName.toUpperCase());
        if(dataList.get(position).favorite == 1){
            holder.btnFavorite.setImageResource(R.drawable.ic_favorite_selected);
        }else{
            holder.btnFavorite.setImageResource(R.drawable.ic_favorite_unselected);
        }

        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CryptocurrencyEntity crypto = dataList.get(holder.getAdapterPosition());
                if(crypto.favorite == 0){
                    holder.btnFavorite.setImageResource(R.drawable.ic_favorite_selected);
                    db.databaseDao().updateFavoriteSettingCrypto(crypto.uid, 1);
                    crypto.favorite = 1;
                    dataList.set(holder.getAdapterPosition(), crypto);
                }else{
                    holder.btnFavorite.setImageResource(R.drawable.ic_favorite_unselected);
                    db.databaseDao().updateFavoriteSettingCrypto(crypto.uid,0);
                    crypto.favorite = 0;
                    dataList.set(holder.getAdapterPosition(), crypto);
                }
            }
        });

        holder.itemView.setTag(position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvLongName, tvShortName;
        private ImageView btnFavorite;
        private LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLongName = itemView.findViewById(R.id.cryptoLongName);
            tvShortName = itemView.findViewById(R.id.cryptoShortName);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);

            item = itemView.findViewById(R.id.layoutTransactions);
            itemView.setOnClickListener(myClickListener);
        }
    }
}

