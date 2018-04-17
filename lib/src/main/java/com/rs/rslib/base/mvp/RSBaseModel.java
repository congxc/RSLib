package com.rs.rslib.base.mvp;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

/**
 * author: xiecong
 * create time: 2017/11/15 11:34
 * lastUpdate time: 2017/11/15 11:34
 */

public abstract class RSBaseModel<T> implements IModel, LifecycleObserver {

    @Override
    public void onDestroy() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(LifecycleOwner owner) {
        owner.getLifecycle().removeObserver(this);
    }
}

