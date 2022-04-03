package com.bobcikprogramming.kryptoevidence.View;

import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.Controller.FragmentOverViewController;
import com.bobcikprogramming.kryptoevidence.Controller.SharedMethods;
import com.bobcikprogramming.kryptoevidence.Model.PDFEntity;
import com.bobcikprogramming.kryptoevidence.R;

import java.math.BigDecimal;
import java.util.List;


public class FragmentOverView extends Fragment implements View.OnClickListener {

    private LinearLayout layoutShowMore, layoutTop, layoutOwned;
    private FrameLayout overviewLayout;
    private TextView tvOverviewHeadline, tvAnnualReport, tvSelectedYear, tvCurrency, btnPrevYear, btnNextYear;
    private ImageView imgBtnModeDark, imgBtnModeLight, imgBtnModeBySystem, imgBtnShowMore, imgBtnDelete;
    private EditText etSearch;
    private View view;
    private RecyclerView recyclerView;

    private RecyclerViewOwnedCrypto adapter;

    private boolean showMoreOpen;
    private boolean isKeyboardShowing;
    private int position;

    private FragmentOverViewController controller;
    private SharedMethods shared;

    public FragmentOverView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_over_view, container, false);

        showMoreOpen = false;
        isKeyboardShowing = false;

        controller = new FragmentOverViewController(getContext());
        shared = new SharedMethods();

        setupUIViews();
        setModeofGUI();

        searchOnChange();
        adapter = new RecyclerViewOwnedCrypto(getContext(), controller.filter(""));
        recyclerView.setAdapter(adapter);

        position = controller.getLastPosition();
        showAnnualReport();
        onKeyboardVisibility();
        hideKeyBoardOnRecyclerTouch();

        return view;
    }

    private void setupUIViews(){
        imgBtnModeLight = view.findViewById(R.id.imgBtnModeLight);
        imgBtnModeDark = view.findViewById(R.id.imgBtnModeDark);
        imgBtnModeBySystem = view.findViewById(R.id.imgBtnModeBySystem);
        imgBtnShowMore = view.findViewById(R.id.imgBtnShowMore);
        imgBtnDelete = view.findViewById(R.id.imgBtnDelete);

        layoutShowMore = view.findViewById(R.id.layoutShowMore);
        layoutTop = view.findViewById(R.id.layoutTop);
        layoutOwned = view.findViewById(R.id.layoutOwned);
        overviewLayout = view.findViewById(R.id.overViewLayout);

        overviewLayout.setOnClickListener(this);
        imgBtnModeLight.setOnClickListener(this);
        imgBtnModeDark.setOnClickListener(this);
        imgBtnModeBySystem.setOnClickListener(this);
        imgBtnShowMore.setOnClickListener(this);
        imgBtnDelete.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recyclerViewOwnedCrypto);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        btnPrevYear = view.findViewById(R.id.btnPrevYear);
        btnNextYear = view.findViewById(R.id.btnNextYear);

        btnPrevYear.setOnClickListener(this);
        btnNextYear.setOnClickListener(this);

        tvOverviewHeadline = view.findViewById(R.id.tvOverviewHeadline);
        tvAnnualReport = view.findViewById(R.id.tvAnnualReport);
        tvCurrency = view.findViewById(R.id.tvCurrency);
        tvSelectedYear = view.findViewById(R.id.tvSelectedYear);

        tvAnnualReport.setSelected(true);

        etSearch = view.findViewById(R.id.etSearch);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imgBtnModeLight:
                controller.writeToFile("light");
                setModeofGUI();
                imgBtnModeLight.setImageResource(R.drawable.ic_light_mode_selected);
                imgBtnModeDark.setImageResource(R.drawable.ic_dark_mode_unselected);
                imgBtnModeBySystem.setImageResource(R.drawable.ic_system_unselected);
                shared.hideKeyBoard(getActivity());
                break;
            case R.id.imgBtnModeDark:
                controller.writeToFile("dark");
                setModeofGUI();
                imgBtnModeLight.setImageResource(R.drawable.ic_light_mode_unselected);
                imgBtnModeDark.setImageResource(R.drawable.ic_dark_mode_selected);
                imgBtnModeBySystem.setImageResource(R.drawable.ic_system_unselected);
                shared.hideKeyBoard(getActivity());
                break;
            case R.id.imgBtnModeBySystem:
                controller.writeToFile("system");
                setModeofGUI();
                imgBtnModeLight.setImageResource(R.drawable.ic_light_mode_unselected);
                imgBtnModeDark.setImageResource(R.drawable.ic_dark_mode_unselected);
                imgBtnModeBySystem.setImageResource(R.drawable.ic_system_selected);
                shared.hideKeyBoard(getActivity());
                break;
            case R.id.imgBtnShowMore:
                if(!showMoreOpen) {
                    // https://stackoverflow.com/a/3940823
                    imgBtnShowMore.setImageResource(R.drawable.ic_show_more_anim);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        AnimatedVectorDrawable animatedVectorDrawable =
                                (AnimatedVectorDrawable) imgBtnShowMore.getDrawable();
                        animatedVectorDrawable.start();
                    } else{
                        AnimatedVectorDrawableCompat animatedVectorDrawable =
                                (AnimatedVectorDrawableCompat) imgBtnShowMore.getDrawable();
                        animatedVectorDrawable.start();
                    }

                    Animation layoutShow = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right);
                    layoutShowMore.startAnimation(layoutShow);
                    layoutShowMore.setVisibility(View.VISIBLE);

                    showMoreOpen = true;
                }else{
                    imgBtnShowMore.setImageResource(R.drawable.ic_show_more_anim_reverse);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        AnimatedVectorDrawable animatedVectorDrawable =
                                (AnimatedVectorDrawable) imgBtnShowMore.getDrawable();
                        animatedVectorDrawable.start();
                    } else{
                        AnimatedVectorDrawableCompat animatedVectorDrawable =
                                (AnimatedVectorDrawableCompat) imgBtnShowMore.getDrawable();
                        animatedVectorDrawable.start();
                    }

                    Animation layoutHide = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left);
                    layoutShowMore.startAnimation(layoutHide);
                    layoutShowMore.setVisibility(View.INVISIBLE);

                    showMoreOpen = false;
                }
                shared.hideKeyBoard(getActivity());
                break;
            case R.id.btnPrevYear:
                position -= 1;
                showAnnualReport();
                shared.hideKeyBoard(getActivity());
                break;
            case R.id.btnNextYear:
                position += 1;
                showAnnualReport();
                shared.hideKeyBoard(getActivity());
                break;
            case R.id.overViewLayout:
                shared.hideKeyBoard(getActivity());
                break;
            case R.id.imgBtnDelete:
                etSearch.setText("");
                break;
        }
    }

    private void setModeofGUI(){
        String modeType = controller.readFromFile();

        if(modeType.equals("dark")){
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
            imgBtnModeLight.setImageResource(R.drawable.ic_light_mode_unselected);
            imgBtnModeDark.setImageResource(R.drawable.ic_dark_mode_selected);
            imgBtnModeBySystem.setImageResource(R.drawable.ic_system_unselected);
        }else if(modeType.equals("light")){
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
            imgBtnModeLight.setImageResource(R.drawable.ic_light_mode_selected);
            imgBtnModeDark.setImageResource(R.drawable.ic_dark_mode_unselected);
            imgBtnModeBySystem.setImageResource(R.drawable.ic_system_unselected);


        }else{
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            imgBtnModeLight.setImageResource(R.drawable.ic_light_mode_unselected);
            imgBtnModeDark.setImageResource(R.drawable.ic_dark_mode_unselected);
            imgBtnModeBySystem.setImageResource(R.drawable.ic_system_selected);
        }
    }

    private void showAnnualReport(){
        List<PDFEntity> listAnnualReport = controller.showAnnualReport();
        if(listAnnualReport == null){
            tvCurrency.setVisibility(View.GONE);
            /** https://stackoverflow.com/a/6999195 */
            tvAnnualReport.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            tvAnnualReport.setText("Roční výpis nenalezen");

            btnPrevYear.setVisibility(View.GONE);
            btnNextYear.setVisibility(View.GONE);
            tvSelectedYear.setVisibility(View.GONE);
        }else{
            String headline = "Roční zisk";
            tvAnnualReport.setTextColor(ContextCompat.getColor(getContext(), R.color.overviewProfitTextColor));
            if(shared.getBigDecimal(listAnnualReport.get(position).total).compareTo(BigDecimal.ZERO) < 0){
                headline = "Roční ztráta";
                tvAnnualReport.setTextColor(ContextCompat.getColor(getContext(), R.color.overviewLossTextColor));
            }
            tvOverviewHeadline.setText(headline);

            tvCurrency.setVisibility(View.VISIBLE);
            tvAnnualReport.setTextSize(TypedValue.COMPLEX_UNIT_SP,35);
            tvAnnualReport.setText(listAnnualReport.get(position).total);

            btnNextYear.setVisibility(View.GONE);
            tvSelectedYear.setVisibility(View.VISIBLE);
            tvSelectedYear.setText(listAnnualReport.get(position).year);

            if(position > 0){
                btnPrevYear.setVisibility(View.VISIBLE);
                btnPrevYear.setText(listAnnualReport.get(position - 1).year);
            }else{
                btnPrevYear.setVisibility(View.GONE);
            }

            if(position < (listAnnualReport.size() - 1)){
                btnNextYear.setVisibility(View.VISIBLE);
                btnNextYear.setText(listAnnualReport.get(position + 1).year);
            }else{
                btnNextYear.setVisibility(View.GONE);
            }
        }
    }

    private void searchOnChange(){
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searching = charSequence.toString();
                adapter = new RecyclerViewOwnedCrypto(getContext(), controller.filter(searching));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /** https://stackoverflow.com/a/26964010 */
    private void onKeyboardVisibility(){
        overviewLayout.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    Rect r = new Rect();
                    overviewLayout.getWindowVisibleDisplayFrame(r);
                    int screenHeight = overviewLayout.getRootView().getHeight();

                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    int keypadHeight = screenHeight - r.bottom;

                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        if (!isKeyboardShowing) {
                            isKeyboardShowing = true;
                            layoutTop.setVisibility(View.GONE);
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)layoutOwned.getLayoutParams();
                            int newTop = shared.dpToPx(20, getContext());
                            params.setMargins(0, newTop, 0, 0);
                            layoutOwned.setLayoutParams(params);
                        }
                    }
                    else {
                        // keyboard is closed
                        if (isKeyboardShowing) {
                            isKeyboardShowing = false;
                            Animation layoutShow = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
                            layoutTop.startAnimation(layoutShow);
                            layoutTop.setVisibility(View.VISIBLE);
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)layoutOwned.getLayoutParams();
                            int newTop = shared.dpToPx(40, getContext());
                            params.setMargins(0, newTop, 0, 0);
                            layoutOwned.setLayoutParams(params);
                        }
                    }
                }
            });
    }

    private void hideKeyBoardOnRecyclerTouch(){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                shared.hideKeyBoard(getActivity());
                return true;
            }
        });
    }
}