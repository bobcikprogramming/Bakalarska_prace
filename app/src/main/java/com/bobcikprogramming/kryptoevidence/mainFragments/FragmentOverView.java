package com.bobcikprogramming.kryptoevidence.mainFragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class FragmentOverView extends Fragment implements View.OnClickListener {

    private LinearLayout btnOverview, btnTransactions;
    private TextView txOverview, txTransactions;
    private ImageView imgBtnModeDark, imgBtnModeLight, imgBtnModeBySystem;
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
        setModeofGUI();
        return view;
    }

    private void setupUIViews(){
        imgBtnModeLight = view.findViewById(R.id.imgBtnModeLight);
        imgBtnModeDark = view.findViewById(R.id.imgBtnModeDark);
        imgBtnModeBySystem = view.findViewById(R.id.imgBtnModeBySystem);

        imgBtnModeLight.setOnClickListener(this);
        imgBtnModeDark.setOnClickListener(this);
        imgBtnModeBySystem.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imgBtnModeLight:
                writeToFile("light");
                setModeofGUI();
                imgBtnModeLight.setImageResource(R.drawable.ic_light_mode_selected);
                imgBtnModeDark.setImageResource(R.drawable.ic_dark_mode_unselected);
                imgBtnModeBySystem.setImageResource(R.drawable.ic_system_unselected);
                break;
            case R.id.imgBtnModeDark:
                writeToFile("dark");
                setModeofGUI();
                imgBtnModeLight.setImageResource(R.drawable.ic_light_mode_unselected);
                imgBtnModeDark.setImageResource(R.drawable.ic_dark_mode_selected);
                imgBtnModeBySystem.setImageResource(R.drawable.ic_system_unselected);
                break;
            case R.id.imgBtnModeBySystem:
                writeToFile("system");
                setModeofGUI();
                imgBtnModeLight.setImageResource(R.drawable.ic_light_mode_unselected);
                imgBtnModeDark.setImageResource(R.drawable.ic_dark_mode_unselected);
                imgBtnModeBySystem.setImageResource(R.drawable.ic_system_selected);
                break;
        }
    }

    private void setModeofGUI(){
        String modeType = readFromFile();

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

    //https://stackoverflow.com/a/9306962
    private String readFromFile(){
        String modeType = "system";

        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new
                    File(getContext().getFilesDir()+"/mode.txt")));

            modeType = bufferedReader.readLine();

            if(modeType.isEmpty()){
                writeToFile("system");
            }

            bufferedReader.close();
        }catch (Exception e){
            File file = new File(getContext().getFilesDir()+"/mode.txt");
            if(!file.exists())
            {
                try {
                    file.createNewFile();
                    writeToFile("system");
                }catch (Exception createFileErr){
                    System.err.println(createFileErr);
                }
            }
        }

        return modeType;
    }

    private void writeToFile(String mode){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new
                    File(getContext().getFilesDir() + "/mode.txt")));
            bufferedWriter.write(mode);
            bufferedWriter.close();
        }catch(Exception e){
            System.err.println(e);
        }
    }
}