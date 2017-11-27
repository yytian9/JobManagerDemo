package com.example.yytian.jobmanagerdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.birbit.android.jobqueue.CancelResult;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;
import com.birbit.android.jobqueue.callback.JobManagerCallback;
import com.example.yytian.jobmanagerdemo.base.BaseApplication;
import com.example.yytian.jobmanagerdemo.sample.MyJob;

public class MainActivity extends AppCompatActivity {

    private JobManager jobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jobManager= BaseApplication.getJobManager();

        findViewById(R.id.btn_add_one_job).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                jobManager.addJobInBackground(new MyJob("任务1")); //启动任务，跑！
                jobManager.cancelJobsInBackground(new CancelResult.AsyncCancelCallback() {
                    @Override
                    public void onCancelled(CancelResult cancelResult) {

                    }
                }, TagConstraint.ANY,"任务1");
                jobManager.addCallback(new JobManagerCallback() {
                    @Override
                    public void onJobAdded(@NonNull Job job) {

                    }

                    @Override
                    public void onJobRun(@NonNull Job job, int resultCode) {

                    }

                    @Override
                    public void onJobCancelled(@NonNull Job job, boolean byCancelRequest, @Nullable Throwable throwable) {

                    }

                    @Override
                    public void onDone(@NonNull Job job) {

                    }

                    @Override
                    public void onAfterJobRun(@NonNull Job job, int resultCode) {

                    }
                });
                //jobManager.addJobInBackground(new MyJob("任务2"));
                //jobManager.addJobInBackground(new MyJob("任务3"));
                //jobManager.addJobInBackground(new MyJob("任务4"));
                //jobManager.addJobInBackground(new MyJob("任务5"));
                //jobManager.addJobInBackground(new MyJob("任务6"));
            }
        });
    }
}
