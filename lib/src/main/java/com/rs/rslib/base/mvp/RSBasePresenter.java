package com.rs.rslib.base.mvp;

import android.app.Service;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v4.app.Fragment;

import com.rs.rslib.interfaces.Lifecycleable;
import com.rs.rslib.utils.rx.RxAndroidUtils;
import com.rs.rslib.utils.rx.SimpleDisposableSubscriber;
import com.rs.rslib.utils.rx.SimpleFlowableOnSubscribe;
import com.rs.rslib.utils.rx.SimpleFlowableSubsriber;

import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * author: xiecong
 * create time: 2017/11/14 15:27
 * lastUpdate time: 2017/11/14 15:27
 */

public abstract class RSBasePresenter<M extends IModel, V extends IView> implements IPresenter, LifecycleObserver {
    protected M mModel;
    protected V mRootView;

    public void attachVM(V rootView, M model) {
        this.mRootView = rootView;
        this.mModel = model;
        onStart();
    }

    @Override
    public void onStart() {
        //将 LifecycleObserver 注册给 LifecycleOwner 后 @OnLifecycleEvent 才可以正常使用
        if (mRootView != null && mRootView instanceof LifecycleOwner) {
            ((LifecycleOwner) mRootView).getLifecycle().addObserver(this);//给view添加观察者-> this(presenter)
            if (mModel != null && mModel instanceof LifecycleObserver) {
                ((LifecycleOwner) mRootView).getLifecycle().addObserver((LifecycleObserver) mModel);//给view添加观察者-> model
            }
        }
    }


    public void detachVM() {
        onDestroy();
        this.mModel = null;
        this.mRootView = null;
    }

    @Override
    public void onDestroy() {
        if (mModel != null)
            mModel.onDestroy();
    }

    /**
     * 只有当 {@code mRootView} 不为 null, 并且 {@code mRootView} 实现了 {@link LifecycleOwner} 时, 此方法才会被调用
     * 所以当您想在 {@link Service} 以及一些自定义 {View} 或自定义类中使用 {@code Presenter} 时
     * 您也将不能继续使用 {@link OnLifecycleEvent} 绑定生命周期
     *
     * @param owner link { SupportActivity} and {@link Fragment}
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(LifecycleOwner owner) {
        /**
         * 注意, 如果在这里调用了 {@link #onDestroy()} 方法, 会出现某些地方引用 {@code mModel} 或 {@code mRootView} 为 null 的情况
         * 比如在 {@link RxLifecycle} 终止 {@link Observable} 时, 在 {@link Observable#doFinally(Action)} 中却引用了 {@code mRootView} 做一些释放资源的操作, 此时会空指针
         * 或者如果你声明了多个 @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY) 时在其他 @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
         * 中引用了 {@code mModel} 或 {@code mRootView} 也可能会出现此情况
         */
        owner.getLifecycle().removeObserver(this);
    }

    /**
     * @param onSubscribe
     * @param subsriber
     * @param <T>
     * 执行异步任务
     */
    public  <T> void excuteFlowable(SimpleFlowableOnSubscribe<T> onSubscribe, SimpleDisposableSubscriber<T> subsriber){
        Lifecycleable lifecycleable = null;
        if (mRootView instanceof Lifecycleable) {
            lifecycleable = (Lifecycleable) mRootView;
        } else {
            throw new IllegalArgumentException("view isn't Lifecycleable");
        }
        RxAndroidUtils.createSimpleFlowable(lifecycleable,onSubscribe,subsriber);

    }
    public  <T> void excuteFlowable(SimpleFlowableOnSubscribe<T> onSubscribe, SimpleFlowableSubsriber<T> subsriber){
        Lifecycleable lifecycleable = null;
        if (mRootView instanceof Lifecycleable) {
            lifecycleable = (Lifecycleable) mRootView;
        } else {
            throw new IllegalArgumentException("view isn't Lifecycleable");
        }
        RxAndroidUtils.createLifecycleFlowable(lifecycleable, onSubscribe, subsriber, new Consumer<Subscription>() {
            @Override
            public void accept(Subscription subscription) throws Exception {
                mRootView.showLoading();
            }
        });
    }
}
