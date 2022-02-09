package com.bobcikprogramming.kryptoevidence.mainFragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.CryptoSelection;
import com.bobcikprogramming.kryptoevidence.MainActivity;
import com.bobcikprogramming.kryptoevidence.R;
import com.bobcikprogramming.kryptoevidence.RecyclerViewTransactions;
import com.bobcikprogramming.kryptoevidence.TransactionViewer;
import com.bobcikprogramming.kryptoevidence.database.AppDatabase;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithHistory;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithPhotos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FragmentTransactions extends Fragment {

    private RecyclerView recyclerView;
    private View view;
    private ImageView btnAdd, btnFilter, btnReset;
    private TextView tvHeadline, tvDateFrom, tvDateTo;
    //private Spinner spinnerTransaction;
    private LinearLayout layoutFilter;

    boolean filterActived;

    private Calendar calendarDateTo;
    private Calendar calendarDateFrom;
    private boolean isSetDateFrom, isSetDateTo;

    private List<TransactionWithPhotos> dataFromDatabase;
    private RecyclerViewTransactions adapter;

    private DatePickerDialog.OnDateSetListener dateSetListenerDateFrom;
    private DatePickerDialog.OnDateSetListener dateSetListenerDateTo;

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

        filterActived = false;
        isSetDateFrom = false;
        isSetDateTo = false;
        calendarDateFrom = Calendar.getInstance();
        calendarDateTo = Calendar.getInstance();

        setupUIViews();

        openCalendarForDateFrom();
        openCalendarForDateTo();

        //spinnerTransaction.setAdapter(getSpinnerAdapter(R.array.transaction, R.layout.spinner_item_for_filter, R.layout.spinner_dropdown_item));

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filterActived){
                    Animation layoutShow = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right);
                    Animation layoutHide = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left);

                    btnFilter.setImageResource(R.drawable.ic_filter);
                    btnReset.setVisibility(View.GONE);

                    tvHeadline.startAnimation(layoutShow);
                    tvHeadline.setVisibility(View.VISIBLE);

                    layoutFilter.startAnimation(layoutHide);
                    layoutFilter.setVisibility(View.GONE);

                    calendarDateTo = Calendar.getInstance();
                    calendarDateFrom = Calendar.getInstance();
                    isSetDateFrom = false;
                    isSetDateTo = false;
                    adapter.setTransactionData(getDataToShow(dataFromDatabase));
                }else{
                    Animation layoutHide = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right);
                    Animation layoutShow = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left);

                    btnFilter.setImageResource(R.drawable.ic_close);
                    btnReset.setVisibility(View.VISIBLE);

                    tvHeadline.startAnimation(layoutShow);
                    tvHeadline.setVisibility(View.GONE);

                    layoutFilter.startAnimation(layoutHide);
                    layoutFilter.setVisibility(View.VISIBLE);

                    tvDateFrom.setText("Nastavit");
                    tvDateFrom.setTextColor(ContextCompat.getColor(getContext(), R.color.button));
                    tvDateTo.setText("Nastavit");
                    tvDateTo.setTextColor(ContextCompat.getColor(getContext(), R.color.button));

                    calendarDateTo = Calendar.getInstance();
                    calendarDateFrom = Calendar.getInstance();
                    isSetDateFrom = false;
                    isSetDateTo = false;
                    adapter.setTransactionData(getDataToShow(dataFromDatabase));
                }
                filterActived = !filterActived;
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimatedVectorDrawable animatedVectorDrawableRefresh =
                        (AnimatedVectorDrawable) btnReset.getDrawable();
                animatedVectorDrawableRefresh.start();

                tvDateFrom.setText("Nastavit");
                tvDateFrom.setTextColor(ContextCompat.getColor(getContext(), R.color.button));
                tvDateTo.setText("Nastavit");
                tvDateTo.setTextColor(ContextCompat.getColor(getContext(), R.color.button));

                calendarDateTo = Calendar.getInstance();
                calendarDateFrom = Calendar.getInstance();
                isSetDateFrom = false;
                isSetDateTo = false;
                adapter.setTransactionData(getDataToShow(dataFromDatabase));
            }
        });

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
            Intent infoActivity = new Intent(getContext(), TransactionViewer.class);
            infoActivity.putExtra("position", position);
            infoActivityResultLauncher.launch(infoActivity);
            /*Intent infoActivity = new Intent(getContext(), TransactionViewer.class);
            startActivity(infoActivity);*/
        }
    };

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

        //spinnerTransaction = view.findViewById(R.id.spinnerTransaction);

        layoutFilter = view.findViewById(R.id.layoutFilter);

    }

    private ArrayAdapter<CharSequence> getSpinnerAdapter(int itemId, int layoutId, int dropDownId){
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), itemId, layoutId);
        spinnerAdapter.setDropDownViewResource(dropDownId);
        return spinnerAdapter;
    }

    private void loadDataFromDb(){
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        dataFromDatabase = db.databaseDao().getAll();
        sortListByTime(dataFromDatabase);
        sortListByDate(dataFromDatabase);
        adapter.setTransactionData(getDataToShow(dataFromDatabase));
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

    public void openCalendarForDateFrom(){
        tvDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialogWindow(true, dateSetListenerDateFrom);
            }
        });

        dateSetListenerDateFrom = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                setDateToTextView(year, month, day, tvDateFrom);

                calendarDateFrom.set(Calendar.YEAR, year);
                calendarDateFrom.set(Calendar.MONTH, month);
                calendarDateFrom.set(Calendar.DAY_OF_MONTH, day); //day of month -> protože měsíce mají různý počet dní

                isSetDateFrom = true;

                adapter.setTransactionData(getDataToShow(dataFromDatabase));
            }
        };
    }

    public void openCalendarForDateTo(){
        tvDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialogWindow(false, dateSetListenerDateTo);
            }
        });

        dateSetListenerDateTo = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                setDateToTextView(year, month, day, tvDateTo);

                calendarDateTo.set(Calendar.YEAR, year);
                calendarDateTo.set(Calendar.MONTH, month);
                calendarDateTo.set(Calendar.DAY_OF_MONTH, day); //day of month -> protože měsíce mají různý počet dní

                isSetDateTo = true;

                if(calendarDateTo.getTimeInMillis() < calendarDateFrom.getTimeInMillis()){
                    tvDateFrom.setText("Nastavit");
                    tvDateFrom.setTextColor(ContextCompat.getColor(getContext(), R.color.button));

                    calendarDateFrom = Calendar.getInstance();
                    isSetDateFrom = false;
                }

                adapter.setTransactionData(getDataToShow(dataFromDatabase));
            }
        };
    }

    private void openDateDialogWindow(boolean isDateFrom, DatePickerDialog.OnDateSetListener dateSetListener){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH); //day of month -> protože měsíce mají různý počet dní

        if(isSetDateFrom && isDateFrom){
            String[] dateSplit = tvDateFrom.getText().toString().split("\\.");
            day = Integer.parseInt(dateSplit[0]);
            month = Integer.parseInt(dateSplit[1]) - 1;
            year = Integer.parseInt(dateSplit[2]);
        }

        if(isSetDateTo && !isDateFrom){
            String[] dateSplit = tvDateTo.getText().toString().split("\\.");
            day = Integer.parseInt(dateSplit[0]);
            month = Integer.parseInt(dateSplit[1]) - 1;
            year = Integer.parseInt(dateSplit[2]);
        }

        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(), R.style.TimeDatePicker, dateSetListener, year, month, day
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        long maxDate = isDateFrom ? calendarDateTo.getTimeInMillis() : System.currentTimeMillis(); //https://stackoverflow.com/a/11430439
        dialog.getDatePicker().setMaxDate(maxDate); // https://stackoverflow.com/a/20971151
        dialog.show();
    }

    private void setDateToTextView(int year, int month, int day, TextView textViewId){
        month = month + 1; // bere se od 0
        String date = day + "." + month + "." + year;
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy");
        SimpleDateFormat dateFormatSecond = new SimpleDateFormat("dd.MM.yyyy");
        try{
            Date dateFormatToShow = dateFormat.parse(date);
            textViewId.setText(dateFormatSecond.format(dateFormatToShow));
            textViewId.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
        catch (Exception e){
            System.err.println("Chyba při parsování data: "+e);
        }
    }

    private List<TransactionWithPhotos> getDataToShow(List<TransactionWithPhotos> dataFromDatabase){
        if(isSetDateFrom || isSetDateTo) {
            int yearFrom = calendarDateFrom.get(Calendar.YEAR);
            int monthFrom = calendarDateFrom.get(Calendar.MONTH) + 1;
            int dayFrom = calendarDateFrom.get(Calendar.DAY_OF_MONTH);
            String dateFrom = dayFrom + "." + monthFrom + "." + yearFrom;

            int yearTo = calendarDateTo.get(Calendar.YEAR);
            int monthTo = calendarDateTo.get(Calendar.MONTH) + 1;
            int dayTo = calendarDateTo.get(Calendar.DAY_OF_MONTH);
            String dateTo = dayTo + "." + monthTo + "." + yearTo;

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            List<TransactionWithPhotos> newDataList = new ArrayList<TransactionWithPhotos>();

            if(isSetDateFrom && isSetDateTo){
                Date dateFromParsed = null;
                Date dateToParsed = null;
                try {
                    dateFromParsed = dateFormat.parse(dateFrom);
                    dateToParsed = dateFormat.parse(dateTo);
                } catch (Exception e) {
                    System.err.println("Chyba při parsování data: " + e);
                    return dataFromDatabase;
                }

                for (TransactionWithPhotos transaction : dataFromDatabase) {
                    try {
                        Date dateOfTransaction = dateFormat.parse(transaction.transaction.date);
                        // https://stackoverflow.com/questions/2592501/how-to-compare-dates-in-java
                        if (!dateOfTransaction.before(dateFromParsed) && !dateOfTransaction.after(dateToParsed)) {
                            newDataList.add(transaction);
                        }
                    } catch (Exception e) {
                        System.err.println("Chyba při parsování data: " + e);
                        return dataFromDatabase;
                    }
                }
            }
            else if (isSetDateFrom) {
                Date dateFromParsed = null;
                try {
                    dateFromParsed = dateFormat.parse(dateFrom);
                } catch (Exception e) {
                    System.err.println("Chyba při parsování data: " + e);
                    return dataFromDatabase;
                }

                for (TransactionWithPhotos transaction : dataFromDatabase) {
                    try {
                        Date dateOfTransaction = dateFormat.parse(transaction.transaction.date);
                        // https://stackoverflow.com/questions/2592501/how-to-compare-dates-in-java
                        if (!dateOfTransaction.before(dateFromParsed)) {
                            newDataList.add(transaction);
                        }
                    } catch (Exception e) {
                        System.err.println("Chyba při parsování data: " + e);
                        return dataFromDatabase;
                    }
                }
            } else{
                Date dateToParsed;
                try {
                    dateToParsed = dateFormat.parse(dateTo);
                } catch (Exception e) {
                    System.err.println("Chyba při parsování data: " + e);
                    return dataFromDatabase;
                }

                for (TransactionWithPhotos transaction : dataFromDatabase) {
                    try {
                        Date dateOfTransaction = dateFormat.parse(transaction.transaction.date);
                        // https://stackoverflow.com/questions/2592501/how-to-compare-dates-in-java
                        if (!dateOfTransaction.after(dateToParsed)) {
                            newDataList.add(transaction);
                        }
                    } catch (Exception e) {
                        System.err.println("Chyba při parsování data: " + e);
                        return dataFromDatabase;
                    }
                }
            }

            return newDataList;
        }else{
            return dataFromDatabase;
        }
    }

}