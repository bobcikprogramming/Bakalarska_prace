package com.bobcikprogramming.kryptoevidence.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bobcikprogramming.kryptoevidence.Model.CryptocurrencyEntity;
import com.bobcikprogramming.kryptoevidence.R;

import java.util.ArrayList;

public class RecyclerViewOwnedCrypto extends RecyclerView.Adapter<RecyclerViewOwnedCrypto.ViewHolder>{

    private ArrayList<CryptocurrencyEntity> dataList;
    private Context context;

    public RecyclerViewOwnedCrypto(Context context, ArrayList<CryptocurrencyEntity> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_owned_crypto, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvShortName.setText(dataList.get(position).shortName);
        holder.tvLongName.setText(dataList.get(position).longName);
        holder.tvAmount.setText(dataList.get(position).amount);

        holder.tvShortName.setSelected(true);
        holder.tvLongName.setSelected(true);
        holder.tvAmount.setSelected(true);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvLongName, tvShortName, tvAmount;
        private LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            tvShortName = itemView.findViewById(R.id.tvShortName);
            tvLongName = itemView.findViewById(R.id.tvLongName);
            tvAmount = itemView.findViewById(R.id.tvAmount);

            item = itemView.findViewById(R.id.layoutOwnedCrypto);
        }
    }
}

