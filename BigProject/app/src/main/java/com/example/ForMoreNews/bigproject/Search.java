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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 金子童 on 2017/9/11.
 */

public class Search extends BaseActivity {

    public static final String ARGS_PAGE = "args_page";
    private static final int RESULT_OK = -1;
    private Handler handler = new Handler();
    private ArrayList<New> search_newses = new ArrayList<>();
    private ListView listView;
    private boolean save;
    private New news;
    private Retrofit retrofit;
    private NewsService requestServices;
    private NewDetail detail;
    private String search;
    private New temp_news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        search = intent.getStringExtra("search");

        retrofit = new Retrofit.Builder()
                .baseUrl("http://166.111.68.66:2042/news/action/query/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestServices = retrofit.create(NewsService.class);

        Call<NewsSummary> call = requestServices.getNewsSearch(search);
        call.enqueue(new Callback<NewsSummary>() {
            @Override
            public void onResponse(Call<NewsSummary> call, Response<NewsSummary> response) {
                search_newses = response.body().getNewsSummary();

                for (int i = 0; i < search_newses.size(); i++) {
                    temp_news = search_newses.get(i);
                    final int ii = i;
                    Call<NewDetail> _call = requestServices.getNewDetail(temp_news.getPostid());
                    _call.enqueue(new Callback<NewDetail>() {
                        @Override
                        public void onResponse(Call<NewDetail> _call, Response<NewDetail> _response) {
                            detail = _response.body();
                            search_newses.get(ii).setBody(detail.getBody());
                            search_newses.get(ii).setName(detail.getLink());
                            Log.i("here", "okok");

                        }

                        @Override
                        public void onFailure(Call<NewDetail> call, Throwable t) {
                            Log.i("LHD", t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<NewsSummary> call, Throwable t) {
                Log.i("LHD", t.getMessage());
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView00);
        final NewsListAdapter Badapter = new NewsListAdapter();
        listView.setAdapter(Badapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                news = search_newses.get(position);

                Toast.makeText(Search.this, "You clicked:\n" + news.getTitle(),
                        Toast.LENGTH_LONG).show();
                Log.i("ID", news.getPostid());
                Call<NewDetail> call = requestServices.getNewDetail(news.getPostid());
                call.enqueue(new Callback<NewDetail>() {
                    @Override
                    public void onResponse(Call<NewDetail> call, Response<NewDetail> response) {
                        detail = response.body();

                        Intent intent = new Intent(Search.this, Details.class);

                        intent.putExtra("title", detail.getTitle());
                        intent.putExtra("body", detail.getBody());
                        intent.putExtra("source", detail.getSource());
                        intent.putExtra("picture", detail.getImg());
                        intent.putExtra("isLiked", false);
                        intent.putStringArrayListExtra("name", detail.getLink());
                        startActivityForResult(intent, 20);
                    }

                    @Override
                    public void onFailure(Call<NewDetail> call, Throwable t) {
                        Log.i("LHD", t.getMessage());
                    }
                });


            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();  //获取context的方法：this.getActivity()
        ImageLoader.getInstance().init(config);

        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        Cursor cursor = db.query("News", null, "like = ?", new String[]{String.valueOf(1)}, null, null, null);
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

                New news = new New();
                news.add(title, origin, image, id, category, src, body, like, name, click);
                search_newses.add(news);
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
        finish();
        Intent intent0 = new Intent(Search.this, Search.class);
        startActivity(intent0);
    }

    public DisplayImageOptions getDisplayOption() {
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
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
            return search_newses.size();
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
            ImageLoader.getInstance().displayImage(search_newses.get(position).getImgsrc(), viewHolder.imageView, getDisplayOption());
            viewHolder.copyright.setText(search_newses.get(position).getTitle());
            viewHolder.source.setText(search_newses.get(position).getSource());

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

