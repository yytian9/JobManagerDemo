package com.example.yytian.jobmanagerdemo.job.main;

import com.example.yytian.jobmanagerdemo.job.callback.JobCallback;
import com.example.yytian.jobmanagerdemo.job.config.JobConfig;
import com.example.yytian.jobmanagerdemo.job.constant.ComType;

/**
 * Created by yytian on 17-11-27.
 */

public class JobContract {
    interface View {
        void onCreate();

        void onResume();

        void resumeAllJob();

        boolean checkJobRunningByID(int job);

        boolean addJob(Runnable runnable, ComType.THREAD_TYPE threadType);
        boolean addJob(Runnable runnable, ComType.THREAD_TYPE threadType, JobCallback jobCallback);
        boolean addJob(ComType.JOB_TYPE jobType, JobCallback jobCallback);
        boolean addJob(JobConfig jobConfig);

        boolean clearAll();

        boolean clearIdleJob();

        void onPause();

        void onDestroy();

    }

    interface Presenter  {


        void subscribe();

        void unSubscribe();
    }
    interface Model{

    }
}
