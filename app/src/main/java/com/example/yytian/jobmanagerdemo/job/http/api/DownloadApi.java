package com.example.yytian.jobmanagerdemo.job.http.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * author:  yytian
 * time:    2017/8/24 11:07
 * des:
 */

public interface DownloadApi {
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);
}
