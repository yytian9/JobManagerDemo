package com.example.yytian.jobmanagerdemo.job.config;

import com.example.yytian.jobmanagerdemo.job.constant.ComType;

/**
 * Created by yytian on 17-11-27.
 */

public class JobConfig {

    private ComType.JOB_TYPE mJobType;

    public JobConfig() {

    }
    public JobConfig setJobType(ComType.JOB_TYPE jobType){
        mJobType =jobType;
        return this;
    }

}
