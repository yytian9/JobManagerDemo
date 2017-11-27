package com.example.yytian.jobmanagerdemo.job.main;

import com.example.yytian.jobmanagerdemo.job.callback.JobCallback;
import com.example.yytian.jobmanagerdemo.job.config.JobConfig;
import com.example.yytian.jobmanagerdemo.job.constant.ComType;

/**
 * Created by yytian on 17-11-27.
 */

public class JobManager implements JobContract.View {
    public JobManager() {
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void resumeAllJob() {

    }

    @Override
    public boolean checkJobRunningByID(int job) {
        return false;
    }

    @Override
    public boolean addJob(Runnable runnable, ComType.THREAD_TYPE threadType) {
        return false;
    }

    @Override
    public boolean addJob(Runnable runnable, ComType.THREAD_TYPE threadType, JobCallback jobCallback) {
        return false;
    }

    @Override
    public boolean addJob(ComType.JOB_TYPE jobType, JobCallback jobCallback) {
        return false;
    }

    @Override
    public boolean addJob(JobConfig jobConfig) {
        return false;
    }

    @Override
    public boolean clearAll() {
        return false;
    }

    @Override
    public boolean clearIdleJob() {
        return false;
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }
}
