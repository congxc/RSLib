package com.rs.rslib.base.mvp;

/**
 * author: xiecong
 * create time: 2017/11/14 15:00
 * lastUpdate time: 2017/11/14 15:00
 *  description:MVP - P
 */

public interface IPresenter {

    /**
     * 做一些初始化操作
     */
    void onStart();

    /**
     * 销毁
     */
    void onDestroy();


}

