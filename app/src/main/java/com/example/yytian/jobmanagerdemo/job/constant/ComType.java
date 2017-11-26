package com.example.yytian.jobmanagerdemo.job.constant;

/**
 * Created by yytian on 17-11-27.
 */

public class ComType {
    public static enum JOB_TYPE {
        NET_REQUEST,
        DOWNLOAD,
        INTERVAL,
        COUNTDOWN;

        private JOB_TYPE() {
        }
    }
    public static enum THREAD_TYPE {
        UI,
        COMPUTE,
        IO;

        private THREAD_TYPE() {
        }
    }
    public static enum RUN_TYPE {
        SINGLE,
        MULTIPLE;

        private RUN_TYPE() {
        }
    }
}
