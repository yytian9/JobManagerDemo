package com.example.yytian.jobmanagerdemo.job.http;

import com.example.yytian.jobmanagerdemo.job.http.api.FungoApi;
import com.google.gson.JsonElement;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Author  Farsky
 * Date    2016/11/14 0014
 * Des
 */
public class FungoRequest {

    private final FungoApi mFungoApi;

    public FungoRequest(FungoApi fungoApi) {
        mFungoApi = fungoApi;
    }

    /**
     * @param sourceUrl 接口号
     * @param params    请求中data对应的参数，一个json格式数据
     * @param clazz     请求成功对象data里面的实体对象
     * @param <T>       请求成功对象data里面的实体对象，对过clazz确定
     * @return 返回成功后的数据
     */
    public <T> Observable<T> getRequest(String sourceUrl, Map<String, Object> params, Class<T> clazz, boolean needRetry) {
        return getRequest(sourceUrl, params, clazz, needRetry, CacheTime.NOT_CACHE);
    }

    public <T> Observable<T> getRequest(String sourceUrl, Map<String, Object> params, Class<T> clazz, boolean needRetry, int cacheTime) {

        //********normal request , with response**************//
        if (clazz != null) {
            //3.2不用重试机制
            return getRequestByStr(sourceUrl, params, clazz, cacheTime);
        }

        BaseRequestInfo encryptionParams = getEncryptionData(sourceUrl, params);
        //********special request , without response ,without retry**************//
        if (sourceUrl.contains("http:") || sourceUrl.contains("https:")) {
            mFungoApi.requestWithFullUrl(sourceUrl, encryptionParams);
        } else {
            mFungoApi.request(sourceUrl, encryptionParams);
        }
        //不用返回值的
        return null;
    }

    public <T> Observable<T> getGetRequest(String sourceUrl, Class<T> clazz, boolean needRetry) {
        return getGetRequest(sourceUrl, clazz, needRetry, CacheTime.NOT_CACHE);
    }

    public <T> Observable<T> getGetRequest(String sourceUrl, Class<T> clazz, boolean needRetry, int cacheTime) {
        if (clazz != null) {
            //3.2不用重试机制
            return getRequestByStrWithGet(sourceUrl, clazz, cacheTime);
        }
        mFungoApi.requestGetWithFullUrl(sourceUrl);
        return null;
    }

    private <T> Observable<T> getRequestByStrWithGet(final String sourceUrl, final Class<T> clazz, int cacheTime) {
        if (!NetworkUtil.isNetworkConnected()) {
            return Observable.create(e -> {
                if (e != null && !e.isDisposed()) {
                    e.onError(new RequestError(MessageCode.NETWORK_UN_CONNECTED, ConstantsStr.NET_UN_CONNECTED));//当前无网络
                    e.onComplete();
                }
            });
        }
        return Observable.just(sourceUrl)
                .flatMap(new Function<String, ObservableSource<JsonElement>>() {
                    @Override
                    public ObservableSource<JsonElement> apply(String url) throws Exception {

                        return mFungoApi.requestGetWithFullUrl(url);
                    }
                })
                .flatMap(new Function<JsonElement, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(final JsonElement data) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<T>() {

                            @Override
                            public void subscribe(ObservableEmitter<T> subscriber) throws Exception {
                                if (data == null) {
                                    Object commonData = new Object();
                                    subscriber.onNext((T) commonData);
                                }

                                try {
                                    Logger.i("Fungo Request OK", "---> sourceUrl ：" + sourceUrl + "\n success response : ---> " + data.toString());
                                    if (cacheTime != CacheTime.NOT_CACHE) {
                                        ACache.get(BaseApplication.getApplication()).put(sourceUrl, data.toString(), cacheTime);
                                    }
                                    T bean = GsonUtils.fromJson(data.toString(), clazz);
                                    if (!subscriber.isDisposed()) {
                                        subscriber.onNext(bean);
                                    }
                                } catch (Exception e) {
                                    Logger.e("logout" + " :" + e.getMessage());
                                    //compare to server,the mDataBean's type of client is different
                                    if (!subscriber.isDisposed()) {
                                        subscriber.onError(new RequestError(MessageCode.JSON_DATA_ERROR, data.toString()));
                                    }
                                }
                            }
                        });
                    }

                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(Throwable e) throws Exception {
                        //错误日志
                        /*StringBuilder sb = new StringBuilder(100);
                        sb.append(TimeUtils.getNowDatetime()).append(' ').append("--->  sourceUrl ：").append(sourceUrl)
                                .append("\n Error message : ---> ").append(e.toString());
                        if (e instanceof RequestError) {
                            sb.append("\n Error Code : ---> ").append(((RequestError) e).getState());
                        }
                        Logger.e("FungoRequest--"+sb.toString());
                        ErrorLogHandler.getInstance().appendErrorLog(sb.toString());*/
                        if (e instanceof RequestError) {

                            if (((RequestError) e).getState() == MessageCode.SERVER_ERROR_500)
                                e = new RequestError(MessageCode.SERVER_ERROR_500, ConstantsStr.SERVER_ERR, RequestError.TYPE_SERVER);

                        } else if (e instanceof HttpException) {
                            if (CommonUtils.isServerErr(((HttpException) e).code())) {
                                e = new RequestError(MessageCode.SERVER_ERROR_500, ConstantsStr.SERVER_ERR, RequestError.TYPE_SERVER);//服务器挂了
                            } else {
                                e = new RequestError(MessageCode.HTTP_SERIES_ERROR, ConstantsStr.NET_TIME_OUT, RequestError.TYPE_SERVER);
                            }
                        } else if (e instanceof SocketTimeoutException
                                || e instanceof TimeoutException) {   //transform this error to local error,since this way is easier to handle
                            e = new RequestError(MessageCode.HTTP_SERIES_ERROR, ConstantsStr.NET_TIME_OUT);//服务连接超时
                        } else if (e instanceof ConnectException) {
                            e = new RequestError(MessageCode.HTTP_SERIES_ERROR, ConstantsStr.NET_NOT_GOOD);//当前网络较差
                        }
                        if (e instanceof RequestError) {
                            //在这里做全局的错误处理
                            Logger.i("Fungo Request Error", "--->  sourceUrl ：" + sourceUrl + "\n Error Code : ---> " + ((RequestError) e).getState() + "\n Error message : ---> " + e.toString());
                            e.printStackTrace();
                        } else {
                            Logger.i("Fungo Request Error", "--->  sourceUrl ：" + sourceUrl + "\n Error message : ---> " + e.toString());
                        }
                        return Observable.error(e);
                    }
                });

    }

    private <T> Observable<T> getRequestByStr(final String sourceUrl, Map<String, Object> params, final Class<T> clazz, int cacheTime) {
        BaseRequestInfo dataParams = getEncryptionData(sourceUrl, params);
        //没网络主动抛出，程序决定是否处理
        if (!NetworkUtil.isNetworkConnected()) {
            return Observable.create(e -> {
                if (e != null && !e.isDisposed()) {
                    e.onError(new RequestError(MessageCode.NETWORK_UN_CONNECTED, ConstantsStr.NET_UN_CONNECTED));//当前无网络
                    e.onComplete();
                }
            });
        }

        return Observable.just(sourceUrl)
                .flatMap(new Function<String, ObservableSource<BaseEntity>>() {
                    @Override
                    public ObservableSource<BaseEntity> apply(String url) throws Exception {
                        if (url.contains("http:") || url.contains("https:")) {
                            return mFungoApi.requestWithFullUrl(url, dataParams);
                        }
                        return mFungoApi.request(url, dataParams);
                    }
                })
                .flatMap(new Function<BaseEntity, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(final BaseEntity baseEntity) throws Exception {
                        if (baseEntity.errno == 0) {
                            return Observable.create(new ObservableOnSubscribe<T>() {

                                @Override
                                public void subscribe(ObservableEmitter<T> subscriber) throws Exception {
                                    if (baseEntity.data == null) {
                                        Object commonData = new Object();
                                        subscriber.onNext((T) commonData);
                                    }

                                    try {
                                        JsonElement data = baseEntity.data;
                                        Logger.i("Fungo Request OK", "---> sourceUrl ：" + sourceUrl + "\n success response : ---> " + data.toString());
                                        if (cacheTime != CacheTime.NOT_CACHE) {
                                            ACache.get(BaseApplication.getApplication()).put(new CacheHelper().getCacheKey(sourceUrl, params), data.toString(), cacheTime);
                                        }
                                        T bean = GsonUtils.fromJson(data.toString(), clazz);
                                        if (!subscriber.isDisposed()) {
                                            subscriber.onNext(bean);
                                        }
                                    } catch (Exception e) {
                                        Logger.e("logout" + " :" + e.getMessage());
                                        //compare to server,the mDataBean's type of client is different
                                        if (!subscriber.isDisposed()) {
                                            subscriber.onError(new RequestError(MessageCode.JSON_DATA_ERROR, baseEntity.toString()));
                                        }
                                    }
                                }
                            });
                        } else {
                            Logger.e("logout" + " new RequestError: baseEntity.errno = " + baseEntity.errno + ",baseEntity.desc = " + baseEntity.desc);
                            return Observable.error(new RequestError(baseEntity.errno, baseEntity.desc, RequestError.TYPE_SERVER));
                        }
                    }

                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(Throwable e) throws Exception {
                        //错误日志
                        /*StringBuilder sb = new StringBuilder(100);
                        sb.append(TimeUtils.getNowDatetime()).append(' ').append("--->  sourceUrl ：").append(sourceUrl)
                                .append("\n Error message : ---> ").append(e.toString());
                        if (e instanceof RequestError) {
                            sb.append("\n Error Code : ---> ").append(((RequestError) e).getState());
                        }
                        Logger.e("FungoRequest--"+sb.toString());
                        ErrorLogHandler.getInstance().appendErrorLog(sb.toString());*/
                        if (e instanceof RequestError) {

                            if (((RequestError) e).getState() == MessageCode.SERVER_ERROR_500)
                                e = new RequestError(MessageCode.SERVER_ERROR_500, ConstantsStr.SERVER_ERR, RequestError.TYPE_SERVER);

                        } else if (e instanceof HttpException) {
                            if (CommonUtils.isServerErr(((HttpException) e).code())) {
                                e = new RequestError(MessageCode.SERVER_ERROR_500, ConstantsStr.SERVER_ERR, RequestError.TYPE_SERVER);//服务器挂了
                            } else {
                                e = new RequestError(MessageCode.HTTP_SERIES_ERROR, ConstantsStr.NET_TIME_OUT, RequestError.TYPE_SERVER);
                            }
                        } else if (e instanceof SocketTimeoutException
                                || e instanceof TimeoutException) {   //transform this error to local error,since this way is easier to handle
                            e = new RequestError(MessageCode.HTTP_SERIES_ERROR, ConstantsStr.NET_TIME_OUT);//服务连接超时
                        } else if (e instanceof ConnectException) {
                            e = new RequestError(MessageCode.HTTP_SERIES_ERROR, ConstantsStr.NET_NOT_GOOD);//当前网络较差
                        }
                        if (e instanceof RequestError) {
                            //在这里做全局的错误处理
                            Logger.i("Fungo Request Error", "--->  sourceUrl ：" + sourceUrl + "\n Error Code : ---> " + ((RequestError) e).getState() + "\n Error message : ---> " + e.toString());
                            e.printStackTrace();
                        } else {
                            Logger.i("Fungo Request Error", "--->  sourceUrl ：" + sourceUrl + "\n Error message : ---> " + e.toString());
                        }
                        return Observable.error(e);
                    }
                });

    }

    /**
     * this's ready for encryption
     */
    private BaseRequestInfo getEncryptionData(String sourceUrl, Map<String, Object> params) {

        BaseRequestInfo baseRequestInfo = new BaseRequestInfo(params);

        Logger.i("Fungo Request Entity", "--->  sourceUrl ：" + sourceUrl + "\n request body : ---> " + GsonUtils.toJson(baseRequestInfo));

        return baseRequestInfo;

    }


}


