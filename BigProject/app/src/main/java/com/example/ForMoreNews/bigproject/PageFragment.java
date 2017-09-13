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
import java.util.Set;
import java.util.TreeMap;

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
    private ArrayList<New> _newses = new ArrayList<>();
    private PullToRefreshListView listView;
    private boolean save;
    private New news;
    private int pos = 1;
    private Retrofit retrofit;
    private NewsService requestServices;
    private NewDetail detail;
    private ContentValues values;
    private SQLiteDatabase db;


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

        retrofit = new Retrofit.Builder()
                .baseUrl("http://166.111.68.66:2042/news/action/query/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestServices = retrofit.create(NewsService.class);

        mPage = getArguments().getInt(ARGS_PAGE);


    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        listView = (PullToRefreshListView) view.findViewById(R.id.listView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        final NewsListAdapter Badapter = new NewsListAdapter();
        listView.setAdapter(Badapter);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

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
                            String cat = MyFragmentPagerAdapter.categorys_show[mPage - 1];
                            if(!cat.equals("0")) {
                                Call<NewsSummary> call = requestServices.getNewsList("30", cat, 1);

                                call.enqueue(new Callback<NewsSummary>() {
                                    @Override
                                    public void onResponse(Call<NewsSummary> call, Response<NewsSummary> response) {
                                        newses = response.body().getNewsSummary();

                                        for (int i = 0; i < newses.size(); i++) {
                                            final int ii = i;
                                            Call<NewDetail> _call = requestServices.getNewDetail(newses.get(i).getPostid());
                                            _call.enqueue(new Callback<NewDetail>() {
                                                @Override
                                                public void onResponse(Call<NewDetail> _call, Response<NewDetail> _response) {
                                                    detail = _response.body();
                                                    newses.get(ii).setBody(detail.getBody());
                                                    newses.get(ii).setName(detail.getLink());
                                                    newses.get(ii).setSearch(detail.getKeywords());
                                                    Log.i("LHD", newses.get(ii).getBody());

                                                }

                                                @Override
                                                public void onFailure(Call<NewDetail> call, Throwable t) {
                                                    Log.i("LHD", t.getMessage());
                                                }
                                            });
                                        }

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Badapter.notifyDataSetChanged();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure(Call<NewsSummary> call, Throwable t) {
                                        Log.i("LHD", "访问失败");
                                    }
                                });
                            }
                            else {
                                Call<NewsSummary> call = requestServices.getNewsSearch("北京");

                                call.enqueue(new Callback<NewsSummary>() {
                                    @Override
                                    public void onResponse(Call<NewsSummary> call, Response<NewsSummary> response) {
                                        newses = response.body().getNewsSummary();

                                        for (int i = 0; i < newses.size(); i++) {
                                            final int ii = i;
                                            Call<NewDetail> _call = requestServices.getNewDetail(newses.get(i).getPostid());
                                            _call.enqueue(new Callback<NewDetail>() {
                                                @Override
                                                public void onResponse(Call<NewDetail> _call, Response<NewDetail> _response) {
                                                    detail = _response.body();
                                                    newses.get(ii).setBody(detail.getBody());
                                                    newses.get(ii).setName(detail.getLink());
                                                    newses.get(ii).setSearch(detail.getKeywords());
                                                    Log.i("LHD", newses.get(ii).getBody());

                                                }

                                                @Override
                                                public void onFailure(Call<NewDetail> call, Throwable t) {
                                                    Log.i("LHD", t.getMessage());
                                                }
                                            });
                                        }

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Badapter.notifyDataSetChanged();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure(Call<NewsSummary> call, Throwable t) {
                                        Log.i("LHD", "访问失败");
                                    }
                                });
                            }

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
                            if(!cat.equals("0")) {
                                Call<NewsSummary> call = requestServices.getNewsList("30", cat, pos);
                                call.enqueue(new Callback<NewsSummary>() {
                                    @Override
                                    public void onResponse(Call<NewsSummary> call, Response<NewsSummary> response) {
                                        _newses = response.body().getNewsSummary();
                                        newses.addAll(_newses);
                                        for (int i = newses.size() - _newses.size(); i < newses.size(); i++) {

                                            final int ii = i;
                                            Call<NewDetail> _call = requestServices.getNewDetail(newses.get(i).getPostid());
                                            _call.enqueue(new Callback<NewDetail>() {
                                                @Override
                                                public void onResponse(Call<NewDetail> _call, Response<NewDetail> _response) {
                                                    detail = _response.body();
                                                    newses.get(ii).setBody(detail.getBody());
                                                    newses.get(ii).setName(detail.getLink());
                                                    newses.get(ii).setSearch(detail.getKeywords());

                                                }

                                                @Override
                                                public void onFailure(Call<NewDetail> call, Throwable t) {
                                                    Log.i("LHD", t.getMessage());
                                                }
                                            });
                                        }
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Badapter.notifyDataSetChanged();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure(Call<NewsSummary> call, Throwable t) {
                                        Log.i("LHD", "访问失败");
                                    }
                                });
                            } else {
                                Call<NewsSummary> call = requestServices.getNewsSearch("北京");
                                call.enqueue(new Callback<NewsSummary>() {
                                    @Override
                                    public void onResponse(Call<NewsSummary> call, Response<NewsSummary> response) {
                                        _newses = response.body().getNewsSummary();
                                        newses.addAll(_newses);
                                        for (int i = newses.size() - _newses.size(); i < newses.size(); i++) {

                                            final int ii = i;
                                            Call<NewDetail> _call = requestServices.getNewDetail(newses.get(i).getPostid());
                                            _call.enqueue(new Callback<NewDetail>() {
                                                @Override
                                                public void onResponse(Call<NewDetail> _call, Response<NewDetail> _response) {
                                                    detail = _response.body();
                                                    newses.get(ii).setBody(detail.getBody());
                                                    newses.get(ii).setName(detail.getLink());
                                                    newses.get(ii).setSearch(detail.getKeywords());
                                                }

                                                @Override
                                                public void onFailure(Call<NewDetail> call, Throwable t) {
                                                    Log.i("LHD", t.getMessage());
                                                }
                                            });
                                        }
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Badapter.notifyDataSetChanged();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure(Call<NewsSummary> call, Throwable t) {
                                        Log.i("LHD", "访问失败");
                                    }
                                });
                            }

                        }
                    }).start();

                } else {
                    Toast.makeText(getActivity(), "Sorry. Can not connect to the Internet,\nrefreshment failed.", Toast.LENGTH_LONG).show();
                }
                new FinishRefresh().execute();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                news = newses.get(position - 1);
                newses.get(position - 1).setIsClicked(true);

                Toast.makeText(getActivity(), "You clicked:\n" + news.getTitle(),
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getActivity(), Details.class);

                intent.putExtra("title", news.getTitle());
                intent.putExtra("body", news.getBody());
                intent.putExtra("source", news.getSource());
                intent.putExtra("picture", news.getImgsrc());
                Log.i("outfuckbody", "kkk");

                intent.putStringArrayListExtra("name", news.getName());

                startActivityForResult(intent, 11);
                SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
                Cursor cursor = db.query("News", null, "title = ?", new String[]{newses.get(position - 1).getTitle()}, null, null, null);

                if (cursor.moveToFirst()) {
                    String like = cursor.getString(cursor
                            .getColumnIndex("like"));
                    intent.putExtra("isLiked", Boolean.parseBoolean(like));
                }
                cursor.close();
                Log.i("outfuck", "fuck");
                ContentValues values = new ContentValues();

                values.put("search", news.getSearch());
                values.put("click", "true");
                db.update("News", values, "title = ?",
                        new String[] {news.getTitle()});



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
                    String cat = MyFragmentPagerAdapter.categorys_show[mPage - 1];
                    Log.i("tuijian", cat);

                    if(!cat.equals("0")) {
                        Call<NewsSummary> call = requestServices.getNewsList("30", cat, 1);
                        call.enqueue(new Callback<NewsSummary>() {
                            @Override
                            public void onResponse(Call<NewsSummary> call, Response<NewsSummary> response) {
                                newses.addAll(response.body().getNewsSummary());

                                for (int i = 0; i < newses.size(); i++) {

                                    final int ii = i;

                                    db = MainActivity.dbHelper.getWritableDatabase();
                                    Cursor cursor = db.query("News", null, "title = ?", new String[]{newses.get(i).getTitle()}, null, null, null);
                                    values = new ContentValues();

                                    if (!cursor.moveToFirst()) {

                                        Call<NewDetail> _call = requestServices.getNewDetail(newses.get(i).getPostid());
                                        _call.enqueue(new Callback<NewDetail>() {
                                            @Override
                                            public void onResponse(Call<NewDetail> _call, Response<NewDetail> _response) {
                                                detail = _response.body();
                                                newses.get(ii).setBody(detail.getBody());
                                                newses.get(ii).setName(detail.getLink());
                                                newses.get(ii).setSearch(detail.getKeywords());
                                                values.put("image", newses.get(ii).getImgsrc());
                                                values.put("title", newses.get(ii).getTitle());
                                                values.put("origin", newses.get(ii).getSource());
                                                values.put("source", newses.get(ii).getUrl());
                                                values.put("id", newses.get(ii).getPostid());
                                                values.put("category", newses.get(ii).getCategory());
                                                values.put("like", String.valueOf(newses.get(ii).getIsLiked()));
                                                values.put("body", newses.get(ii).getBody());
                                                values.put("click", "");
                                                values.put("search", "");

                                                String a = "";
                                                for (int j = 0; j < newses.get(ii).getName().size() - 1; j++) {
                                                    a += newses.get(ii).getName().get(j);
                                                    a += ";";
                                                }
                                                if (newses.get(ii).getName().size() > 0)
                                                    a += newses.get(ii).getName().get(newses.get(ii).getName().size() - 1);
                                                values.put("name", a);
                                                db.insert("News", null, values);
                                                values.clear();
                                            }

                                            @Override
                                            public void onFailure(Call<NewDetail> call, Throwable t) {
                                                Log.i("LHD", t.getMessage());
                                            }
                                        });
                                        Log.i("outbody", newses.get(i).getName().toString());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Badapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Badapter.notifyDataSetChanged();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<NewsSummary> call, Throwable t) {
                                Log.i("LHD", "访问失败");
                            }
                        });
                    }
                    else{
                        SQLiteDatabase db0 = MainActivity.dbHelper.getWritableDatabase();
                        Cursor cursor = db0.query("News", null, "click = ?", new String[]{"true"}, null, null, null);
                        String result = "";
                        ArrayList<String> word0 = new ArrayList<>();
                        ArrayList<Double> num0 = new ArrayList<>();

                        if (cursor.moveToFirst()) {
                            String search = cursor.getString(cursor
                                    .getColumnIndex("search"));
                            search = "";
                            if(search != null && search != "") {
                                do {


                                    String[] _search = search.split(";");
                                    for (String a : _search) {
                                        String[] aa = a.split(",");
                                        word0.add(aa[0]);
                                        num0.add(Double.parseDouble(aa[1]));
                                    }

                                } while (cursor.moveToNext());

                                ArrayList<String> word = new ArrayList<>();
                                ArrayList<Double> num = new ArrayList<>();
                                for (int i = 0; i < word0.size(); i++) {
                                    int flag = 0;
                                    for (int j = 0; j < word.size(); j++) {
                                        if (word0.get(i).equals(word.get(j))) {
                                            flag = 1;
                                            double temp = num.get(j);
                                            temp += num0.get(i);
                                            num.set(j, temp);
                                        }
                                    }
                                    if (flag == 0) {
                                        num.add(num0.get(i));
                                        word.add(word0.get(i));
                                    }
                                }
                                double tt = 0;
                                for (int i = 0; i < num.size(); i++) {
                                    if (num.get(i) > tt) {
                                        tt = num.get(i);
                                        result = word.get(i);
                                    }
                                }
                            }
                            else {
                                result = "中国";
                            }
                        }
                        else {
                            result = "中国";
                        }
                        cursor.close();
                        Call<NewsSummary> call = requestServices.getNewsSearch(result);
                        call.enqueue(new Callback<NewsSummary>() {
                            @Override
                            public void onResponse(Call<NewsSummary> call, Response<NewsSummary> response) {
                                newses.addAll(response.body().getNewsSummary());

                                for (int i = 0; i < newses.size(); i++) {

                                    final int ii = i;

                                    db = MainActivity.dbHelper.getWritableDatabase();
                                    Cursor cursor = db.query("News", null, "title = ?", new String[]{newses.get(i).getTitle()}, null, null, null);
                                    values = new ContentValues();

                                    if (!cursor.moveToFirst()) {

                                        Call<NewDetail> _call = requestServices.getNewDetail(newses.get(i).getPostid());
                                        _call.enqueue(new Callback<NewDetail>() {
                                            @Override
                                            public void onResponse(Call<NewDetail> _call, Response<NewDetail> _response) {
                                                detail = _response.body();
                                                newses.get(ii).setBody(detail.getBody());
                                                newses.get(ii).setName(detail.getLink());
                                                values.put("image", newses.get(ii).getImgsrc());
                                                values.put("title", newses.get(ii).getTitle());
                                                values.put("origin", newses.get(ii).getSource());
                                                values.put("source", newses.get(ii).getUrl());
                                                values.put("id", newses.get(ii).getPostid());
                                                values.put("category", newses.get(ii).getCategory());
                                                values.put("like", String.valueOf(newses.get(ii).getIsLiked()));
                                                values.put("body", newses.get(ii).getBody());
                                                values.put("click", "");
                                                values.put("search", "");
                                                String a = "";
                                                for (int j = 0; j < newses.get(ii).getName().size() - 1; j++) {
                                                    a += newses.get(ii).getName().get(j);
                                                    a += ";";
                                                }
                                                if (newses.get(ii).getName().size() > 0)
                                                    a += newses.get(ii).getName().get(newses.get(ii).getName().size() - 1);
                                                values.put("name", a);
                                                db.insert("News", null, values);
                                                values.clear();
                                                Log.i("initfuck", "fuck");
                                            }

                                            @Override
                                            public void onFailure(Call<NewDetail> call, Throwable t) {
                                                Log.i("LHD", t.getMessage());
                                            }
                                        });
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Badapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Badapter.notifyDataSetChanged();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<NewsSummary> call, Throwable t) {
                                Log.i("LHD", "访问失败");
                            }
                        });
                    }

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
            listView.onRefreshComplete();
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
                if(newses.get(position).getIsClicked()) {
                    convertView.setBackgroundColor(Color.parseColor("#A3A3A3"));
                }
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
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
