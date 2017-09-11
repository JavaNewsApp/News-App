package com.example.ForMoreNews.bigproject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

/**
 * Created by yu on 17-9-11.
 */

public class SearchActivity extends BaseActivity{
    private String[] mStrs =  {"0", "1", "2", "3", "4", "5", "6"};
    private ArrayList <String> strs = new ArrayList<>();
    private SearchView mSearchView;
    private ListView mListView;
    private String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        Intent intent = getIntent();
        search = intent.getStringExtra("search");

        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setSubmitButtonEnabled(true);  //提交按钮
        mListView = (ListView) findViewById(R.id.listView);
        if(strs.size() > 0) {
            int cnt = 0;
            for (int i = (strs.size() > 7 ? 6 : strs.size()); i >= 0; i--) {
                mStrs[cnt] = strs.get(i);
                cnt++;
            }
        }
        mListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mStrs));
        mListView.setTextFilterEnabled(true);
        Log.i("search0", "5");
        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(mSearchView != null){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null){
                        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
                        doMySearch(query);
                    }
                    mSearchView.clearFocus();  //不获取焦点
                }
                return true;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    mListView.setFilterText(newText);
                }else{
                    mListView.clearTextFilter();
                }
                Log.i("search0", "6");
                return false;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
        Log.i("search0", "7");
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
        Log.i("search0", "8");
    }

    private void doMySearch(String query) {
        strs.add(query);
        Intent intent=new Intent(SearchActivity.this, Search.class);
        intent.putExtra("search", query);
        startActivity(intent);
    }
}
