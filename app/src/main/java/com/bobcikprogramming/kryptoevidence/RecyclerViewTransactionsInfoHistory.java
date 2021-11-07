package com.bobcikprogramming.kryptoevidence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewTransactionsInfoHistory extends RecyclerView.Adapter<RecyclerViewTransactionsInfoHistory.ViewHolder>{

    private ArrayList<TransactionHistoryList> dataList;
    private Context context;

    public RecyclerViewTransactionsInfoHistory(Context context, ArrayList<TransactionHistoryList> dataList) {
        this.context = context;
        this.dataList = dataList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_transaction_info_history, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TransactionHistoryList history = dataList.get(position);

        holder.histValueDateTimeOfChange.setText(history.changeValueDate + " v " + history.changeValueTime);

        switch(history.transactionType){
            case "Nákup":
                setupUI(history.quantityBoughtValue != null, holder.histDescFirstRow, holder.histValueFirstRow, holder.layoutFirstRow, history.quantityBoughtDesc, history.quantityBoughtValue);
                setupUI(history.priceBoughtValue != null, holder.histDescSecondRow, holder.histValueSecondRow, holder.layoutSecondRow, history.priceBoughtDesc, history.priceBoughtValue);
                setupUI(history.currencyValue != null, holder.histDescThirdRow, holder.histValueThirdRow, holder.layoutThirdRow, history.currencyDesc, history.currencyValue);
                setupUI(history.feeValue != null, holder.histDescFourthRow, holder.histValueFourthRow, holder.layoutFourthRow, history.feeDesc, history.feeValue);
                setupUI(history.dateValue != null, holder.histDescFifthRow, holder.histValueFifthRow, holder.layoutFifthRow, history.dateDesc, history.dateValue);
                setupUI(history.timeValue != null, holder.histDescSixthRow, holder.histValueSixthRow, holder.layoutSixthRow, history.timeDesc, history.timeValue);
                setupUI(history.noteValue != null, holder.histDescSeventhRow, holder.histValueSeventhRow, holder.layoutSeventhRow, history.noteDesc, history.noteValue);
                holder.histValueSeventhRow.setSingleLine(false);
                setupUI(false, holder.histDescEighthRow, holder.histValueEighthRow, holder.layoutEighthRow, "", "");
                setupUI(false, holder.histDescNinthRow, holder.histValueNinthRow, holder.layoutNinthRow, "", "");
                setupUI(false, holder.histDescTenthRow, holder.histValueTenthRow, holder.layoutTenthRow, "", "");
                break;
            case "Prodej":
                setupUI(history.quantitySoldValue != null, holder.histDescFirstRow, holder.histValueFirstRow, holder.layoutFirstRow, history.quantitySoldDesc, history.quantitySoldValue);
                setupUI(history.priceSoldValue != null, holder.histDescSecondRow, holder.histValueSecondRow, holder.layoutSecondRow, history.priceSoldDesc, history.priceSoldValue);
                setupUI(history.currencyValue != null, holder.histDescThirdRow, holder.histValueThirdRow, holder.layoutThirdRow, history.currencyDesc, history.currencyValue);
                setupUI(history.feeValue != null, holder.histDescFourthRow, holder.histValueFourthRow, holder.layoutFourthRow, history.feeDesc, history.feeValue);
                setupUI(history.dateValue != null, holder.histDescFifthRow, holder.histValueFifthRow, holder.layoutFifthRow, history.dateDesc, history.dateValue);
                setupUI(history.timeValue != null, holder.histDescSixthRow, holder.histValueSixthRow, holder.layoutSixthRow, history.timeDesc, history.timeValue);
                setupUI(history.noteValue != null, holder.histDescSeventhRow, holder.histValueSeventhRow, holder.layoutSeventhRow, history.noteDesc, history.noteValue);
                holder.histValueSeventhRow.setSingleLine(false);
                setupUI(false, holder.histDescEighthRow, holder.histValueEighthRow, holder.layoutEighthRow, "", "");
                setupUI(false, holder.histDescNinthRow, holder.histValueNinthRow, holder.layoutNinthRow, "", "");
                setupUI(false, holder.histDescTenthRow, holder.histValueTenthRow, holder.layoutTenthRow, "", "");
                break;
            case "Směna":
                setupUI(history.quantityBoughtValue != null, holder.histDescFirstRow, holder.histValueFirstRow, holder.layoutFirstRow, history.quantityBoughtDesc, history.quantityBoughtValue);
                setupUI(history.priceBoughtValue != null, holder.histDescSecondRow, holder.histValueSecondRow, holder.layoutSecondRow, history.priceBoughtDesc, history.priceBoughtValue);
                setupUI(history.currencyValue != null, holder.histDescThirdRow, holder.histValueThirdRow, holder.layoutThirdRow, history.currencyDesc, history.currencyValue);
                setupUI(history.longNameSoldValue != null, holder.histDescFourthRow, holder.histValueFourthRow, holder.layoutFourthRow, history.longNameSoldDesc, history.longNameSoldValue);
                setupUI(history.quantitySoldValue != null, holder.histDescFifthRow, holder.histValueFifthRow, holder.layoutFifthRow, history.quantitySoldDesc, history.quantitySoldValue);
                setupUI(history.priceSoldValue != null, holder.histDescSixthRow, holder.histValueSixthRow, holder.layoutSixthRow, history.priceSoldDesc, history.priceSoldValue);
                setupUI(history.feeValue != null, holder.histDescSeventhRow, holder.histValueSeventhRow, holder.layoutSeventhRow, history.feeDesc, history.feeValue);
                setupUI(history.dateValue != null, holder.histDescEighthRow, holder.histValueEighthRow, holder.layoutEighthRow, history.dateDesc, history.dateValue);
                setupUI(history.timeValue != null, holder.histDescNinthRow, holder.histValueNinthRow, holder.layoutNinthRow, history.timeDesc, history.timeValue);
                setupUI(history.noteValue != null, holder.histDescTenthRow, holder.histValueTenthRow, holder.layoutTenthRow, history.noteDesc, history.noteValue);
                holder.histValueTenthRow.setSingleLine(false);
                break;
        }

        if((dataList.size()-1) == position){
            holder.lastUnderline.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void setupUI(boolean visible, TextView tvDescription, TextView tvValue, LinearLayout layout, String description, String value){
        if(visible) {
            layout.setVisibility(View.VISIBLE);
            tvDescription.setText(description);
            tvValue.setText(value);
        }else{
            layout.setVisibility(View.GONE);
        }
    }

    public void updateDataList(ArrayList<TransactionHistoryList> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView histDescFirstRow, histDescSecondRow, histDescThirdRow, histDescFourthRow, histDescFifthRow, histDescSixthRow, histDescSeventhRow, histDescEighthRow, histDescNinthRow, histDescTenthRow;
        private TextView histValueDateTimeOfChange, histValueFirstRow, histValueSecondRow, histValueThirdRow, histValueFourthRow, histValueFifthRow, histValueSixthRow, histValueSeventhRow, histValueEighthRow, histValueNinthRow, histValueTenthRow;

        private LinearLayout item;
        private LinearLayout layoutFirstRow, layoutSecondRow, layoutThirdRow, layoutFourthRow, layoutFifthRow, layoutSixthRow, layoutSeventhRow, layoutEighthRow, layoutNinthRow, layoutTenthRow;
        private LinearLayout lastUnderline;

        public ViewHolder(View itemView) {
            super(itemView);

            histDescFirstRow = itemView.findViewById(R.id.histDescFirstRow);
            histDescSecondRow = itemView.findViewById(R.id.histDescSecondRow);
            histDescThirdRow = itemView.findViewById(R.id.histDescThirdRow);
            histDescFourthRow = itemView.findViewById(R.id.histDescFourthRow);
            histDescFifthRow = itemView.findViewById(R.id.histDescFifthRow);
            histDescSixthRow = itemView.findViewById(R.id.histDescSixthRow);
            histDescSeventhRow = itemView.findViewById(R.id.histDescSeventhRow);
            histDescEighthRow = itemView.findViewById(R.id.histDescEighthRow);
            histDescNinthRow = itemView.findViewById(R.id.histDescNinthRow);
            histDescTenthRow = itemView.findViewById(R.id.histDescTenthRow);

            histValueDateTimeOfChange = itemView.findViewById(R.id.histValueDateTimeOfChange);
            histValueFirstRow = itemView.findViewById(R.id.histValueFirstRow);
            histValueSecondRow = itemView.findViewById(R.id.histValueSecondRow);
            histValueThirdRow = itemView.findViewById(R.id.histValueThirdRow);
            histValueFourthRow = itemView.findViewById(R.id.histValueFourthRow);
            histValueFifthRow = itemView.findViewById(R.id.histValueFifthRow);
            histValueSixthRow = itemView.findViewById(R.id.histValueSixthRow);
            histValueSeventhRow = itemView.findViewById(R.id.histValueSeventhRow);
            histValueEighthRow = itemView.findViewById(R.id.histValueEighthRow);
            histValueNinthRow = itemView.findViewById(R.id.histValueNinthRow);
            histValueTenthRow = itemView.findViewById(R.id.histValueTenthRow);

            lastUnderline = itemView.findViewById(R.id.lastUnderline);

            layoutFirstRow = itemView.findViewById(R.id.layoutFirstRow);
            layoutSecondRow = itemView.findViewById(R.id.layoutSecondRow);
            layoutThirdRow = itemView.findViewById(R.id.layoutThirdRow);
            layoutFourthRow = itemView.findViewById(R.id.layoutFourthRow);
            layoutFifthRow = itemView.findViewById(R.id.layoutFifthRow);
            layoutSixthRow = itemView.findViewById(R.id.layoutSixthRow);
            layoutSeventhRow = itemView.findViewById(R.id.layoutSeventhRow);
            layoutEighthRow = itemView.findViewById(R.id.layoutEighthRow);
            layoutNinthRow = itemView.findViewById(R.id.layoutNinthRow);
            layoutTenthRow = itemView.findViewById(R.id.layoutTenthRow);

            item = itemView.findViewById(R.id.layoutTransactionsInfo);

        }
    }
}