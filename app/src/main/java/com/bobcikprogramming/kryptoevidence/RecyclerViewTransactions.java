package com.bobcikprogramming.kryptoevidence;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewTransactions extends RecyclerView.Adapter<RecyclerViewTransactions.ViewHolder>  {

    private ArrayList<RecyclerViewTransactionsData> dataList;
    private Context context;
    private boolean isEnable = false;

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
                if(!isEnable) {
                    Toast.makeText(view.getContext(), "TODO: Otevře možnost k mazání transakcí... Podrženo na itemu id: " + holder.getAdapterPosition(), Toast.LENGTH_LONG).show();
                    holder.rbDeleteItem.setVisibility(View.VISIBLE);
                    isEnable = true;
                }
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton rbItem = holder.rbDeleteItem;
                if(isEnable){
                    rbItem.setVisibility(View.VISIBLE);
                    if(rbItem.isChecked()){
                        rbItem.setChecked(false);
                        rbItem.setVisibility(View.INVISIBLE);
                    }else{
                        rbItem.setChecked(true);
                        rbItem.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewOperation, textViewNameBuy, textViewNameSell, textViewQuantityBuy, textViewQuantitySell;
        private RadioButton rbDeleteItem;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewOperation = itemView.findViewById(R.id.textViewOperation);
            textViewNameBuy = itemView.findViewById(R.id.textViewNameBuy);
            textViewNameSell = itemView.findViewById(R.id.textViewNameSell);
            textViewQuantityBuy = itemView.findViewById(R.id.textViewQuantityBuy);
            textViewQuantitySell = itemView.findViewById(R.id.textViewQuantitySell);

            rbDeleteItem = itemView.findViewById(R.id.radioButtonDeleteItemTransaction);
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