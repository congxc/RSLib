package com.rs.rslib.utils.rx;

import org.reactivestreams.Subscription;

import io.reactivex.FlowableSubscriber;

/**
 * author: xiecong
 * create time: 2018/4/12 09:38
 * lastUpdate time: 2018/4/12 09:38
 * Rx观察者  下游 onNext()接收数据 onError()接收错误消息 onComplete（）接收完成消息
 */

public abstract class SimpleFlowableSubsriber<T> implements FlowableSubscriber<T> {
    @Override
    public void onSubscribe(Subscription s) {
        s.request(1);
    }

    @Override
    public void onComplete() {

    }
}
