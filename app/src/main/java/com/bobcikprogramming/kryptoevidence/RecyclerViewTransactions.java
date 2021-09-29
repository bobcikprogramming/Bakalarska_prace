package com.bobcikprogramming.kryptoevidence;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;

import java.util.List;

public class RecyclerViewTransactions extends RecyclerView.Adapter<RecyclerViewTransactions.ViewHolder>  {

    private List<TransactionEntity> dataList;
    private Context context;

    public RecyclerViewTransactions(Context context/*, ArrayList<RecyclerViewRaidingAdvancedData> dataList*/) {
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_transactions, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TransactionEntity data = this.dataList.get(position);

        holder.textViewOperation.setText(data.transactionType);
        holder.textViewDate.setText(data.date);
        holder.textViewTime.setText(data.time);

        changeItemViewByTransactionType(data.transactionType, holder, data);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewOperation, textViewDate, textViewTime, tvNameFL, tvNameSL, tvQuantityFL, tvQuantitySL, tvDesPriceFL, tvDesPriceSL, tvPriceFL, tvPriceSL, tvDescriptionFL, tvDescriptionSL;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewOperation = itemView.findViewById(R.id.textViewOperation);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            tvNameFL = itemView.findViewById(R.id.textViewNameFirstLine);
            tvNameSL = itemView.findViewById(R.id.textViewNameSecondLine);
            tvQuantityFL = itemView.findViewById(R.id.textViewQuantityFirstLine);
            tvQuantitySL = itemView.findViewById(R.id.textViewQuantitySecondLine);
            tvDesPriceFL = itemView.findViewById(R.id.textViewDescriptionPriceBuy);
            tvDesPriceSL = itemView.findViewById(R.id.textViewDescriptionPriceSell);
            tvPriceFL = itemView.findViewById(R.id.textViewPriceFirst);
            tvPriceSL = itemView.findViewById(R.id.textViewPriceSecondLine);
            tvDescriptionFL = itemView.findViewById(R.id.textViewDescriptionFirstLine);
            tvDescriptionSL = itemView.findViewById(R.id.textViewDescriptionSecondLine);
        }
    }

    /*public void addToArray(RecyclerViewTransactionsData data){
        dataList.add(0, data);
        notifyItemInserted(0);
    }*/

    private void changeItemViewByTransactionType(String transactionType, ViewHolder holder, TransactionEntity data){
        switch (transactionType){
            case "Nákup":
                holder.textViewOperation.setTextColor(ContextCompat.getColor(context, R.color.headlineBuy));
                holder.tvDesPriceFL.setVisibility(View.VISIBLE);
                holder.tvPriceFL.setVisibility(View.VISIBLE);
                holder.tvDesPriceSL.setVisibility(View.GONE);
                holder.tvPriceSL.setVisibility(View.GONE);

                holder.tvDescriptionFL.setText("Koupeno:");
                holder.tvDescriptionSL.setText("Platba:");

                holder.tvNameFL.setText(data.nameBought);
                holder.tvQuantityFL.setText(String.valueOf(data.quantityBought));
                holder.tvPriceFL.setText(String.valueOf(data.priceBought + " " + data.currency));
                holder.tvNameSL.setText(data.currency);
                holder.tvQuantitySL.setText(String.valueOf(data.quantitySold));
                break;
            case "Prodej":
                holder.textViewOperation.setTextColor(ContextCompat.getColor(context, R.color.headlineSell));
                holder.tvDesPriceFL.setVisibility(View.VISIBLE);
                holder.tvPriceFL.setVisibility(View.VISIBLE);
                holder.tvDesPriceSL.setVisibility(View.GONE);
                holder.tvPriceSL.setVisibility(View.GONE);

                holder.tvDescriptionFL.setText("Prodáno:");
                holder.tvDescriptionSL.setText("Zisk:");

                holder.tvNameFL.setText(data.nameSold);
                holder.tvQuantityFL.setText(String.valueOf(data.quantitySold));
                holder.tvPriceFL.setText(String.valueOf(data.priceSold + " " + data.currency));
                holder.tvNameSL.setText(data.currency);
                holder.tvQuantitySL.setText(String.valueOf(data.quantityBought));
                break;
            case "Směna":
                holder.textViewOperation.setTextColor(ContextCompat.getColor(context, R.color.headlineChange));
                holder.tvDesPriceFL.setVisibility(View.VISIBLE);
                holder.tvPriceFL.setVisibility(View.VISIBLE);
                holder.tvDesPriceSL.setVisibility(View.VISIBLE);
                holder.tvPriceSL.setVisibility(View.VISIBLE);

                holder.tvDescriptionFL.setText("Prodáno:");
                holder.tvDescriptionSL.setText("Zisk:");

                holder.tvNameFL.setText(data.nameSold);
                holder.tvQuantityFL.setText(String.valueOf(data.quantitySold));
                holder.tvPriceFL.setText(String.valueOf(data.priceSold + " " + data.currency));
                holder.tvNameSL.setText(data.nameBought);
                holder.tvQuantitySL.setText(String.valueOf(data.quantityBought));
                holder.tvPriceSL.setText(String.valueOf(data.priceBought + " " + data.currency));
                break;

        }
    }

    public void setTransactionData(List<TransactionEntity> transactionData){
        this.dataList = transactionData;
        Toast.makeText(context, String.valueOf(this.dataList.size()), Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
    }

    public void removeFromArray(int position){
        dataList.remove(position);
        notifyDataSetChanged();
    }
}

class RecyclerViewTransactionsData {

    private String operation, nameBuy, nameSell;
    private Double quantityBuy, quantitySell;

    public RecyclerViewTransactionsData(String operation, String nameBuy, Double quantityBuy, String nameSell, Double quantitySell){
        this.operation = operation;
        this.nameBuy = nameBuy;
        this.quantityBuy = quantityBuy;
        this.nameSell = nameSell;
        this.quantitySell = quantitySell;
    }

    public String getOperation() {
        return operation;
    }

    public String getNameBuy() {
        return nameBuy;
    }

    public Double getQuantityBuy() {
        return quantityBuy;
    }

    public String getNameSell() {
        return nameSell;
    }

    public Double getQuantitySell() {
        return quantitySell;
    }
}