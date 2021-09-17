package com.bobcikprogramming.kryptoevidence;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class FragmentOverView extends Fragment implements View.OnClickListener {

    private LinearLayout btnOverview, btnTransactions;
    private TextView txOverview, txTransactions;
    private View view;

    public FragmentOverView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_over_view, container, false);
        setupUIViews();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabOverviewFragmentOverview()).commit();
        txOverview.setTextColor(ContextCompat.getColor(getContext(), R.color.navBarSelect));
        return view;
    }

    private void setupUIViews(){
        btnOverview = view.findViewById(R.id.tabButtonOverview);
        btnTransactions = view.findViewById(R.id.tabButtonTransactions);
        txOverview = view.findViewById(R.id.tabTextViewOverview);
        txTransactions = view.findViewById(R.id.tabTextViewTransactions);

        btnOverview.setOnClickListener(this);
        btnTransactions.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tabButtonOverview:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabOverviewFragmentOverview()).commit();
                resetColor();
                txOverview.setTextColor(ContextCompat.getColor(getContext(), R.color.navBarSelect));
                break;
            case R.id.tabButtonTransactions:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabOverviewFragmentTransactions()).commit();
                resetColor();
                txTransactions.setTextColor(ContextCompat.getColor(getContext(), R.color.navBarSelect));
                break;
        }
    }

    private void resetColor(){
        txOverview.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        txTransactions.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }
}