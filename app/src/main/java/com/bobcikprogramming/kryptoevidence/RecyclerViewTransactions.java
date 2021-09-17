package com.bobcikprogramming.kryptoevidence;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewTransactions extends RecyclerView.Adapter<RecyclerViewTransactions.ViewHolder>  {

    private ArrayList<RecyclerViewTransactionsData> dataList;
    private Context context;

    public RecyclerViewTransactions(Context context/*, ArrayList<RecyclerViewRaidingAdvancedData> dataList*/) {
        this.context = context;
        //this.dataList = dataList;
        this.dataList = new ArrayList<>();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_transactions, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecyclerViewTransactionsData data = dataList.get(position);

        holder.textViewOperation.setText(data.getOperation());
        holder.textViewNameBuy.setText(data.getNameBuy());
        holder.textViewNameSell.setText(data.getNameSell());
        holder.textViewQuantityBuy.setText(data.getQuantityBuy().toString());
        holder.textViewQuantitySell.setText(data.getQuantitySell().toString());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(view.getContext(), "TODO: Otevře možnost k mazání transakcí... Podrženo na itemu id: "+holder.getAdapterPosition(), Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewOperation, textViewNameBuy, textViewNameSell, textViewQuantityBuy, textViewQuantitySell;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewOperation = (TextView) itemView.findViewById(R.id.textViewOperation);
            textViewNameBuy = (TextView) itemView.findViewById(R.id.textViewNameBuy);
            textViewNameSell = (TextView) itemView.findViewById(R.id.textViewNameSell);
            textViewQuantityBuy = (TextView) itemView.findViewById(R.id.textViewQuantityBuy);
            textViewQuantitySell = (TextView) itemView.findViewById(R.id.textViewQuantitySell);
        }
    }

    public void addToArray(RecyclerViewTransactionsData data){
        dataList.add(0, data);
        notifyItemInserted(0);
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