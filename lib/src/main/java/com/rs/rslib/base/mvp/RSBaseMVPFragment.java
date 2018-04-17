package com.rs.rslib.base.mvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.rs.rslib.interfaces.FragmentLifecycleable;
import com.rs.rslib.interfaces.IFragment;
import com.rs.rslib.utils.LogUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * author: xiecong
 * create time: 2018/4/11 18:40
 * lastUpdate time: 2018/4/11 18:40
 */

public abstract class RSBaseMVPFragment<P extends RSBasePresenter, M extends IModel> extends Fragment implements IFragment,FragmentLifecycleable{

    protected P mPresenter;
    protected M mModel;

    private Unbinder mUnbinder;
    private BehaviorSubject<FragmentEvent> mLifecycleSubject = BehaviorSubject.create();
    private long lastClickTime;

    private BehaviorSubject<FragmentEvent> mFragmentEventBehaviorSubject = BehaviorSubject.create();

    @Override
    public Subject<FragmentEvent> provideLifecycleSubject() {
        return mFragmentEventBehaviorSubject;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
        int layoutResId = getLayoutResId();
        View rootView = null;
        if (layoutResId != 0) {
            rootView = inflater.inflate(layoutResId,container,false);
            mUnbinder = ButterKnife.bind(this,rootView);
        }
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLifecycleSubject.onNext(FragmentEvent.DETACH);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mLifecycleSubject.onNext(FragmentEvent.DESTROY);
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        if (mPresenter != null) {
            mPresenter.detachVM();
        }
        mPresenter = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(savedInstanceState);
        initWidget();
    }

    @Override
    public void init(Bundle savedInstanceState) {
        //初始化presenter 和 model
        try {
            Class<P> presenterClass = getPresenterClass();
            if (presenterClass != null) {
                mPresenter = presenterClass.newInstance();
            }else {
                LogUtils.error("rs"+ getClass().getCanonicalName(), " presenterClass is null" );
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("create presenter error");
        }
        try {
            Class<M> modelClass = getModelClass();
            if (modelClass != null) {
                mModel = modelClass.newInstance();
            }else {
                LogUtils.error("rs"+ getClass().getCanonicalName(), " modelClass  is null" );
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("create model error");
        }
        if (mPresenter != null && this instanceof IView) {
            mPresenter.attachVM((IView) this, mModel);
        }
    }

    @Override
    public void bindEventListener() {

    }
    protected abstract Class<P> getPresenterClass();

    protected abstract Class<M> getModelClass();

    protected boolean checkSingleClick() {
        if (System.currentTimeMillis() - lastClickTime < 500) {
            return false;
        }
        lastClickTime = System.currentTimeMillis();
        return true;
    }

    @Override
    public void launchActivity(Class clzz, Bundle bundle) {
        if (!checkSingleClick()) {
            return;
        }
        Intent intent = new Intent(getActivity(), clzz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @Override
    public void launchActivity(Class clzz) {
        launchActivity(clzz, null);
    }

    public void launchActivityForResult(Class clzz, Bundle bundle, int requestCode) {
        if (!checkSingleClick()) {
            return;
        }
        Intent intent = new Intent(getActivity(), clzz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    public void launchActivityForResult(Class clzz, int requestCode) {
        launchActivityForResult(clzz, null, requestCode);
    }

    /**
     * 显示软键盘  系統強制显示软键盘方法 v为获得焦点的View
     */
    public  void showSoftInputBord(final View view) {
        if (view != null){
            ((InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    /**
     * 隐藏软键盘  系統強制关闭软键盘方法 v为获得焦点的View
     */
    public  void hidesoftInputBord(View view) {
        if (view != null)
            ((InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示软键盘  系統強制显示软键盘方法 v为获得焦点的View
     */
    public  void showSoftInputBord() {
        View view = getActivity().getCurrentFocus();
        if (view != null){
            ((InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    /**
     * 隐藏软键盘  系統強制关闭软键盘方法 v为获得焦点的View
     */
    public  void hidesoftInputBord() {
        View view = getActivity().getCurrentFocus();
        if (view != null)
            ((InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
