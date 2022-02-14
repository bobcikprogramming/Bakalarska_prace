package com.bobcikprogramming.kryptoevidence;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bobcikprogramming.kryptoevidence.database.AppDatabase;
import com.bobcikprogramming.kryptoevidence.database.PhotoEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithPhotos;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TransactionPhotoViewer extends AppCompatActivity implements View.OnClickListener {

    private ViewPager photoViewer;
    private ImageView imgBack, imgDelete;
    TabLayout tabLayout;

    private ViewPagerAdapter viewPagerAdapter;

    private TransactionWithPhotos transactionWithPhotos;
    private List<TransactionWithPhotos> transaction;
    private List<PhotoEntity> photos;

    String transactionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);

        transactionID = getIntent().getStringExtra("transactionID");
        AppDatabase db = AppDatabase.getDbInstance(this);
        transactionWithPhotos = db.databaseDao().getTransactionByTransactionID(transactionID);
        transaction = db.databaseDao().getAll();

        setupUIViews();
        setViewPagerAdapter();

        imgDelete.setVisibility(View.GONE);

        tabLayout.setupWithViewPager(photoViewer, true);
    }

    private void setupUIViews(){
        photoViewer = findViewById(R.id.viewPagerPhoto);

        tabLayout = findViewById(R.id.tabDots);

        imgBack = findViewById(R.id.imgPhotoViewerBack);
        imgDelete = findViewById(R.id.imgPhotoViewerDelete);

        imgBack.setOnClickListener(this);
    }

    private ArrayList<Uri> getPhotosUri(){
        ArrayList<Uri> photosUri = new ArrayList<>();
        AppDatabase db = AppDatabase.getDbInstance(this);
        photos =  db.databaseDao().getPhotoByTransactionID(transactionID); //transaction.get(i).photos; //transactionWithPhotos.photos;
        for(PhotoEntity photo : photos){
            photosUri.add(Uri.parse(photo.dest));
        }
        return photosUri;
    }

    private void setViewPagerAdapter(){
        viewPagerAdapter = new ViewPagerAdapter(TransactionPhotoViewer.this, getPhotosUri());
        photoViewer.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imgPhotoViewerBack:
                closeActivity();
                break;
        }
    }

    private void closeActivity(){
        Intent intent = new Intent();
        //intent.putParcelableArrayListExtra("photos",photos);
        setResult(RESULT_OK, intent );
        finish();
    }

}