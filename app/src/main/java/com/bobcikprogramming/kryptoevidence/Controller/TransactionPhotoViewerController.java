package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.Uri;

import androidx.viewpager.widget.ViewPager;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.PhotoEntity;
import com.bobcikprogramming.kryptoevidence.View.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

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

    /**
     * Metoda pro získání seznamu Uri cest ke snímkům z databáze.
     * @return Seznam Uri cest ke snímkům
     */
    public ArrayList<Uri> getPhotosUri(){
        ArrayList<Uri> photosUri = new ArrayList<>();
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<PhotoEntity> photos =  db.databaseDao().getPhotoByTransactionID(transactionID);
        for(PhotoEntity photo : photos){
            photosUri.add(Uri.parse(photo.dest));
        }
        return photosUri;
    }

    /**
     * Metoda pro inicializování viewPagerAdapteru a jeho nastavení pro ViewPager.
     */
    public void setViewPagerAdapter(){
        viewPagerAdapter = new ViewPagerAdapter(context, getPhotosUri());
        photoViewer.setAdapter(viewPagerAdapter);
    }
}
