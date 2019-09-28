package com.example.targetstationary.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.targetstationary.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;

public class MyPager extends PagerAdapter {

    private Context context;
    public List<String> images = new ArrayList<String>();

    public MyPager(Context context) {
        this.context = context;
    }
    /*
    This callback is responsible for creating a page. We inflate the layout and set the drawable
    to the ImageView based on the position. In the end we add the inflated layout to the parent
    container .This method returns an object key to identify the page view, but in this example page view
    itself acts as the object key
    */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.slider_item, null);
        ImageView imageView = view.findViewById(R.id.imageSlide);


        Picasso.get().load(getImageAt(position)).into(imageView);
        container.addView(view);

        return view;
    }
    /*
    This callback is responsible for destroying a page. Since we are using view only as the
    object key we just directly remove the view from parent container
    */
    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }
    /*
    Returns the count of the total pages
    */
    @Override
    public int getCount() {
        return 4;
    }
    /*
    Used to determine whether the page view is associated with object key returned by instantiateItem.
    Since here view only is the key we return view==object
    */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }


    private String getImageAt(int position) {
        switch (position) {
            case 0:
                return images.get(0);
            case 1:
                return images.get(1);
            case 2:
                return images.get(2);
            case 3:
                return images.get(3);
            default:
                return images.get(0);
        }
    }
}
