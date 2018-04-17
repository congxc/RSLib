package com.rs.rslib.base.mvp;

/**
 * author: xiecong
 * create time: 2017/11/14 14:59
 * lastUpdate time: 2017/11/14 14:59
 * description:MVP - VIEW
 */
public interface IView {
    /**
     * 显示加载
     */
    void showLoading();
    /**
     * 显示空
     */
    void showEmpty();
    /**
     * 隐藏加载
     */
    void hideLoading();

}

