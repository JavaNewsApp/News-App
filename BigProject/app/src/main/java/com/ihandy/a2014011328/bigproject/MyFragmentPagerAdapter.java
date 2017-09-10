package com.ihandy.a2014011328.bigproject;

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

    public int COUNT = 12;
    public int num;

    public static String[] categorys_show = new String[20];
    public static String[] categorys = new String[]{"1", "2", "3", "4","5","6","7", "8", "9", "10", "11", "12"};
    public static String[] titles_show = new String[20];
    public static String[] titles = new String[]{"科技", "教育", "军事", "国内","社会","文化","国际", "汽车", "体育", "财经", "健康", "娱乐"};
    private Context context;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        boolean is[] = new boolean[12];
        num = 0;
        for(int i = 1; i < 13; i++){
            is[i-1] = settings.getBoolean("s"+i, true);
            if(is[i - 1] == true) {
                num++;
                titles_show[num - 1] = titles[i-1];
                categorys_show[num - 1] = categorys[i-1];
            }
            Log.i("here","The settings of "+i+" is "+is[i-1]);
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
        return titles[position];
    }
}


