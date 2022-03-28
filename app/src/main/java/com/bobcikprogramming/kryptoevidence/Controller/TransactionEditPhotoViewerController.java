package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.PhotoEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;
import com.bobcikprogramming.kryptoevidence.View.ViewPagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TransactionEditPhotoViewerController {

    private ViewPager photoViewer;
    private List<PhotoEntity> photos;
    private ArrayList<Uri> photosUri;
    private Context context;
    private String transactionID;
    private ViewPagerAdapter viewPagerAdapter;
    private boolean photoChange;

    private ImageManager imgManager;

    public TransactionEditPhotoViewerController(Context context, ViewPager photoViewer, String transactionID){
        this.context = context;
        this.photoViewer = photoViewer;
        this.transactionID = transactionID;

        imgManager = new ImageManager();
        photoChange = false;

        setPhotosUri();
        setViewPagerAdapter();
    }

    private void setPhotosUri(){
        photosUri = new ArrayList<>();
        AppDatabase db = AppDatabase.getDbInstance(context);
        photos = db.databaseDao().getPhotoByTransactionID(transactionID);
        for(PhotoEntity photo : photos){
            photosUri.add(Uri.parse(photo.dest));
        }
    }

    private void setViewPagerAdapter(){
        if(photosUri == null){
            System.err.println(">>>>>>>>>>>>>>>>>> chyba 7");
        }
        viewPagerAdapter = new ViewPagerAdapter(context, photosUri);
        photoViewer.setAdapter(viewPagerAdapter);
    }

    public void deletePhoto(int position){
        AppDatabase db = AppDatabase.getDbInstance(context);
        if(deleteImage(photos.get(position).dest)) {
            photoChange = true;
            photos = db.databaseDao().getPhotoByTransactionID(transactionID);
            db.databaseDao().deletePhotoById(String.valueOf(photos.get(position).uidPhoto));
            viewPagerAdapter.removeItem(position);
            photos = db.databaseDao().getPhotoByTransactionID(transactionID);
        }
    }

    private boolean deleteImage(String path){
        File toDelete = new File(path);
        if(toDelete.exists()){
            if(toDelete.delete()){
                Toast.makeText(context, "Snímek úspěšně smazán.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Snímek se nepodařilo smazat. Opakujte prosím akci.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            return false;
        }
        return true;
    }

    public void saveImageToDatabase(Uri uri){
        AppDatabase db = AppDatabase.getDbInstance(context);
        photosUri.add(uri);
        viewPagerAdapter.notifyDataSetChanged();

        PhotoEntity photoEntity = new PhotoEntity();

        String path = imgManager.saveImage(context, uri);
        if(!path.isEmpty()) {
            photoEntity.dest = path;
            photoEntity.transactionId = Long.parseLong(transactionID);
            db.databaseDao().insertPhoto(photoEntity);
            photos.add(photoEntity);

            photoChange = true;
        }
    }

    public ViewPagerAdapter getViewPagerAdapter() {
        return viewPagerAdapter;
    }

    public boolean transactionWithPhotoIsEmpty(){
        return photos.isEmpty();
    }

    public boolean isPhotoChange() {
        return photoChange;
    }

    public boolean noPhotoToShow(){
        AppDatabase db = AppDatabase.getDbInstance(context);
        return db.databaseDao().getPhotoByTransactionID(transactionID).isEmpty();
    }

    public ArrayList<Uri> getPhotosUri() {
        return photosUri;
    }
}
