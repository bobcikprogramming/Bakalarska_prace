package com.bobcikprogramming.kryptoevidence.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bobcikprogramming.kryptoevidence.Controller.TransactionEditPhotoViewerController;
import com.bobcikprogramming.kryptoevidence.R;
import com.google.android.material.tabs.TabLayout;

public class TransactionEditPhotoViewer extends AppCompatActivity implements View.OnClickListener {

    private ViewPager photoViewer;
    private ImageView imgBack, imgDelete, imgAdd;
    private TabLayout tabLayout;

    private TransactionEditPhotoViewerController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);

        setupUIViews();

        String transactionID = getIntent().getStringExtra("transactionID");
        controller = new TransactionEditPhotoViewerController(this, photoViewer, transactionID);


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
        imgAdd = findViewById(R.id.imgPhotoViewerAdd);

        imgBack.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        imgAdd.setVisibility(View.VISIBLE);
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
                deletePhoto();
                break;
            case R.id.imgPhotoViewerAdd:
                androidGallery.launch("image/*");
                break;
        }
    }

    /**
     * Metoda reagující na kliknutí na nativní android tlačítko zpět a uzavírající activity
     */
    @Override
    public void onBackPressed() {
        closeActivity();
    }

    /**
     * Metoda pro smazání snímku
     */
    private void deletePhoto(){
        int position = photoViewer.getCurrentItem();
        confirmDialogDelete(position);
        photoViewer.setAdapter(controller.getViewPagerAdapter());
        if(controller.transactionWithPhotoIsEmpty()){
            closeActivity();
        }
    }

    /**
     * Metoda pro vytvoření dialogového okna k potvrzení smazání snímku
     * @param position pozice snímku
     */
    private void confirmDialogDelete(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        builder.setCancelable(true);
        builder.setTitle("Smazat snímek");
        builder.setMessage("Opravdu chcete smazat snímek?");
        builder.setPositiveButton("Smazat",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        controller.deletePhoto(position);
                        if(controller.noPhotoToShow()){
                            closeActivity();
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

    /**
     * Metoda pro ukončení activity
     */
    private void closeActivity(){
        Intent intent = new Intent();
        intent.putExtra("photoChange", controller.isPhotoChange());
        setResult(RESULT_OK, intent );
        finish();
    }

    /**
     * Metoda zpracující návrat z aktivity
     */
    ActivityResultLauncher<String> androidGallery = registerForActivityResult(
        new ActivityResultContracts.GetContent(),
        new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if(!controller.getPhotosUri().contains(uri) && uri != null){
                    controller.saveImageToDatabase(uri);
                }
            }
        });
}