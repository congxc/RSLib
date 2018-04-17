package com.rs.rslib.utils.rx;

import android.view.View;

import com.rs.rslib.interfaces.ActivityLifecycleable;
import com.rs.rslib.interfaces.FragmentLifecycleable;
import com.rs.rslib.interfaces.Lifecycleable;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import org.reactivestreams.Subscription;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * author: xiecong
 * create time: 2018/4/11 19:08
 * lastUpdate time: 2018/4/11 19:08
 */

public class RxAndroidUtils {

    private static Map<String,ResourceSubscriber> mResourceSubscriberMap = new HashMap<>();

    /*******************简化方法*******************************************************************/
    /**
     * 创建异步操作并返回数据到UI线程 同时有onStart onNext onError onComplete 并且可以手动取消订阅（终止接收数据）
     * @param onSubscribe 上游被观察者
     * @param subscriber 下游观察者
     * @param <T>
     */
    public static <T> void createSimpleFlowable(SimpleFlowableOnSubscribe<T> onSubscribe, SimpleDisposableSubscriber<T> subscriber){
        Flowable.create(onSubscribe, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    /**
     * 创建异步操作并返回数据到UI线程 同时有onStart onNext onError onComplete 并且可以自动取消订阅（终止接收数据）
     * @param onSubscribe 上游被观察者
     * @param subscriber 下游观察者
     * @param <T>
     */
    public static <T> void createSimpleFlowable(Lifecycleable lifecycleable,SimpleFlowableOnSubscribe<T> onSubscribe, SimpleDisposableSubscriber<T> subscriber){
        LifecycleTransformer<T> transformer = null;
        if (lifecycleable instanceof ActivityLifecycleable) {
            Subject<ActivityEvent> lifecycleSubject = ((ActivityLifecycleable) lifecycleable).provideLifecycleSubject();
            transformer = RxLifecycleAndroid.bindActivity(lifecycleSubject);
        }else if(lifecycleable instanceof FragmentLifecycleable){
            Subject<FragmentEvent> lifecycleSubject = ((FragmentLifecycleable) lifecycleable).provideLifecycleSubject();
            transformer = RxLifecycleAndroid.bindFragment(lifecycleSubject);
        }else{
            throw new TypeNotPresentException("activity or fragment type",new Exception("your activity or fragment type error"));
        }
        Flowable.create(onSubscribe, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .compose(transformer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 创建异步操作并返回数据到UI线程 同时有onStart onNext onError onComplete 并且可以自动取消订阅（终止接收数据）
     * @param onSubscribe 上游被观察者
     * @param subscriber 下游观察者
     * @param <T>
     */
    public static <T> void createSimpleFlowable(View view,SimpleFlowableOnSubscribe<T> onSubscribe, SimpleDisposableSubscriber<T> subscriber){
        LifecycleTransformer<T> transformer = RxLifecycleAndroid.bindView(view);
        Flowable.create(onSubscribe, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .compose(transformer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    /**********************简化方法************************************************************************/
    /**
     * 创建异步操作并返回数据到UI线程
     * @param onSubscribe 上游被观察者
     * @param subscriber 下游观察者
     * @param <T>
     */
    public static <T> void createFlowable(FlowableOnSubscribe<T> onSubscribe, FlowableSubscriber<T> subscriber){
        Flowable.create(onSubscribe, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    /**
     * 创建异步操作并返回数据到UI线程
     * @param onSubscribe 上游被观察者
     * @param subscriber 下游观察者
     * @param onStart 上游开始执行
     * @param <T>
     */
    public static <T> void createFlowable(FlowableOnSubscribe<T> onSubscribe
            , FlowableSubscriber<T> subscriber, Consumer<Subscription> onStart){
        Flowable.create(onSubscribe, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(onStart)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**********************************************************************************************/
    /**
     *  创建 可以解除订阅 的 异步操作并返回数据到UI线程 需要手动调用{@releaseSubscriber(String key)}
     * @param key
     * @param onSubscribe
     * @param subscriber
     * @param <T>
     */
    public static <T> void createDisposableFlowable(String key,FlowableOnSubscribe<T> onSubscribe, ResourceSubscriber<T> subscriber){

        mResourceSubscriberMap.put(key,subscriber);

        Flowable.create(onSubscribe,BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public static <T> Disposable createDisposableFlowable(FlowableOnSubscribe<T> onSubscribe, Consumer<T> onNext,Consumer<Throwable> onError){
       return Flowable.create(onSubscribe, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext,onError);
    }

    public static <T> Disposable createDisposableFlowable(FlowableOnSubscribe<T> onSubscribe,  Consumer<T> onNext
            ,Consumer<Throwable> onError, Consumer<Subscription> onStart){
        return Flowable.create(onSubscribe, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(onStart)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext,onError);
    }


    /**********************************************************************************************/
    /**
     *  创建 可以自动解除订阅 的 异步操作并返回数据到UI线程
     * @param onSubscribe
     * @param subscriber
     * @param <T>
     */
    public static <T> void createLifecycleFlowable(Lifecycleable lifecycleable,FlowableOnSubscribe<T> onSubscribe, FlowableSubscriber<T> subscriber){
        LifecycleTransformer<T> transformer = null;
        if (lifecycleable instanceof ActivityLifecycleable) {
            Subject<ActivityEvent> lifecycleSubject = ((ActivityLifecycleable) lifecycleable).provideLifecycleSubject();
            transformer = RxLifecycleAndroid.bindActivity(lifecycleSubject);
        }else if(lifecycleable instanceof FragmentLifecycleable){
            Subject<FragmentEvent> lifecycleSubject = ((FragmentLifecycleable) lifecycleable).provideLifecycleSubject();
            transformer = RxLifecycleAndroid.bindFragment(lifecycleSubject);
        }else{
            throw new TypeNotPresentException("activity or fragment type",new Exception("your activity or fragment type error"));
        }

        Flowable.create(onSubscribe, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .compose(transformer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public static <T> void createLifecycleFlowable(Lifecycleable lifecycleable,FlowableOnSubscribe<T> onSubscribe,
                                          FlowableSubscriber<T> subscriber,Consumer<Subscription> onStart){
        LifecycleTransformer<T> transformer = null;
        if (lifecycleable instanceof ActivityLifecycleable) {
            Subject<ActivityEvent> lifecycleSubject = ((ActivityLifecycleable) lifecycleable).provideLifecycleSubject();
            transformer = RxLifecycleAndroid.bindActivity(lifecycleSubject);
        }else if(lifecycleable instanceof FragmentLifecycleable){
            Subject<FragmentEvent> lifecycleSubject = ((FragmentLifecycleable) lifecycleable).provideLifecycleSubject();
            transformer = RxLifecycleAndroid.bindFragment(lifecycleSubject);
        }else{
            throw new TypeNotPresentException("activity or fragment type",new Exception("your activity or fragment type error"));
        }

        Flowable.create(onSubscribe, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .compose(transformer)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(onStart)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    public static <T> void createLifecycleFlowable(View view, FlowableOnSubscribe<T> onSubscribe, FlowableSubscriber<T> subscriber){

        LifecycleTransformer<T> transformer = RxLifecycleAndroid.bindView(view);

        Flowable.create(onSubscribe, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .compose(transformer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public static <T> void createLifecycleFlowable(View view, FlowableOnSubscribe<T> onSubscribe
            , FlowableSubscriber<T> subscriber,Consumer<Subscription> onStart){

        LifecycleTransformer<T> transformer = RxLifecycleAndroid.bindView(view);

        Flowable.create(onSubscribe, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .compose(transformer)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(onStart)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**********************************************************************************************/
    /**
     * 释放Rx 解除订阅
     * @param key
     */
    public static void releaseSubscriber(String key){
        if (mResourceSubscriberMap != null && mResourceSubscriberMap.containsKey(key)) {
            ResourceSubscriber subscriber = mResourceSubscriberMap.get(key);
            if (subscriber != null && !subscriber.isDisposed()) {
                subscriber.dispose();
                mResourceSubscriberMap.remove(key);
            }
        }
    }

}
