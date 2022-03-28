package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.Uri;

import androidx.viewpager.widget.ViewPager;

import com.bobcikprogramming.kryptoevidence.View.ViewPagerAdapter;

import java.util.ArrayList;

public class PhotoViewerController {

    private ArrayList<Uri> photos;
    private ViewPagerAdapter viewPagerAdapter;
    private Context context;
    private ViewPager photoViewer;

    public PhotoViewerController(ArrayList<Uri> photos, ViewPager photoViewer, Context context){
        this.photos = photos;
        if(photos == null){
            System.err.println(">>>>>>>>>>>>>>>>>> chyba 4");
        }
        this.context = context;
        this.photoViewer = photoViewer;
    }

    public void setViewPagerAdapter(){
        viewPagerAdapter = new ViewPagerAdapter(context, photos);
        photoViewer.setAdapter(viewPagerAdapter);
    }

    public void deletePhoto(){
        int position = photoViewer.getCurrentItem();
        photos.remove(position);
        photoViewer.setAdapter(viewPagerAdapter);
    }

    public ArrayList<Uri> getPhotos(){
        return photos;
    }

    public boolean isEmpty(){
        return photos.isEmpty();
    }
}
