package com.bobcikprogramming.kryptoevidence;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

        tabLayout.setupWithViewPager(photoViewer, true);
    }

    private void setupUIViews(){
        photoViewer = findViewById(R.id.viewPagerPhoto);

        tabLayout = findViewById(R.id.tabDots);

        imgBack = findViewById(R.id.imgPhotoViewerBack);
        imgDelete = findViewById(R.id.imgPhotoViewerDelete);

        imgBack.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
    }

    private ArrayList<Uri> getPhotosUri(){
        ArrayList<Uri> photosUri = new ArrayList<>();
        int i = 0;
        while(transaction.get(i).transaction.uidTransaction != Long.parseLong(transactionID)){
            i++;
        }
        List<PhotoEntity> photos =  transaction.get(i).photos; //transactionWithPhotos.photos;
        System.out.println("----------------------SIZE photos: "+ photos.size() + " ----------------");
        System.out.println("----------------------SIZE transactionWithPhotos: "+ transactionWithPhotos.photos.size() + " ----------------");
        for(PhotoEntity photo : photos){
            photosUri.add(Uri.parse(photo.dest));
        }
        System.out.println("----------------------SIZE: "+photosUri.size() + " ----------------");
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
            case R.id.imgPhotoViewerDelete:
                deletePhoto();
                break;
        }
    }

    private void deletePhoto(){
        int position = photoViewer.getCurrentItem();
        confirmDialogDelete(position);
        photoViewer.setAdapter(viewPagerAdapter);
        if(transactionWithPhotos.photos.isEmpty()){
            closeActivity();
        }
    }

    private void confirmDialogDelete(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        builder.setCancelable(true);
        builder.setTitle("Smazat snímek");
        builder.setMessage("Opravdu chcete smazat snímek?");
        builder.setPositiveButton("Smazat",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppDatabase db = AppDatabase.getDbInstance(TransactionPhotoViewer.this);
                        //db.databaseDao().deletePhoto(photos.get(position));

                        Intent intent = new Intent();
                        /*intent.putExtra("changed", true);
                        intent.putExtra("deleted", true);*/
                        setResult(RESULT_OK, intent );
                        finish();
                    }
                });
        builder.setNegativeButton("Zrušit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void closeActivity(){
        Intent intent = new Intent();
        //intent.putParcelableArrayListExtra("photos",photos);
        setResult(RESULT_OK, intent );
        finish();
    }
}