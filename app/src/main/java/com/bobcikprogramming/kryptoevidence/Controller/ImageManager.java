package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImageManager {

    public ImageManager(){}

    /** https://stackoverflow.com/a/17674787 */
    public ArrayList<String> saveImage(Context context, ArrayList<Uri> photos){
        ArrayList<String> photosPath = new ArrayList<>();

        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File dir = cw.getDir("Images", context.MODE_PRIVATE);


        for(Uri photo : photos){
            Bitmap bitmap;
            FileOutputStream fos = null;

            File myPath = new File(dir, System.currentTimeMillis() + ".jpg");

            try {
                /** https://stackoverflow.com/a/4717740 */
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photo);
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
            photosPath.add(String.valueOf(myPath));
        }

        return photosPath;
    }

    public String saveImage(Context context, Uri photo){
        ContextWrapper cw = new ContextWrapper(context);
        File dir = cw.getDir("Images", Context.MODE_PRIVATE);

        Bitmap bitmap;
        FileOutputStream fos = null;

        File myPath = new File(dir, System.currentTimeMillis() + ".jpg");

        try {
            /** https://stackoverflow.com/a/4717740 */
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photo);
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
}
