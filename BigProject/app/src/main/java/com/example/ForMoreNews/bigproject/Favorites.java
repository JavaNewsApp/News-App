package com.example.ForMoreNews.bigproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;


public class Favorites extends BaseActivity {

    public static final String ARGS_PAGE = "args_page";
    private static final int RESULT_OK = -1;
    private Handler handler = new Handler();
    private ArrayList<New> newses = new ArrayList<>();
    private ListView listView;
    private boolean save;
    private New news;
    private NewDetail detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Log.i("favourite", "" + newses.size());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView00);
        final NewsListAdapter Badapter = new NewsListAdapter();
        listView.setAdapter(Badapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                news = newses.get(position);

                Toast.makeText(Favorites.this, "You clicked:\n" + news.getTitle(),
                        Toast.LENGTH_LONG).show();
                Log.i("ID", news.getPostid());

                        Intent intent = new Intent(Favorites.this, Details.class);

                        intent.putExtra("title", news.getTitle());
                        intent.putExtra("body", news.getBody());
                        intent.putExtra("source", news.getSource());
                        intent.putExtra("picture", news.getImgsrc());
                        intent.putStringArrayListExtra("name", news.getName());
                        intent.putExtra("isLiked", true);
                        startActivityForResult(intent, 2);
            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();  //获取context的方法：this.getActivity()
        ImageLoader.getInstance().init(config);

        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        Cursor cursor = db.query("News", null, "like = ?", new String[]{String.valueOf(true)}, null, null, null);
        //Cursor cursor = db.query("News", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor
                        .getColumnIndex("title"));
                String origin = cursor.getString(cursor
                        .getColumnIndex("origin"));
                String category = cursor.getString(cursor
                        .getColumnIndex("category"));
                String src = cursor.getString(cursor
                        .getColumnIndex("source"));
                String image = cursor.getString(cursor
                        .getColumnIndex("image"));
                String id = cursor.getString(cursor
                        .getColumnIndex("id"));
                String body = cursor.getString(cursor
                        .getColumnIndex("body"));
                String like = cursor.getString(cursor
                        .getColumnIndex("like"));
                String name = cursor.getString(cursor
                        .getColumnIndex("name"));
                String click = cursor.getString(cursor
                        .getColumnIndex("click"));
                Log.i("likeboss", like);

                New news = new New();
                news.add(title, origin, image, id, category, src, body, like, name, "false");
                newses.add(news);
            } while (cursor.moveToNext());
        }
        cursor.close();

        handler.post(new Runnable() {
            @Override
            public void run() {
                Badapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 2:
                if(resultCode == RESULT_OK){
                    finish();
                    Intent intent0 = new Intent(Favorites.this, Favorites.class);
                    startActivity(intent0);
                }
                break;
            default:
        }
    }

    public DisplayImageOptions getDisplayOption() {
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();//构建完成
        return options;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView copyright;
        TextView source;
    }

    public class NewsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return newses.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.news_item, parent, false);

                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.news_image);
                viewHolder.copyright = (TextView) convertView.findViewById(R.id.news_name);
                viewHolder.source = (TextView) convertView.findViewById(R.id.news_source);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //异步加载图片方法
            ImageLoader.getInstance().displayImage(newses.get(position).getImgsrc(), viewHolder.imageView, getDisplayOption());
            viewHolder.copyright.setText(newses.get(position).getTitle());
            viewHolder.source.setText(newses.get(position).getSource());

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }
    }

}
