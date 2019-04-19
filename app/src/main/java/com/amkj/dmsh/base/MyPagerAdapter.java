package com.amkj.dmsh.base;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.amkj.dmsh.homepage.view.HomeWlfareItemView;

import java.util.List;

/**
 * Created by xiaoxin on 2016/6/8.
 */
public class MyPagerAdapter extends PagerAdapter {

    private List<HomeWlfareItemView> views;

    public MyPagerAdapter(List<HomeWlfareItemView> views) {
        this.views = views;
    }

    public void refresh(List<HomeWlfareItemView> views) {
        this.views = views;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return views == null ? 0 : views.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}