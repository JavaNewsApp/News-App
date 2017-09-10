package com.example.ForMoreNews.bigproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
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


public class PageFragment extends Fragment {

    public static final String ARGS_PAGE = "args_page";
    private static final int RESULT_OK = -1;
    private int mPage;

    private Handler handler = new Handler();
    private ArrayList<New> newses = new ArrayList<>();
    private ArrayList<New> tempnew = new ArrayList<>();
    private PullToRefreshListView listView1;
    private boolean save;
    private New news;
    private int pos = 0;
    private Retrofit retrofit;
    private NewsService requestServices;
    private NewDetail detail;


    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();

        args.putInt(ARGS_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARGS_PAGE);
        retrofit = new Retrofit.Builder()
                .baseUrl("http://166.111.68.66:2042/news/action/query/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestServices = retrofit.create(NewsService.class);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        listView1 = (PullToRefreshListView) view.findViewById(R.id.listView);
        listView1.setMode(PullToRefreshBase.Mode.BOTH);
        final NewsListAdapter Badapter = new NewsListAdapter();
        listView1.setAdapter(Badapter);

        listView1.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            // 下拉Pulling Down
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉的时候刷新新闻列表

                ConnectivityManager cwjManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cwjManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {

                    newses.clear();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            pos++;
                            String cat = MyFragmentPagerAdapter.categorys_show[mPage - 1];
                            Call<NewsSummary> call = requestServices.getNewsList("30", cat, pos);

                            call.enqueue(new Callback<NewsSummary>() {
                                @Override
                                public void onResponse(Call<NewsSummary> call, Response<NewsSummary> response) {
                                    newses.addAll(response.body().getNewsSummary());
                                }

                                @Override
                                public void onFailure(Call<NewsSummary> call, Throwable t) {
                                    Log.i("LHD", "访问失败");
                                }
                            });

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Badapter.notifyDataSetChanged();
                                }
                            });

                        }
                    }).start();

                } else {
                    Toast.makeText(getActivity(), "Sorry. Can not connect to the Internet,\nrefreshment failed.", Toast.LENGTH_LONG).show();
                }
                new FinishRefresh().execute();
            }

            // 上拉Pulling Up
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉的时候读取更多的新闻

                ConnectivityManager cwjManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cwjManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            pos++;
                            String cat = MyFragmentPagerAdapter.categorys_show[mPage - 1];

                            Call<NewsSummary> call = requestServices.getNewsList("30", cat, pos);
                            call.enqueue(new Callback<NewsSummary>() {
                                @Override
                                public void onResponse(Call<NewsSummary> call, Response<NewsSummary> response) {
                                    newses.addAll(response.body().getNewsSummary());
                                }

                                @Override
                                public void onFailure(Call<NewsSummary> call, Throwable t) {
                                    Log.i("LHD", "访问失败");
                                }
                            });

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Badapter.notifyDataSetChanged();
                                }
                            });

                        }
                    }).start();

                } else {
                    Toast.makeText(getActivity(), "Sorry. Can not connect to the Internet,\nrefreshment failed.", Toast.LENGTH_LONG).show();
                }
                new FinishRefresh().execute();
            }
        });

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                news = newses.get(position - 1);
                news.setIsCliced(true);

                Toast.makeText(getActivity(), "You clicked:\n" + news.getTitle(),
                        Toast.LENGTH_LONG).show();

                Log.i("ID", news.getPostid());
                Call<NewDetail> call = requestServices.getNewDetail(news.getPostid());
                call.enqueue(new Callback<NewDetail>() {
                    @Override
                    public void onResponse(Call<NewDetail> call, Response<NewDetail> response) {
                        detail = response.body();

                        Intent intent = new Intent(getActivity(), Details.class);

                        intent.putExtra("title", detail.getTitle());
                        intent.putExtra("body", detail.getBody());
                        intent.putExtra("source", detail.getSource());
                        intent.putExtra("picture", detail.getImg());
                        intent.putExtra("isLiked", news.getIsLiked());
                        startActivityForResult(intent, 11);
                    }

                    @Override
                    public void onFailure(Call<NewDetail> call, Throwable t) {
                        Log.i("LHD", t.getMessage());
                    }
                });
            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this.getActivity()).build();
        ImageLoader.getInstance().init(config);

        ConnectivityManager cwjManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cwjManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {

            //网络请求必须写在新起的线程中
            new Thread(new Runnable() {
                @Override
                public void run() {

                    pos++;
                    String cat = MyFragmentPagerAdapter.categorys_show[mPage - 1];
                    Call<NewsSummary> call = requestServices.getNewsList("30", cat, pos);
                    call.enqueue(new Callback<NewsSummary>() {
                        @Override
                        public void onResponse(Call<NewsSummary> call, Response<NewsSummary> response) {
                            tempnew = response.body().getNewsSummary();
                            newses.addAll(tempnew);
                        }

                        @Override
                        public void onFailure(Call<NewsSummary> call, Throwable t) {
                            Log.i("LHD", "访问失败");
                        }
                    });

                    for (int i = 0; i < tempnew.size(); i++) {
                        New news = tempnew.get(i);
                        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
                        Cursor cursor = db.query("News", null, "title = ?", new String[]{news.getTitle()}, null, null, null);
                        if (!cursor.moveToFirst()) {
                            ContentValues values = new ContentValues();
                            values.put("image", news.getImgsrc());
                            values.put("title", news.getTitle());
                            values.put("origin", news.getSource());
                            values.put("source", news.getUrl());
                            values.put("id", news.getPostid());
                            values.put("category", news.getCategory());
                            values.put("like", 0);
                            db.insert("News", null, values);
                            values.clear();
                        } else {
                        }

                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Badapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();
        } else {
            Toast.makeText(getActivity(), "Can not connect to the Internet >< ", Toast.LENGTH_SHORT).show();

            SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
            Cursor cursor = db.query("News", null, "category = ?", new String[]{MyFragmentPagerAdapter.titles_show[mPage - 1]}, null, null, null);
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
                    New news = new New();
                    news.add(title, origin, image, id, category, src);
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

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 11:
                if (resultCode == RESULT_OK) {
                    save = data.getBooleanExtra("likeData", false);
                    news.setIsLiked(save);
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
                .build();
        return options;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView copyright;
        TextView source;
    }

    // 上拉下拉所用
    private class FinishRefresh extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listView1.onRefreshComplete();
        }
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.news_item, parent, false);

                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.news_image);
                viewHolder.copyright = (TextView) convertView.findViewById(R.id.news_name);
                viewHolder.source = (TextView) convertView.findViewById(R.id.news_source);

                convertView.setTag(viewHolder);
                if(newses.get(position).getIsClicked() == true) {
                    convertView.setBackgroundColor(Color.parseColor("#A3A3A3"));
                }
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


