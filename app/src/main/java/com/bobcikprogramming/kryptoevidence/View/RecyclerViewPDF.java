package com.bobcikprogramming.kryptoevidence.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bobcikprogramming.kryptoevidence.Controller.RecyclerViewPDFList;
import com.bobcikprogramming.kryptoevidence.R;

import java.util.ArrayList;

public class RecyclerViewPDF extends RecyclerView.Adapter<RecyclerViewPDF.ViewHolder>{

    private ArrayList<RecyclerViewPDFList> dataList;
    private Context context;

    public RecyclerViewPDF(Context context, ArrayList<RecyclerViewPDFList> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_pdf, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvYear.setText(dataList.get(position).getYear());
        holder.tvDate.setText(dataList.get(position).getDate());
    }

    public void setDataList(ArrayList<RecyclerViewPDFList> dataList) {
        this.dataList = dataList;
        notifyItemInserted(dataList.size()-1);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvYear, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);

            tvYear = itemView.findViewById(R.id.tvYear);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}

