package com.ihandy.a2014011328.bigproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PageFragment extends Fragment {
    public static final String ARGS_PAGE = "args_page";
    private static final int RESULT_OK = -1;
    private int mPage;

    private Handler handler = new Handler();
    private ArrayList<News> newses = new ArrayList<>();
    private ListView listView;
    private PullToRefreshListView listView1;
    private boolean save;
    private News news;
    private int pos = 0;


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
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page,container,false);
        listView1 = (PullToRefreshListView) view.findViewById(R.id.listView);
        listView1.setMode(PullToRefreshBase.Mode.BOTH);
        final NewsListAdapter Badapter = new NewsListAdapter();
        listView1.setAdapter(Badapter);

        listView1.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            // 下拉Pulling Down
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉的时候刷新新闻列表

                ConnectivityManager cwjManager=(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cwjManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()){

                newses.clear();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String cat = "";
                        cat = MyFragmentPagerAdapter.titles1[mPage-1];
                        //HttpRequest request = HttpRequest.get("http://assignment.crazz.cn/news/query?locale=en&category="+cat);
                        HttpRequest request = HttpRequest.get("http://166.111.68.66:2042/news/action/query/latest?pageNo=" + pos + "&pageSize=30&category=" + cat);
                        String body = request.body();
                        try {
                            JSONArray jsonArray = new JSONObject(body).getJSONArray("list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                                String title = jsonObject.getString("news_Title");
                                String origin = jsonObject.getString("news_Source");
                                String category = jsonObject.getString("newsClassTag");
                                String id = jsonObject.getString("news_ID");
                                String [] image = jsonObject.getString("news_Pictures").split(";");
                                String src = jsonObject.getString("news_URL");
                                newses.add(new News(title, origin, image[0], id, category, src));
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Badapter.notifyDataSetChanged();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                    Toast.makeText(getActivity(),"Refreshing finished",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(),"Sorry. Can not connect to the Internet,\nrefreshment failed.",Toast.LENGTH_LONG).show();
                }
                new FinishRefresh().execute();
            }

            // 上拉Pulling Up
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉的时候读取更多的新闻

                ConnectivityManager cwjManager=(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cwjManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        News last = newses.get(newses.size() - 1);
                        String cat ="";
                        cat =  MyFragmentPagerAdapter.titles1[mPage-1];
                        //HttpRequest request = HttpRequest.get("http://assignment.crazz.cn/news/query?locale=en&category="+cat+"&max_news_id="+lastId);
                        HttpRequest request = HttpRequest.get("http://166.111.68.66:2042/news/action/query/latest?pageNo=" + pos + "&pageSize=30&category=" + cat);
                        
                        String body = request.body();
                        try {
                            JSONArray jsonArray = new JSONObject(body).getJSONArray("list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                                String title = jsonObject.getString("news_Title");
                                String origin = jsonObject.getString("news_Source");
                                String category = jsonObject.getString("newsClassTag");
                                String id = jsonObject.getString("news_ID");
                                String [] image = jsonObject.getString("news_Pictures").split(";");
                                String src = jsonObject.getString("news_URL");
                                newses.add(new News(title, origin, image[0], id, category, src));}

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Badapter.notifyDataSetChanged();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();}
                else{
                    Toast.makeText(getActivity(),"Can not connect to the Internet >< ",Toast.LENGTH_SHORT).show();
                }
                new FinishRefresh().execute();
            }

        });

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                news = newses.get(position - 1);

                pos = position;
                if(news.getSource() == "null"){
                    Toast.makeText(getActivity(), "Sorry, no more details.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "You clicked:\n"+ news.getTitle(),
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(),Details.class);
                    //传出的参数有：title, url, isLiked
                    Log.i("okok", "fuck");
                    intent.putExtra("title", news.getTitle());
                    intent.putExtra("source", news.getSource());
                    intent.putExtra("isLiked", news.getIsLiked());
                    startActivityForResult(intent, 11);

                }

            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this.getActivity()).build();  //获取context的方法：this.getActivity()
        ImageLoader.getInstance().init(config);

        ConnectivityManager cwjManager=(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cwjManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()){

            //网络请求必须写在新起的线程中
            new Thread(new Runnable() {
            @Override
            public void run() {
                pos++;
                String cat ="";
                cat =  MyFragmentPagerAdapter.titles1[mPage-1];
                //HttpRequest request = HttpRequest.get("http://assignment.crazz.cn/news/query?locale=en&category="+cat);
                HttpRequest request = HttpRequest.get("http://166.111.68.66:2042/news/action/query/latest?pageNo=" + pos + "&pageSize=10&category=" + cat);
                String body = request.body();
                Log.i("Print", body);

                try {
                    JSONArray jsonArray = new JSONObject(body).getJSONArray("list");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                        String title = jsonObject.getString("news_Title");
                        String origin = jsonObject.getString("news_Source");
                        String category = jsonObject.getString("newsClassTag");
                        String id = jsonObject.getString("news_ID");
                        String [] image = jsonObject.getString("news_Pictures").split(";");
                        String src = jsonObject.getString("news_URL");

                        newses.add(new News(title, origin, image[0], id, category, src));

                        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
                        Cursor cursor = db.query("News",null,"title = ?",new String[]{title},null,null,null);
                        if(!cursor.moveToFirst()){
                            ContentValues values = new ContentValues();
                            values.put("image", image[0]);
                            values.put("title", title);
                            values.put("origin", origin);
                            values.put("source", src);
                            values.put("id", id);
                            values.put("category", category);
                            values.put("like", 0);
                            db.insert("News", null, values);
                            values.clear();}
                        else{}

                    }

                    //非主线程无法修改UI，所以使用handler将修改UI的代码抛到主线程做
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Badapter.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        }
        else
        {
            Toast.makeText(getActivity(),"Can not connect to the Internet >< ",Toast.LENGTH_SHORT).show();

            SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
            Cursor cursor = db.query("News", null, "category = ?", new String[]{MyFragmentPagerAdapter.titles00[mPage-1]}, null, null, null);
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
                    newses.add(new News(title, origin, image, id, category, src));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 11:
                if(resultCode == RESULT_OK){
                    save = data.getBooleanExtra("likeData",false);
                    Log.d("Here",save+"");
                    news.setIsLiked(save);
                }
                break;
            default:
        }
    }


    public DisplayImageOptions getDisplayOption() {
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true) //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.ARGB_8888)//设置图片的解码类型//
                .build();//构建完成
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
        protected void onPostExecute(Void result){
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
                //convertView = getLayoutInflater().inflate(R.layout.image_list_item, parent, false);
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.news_item, parent, false);

                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.news_image);
                viewHolder.copyright = (TextView) convertView.findViewById(R.id.news_name);
                viewHolder.source = (TextView) convertView.findViewById(R.id.news_source);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //异步加载图片方法
            ImageLoader.getInstance().displayImage(newses.get(position).getImage(), viewHolder.imageView, getDisplayOption());
            viewHolder.copyright.setText(newses.get(position).getTitle());
            viewHolder.source.setText(newses.get(position).getOrigin());

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
