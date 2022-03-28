package com.bobcikprogramming.kryptoevidence.View;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bobcikprogramming.kryptoevidence.Controller.CalendarManager;
import com.bobcikprogramming.kryptoevidence.Controller.FragmentPDFController;
import com.bobcikprogramming.kryptoevidence.Controller.MonthYearPickerDialog;
import com.bobcikprogramming.kryptoevidence.R;


public class FragmentPDF extends Fragment {

    private RecyclerView recyclerView;
    private ImageView btnAdd;

    private String selectedYear;

    private CalendarManager calendar;
    private FragmentPDFController controller;

    public FragmentPDF() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pdf, container, false);

        calendar = new CalendarManager();
        controller = new FragmentPDFController(getContext(), getActivity());

        setupUIViews(view);

        recyclerView.setAdapter(controller.getAdapter());

        openCalendar();

        return view;
    }

    private void setupUIViews(View view){
        recyclerView = view.findViewById(R.id.recyclerViewPDF);
        LinearLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        btnAdd = view.findViewById(R.id.btnAdd);
    }

    public void openCalendar(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthYearPickerDialog newFragment = new MonthYearPickerDialog(controller);
                newFragment.show(getChildFragmentManager(), "DatePicker");
            }
        });
    }

}