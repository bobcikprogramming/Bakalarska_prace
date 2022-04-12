package com.bobcikprogramming.kryptoevidence.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.service.autofill.OnClickAction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.Controller.PhotoViewerController;
import com.bobcikprogramming.kryptoevidence.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class PhotoViewer extends AppCompatActivity implements View.OnClickListener {

    private ViewPager photoViewer;
    private ImageView imgBack, imgDelete;
    private TabLayout tabLayout;

    private PhotoViewerController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);

        setupUIViews();

        controller = new PhotoViewerController(getIntent().getParcelableArrayListExtra("photos"), photoViewer, this);

        controller.setViewPagerAdapter();

        tabLayout.setupWithViewPager(photoViewer, true);
    }

    /**
     * Metoda pro inicializování prvků UI
     */
    private void setupUIViews(){
        photoViewer = findViewById(R.id.viewPagerPhoto);

        tabLayout = findViewById(R.id.tabDots);

        imgBack = findViewById(R.id.imgPhotoViewerBack);
        imgDelete = findViewById(R.id.imgPhotoViewerDelete);

        imgBack.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
    }

    /**
     * Metoda zpracovávající reakci na kliknutí na daný prvek
     * @param view Základní prvek UI komponent
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imgPhotoViewerBack:
                closeActivity();
                break;
            case R.id.imgPhotoViewerDelete:
                controller.deletePhoto();
                if(controller.isEmpty()){
                    closeActivity();
                }
                break;
        }
    }

    /**
     * Metoda pro ukončení activity
     */
    private void closeActivity(){
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("photos", controller.getPhotos());
        setResult(RESULT_OK, intent );
        finish();
    }
}