package com.example.yytian.jobmanagerdemo.job.http.api;


import com.google.gson.JsonElement;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Author  Farsky
 * Date    2016/11/14 0014
 * Des     common request api,use for encapsulating http framework
 */
public interface FungoApi {

    @GET()
    Observable<JsonElement> requestGetWithFullUrl(@Url String sourceUrl);
}
