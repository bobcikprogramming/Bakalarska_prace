package com.bobcikprogramming.kryptoevidence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithPhotos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecyclerViewSelection extends RecyclerView.Adapter<RecyclerViewSelection.ViewHolder>{

    private ArrayList<RecyclerViewSelectionList> dataList;
    private Context context;
    private View.OnClickListener myClickListener;

    public RecyclerViewSelection(Context context, ArrayList<RecyclerViewSelectionList> dataList, View.OnClickListener myClickListener) {
        this.context = context;
        this.dataList = dataList;
        this. myClickListener = myClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_crypto_selection, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvLongName.setText(dataList.get(position).longName);
        holder.tvShortName.setText(dataList.get(position).shortName);

        holder.itemView.setTag(position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvLongName, tvShortName;
        private LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLongName = itemView.findViewById(R.id.cryptoLongName);
            tvShortName = itemView.findViewById(R.id.cryptoShortName);

            item = itemView.findViewById(R.id.layoutTransactions);
            itemView.setOnClickListener(myClickListener);
        }
    }
}

class RecyclerViewSelectionList {
    String longName, shortName;
    public RecyclerViewSelectionList(String longName, String shortName){
        this.longName = longName;
        this.shortName = shortName;
    }
}