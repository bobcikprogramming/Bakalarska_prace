package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.Uri;

import androidx.viewpager.widget.ViewPager;

import com.bobcikprogramming.kryptoevidence.View.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

public class PhotoViewerController {

    private ArrayList<Uri> photos;
    private ViewPagerAdapter viewPagerAdapter;
    private Context context;
    private ViewPager photoViewer;

    public PhotoViewerController(ArrayList<Uri> photos, ViewPager photoViewer, Context context){
        this.photos = photos;
        this.context = context;
        this.photoViewer = photoViewer;
    }

    /**
     * Metoda pro inicializaci viewPagerAdapteru a jeho nastavení pro ViewPager.
     */
    public void setViewPagerAdapter(){
        viewPagerAdapter = new ViewPagerAdapter(context, photos);
        photoViewer.setAdapter(viewPagerAdapter);
    }

    /**
     * Metoda pro smazání snímku.
     */
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
