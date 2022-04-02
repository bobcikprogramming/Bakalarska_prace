package com.bobcikprogramming.kryptoevidence.View;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bobcikprogramming.kryptoevidence.Controller.CalendarManager;
import com.bobcikprogramming.kryptoevidence.Controller.RecyclerViewPDFList;
import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.PDFEntity;
import com.bobcikprogramming.kryptoevidence.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewPDF extends RecyclerView.Adapter<RecyclerViewPDF.ViewHolder>{

    private List<PDFEntity> dataList;
    private Context context;

    private CalendarManager calendar;

    public RecyclerViewPDF(Context context, List<PDFEntity> dataList) {
        this.context = context;
        this.dataList = dataList;

        calendar = new CalendarManager();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_pdf, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvYear.setText(dataList.get(position).year);
        holder.tvDate.setText(calendar.getDateFromMillis(dataList.get(position).date));

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePDF(dataList.get(holder.getAdapterPosition()).fileName);
            }
        });
    }

    public void setDataList(List<PDFEntity> dataList) {
        this.dataList = dataList;
        notifyItemInserted(dataList.size()-1);
    }

    private void deletePDF(String fileName){
        AppDatabase db = AppDatabase.getDbInstance(context);

        if(deletePDFFile(fileName)){
            db.databaseDao().deletePDFEntity(fileName);
        }
    }

    private boolean deletePDFFile(String fileName){
        String dirName = Environment.getExternalStorageDirectory() + "/kryptoevidence_pdf";
        File path = new File(dirName, fileName);
        File toDelete = path;
        if(toDelete.exists()){
            if(toDelete.delete()){
                Toast.makeText(context, "PDF záznam úspěšně smazán.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "PDF záznam se nepodařilo smazat. Opakujte prosím akci.", Toast.LENGTH_LONG).show();
                return false;
            }
        }else{
            return false;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvYear, tvDate;
        private ImageView btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            tvYear = itemView.findViewById(R.id.tvYear);
            tvDate = itemView.findViewById(R.id.tvDate);

            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

