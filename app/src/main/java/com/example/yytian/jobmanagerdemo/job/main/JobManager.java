package com.example.yytian.jobmanagerdemo.job.main;

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
