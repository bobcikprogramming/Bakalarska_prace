package com.bobcikprogramming.kryptoevidence;

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

import java.util.ArrayList;

public class PhotoViewer extends AppCompatActivity implements View.OnClickListener {

    private ViewPager photoViewer;
    private ImageView imgBack, imgDelete;

    private ViewPagerAdapter viewPagerAdapter;

    private ArrayList<Uri> photos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);

        photos = getIntent().getParcelableArrayListExtra("photos");
        setupUIViews();
        setViewPagerAdapter();
    }

    private void setupUIViews(){
        photoViewer = findViewById(R.id.viewPagerPhoto);

        imgBack = findViewById(R.id.imgPhotoViewerBack);
        imgDelete = findViewById(R.id.imgPhotoViewerDelete);

        imgBack.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
    }

    private void setViewPagerAdapter(){
        viewPagerAdapter = new ViewPagerAdapter(PhotoViewer.this, photos);
        photoViewer.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imgPhotoViewerBack:
                closeActivity();
                break;
            case R.id.imgPhotoViewerDelete:
                deletePhoto();
                break;
        }
    }

    private void deletePhoto(){
        int position = photoViewer.getCurrentItem();
        photos.remove(position);
        photoViewer.setAdapter(viewPagerAdapter);
        if(photos.isEmpty()){
            closeActivity();
        }
    }

    private void closeActivity(){
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("photos",photos);
        setResult(RESULT_OK, intent );
        finish();
    }
}