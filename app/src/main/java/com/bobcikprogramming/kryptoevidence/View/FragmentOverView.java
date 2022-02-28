package com.bobcikprogramming.kryptoevidence.View;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.Controller.FragmentOverViewController;
import com.bobcikprogramming.kryptoevidence.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class FragmentOverView extends Fragment implements View.OnClickListener {

    private LinearLayout btnOverview, btnTransactions, layoutShowMore;
    private TextView txOverview, txTransactions;
    private ImageView imgBtnModeDark, imgBtnModeLight, imgBtnModeBySystem, imgBtnShowMore;
    private View view;
    private RecyclerView recyclerView;

    private RecyclerViewOwnedCrypto adapter;

    private boolean showMoreOpen;
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
}