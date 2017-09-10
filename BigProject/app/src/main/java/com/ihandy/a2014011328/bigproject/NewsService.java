package com.ihandy.a2014011328.bigproject;

/**
 * Created by 金子童 on 2017/9/9.
 */

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.Call;

public interface NewsService {
    @GET("latest")
    Call<NewsSummary> getNewsList(
            @Query("pageSize") String size,
            @Query("category") String id,
            @Query("pageNo") int startPage);

    @GET("/news/action/query/detail")
    Call<NewsSummary> getNewsDetail(
            @Query("newsId") String size);
}
