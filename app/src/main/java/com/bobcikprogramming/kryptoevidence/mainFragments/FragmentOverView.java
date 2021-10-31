package com.bobcikprogramming.kryptoevidence.mainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.R;


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
        return view;
    }

    private void setupUIViews(){

    }

    @Override
    public void onClick(View view) {
    }
}