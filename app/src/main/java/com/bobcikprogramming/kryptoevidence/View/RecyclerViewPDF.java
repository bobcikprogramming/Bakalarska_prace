package com.bobcikprogramming.kryptoevidence.View;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bobcikprogramming.kryptoevidence.BuildConfig;
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

        holder.pdfItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPDF(dataList.get(holder.getAdapterPosition()).fileName);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialogDelete(dataList.get(holder.getAdapterPosition()).fileName, holder.getAdapterPosition());
            }
        });
    }

    public void setDataList(List<PDFEntity> dataList) {
        this.dataList = dataList;
        notifyItemInserted(dataList.size()-1);
    }

    public void openPDF(String fileName){
        File file = new File(Environment.getExternalStorageDirectory() + "/kryptoevidence_pdf", fileName);
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file), "application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent = Intent.createChooser(target, "Open File");
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "Nebyla nalezena aplikace pro čtení PDF souboru.", Toast.LENGTH_LONG).show();
                // Instruct the user to install a PDF reader here, or something
            }
        }else{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            /** https://stackoverflow.com/a/59439316 */
            List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "Nebyla nalezena aplikace pro čtení PDF souboru.", Toast.LENGTH_LONG).show();
                // Instruct the user to install a PDF reader here, or something
            }
        }
    }

    private void deletePDF(String fileName, int position){
        AppDatabase db = AppDatabase.getDbInstance(context);

        if(deletePDFFile(fileName)){
            db.databaseDao().deletePDFEntity(fileName);
            dataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    private boolean deletePDFFile(String fileName){
        String dirName = Environment.getExternalStorageDirectory() + "/kryptoevidence_pdf";
        File toDelete = new File(dirName, fileName);
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

    private void confirmDialogDelete(String fileName, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialog);
        builder.setCancelable(true);
        builder.setTitle("Smazat PDF");
        builder.setMessage("Opravdu chcete smazat PDF záznam?");
        builder.setPositiveButton("Smazat",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePDF(fileName, position);
                    }
                });
        builder.setNegativeButton("Zrušit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvYear, tvDate;
        private ImageView btnDelete;
        private CardView pdfItem;

        public ViewHolder(View itemView) {
            super(itemView);

            tvYear = itemView.findViewById(R.id.tvYear);
            tvDate = itemView.findViewById(R.id.tvDate);

            pdfItem = itemView.findViewById(R.id.pdf_item);

            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

