package com.bobcikprogramming.kryptoevidence;

import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.PhotoEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransactionEditPhotoViewer extends AppCompatActivity implements View.OnClickListener {

    private ViewPager photoViewer;
    private ImageView imgBack, imgDelete, imgAdd;
    TabLayout tabLayout;

    private ViewPagerAdapter viewPagerAdapter;

    private TransactionWithPhotos transactionWithPhotos;
    private List<TransactionWithPhotos> transaction;
    private List<PhotoEntity> photos;
    private ArrayList<Uri> photosUri;

    private boolean photoChange;

    private String transactionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);

        photoChange = false;

        transactionID = getIntent().getStringExtra("transactionID");
        AppDatabase db = AppDatabase.getDbInstance(this);
        transactionWithPhotos = db.databaseDao().getTransactionByTransactionID(transactionID);
        transaction = db.databaseDao().getAll();

        getPhotosUri();

        setupUIViews();
        setViewPagerAdapter();

        imgAdd.setVisibility(View.VISIBLE);
        tabLayout.setupWithViewPager(photoViewer, true);
    }

    private void setupUIViews(){
        photoViewer = findViewById(R.id.viewPagerPhoto);

        tabLayout = findViewById(R.id.tabDots);

        imgBack = findViewById(R.id.imgPhotoViewerBack);
        imgDelete = findViewById(R.id.imgPhotoViewerDelete);
        imgAdd = findViewById(R.id.imgPhotoViewerAdd);

        imgBack.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
    }

    private void getPhotosUri(){
        photosUri = new ArrayList<>();
        AppDatabase db = AppDatabase.getDbInstance(this);
        photos = db.databaseDao().getPhotoByTransactionID(transactionID); //transaction.get(i).photos; //transactionWithPhotos.photos;
        System.out.println("---------------------size:"+photos.size());
        for(PhotoEntity photo : photos){
            System.out.println("---------------------"+photo.uidPhoto+", "+photo.dest);
            photosUri.add(Uri.parse(photo.dest));
        }
    }

    private void setViewPagerAdapter(){
        viewPagerAdapter = new ViewPagerAdapter(TransactionEditPhotoViewer.this, photosUri);
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
            case R.id.imgPhotoViewerAdd:
                androidGallery.launch("image/*");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        closeActivity();
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
                        AppDatabase db = AppDatabase.getDbInstance(TransactionEditPhotoViewer.this);
                        if(deleteImage(photos.get(position).dest)) {
                            photoChange = true;
                            photos = db.databaseDao().getPhotoByTransactionID(transactionID);
                            db.databaseDao().deletePhotoById(String.valueOf(photos.get(position).uidPhoto));
                            if(db.databaseDao().getPhotoByTransactionID(transactionID).isEmpty()){
                                closeActivity();
                            }
                            viewPagerAdapter.removeItem(position);
                        }
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
        intent.putExtra("photoChange", photoChange);
        setResult(RESULT_OK, intent );
        finish();
    }

    ActivityResultLauncher<String> androidGallery = registerForActivityResult(
        new ActivityResultContracts.GetContent(),
        new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if(!photosUri.contains(uri) && uri != null){
                    AppDatabase db = AppDatabase.getDbInstance(TransactionEditPhotoViewer.this);
                    photosUri.add(uri);
                    viewPagerAdapter.notifyDataSetChanged();

                    PhotoEntity photoEntity = new PhotoEntity();

                    String path = saveImage(uri);
                    if(!path.isEmpty()) {
                        photoEntity.dest = path;
                        photoEntity.transactionId = Long.parseLong(transactionID);
                        db.databaseDao().insertPhoto(photoEntity);
                        photos.add(photoEntity);

                        photoChange = true;
                    }
                }
            }
        });

    // https://stackoverflow.com/a/17674787
    private String saveImage(Uri photo){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File dir = cw.getDir("Images", MODE_PRIVATE);

        Bitmap bitmap;
        FileOutputStream fos = null;

        File myPath = new File(dir, System.currentTimeMillis() + ".jpg");

        try {
            // https://stackoverflow.com/a/4717740
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photo);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            fos = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return String.valueOf(myPath);
    }

    // https://stackoverflow.com/a/10716773
    private boolean deleteImage(String path){
        File toDelete = new File(path);
        if(toDelete.exists()){
            if(toDelete.delete()){
                Toast.makeText(this, "Snímek úspěšně smazán.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Snímek se nepodařilo smazat. Opakujte prosím akci.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            return false;
        }
        return true;
    }
}