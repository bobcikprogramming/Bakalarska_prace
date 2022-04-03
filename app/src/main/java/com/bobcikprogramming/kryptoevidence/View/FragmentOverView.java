package com.bobcikprogramming.kryptoevidence.View;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.Controller.FragmentOverViewController;
import com.bobcikprogramming.kryptoevidence.Model.PDFEntity;
import com.bobcikprogramming.kryptoevidence.R;

import java.util.List;


public class FragmentOverView extends Fragment implements View.OnClickListener {

    private LinearLayout layoutShowMore;
    private TextView tvOverviewHeadline, tvAnnualReport, tvSelectedYear, tvCurrency, btnPrevYear, btnNextYear;
    private ImageView imgBtnModeDark, imgBtnModeLight, imgBtnModeBySystem, imgBtnShowMore;
    private View view;
    private RecyclerView recyclerView;

    private RecyclerViewOwnedCrypto adapter;

    private boolean showMoreOpen;
    private int position;
    private FragmentOverViewController controller;

    public FragmentOverView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_over_view, container, false);

        this.showMoreOpen = false;
        controller = new FragmentOverViewController(getContext());

        setupUIViews();
        setModeofGUI();

        adapter = new RecyclerViewOwnedCrypto(getContext(), controller.getDataToShow());
        recyclerView.setAdapter(adapter);

        position = controller.getPosition();
        showAnnualReport();
        return view;
    }

    private void setupUIViews(){
        imgBtnModeLight = view.findViewById(R.id.imgBtnModeLight);
        imgBtnModeDark = view.findViewById(R.id.imgBtnModeDark);
        imgBtnModeBySystem = view.findViewById(R.id.imgBtnModeBySystem);
        imgBtnShowMore = view.findViewById(R.id.imgBtnShowMore);

        layoutShowMore = view.findViewById(R.id.layoutShowMore);

        imgBtnModeLight.setOnClickListener(this);
        imgBtnModeDark.setOnClickListener(this);
        imgBtnModeBySystem.setOnClickListener(this);
        imgBtnShowMore.setOnClickListener(this);

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
                break;
            case R.id.imgBtnModeDark:
                controller.writeToFile("dark");
                setModeofGUI();
                imgBtnModeLight.setImageResource(R.drawable.ic_light_mode_unselected);
                imgBtnModeDark.setImageResource(R.drawable.ic_dark_mode_selected);
                imgBtnModeBySystem.setImageResource(R.drawable.ic_system_unselected);
                break;
            case R.id.imgBtnModeBySystem:
                controller.writeToFile("system");
                setModeofGUI();
                imgBtnModeLight.setImageResource(R.drawable.ic_light_mode_unselected);
                imgBtnModeDark.setImageResource(R.drawable.ic_dark_mode_unselected);
                imgBtnModeBySystem.setImageResource(R.drawable.ic_system_selected);
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
                break;
            case R.id.btnPrevYear:
                position -= 1;
                showAnnualReport();
                break;
            case R.id.btnNextYear:
                position += 1;
                showAnnualReport();
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
                btnPrevYear.setVisibility(View.INVISIBLE);
            }

            if(position < (listAnnualReport.size() - 1)){
                btnNextYear.setVisibility(View.VISIBLE);
                btnNextYear.setText(listAnnualReport.get(position + 1).year);
            }else{
                btnNextYear.setVisibility(View.INVISIBLE);
            }
        }
    }
}