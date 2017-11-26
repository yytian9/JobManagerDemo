package com.example.yytian.jobmanagerdemo.job.callback;

/**
 * Created by yytian on 17-11-27.
 */

public interface JobCallback {
    void onJobStart();
    void onJobSuccess();
    void onJobFailed();
}
