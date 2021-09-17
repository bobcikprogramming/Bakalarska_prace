package com.bobcikprogramming.kryptoevidence;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class TabOverviewFragmentTransactions extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private Button btnAdd;
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



        return view;
    }

    private void setupUIViews(){
        btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewTransaction);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAdd:
                RecyclerViewTransactionsData dataToDataList = new RecyclerViewTransactionsData("Nákup", "BTC", 0.005178, "EUR", 100.0);
                //dataList.add(dataToDataList);
                adapter.addToArray(dataToDataList);
        }
    }
}