package com.example.yytian.jobmanagerdemo.job.main;

import com.example.yytian.jobmanagerdemo.job.schedulers.BaseSchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by yytian on 17-11-27.
 */

public class JobPresenter implements JobContract.Presenter {

    private final JobContract.View mView;
    private final BaseSchedulerProvider mSchedulerProvider;
    private final CompositeDisposable mSubscriptions;
    private final JobContract.Model mModel;

    public JobPresenter(JobContract.View view, BaseSchedulerProvider schedulerProvider,
                          JobContract.Model model) {
        mModel = model;
        mView = view;
        mSchedulerProvider = schedulerProvider;
        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
    }


    @Override
    public void unSubscribe() {
        mSubscriptions.dispose();
    }
}
