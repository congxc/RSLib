package com.rs.rslib.base.mvp;

/**
 * author: xiecong
 * create time: 2017/11/14 15:02
 * lastUpdate time: 2017/11/14 15:02
 * description: 通用 V -  M
 */

public class CommonContract {
    /**************************************/
    public interface View<T> extends IView{
        void setData(T t);
    }

    /**
     * 用于同步返回数据
     * @param <T>
     */
    public interface Model<T> extends IModel{
        T  getData();
    }

}
