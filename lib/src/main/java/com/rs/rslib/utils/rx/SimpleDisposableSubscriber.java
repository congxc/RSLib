package com.rs.rslib.utils.rx;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * author: xiecong
 * create time: 2018/4/12 09:50
 * lastUpdate time: 2018/4/12 09:50
 * Rx观察者（下游） 可以手动调用dispose() 终止接收上游数据 并且自带开始执行回调方法invokeOnStart()
 */

public abstract class SimpleDisposableSubscriber<T> extends ResourceSubscriber<T>{

    @Override
    protected void onStart() {
        super.onStart();
        invokeOnStart();
    }

    /**
     * 上游开始执行
     */
    public abstract void invokeOnStart();

}
