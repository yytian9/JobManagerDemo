package com.example.yytian.jobmanagerdemo.base;

import android.app.Application;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;

/**
 * Created by yytian on 17-11-27.
 */

public class BaseApplication extends Application {
    private static JobManager jobManager;
    private static BaseApplication sInstance;

    public BaseApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        jobManager=configureJobManager();
    }

    public static BaseApplication getInstance() {
        return sInstance;
    }

    public static JobManager getJobManager() {

        return jobManager;
    }

    private JobManager configureJobManager() {
        Configuration.Builder builder = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "yytian job";

                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }

                    @Override
                    public void v(String text, Object... args) {

                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120);//wait 2 minute

        return new JobManager(builder.build());
    }
}
