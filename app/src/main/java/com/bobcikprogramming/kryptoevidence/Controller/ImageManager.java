package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImageManager {

    public ImageManager(){}

    /**
     * Metoda k uložení snímků transakce
     * @param context Třída context activity, ze které je metoda volána
     * @param photos Seznam cest k snímkům
     * @return Seznam cest k uloženým snímkům
     *
     * Metoda pro uložení obrázku inspirována z:
     * Zdroj:   Stack Overflow
     * Dotaz:   https://stackoverflow.com/q/17674634
     * Odpověď: https://stackoverflow.com/a/17674787
     * Autor:   Brijesh Thakur
     * Autor:   https://stackoverflow.com/users/898459/brijesh-thakur
     * Datum:   16. července 2013
     *
     * Metoda pro získání Bitmap z Uri inspirována z:
     * Zdroj:   Stack Overflow
     * Dotaz:   https://stackoverflow.com/q/3879992
     * Odpověď: https://stackoverflow.com/a/4717740
     * Autor:   Mark Ingram
     * Autor:   https://stackoverflow.com/users/986/mark-ingram
     * Datum:   17. ledna 2011
     */
    public ArrayList<String> saveImage(Context context, ArrayList<Uri> photos){
        ArrayList<String> photosPath = new ArrayList<>();

        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File dir = cw.getDir("Images", context.MODE_PRIVATE);


        for(Uri photo : photos){
            Bitmap bitmap;
            FileOutputStream fos = null;

            File myPath = new File(dir, System.currentTimeMillis() + ".jpg");

            try {
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

    /**
     * Metoda k uložení snímku transakce
     * @param context Třída context activity, ze které je metoda volána
     * @param photo Cesta ke snímku
     * @return Cesta k uloženému snímku
     */
    public String saveImage(Context context, Uri photo){
        ContextWrapper cw = new ContextWrapper(context);
        File dir = cw.getDir("Images", Context.MODE_PRIVATE);

        Bitmap bitmap;
        FileOutputStream fos = null;

        File myPath = new File(dir, System.currentTimeMillis() + ".jpg");

        try {
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
