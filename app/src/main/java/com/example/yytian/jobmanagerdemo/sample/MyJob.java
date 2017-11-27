package com.example.yytian.jobmanagerdemo.sample;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

/**
 * Created by yytian on 17-11-27.
 */

public class MyJob extends Job {
    private String tag;

    public MyJob(String tag) {
        super(new Params(500).requireNetwork().persist().groupBy(tag));
        this.tag = tag;
        Log.d(tag, "初始化");
    }

    @Override
    public void onAdded() {
        Log.d(tag, "添加任务");
    }

    //在这里面放置耗时的后台线程化任务
    @Override
    public void onRun() throws Throwable {
        Log.d(tag, "开始运行...");

        int i = 0;
        while (true) {
            i++;

            SystemClock.sleep(2000);
            Log.d(tag, String.valueOf(i));
            if (i == 10)
                break;
        }

        Log.d(tag, "完成");
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        // An error occurred in onRun.
        // Return value determines whether this job should retry or cancel. You can further
        // specify a backoff strategy or change the job's priority. You can also apply the
        // delay to the whole group to preserve jobs' running order.

        Log.d(tag, "runCount：" + runCount);

        return RetryConstraint.createExponentialBackoff(runCount, 1000);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }
}
