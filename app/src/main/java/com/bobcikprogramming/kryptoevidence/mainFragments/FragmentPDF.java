package com.bobcikprogramming.kryptoevidence.mainFragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.PhotoViewer;
import com.bobcikprogramming.kryptoevidence.R;
import com.bobcikprogramming.kryptoevidence.database.AppDatabase;
import com.bobcikprogramming.kryptoevidence.database.PhotoEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithPhotos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FragmentPDF extends Fragment {

    private TextView test;

    public FragmentPDF() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pdf, container, false);
        test = view.findViewById(R.id.test);

        AppDatabase db = AppDatabase.getDbInstance(getContext());
        List<TransactionWithPhotos> dataFromDatabase = db.databaseDao().getAll();
        ArrayList<Uri> images = new ArrayList<>();
        List<PhotoEntity> dataFromDatabasePhoto = db.databaseDao().getPhoto();
        List<TransactionEntity> dataFromDatabaseTransaction = db.databaseDao().getBuy();
        System.out.println("Jsem zde");
        for(PhotoEntity photo : dataFromDatabasePhoto){
            if(photo.transactionId != dataFromDatabaseTransaction.get(dataFromDatabaseTransaction.size()-1).uidTransaction){
                continue;
            }
            System.out.println("hledám foto");
            File imgFile = new  File(photo.dest);
            if(imgFile.exists()){
                System.out.println("foto nalezeno");
                Uri image = Uri.fromFile(imgFile);
                Toast.makeText(getContext(), String.valueOf(image), Toast.LENGTH_SHORT).show();
                images.add(image);

            }
        }




        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!images.isEmpty()) {
                    Intent photoViewer = new Intent(getContext(), PhotoViewer.class);
                    photoViewer.putParcelableArrayListExtra("photos", images);
                    someActivityResultLauncher.launch(photoViewer);
                }
            }
        });
        return view;
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                    }
                }
            });
}