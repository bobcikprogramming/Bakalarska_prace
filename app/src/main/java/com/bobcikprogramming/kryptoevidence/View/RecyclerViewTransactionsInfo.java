package com.bobcikprogramming.kryptoevidence.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bobcikprogramming.kryptoevidence.R;
import com.bobcikprogramming.kryptoevidence.Controller.TransactionInfoList;

import java.util.ArrayList;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

public class RecyclerViewTransactionsInfo extends RecyclerView.Adapter<RecyclerViewTransactionsInfo.ViewHolder>{

    private ArrayList<TransactionInfoList> dataList;
    private Context context;

    public RecyclerViewTransactionsInfo(Context context, ArrayList<TransactionInfoList> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_transaction_info, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.infoDesLeft.setText(dataList.get(position).getLeftDesc());
        holder.infoValueLeft.setText(dataList.get(position).getLeftValue());
        holder.infoValueLeft.setSelected(true);

        if(dataList.get(position).getRightValue() != null){
            holder.infoDesRight.setText(dataList.get(position).getRightDesc());
            holder.infoValueRight.setText(dataList.get(position).getRightValue());
            holder.infoValueRight.setSelected(true);
        }else{
            holder.infoLayoutRight.setVisibility(View.GONE);
        }

        if((dataList.size()-1) == position){
            holder.underline.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void updateDataList(ArrayList<TransactionInfoList> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView infoDesLeft, infoValueLeft, infoDesRight, infoValueRight;
        private LinearLayout item, infoLayoutRight, underline;

        public ViewHolder(View itemView) {
            super(itemView);

            infoDesLeft = itemView.findViewById(R.id.infoDesLeft);
            infoValueLeft = itemView.findViewById(R.id.infoValueLeft);
            infoDesRight = itemView.findViewById(R.id.infoDesRight);
            infoValueRight = itemView.findViewById(R.id.infoValueRight);

            item = itemView.findViewById(R.id.layoutTransactionsInfo);
            infoLayoutRight = itemView.findViewById(R.id.infoLayoutRight);
            underline = itemView.findViewById(R.id.underline);
        }
    }
}