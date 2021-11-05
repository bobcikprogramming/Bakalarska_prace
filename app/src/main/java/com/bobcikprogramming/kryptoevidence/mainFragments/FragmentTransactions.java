package com.bobcikprogramming.kryptoevidence.mainFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.CryptoSelection;
import com.bobcikprogramming.kryptoevidence.MainActivity;
import com.bobcikprogramming.kryptoevidence.PhotoViewer;
import com.bobcikprogramming.kryptoevidence.R;
import com.bobcikprogramming.kryptoevidence.RecyclerViewTransactions;
import com.bobcikprogramming.kryptoevidence.TransactionInfo;
import com.bobcikprogramming.kryptoevidence.TransactionViewer;
import com.bobcikprogramming.kryptoevidence.database.AppDatabase;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithPhotos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FragmentTransactions extends Fragment {

    private RecyclerView recyclerView;
    private View view;
    private ImageView btnAdd;

    private List<TransactionWithPhotos> dataFromDatabase;
    private RecyclerViewTransactions adapter;

    public FragmentTransactions() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_transactions, container, false);

        setupUIViews();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getContext(), CryptoSelection.class);
                infoActivityResultLauncher.launch(myIntent);
            }
        });

        adapter = new RecyclerViewTransactions((getActivity()), myClickListener);
        recyclerView.setAdapter(adapter);
        loadDataFromDb();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).selectBottomMenu(R.id.transactions); //change value depending on your bottom menu position
    }

    // https://stackoverflow.com/a/45711180
    private View.OnClickListener myClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = (int) view.getTag();
            /*Intent infoActivity = new Intent(getContext(), TransactionInfo.class);
            infoActivity.putExtra("transactionID", dataFromDatabase.get(position).transaction.uidTransaction);
            infoActivityResultLauncher.launch(infoActivity);*/
            Intent infoActivity = new Intent(getContext(), TransactionViewer.class);
            startActivity(infoActivity);
        }
    };

    private void setupUIViews(){
        recyclerView = view.findViewById(R.id.recyclerViewTransaction);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        btnAdd = view.findViewById(R.id.btnAdd);

    }

    private void loadDataFromDb(){
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        dataFromDatabase = db.databaseDao().getAll();
        sortListByTime(dataFromDatabase);
        sortListByDate(dataFromDatabase);
        adapter.setTransactionData(dataFromDatabase);
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

    ActivityResultLauncher<Intent> infoActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data = result.getData();
                    boolean changed = data.getBooleanExtra("changed", false);
                    if(changed){
                        loadDataFromDb();
                    }
                }
            });

}