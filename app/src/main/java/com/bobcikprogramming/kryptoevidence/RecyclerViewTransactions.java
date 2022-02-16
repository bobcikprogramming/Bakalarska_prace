package com.bobcikprogramming.kryptoevidence;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;

import java.util.List;

public class RecyclerViewTransactions extends RecyclerView.Adapter<RecyclerViewTransactions.ViewHolder>{

    private List<TransactionWithPhotos> dataList;
    private Context context;
    private View.OnClickListener myClickListener;

    public RecyclerViewTransactions(Context context, View.OnClickListener myClickListener) {
        this.context = context;
        this. myClickListener = myClickListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_transactions, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TransactionWithPhotos data = this.dataList.get(position);
        TransactionEntity transaction = data.transaction;

        holder.textViewOperation.setText(transaction.transactionType);
        holder.textViewDate.setText(transaction.date);
        //holder.textViewTime.setText(transaction.time);

        changeItemViewByTransactionType(transaction.transactionType, holder);
        loadDataToItems(transaction.transactionType, holder, data);

        holder.itemView.setTag(position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewOperation, textViewDate, textViewTime, tvNameFC, tvNameSC, tvQuantityFC, tvQuantitySC, tvDesPriceFL, tvDesPriceSL, tvPriceFL, tvPriceSL, tvDescriptionFC, tvDescriptionSC;
        private LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewOperation = itemView.findViewById(R.id.textViewOperation);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            //textViewTime = itemView.findViewById(R.id.textViewTime);
            tvNameFC = itemView.findViewById(R.id.textViewNameFirstColumn);
            tvNameSC = itemView.findViewById(R.id.textViewNameSecondColumn);
            tvQuantityFC = itemView.findViewById(R.id.textViewQuantityFirstColumn);
            tvQuantitySC = itemView.findViewById(R.id.textViewQuantitySecondColumn);
            /*tvDesPriceFL = itemView.findViewById(R.id.textViewDescriptionPriceBuy);
            tvDesPriceSL = itemView.findViewById(R.id.textViewDescriptionPriceSell);
            tvPriceFL = itemView.findViewById(R.id.textViewPriceFirst);
            tvPriceSL = itemView.findViewById(R.id.textViewPriceSecondLine);*/
            tvDescriptionFC = itemView.findViewById(R.id.textViewDescriptionFirstColumn);
            tvDescriptionSC = itemView.findViewById(R.id.textViewDescriptionSecondColumn);

            item = itemView.findViewById(R.id.layoutTransactions);
            itemView.setOnClickListener(myClickListener);
        }
    }

    /*public void addToArray(RecyclerViewTransactionsData data){
        dataList.add(0, data);
        notifyItemInserted(0);
    }*/

    private void changeItemViewByTransactionType(String transactionType, ViewHolder holder){
        switch (transactionType) {
            case "Nákup":
                holder.textViewOperation.setTextColor(ContextCompat.getColor(context, R.color.green));
                /*holder.tvDesPriceFL.setVisibility(View.VISIBLE);
                holder.tvPriceFL.setVisibility(View.VISIBLE);
                holder.tvDesPriceSL.setVisibility(View.GONE);
                holder.tvPriceSL.setVisibility(View.GONE);*/
                holder.tvDescriptionFC.setText("Koupeno");
                holder.tvDescriptionSC.setText("Platba");
                break;
            case "Prodej":
                holder.textViewOperation.setTextColor(ContextCompat.getColor(context, R.color.red));
                /*holder.tvDesPriceFL.setVisibility(View.VISIBLE);
                holder.tvPriceFL.setVisibility(View.VISIBLE);
                holder.tvDesPriceSL.setVisibility(View.GONE);
                holder.tvPriceSL.setVisibility(View.GONE);*/
                holder.tvDescriptionFC.setText("Prodáno");
                holder.tvDescriptionSC.setText("Zisk");
                break;
            case "Směna":
                holder.textViewOperation.setTextColor(ContextCompat.getColor(context, R.color.blue));
                /*holder.tvDesPriceFL.setVisibility(View.VISIBLE);
                holder.tvPriceFL.setVisibility(View.VISIBLE);
                holder.tvDesPriceSL.setVisibility(View.VISIBLE);
                holder.tvPriceSL.setVisibility(View.VISIBLE);*/
                holder.tvDescriptionFC.setText("Koupeno");
                holder.tvDescriptionSC.setText("Prodáno");
                break;
        }
    }

    private void loadDataToItems(String transactionType, ViewHolder holder, TransactionWithPhotos data){
        TransactionEntity transaction = data.transaction;
        switch (transactionType){
            case "Nákup":
                holder.tvNameFC.setText(transaction.longNameBought);
                holder.tvQuantityFC.setText(editNumberForTextView(transaction.quantityBought));
                //holder.tvPriceFL.setText(String.valueOf(transaction.priceBought + " " + transaction.currency));
                holder.tvNameSC.setText(transaction.currency);
                holder.tvQuantitySC.setText(editNumberForTextView(transaction.quantitySold));
                break;
            case "Prodej":
                holder.tvNameFC.setText(transaction.longNameSold);
                holder.tvQuantityFC.setText(editNumberForTextView(transaction.quantitySold));
                //holder.tvPriceFL.setText(String.valueOf(transaction.priceSold + " " + transaction.currency));
                holder.tvNameSC.setText(transaction.currency);
                holder.tvQuantitySC.setText(editNumberForTextView(transaction.quantityBought));
                break;
            case "Směna":
                holder.tvNameFC.setText(transaction.longNameBought);
                holder.tvQuantityFC.setText(editNumberForTextView(transaction.quantityBought));
                //holder.tvPriceFL.setText(String.valueOf(transaction.priceSold + " " + transaction.currency));
                holder.tvNameSC.setText(transaction.shortNameSold);
                holder.tvQuantitySC.setText(editNumberForTextView(transaction.quantitySold));
                //holder.tvPriceSL.setText(String.valueOf(transaction.priceBought + " " + transaction.currency));
                break;

        }
    }

    public void setTransactionData(List<TransactionWithPhotos> transactionData){
        this.dataList = transactionData;
        Toast.makeText(context, String.valueOf(this.dataList.size()), Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
    }

    public void removeFromArray(int position){
        dataList.remove(position);
        notifyDataSetChanged();
    }

    private String editNumberForTextView(String number){
        Double quantityBought = Double.parseDouble(number);
        if(quantityBought > 999999.0){
            number = "999 999+";
        }else if(number.contains(".")){
            if(number.length() > 7) {
                int lenOfInteger = number.split("\\.")[0].length();
                int toCut = 6 - lenOfInteger;
                double round = Math.pow(10, toCut);
                quantityBought = (double) Math.round(quantityBought * round) / round;
                number = "~" + quantityBought;
            }
        }else{
            // https://stackoverflow.com/a/11149356
            number = number.replaceAll("...(?!$)", "$0 ");
        }
        return number;
    }
}