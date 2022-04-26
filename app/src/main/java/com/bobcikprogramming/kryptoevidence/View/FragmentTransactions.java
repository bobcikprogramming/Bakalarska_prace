package com.bobcikprogramming.kryptoevidence.View;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.Controller.CalendarManager;
import com.bobcikprogramming.kryptoevidence.Controller.FragmentTransactionsController;
import com.bobcikprogramming.kryptoevidence.Controller.MainActivity;
import com.bobcikprogramming.kryptoevidence.R;

import java.util.Calendar;

public class FragmentTransactions extends Fragment implements View.OnClickListener{

    private RecyclerView recyclerView;
    private View view;
    private ImageView btnAdd, btnFilter, btnReset;
    private TextView tvHeadline, tvDateFrom, tvDateTo;
    private LinearLayout layoutFilter;

    private boolean filterActived;
    private Calendar calendarDateTo;
    private Calendar calendarDateFrom;

    private RecyclerViewTransactions adapter;

    private DatePickerDialog.OnDateSetListener dateSetListenerDateFrom;
    private DatePickerDialog.OnDateSetListener dateSetListenerDateTo;

    private FragmentTransactionsController controller;
    private CalendarManager calendar;

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

        controller = new FragmentTransactionsController();
        calendar = new CalendarManager();
        filterActived = false;
        calendarDateFrom = Calendar.getInstance();
        calendarDateTo = Calendar.getInstance();

        setupUIViews();
        openCalendarForDateFrom();
        openCalendarForDateTo();

        adapter = new RecyclerViewTransactions((getActivity()), myClickListener);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        controller.loadDataFromDb(getContext(), adapter, calendarDateFrom, calendarDateTo);
        ((MainActivity)getActivity()).selectBottomMenu(R.id.transactions);
    }

    /**
     * Metoda zpracovávající reakci na kliknutí na daný prvek
     * @param view Základní prvek UI komponent
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnFilter:
                if(filterActived){
                    setFilter(R.anim.slide_left, R.anim.slide_right, R.drawable.ic_filter, View.GONE, View.GONE, View.VISIBLE);
                }else{
                    setFilter(R.anim.slide_right, R.anim.slide_left, R.drawable.ic_close, View.VISIBLE, View.VISIBLE, View.GONE);
                }
                filterActived = !filterActived;
                break;
            case R.id.btnReset:
                AnimatedVectorDrawable animatedVectorDrawableRefresh = (AnimatedVectorDrawable) btnReset.getDrawable();
                animatedVectorDrawableRefresh.start();

                resetFilterValues();
                break;
            case R.id.btnAdd:
                Intent newTransaction = new Intent(getContext(), CryptoSelection.class);
                //activityResultLauncher.launch(newTransaction);
                startActivity(newTransaction);
                break;
        }
    }

    /**
     * Metoda pro inicializování prvků UI
     */
    private void setupUIViews(){
        recyclerView = view.findViewById(R.id.recyclerViewTransaction);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        tvHeadline = view.findViewById(R.id.tvHeadline);
        tvDateFrom = view.findViewById(R.id.tvDateFrom);
        tvDateTo = view.findViewById(R.id.tvDateTo);

        btnAdd = view.findViewById(R.id.btnAdd);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnReset = view.findViewById(R.id.btnReset);

        btnAdd.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        layoutFilter = view.findViewById(R.id.layoutFilter);
    }

    /**
     * Metoda pro nastavení viditelnosti layoutu s filtrem
     * @param animLayout ID animace pro layout
     * @param animTextview ID animace pro text view
     * @param filterIcon ID icony filtru
     * @param resetVisibility Nastavení viditelnosti
     * @param layoutVisibility Nastavení viditelnosti
     * @param textviewVisibility Nastavení viditelnosti
     */
    private void setFilter(int animLayout, int animTextview, int filterIcon, int resetVisibility, int layoutVisibility, int textviewVisibility){
        Animation layout = AnimationUtils.loadAnimation(getContext(), animLayout);
        Animation textview = AnimationUtils.loadAnimation(getContext(), animTextview);

        btnFilter.setImageResource(filterIcon);
        btnReset.setVisibility(resetVisibility);

        layoutFilter.startAnimation(layout);
        layoutFilter.setVisibility(layoutVisibility);

        tvHeadline.startAnimation(textview);
        tvHeadline.setVisibility(textviewVisibility);

        resetFilterValues();
    }

    /**
     * Metoda pro restartování hodnoty filtru
     */
    private void resetFilterValues(){
        calendarDateTo = Calendar.getInstance();
        calendarDateFrom = Calendar.getInstance();
        controller.setSetDateFrom(false);
        controller.setSetDateTo(false);
        controller.refreshAdapter(adapter, calendarDateFrom, calendarDateTo);

        tvDateFrom.setText("Nastavit");
        tvDateFrom.setTextColor(ContextCompat.getColor(getContext(), R.color.button));
        tvDateTo.setText("Nastavit");
        tvDateTo.setTextColor(ContextCompat.getColor(getContext(), R.color.button));
    }

    /**
     * Metoda slouží k předání pozice vybrané transakce z recyclerview do activity, jenž inicializovala aktuální activity
     */
    private View.OnClickListener myClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = controller.getIndexToShow((int) view.getTag());
            Intent infoActivity = new Intent(getActivity(), TransactionViewer.class);
            infoActivity.putExtra("position", position);
            startActivity(infoActivity);
        }
    };

    /**
     * Metoda pro otevření dialogového okna pro výběr data "OD"
     */
    public void openCalendarForDateFrom(){
        tvDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.openDateDialogWindowForFilter(true, dateSetListenerDateFrom, tvDateFrom, tvDateTo, getActivity(), calendarDateTo, controller);
            }
        });

        dateSetListenerDateFrom = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                tvDateFrom.setText(calendar.returnDate(year, month, day));

                calendarDateFrom.set(Calendar.YEAR, year);
                calendarDateFrom.set(Calendar.MONTH, month);
                calendarDateFrom.set(Calendar.DAY_OF_MONTH, day); //day of month -> protože měsíce mají různý počet dní

                controller.setSetDateFrom(true);
                controller.refreshAdapter(adapter, calendarDateFrom, calendarDateTo);
            }
        };
    }

    /**
     * Metoda pro otevření dialogového okna pro výběr data "DO"
     */
    public void openCalendarForDateTo(){
        tvDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.openDateDialogWindowForFilter(false, dateSetListenerDateTo, tvDateFrom, tvDateTo, getActivity(), calendarDateTo, controller);
            }
        });

        dateSetListenerDateTo = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                tvDateTo.setText(calendar.returnDate(year, month, day));

                calendarDateTo.set(Calendar.YEAR, year);
                calendarDateTo.set(Calendar.MONTH, month);
                calendarDateTo.set(Calendar.DAY_OF_MONTH, day); //day of month -> protože měsíce mají různý počet dní

                controller.setSetDateTo(true);

                /** https://stackoverflow.com/a/11430439 */
                if(calendarDateTo.getTimeInMillis() < calendarDateFrom.getTimeInMillis()){
                    tvDateFrom.setText("Nastavit");
                    tvDateFrom.setTextColor(ContextCompat.getColor(getContext(), R.color.button));

                    calendarDateFrom = Calendar.getInstance();
                    controller.setSetDateFrom(false);
                }

                controller.refreshAdapter(adapter, calendarDateFrom, calendarDateTo);
            }
        };
    }
}