package com.anika.cholomanobi.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.anika.cholomanobi.R;
import com.anika.cholomanobi.activity.MainActivity;
import com.anika.cholomanobi.fragment.FullScreenViewFragment;
import com.anika.cholomanobi.fragment.ProductDetailFragment;
import com.anika.cholomanobi.fragment.ProductListFragment;
import com.anika.cholomanobi.helper.Constant;
import com.anika.cholomanobi.model.Slider;

public class SliderAdapter extends PagerAdapter {

    ArrayList<Slider> dataList;
    Activity activity;
    int layout;
    String from;

    public SliderAdapter(ArrayList<Slider> dataList, Activity activity, int layout, String from) {
        this.dataList = dataList;
        this.activity = activity;
        this.layout = layout;
        this.from = from;
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View imageLayout = LayoutInflater.from(activity).inflate(layout, view, false);

        assert imageLayout != null;
        ImageView imgslider = imageLayout.findViewById(R.id.imgslider);
        CardView lytmain = imageLayout.findViewById(R.id.lytmain);

        final Slider singleItem = dataList.get(position);


        Picasso.get()
                .load(singleItem.getImage())
                .fit()
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(imgslider);
        view.addView(imageLayout, 0);

        lytmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equalsIgnoreCase("detail")) {

                    Fragment fragment = new FullScreenViewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("pos", position);
                    fragment.setArguments(bundle);

                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();

                } else {

                    if (singleItem.getType().equals("category")) {

                        Fragment fragment = new ProductListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.ID, singleItem.getType_id());
                        bundle.putString(Constant.NAME, singleItem.getName());
                        bundle.putString(Constant.FROM, "category");
                        fragment.setArguments(bundle);

                        MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();


                    } else if (singleItem.getType().equals("product")) {

                        Fragment fragment = new ProductDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.ID, singleItem.getType_id());
                        bundle.putString(Constant.FROM, "share");
                        bundle.putInt("vpos", 0);
                        fragment.setArguments(bundle);

                        MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();

                    }

                }
            }
        });

        return imageLayout;
    }


    @Override
    public int getCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
