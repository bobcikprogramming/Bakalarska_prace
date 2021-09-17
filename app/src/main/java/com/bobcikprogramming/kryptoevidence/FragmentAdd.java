package com.bobcikprogramming.kryptoevidence;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentAdd extends Fragment implements View.OnClickListener {

    private LinearLayout btnBuy, btnSell, btnChange;
    private TextView txBuy, txSell, txChange;
    private View view;

    public FragmentAdd() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add, container, false);
        setupUIViews();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabBuyFragmentAdd()).commit();
        txBuy.setTextColor(ContextCompat.getColor(getContext(), R.color.navBarSelect));

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).selectBottomMenu(R.id.add); //change value depending on your bottom menu position
    }

    private void setupUIViews(){
        btnBuy = view.findViewById(R.id.tabButtonBuy);
        btnSell = view.findViewById(R.id.tabButtonSell);
        btnChange = view.findViewById(R.id.tabButtonChange);
        txBuy = view.findViewById(R.id.tabTextViewBuy);
        txSell = view.findViewById(R.id.tabTextViewSell);
        txChange = view.findViewById(R.id.tabTextViewChange);

        btnBuy.setOnClickListener(this);
        btnSell.setOnClickListener(this);
        btnChange.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tabButtonBuy:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabBuyFragmentAdd()).commit();
                resetColor();
                txBuy.setTextColor(ContextCompat.getColor(getContext(), R.color.navBarSelect));
                break;
            case R.id.tabButtonSell:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabSellFragmentBuy()).commit();
                resetColor();
                txSell.setTextColor(ContextCompat.getColor(getContext(), R.color.navBarSelect));
                break;
            case R.id.tabButtonChange:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabChangeFragmentAdd()).commit();
                resetColor();
                txChange.setTextColor(ContextCompat.getColor(getContext(), R.color.navBarSelect));
                break;
        }
    }

    private void resetColor(){
        txBuy.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        txSell.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        txChange.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }
}