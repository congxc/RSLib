package com.rs.rslib.interfaces;

import android.os.Bundle;

/**
 * author: xiecong
 * create time: 2017/11/14 15:11
 * lastUpdate time: 2017/11/14 15:11
 */

public interface IActivity {
    /**
     * @return toolBar ID
     */
    int getToolBarId();
    /**
     * @return toolBar资源ID
     */
    int getToolbarLayoutId();

    /**
     * @return 内容视图资源ID
     */
    int getLayoutResId();
    /**
     * 初始化方法
     *
     * @param savedInstanceState
     */
    void init(Bundle savedInstanceState);

    /**
     * 初始化view方法
     *
     * @param savedInstanceState
     */
    void initWidget(Bundle savedInstanceState);

    /**
     * @param savedInstanceState
     */
    void loadData(Bundle savedInstanceState);
    /**
     * 绑定事件
     */
    void bindEventListener();

    /**
     * 跳转  Activity
     */
    void launchActivity(Class<?> clazz);

    void launchActivity(Class<?> clazz, Bundle bundle);

    void launchActivityForResult(Class clzz,  int requestCode);

    void launchActivityForResult(Class clzz, Bundle bundle, int requestCode);

}