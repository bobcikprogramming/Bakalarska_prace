package com.bobcikprogramming.kryptoevidence;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.database.AppDatabase;
import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionHistoryEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithHistory;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithPhotos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionViewer extends AppCompatActivity {

    private ImageView btnBack, btnEdit;
    private ViewPager transactionViewer;

    private ViewPagerAdapterTransaction viewPagerAdapter;
    private TransactionWithPhotos transactionWithPhotos;

    List<TransactionWithPhotos> dataFromDatabase;
    List<TransactionHistoryEntity> dataFromDatabaseHistory;

    private boolean changed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_viewer);
        int position = (int) getIntent().getSerializableExtra("position");

        changed = false;

        transactionViewer = findViewById(R.id.viewPagerTransaction);

        loadDataFromDb();

        viewPagerAdapter = new ViewPagerAdapterTransaction(TransactionViewer.this, dataFromDatabase, dataFromDatabaseHistory);
        transactionViewer.setAdapter(viewPagerAdapter);
        transactionViewer.setCurrentItem(position);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("changed", changed);
                setResult(RESULT_OK, intent );
                finish();
            }
        });

        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("------------------------------"+transactionViewer.getCurrentItem());
                Intent infoActivity = new Intent(TransactionViewer.this, TransactionEdit.class);
                infoActivity.putExtra("transactionID", dataFromDatabase.get(transactionViewer.getCurrentItem()).transaction.uidTransaction);
                infoActivityTransactionEditLauncher.launch(infoActivity);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("change", changed);
        setResult(RESULT_OK, intent );
        finish();
    }

    private void loadDataFromDb(){
        AppDatabase db = AppDatabase.getDbInstance(this);
        dataFromDatabase = db.databaseDao().getAll();
        sortListByTime(dataFromDatabase);
        sortListByDate(dataFromDatabase);

        dataFromDatabaseHistory = db.databaseDao().getHistory();
    }

    private void sortListByDate(List<TransactionWithPhotos> data){
        TransactionWithPhotos tmp;
        for(int i = 0; i < data.size() - 1; i++){
            for(int j = 0; j < data.size() - i - 1; j++){
                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
                try{
                    Date dateFirst = format.parse(data.get(j).transaction.date);
                    Date dateSecond = format.parse(data.get(j+1).transaction.date);
                    if(dateFirst.compareTo(dateSecond) < 0){
                        tmp = data.get(j);
                        data.set(j, data.get(j+1));
                        data.set(j+1, tmp);
                    }
                }catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sortListByTime(List<TransactionWithPhotos> data){
        TransactionWithPhotos tmp;
        for(int i = 0; i < data.size() - 1; i++){
            for(int j = 0; j < data.size() - i - 1; j++){
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                try{
                    Date timeFirst = format.parse(data.get(j).transaction.time);
                    Date timeSecond = format.parse(data.get(j+1).transaction.time);
                    if(timeFirst.compareTo(timeSecond) < 0){
                        tmp = data.get(j);
                        data.set(j, data.get(j+1));
                        data.set(j+1, tmp);
                    }
                }catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    ActivityResultLauncher<Intent> infoActivityTransactionEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data = result.getData();
                    changed = data.getBooleanExtra("changed", false);
                    boolean deleted = data.getBooleanExtra("deleted", false);
                    if(changed){
                        loadDataFromDb();
                        if(deleted){
                            Intent intent = new Intent();
                            intent.putExtra("changed", changed);
                            setResult(RESULT_OK, intent );
                            finish();
                        }else {
                            viewPagerAdapter.updateDatalists(dataFromDatabase, dataFromDatabaseHistory, transactionViewer.getCurrentItem());
                        }
                    }
                }
            });
}