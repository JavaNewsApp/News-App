package com.example.ForMoreNews.bigproject;

/**
 * Created by 金子童 on 2017/9/9.
 */

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.Call;

public interface NewsService {
    @GET("detail")
    Call<NewDetail> getNewDetail(
            @Query("newsId") String id);

    @GET("latest")
    Call<NewsSummary> getNewsList(
            @Query("pageSize") String size,
            @Query("category") String id,
            @Query("pageNo") int startPage);

    @GET("search")
    Call<NewsSummary> getNewsSearch(
            @Query("keyword") String size);
}
