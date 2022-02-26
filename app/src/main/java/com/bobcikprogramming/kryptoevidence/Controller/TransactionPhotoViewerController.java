package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.Uri;

import androidx.viewpager.widget.ViewPager;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.PhotoEntity;
import com.bobcikprogramming.kryptoevidence.View.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TransactionPhotoViewerController {

    private Context context;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager photoViewer;
    private String transactionID;

    public TransactionPhotoViewerController(Context context, ViewPager photoViewer, String transactionID){
        this.context = context;
        this.photoViewer = photoViewer;
        this.transactionID = transactionID;
    }

    public ArrayList<Uri> getPhotosUri(){
        ArrayList<Uri> photosUri = new ArrayList<>();
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<PhotoEntity> photos =  db.databaseDao().getPhotoByTransactionID(transactionID);
        for(PhotoEntity photo : photos){
            photosUri.add(Uri.parse(photo.dest));
        }
        return photosUri;
    }

    public void setViewPagerAdapter(){
        viewPagerAdapter = new ViewPagerAdapter(context, getPhotosUri());
        photoViewer.setAdapter(viewPagerAdapter);
    }
}
