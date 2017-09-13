package com.example.ForMoreNews.bigproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by 金子童 on 2017/9/8.
 */


public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    public int COUNT = 13;
    public int num;

    public static String[] categorys_show = new String[20];
    public static String[] categorys = new String[]{"0", "1", "2", "3", "4","5","6","7", "8", "9", "10", "11", "12"};
    public static String[] titles_show = new String[20];
    public static String[] titles = new String[]{"推荐", "科技", "教育", "军事", "国内","社会","文化","国际", "汽车", "体育", "财经", "健康", "娱乐"};
    private Context context;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        boolean is[] = new boolean[13];
        num = 0;
        for(int i = 0; i < 13; i++){
            is[i] = settings.getBoolean("s"+ i, true);
            Log.i("is[i]", ""+is[i]);
            if(is[i]) {
                num++;
                titles_show[num - 1] = titles[i];
                categorys_show[num - 1] = categorys[i];
            }
        }
        COUNT = num;

        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles_show[position];
    }
}


