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
import java.util.List;

public class RecyclerViewTransactionsInfo extends RecyclerView.Adapter<RecyclerViewTransactionsInfo.ViewHolder>{

    private ArrayList<RecyclerViewTransactionInfoList> dataList;
    private Context context;

    public RecyclerViewTransactionsInfo(Context context, ArrayList<RecyclerViewTransactionInfoList> dataList) {
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

        if(dataList.get(position).getRightValue() != null){
            holder.infoDesRight.setText(dataList.get(position).getRightDesc());
            holder.infoValueRight.setText(dataList.get(position).getRightValue());
        }else{
            holder.infoLayoutRight.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView infoDesLeft, infoValueLeft, infoDesRight, infoValueRight;
        private LinearLayout item, infoLayoutRight;

        public ViewHolder(View itemView) {
            super(itemView);

            infoDesLeft = itemView.findViewById(R.id.infoDesLeft);
            infoValueLeft = itemView.findViewById(R.id.infoValueLeft);
            infoDesRight = itemView.findViewById(R.id.infoDesRight);
            infoValueRight = itemView.findViewById(R.id.infoValueRight);

            item = itemView.findViewById(R.id.layoutTransactionsInfo);
            infoLayoutRight = itemView.findViewById(R.id.infoLayoutRight);

        }
    }
}

class RecyclerViewTransactionInfoList {
    private String leftDesc, leftValue, rightDesc, rightValue;
    public RecyclerViewTransactionInfoList(){

    }

    public String getLeftDesc() {
        return leftDesc;
    }

    public void setLeftDesc(String leftDesc) {
        this.leftDesc = leftDesc;
    }

    public String getLeftValue() {
        return leftValue;
    }

    public void setLeftValue(String leftValue) {
        this.leftValue = leftValue;
    }

    public String getRightDesc() {
        return rightDesc;
    }

    public void setRightDesc(String rightDesc) {
        this.rightDesc = rightDesc;
    }

    public String getRightValue() {
        return rightValue;
    }

    public void setRightValue(String rightValue) {
        this.rightValue = rightValue;
    }
}