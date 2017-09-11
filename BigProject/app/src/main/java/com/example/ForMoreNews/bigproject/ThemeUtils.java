package com.example.ForMoreNews.bigproject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.ColorRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yu on 17-9-11.
 */

public class ThemeUtils {
    public static final String PRE_KEY_ISSPLASH = "key_issplash";
    public static final String SHARE_PREFERENCES_NAME = ".newme.pre";
    public static final String TAG = "ThemeUtils";
    private static final String THEME = "pre_theme";
    public static void changeTheme(final Activity activity) {
        final ChoiceOnClickListener listener = new ChoiceOnClickListener();
        final int index = 0;
        new AlertDialog.Builder(activity)
                .setTitle(R.string.theme)
                .setSingleChoiceItems(new TAdapter(activity, R.layout.item_theme, R.id.check, Theme.getThemes()), index, listener)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        setTheme(activity, listener.getWhich());

                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    public static void setTheme(Activity activity) {
        setTheme(activity, getTheme(activity));
    }

    public static void setTheme(Activity activity,int index) {
        setTheme(activity, Theme.getThemes().get(index));
    }

    public static void setTheme(Activity activity, Theme theme) {
        if (theme == null) {
            return;
        }
        activity.setTheme(theme.style);
        saveTheme(activity, theme);
    }

    private static void saveTheme(Context context, Theme theme) {
        SharedPreferences preferences = context.getSharedPreferences( SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(THEME, theme.tag);
        editor.commit();
    }

    public static Theme getTheme(Context context) {
        SharedPreferences preferences = context.getSharedPreferences( SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String tag = preferences.getString(THEME, Theme.getThemes().get(0).tag);
        return getThemeByTag(tag);
    }

    private static Theme getThemeByTag(String tag)  {
        for (Theme t : Theme.getThemes()) {
            if (t.tag.equals(tag)) {
                return t;
            }
        }
        return Theme.getThemes().get(0);
    }

    private static int getThemeIndex(String tag) {
        for (int i = 0; i < Theme.getThemes().size(); i++) {
            if (Theme.getThemes().get(i).tag.equals(tag)) {
                return i;
            }
        }
        return 0;
    }

    private static class ChoiceOnClickListener implements DialogInterface.OnClickListener {

        private int which = 0;
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            this.which = which;
        }

        public int getWhich() {
            return which;
        }
    }


    public static class Theme {
        public String name;
        public String tag;
        public int color;
        public int style;

        private static List<Theme> THEME_MAP = new ArrayList<>();
        static {
            THEME_MAP.add(new Theme(getString(R.string.theme_name_indigo), getString(R.string.theme_tag_indigo), R.color.indigo_primary, R.style.Indigo_theme));
            THEME_MAP.add(new Theme(getString(R.string.theme_name_red), getString(R.string.theme_tag_red), R.color.red_primary, R.style.Red_theme));
            THEME_MAP.add(new Theme(getString(R.string.theme_name_amber), getString(R.string.theme_tag_amber), R.color.amber_primary, R.style.Amber_theme));
            THEME_MAP.add(new Theme(getString(R.string.theme_name_pink), getString(R.string.theme_tag_pink), R.color.pink_primary, R.style.Pink_theme));
            THEME_MAP.add(new Theme(getString(R.string.theme_name_night), getString(R.string.theme_tag_night), R.color.black_primary, R.style.Night_theme));
        }

        public Theme(String name, String tag, @ColorRes int color, int style) {
            this.name = name;
            this.tag = tag;
            this.color = color;
            this.style = style;
        }

        protected static List<Theme> getThemes() {
            return THEME_MAP;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    private static class TAdapter extends ArrayAdapter<Theme> {

        private int mTextId;
        public TAdapter(Context context, int resource, int textViewResourceId, List<Theme> objects) {
            super(context, resource, textViewResourceId, objects);
            this.mTextId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Theme theme = getItem(position);
            View view = super.getView(position, convertView, parent);
            CheckedTextView textView = (CheckedTextView) view.findViewById(mTextId);
            textView.setTextColor(getContext().getResources().getColor(theme.color));
            return view;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

    private static String getString(int resid) {
        return App.getInstance().getString(resid);
    }

}

