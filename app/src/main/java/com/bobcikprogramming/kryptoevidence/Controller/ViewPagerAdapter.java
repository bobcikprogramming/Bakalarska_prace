package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bobcikprogramming.kryptoevidence.R;

import java.util.ArrayList;
import java.util.Objects;

/** https://www.geeksforgeeks.org/image-slider-in-android-using-viewpager/ */
public class ViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private ArrayList<Uri> photos;
    private LayoutInflater layoutInflater;
    private ImageView imageView;

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
        imageView = itemView.findViewById(R.id.imageView);
        imageView.setImageURI(photos.get(position));
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    public void removeItem(int position){
        photos.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /** https://stackoverflow.com/a/48081760 */
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}

