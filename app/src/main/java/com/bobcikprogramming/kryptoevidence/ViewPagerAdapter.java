package com.bobcikprogramming.kryptoevidence;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.Objects;

// https://www.geeksforgeeks.org/image-slider-in-android-using-viewpager/
public class ViewPagerAdapter extends PagerAdapter {

    private ArrayList<Uri> photos;
    private LayoutInflater layoutInflater;

    public ViewPagerAdapter(Context context, ArrayList<Uri> photos) {
        this.photos = photos;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.photo_item, container, false);
        ImageView imageView = itemView.findViewById(R.id.imageView);
        imageView.setImageURI(photos.get(position));
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}

