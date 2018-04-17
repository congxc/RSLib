package com.rs.rslib.utils.rx;


import com.rs.rslib.utils.LogUtils;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * author: xiecong
 * create time: 2018/4/12 09:31
 * lastUpdate time: 2018/4/12 09:31
 * Rx被观察者 上游
 */

public abstract class SimpleFlowableOnSubscribe<T> implements FlowableOnSubscribe<T> {
    @Override
    public void subscribe(FlowableEmitter<T> e) throws Exception {
        try {
            T t = callNext();
            e.onNext(t);
            if (!e.isCancelled()) {
                // TODO: 2018/4/12 注意 这里如果不做判断 当界面销毁了
                //TODO: 接续走onComplete可能会引起未知错误（比如UI操作 然而UI已经销毁了 nullPointer）
                //TODO: 与onError的区别是  绑定了View(注意是绑定View 而不是Activity、Fragment)  当View销毁，会立即先调用一次onComplete
                //TODO: 当然如果下游是Disposable 手动调用dispose方法 或者是绑定Activity、Fragment 是一次都不会走的
                e.onComplete();
            }else{
                LogUtils.error("cong.xie", "subscribe  onComplete isCancelled ");
            }
        } catch (Exception e1) {
            // TODO: 2018/4/12 注意 这里如果不做判断 当界面销毁了 继续走onError可能会引起未知错误
            // TODO:只要取消了 onError一次都不会调用
            if (!e.isCancelled()) {
                e.onError(e1);
            }else{
                e1.printStackTrace();
                LogUtils.error("cong.xie", "subscribe onError isCancelled ");
            }
        }
    }

    /**
     * 执行方法体
     * @return 数据
     * @throws Exception
     */
    public abstract T callNext() throws Exception;
}
