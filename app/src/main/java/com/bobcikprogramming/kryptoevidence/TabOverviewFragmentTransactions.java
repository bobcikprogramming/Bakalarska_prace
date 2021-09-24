package com.bobcikprogramming.kryptoevidence;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.database.AppDatabase;
import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;

import java.util.ArrayList;
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
        adapter.setTransactionData(dataFromDatabase);
    }
}