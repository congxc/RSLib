package com.rs.rslib.interfaces;

import io.reactivex.subjects.Subject;

/**
 * author: xiecong
 * create time: 2018/4/11 17:05
 * lastUpdate time: 2018/4/11 17:05
 */

public interface Lifecycleable<E> {
    Subject<E> provideLifecycleSubject();
}
