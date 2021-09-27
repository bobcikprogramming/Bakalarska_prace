package com.bobcikprogramming.kryptoevidence;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bobcikprogramming.kryptoevidence.database.AppDatabase;
import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TabOverviewFragmentTransactions extends Fragment {

    private RecyclerView recyclerView;
    private View view;

    private ArrayList<RecyclerViewTransactionsData> dataList = new ArrayList<>();
    private RecyclerViewTransactions adapter;

    public TabOverviewFragmentTransactions() {
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
        view = inflater.inflate(R.layout.fragment_overview_tab_transactions, container, false);
        setupUIViews();

        adapter = new RecyclerViewTransactions((getActivity()));
        recyclerView.setAdapter(adapter);
        loadDataFromDb();


        return view;
    }

    private void setupUIViews(){
        recyclerView = view.findViewById(R.id.recyclerViewTransaction);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

    }

    private void loadDataFromDb(){
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        List<TransactionEntity> dataFromDatabase = db.databaseDao().getAll();
        sortList(dataFromDatabase);
        adapter.setTransactionData(dataFromDatabase);
    }

    private void sortList(List<TransactionEntity> data){
        TransactionEntity tmp;
        for(int i = 0; i < data.size() - 1; i++){
            for(int j = 0; j < data.size() - i - 1; j++){
                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
                try{
                    Date dateFirst = format.parse(data.get(j).date);
                    Date dateSecond = format.parse(data.get(j+1).date);
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
}