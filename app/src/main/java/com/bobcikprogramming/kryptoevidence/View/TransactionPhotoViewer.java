package com.bobcikprogramming.kryptoevidence.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bobcikprogramming.kryptoevidence.Controller.TransactionPhotoViewerController;
import com.bobcikprogramming.kryptoevidence.R;
import com.google.android.material.tabs.TabLayout;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

public class TransactionPhotoViewer extends AppCompatActivity implements View.OnClickListener {

    private ViewPager photoViewer;
    private ImageView imgBack, imgDelete;
    private TabLayout tabLayout;

    private TransactionPhotoViewerController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);

        setupUIViews();

        String transactionID = getIntent().getStringExtra("transactionID");
        controller = new TransactionPhotoViewerController(this, photoViewer, transactionID);

        controller.setViewPagerAdapter();

        tabLayout.setupWithViewPager(photoViewer, true);
    }

    /**
     * Metoda pro inicializování prvků UI.
     */
    private void setupUIViews(){
        photoViewer = findViewById(R.id.viewPagerPhoto);

        tabLayout = findViewById(R.id.tabDots);

        imgBack = findViewById(R.id.imgPhotoViewerBack);
        imgDelete = findViewById(R.id.imgPhotoViewerDelete);

        imgBack.setOnClickListener(this);
        imgDelete.setVisibility(View.GONE);
    }

    /**
     * Metoda zpracovávající reakci na kliknutí na daný prvek.
     * @param view Základní prvek UI komponent
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imgPhotoViewerBack:
                closeActivity();
                break;
        }
    }

    /**
     * Metoda pro ukončení activity.
     */
    private void closeActivity(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent );
        finish();
    }

}