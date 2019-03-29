package com.rs.rslib.base.mvp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.rs.rslib.interfaces.ActivityLifecycleable;
import com.rs.rslib.interfaces.IActivity;
import com.rs.rslib.utils.LogUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * author: xiecong
 * create time: 2018/1/17 11:28
 * lastUpdate time: 2018/1/17 11:28
 */

public abstract class RSBaseMVPActivity<P extends RSBasePresenter, M extends IModel> extends AppCompatActivity
        implements ActivityLifecycleable, IActivity {

    protected P mPresenter;
    protected M mModel;

    private Unbinder mUnbinder;
    protected Toolbar mToolbar;
    private FrameLayout mContainerView;
    private View mLoadingView;
    private View mEmptyView;
    private long mLastClickTime = 0;

    //以此绑定activty生命周期
    private BehaviorSubject<ActivityEvent> mActivityEventBehaviorSubject = BehaviorSubject.create();

    @Override
    public Subject<ActivityEvent> provideLifecycleSubject() {
        return mActivityEventBehaviorSubject;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (needTranslucentSystemUi()) {
            translucentSystemUi();
        }
        mActivityEventBehaviorSubject.onNext(ActivityEvent.CREATE);
        initContentView();
        init();
        initViews(savedInstanceState);
        loadData(savedInstanceState);
        bindEventListener();
    }

    protected void init() {
        //初始化presenter 和 model
        try {
            Class<P> presenterClass = getPresenterClass();
            if (presenterClass != null) {
                mPresenter = presenterClass.newInstance();
            } else {
                LogUtils.error("rs" + getClass().getCanonicalName(), " presenterClass is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("create presenter error");
        }
        try {
            Class<M> modelClass = getModelClass();
            if (modelClass != null) {
                mModel = modelClass.newInstance();
            } else {
                LogUtils.error("rs" + getClass().getCanonicalName(), " modelClass  is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("create model error");
        }
        if (mPresenter != null && this instanceof IView) {
            mPresenter.attachVM((IView) this, mModel);
        }
    }


    protected boolean showToolbar() {
        return false;
    }

    private void initContentView() {
        if (getLayoutResId() != 0) {
            mContainerView = new FrameLayout(this);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mContainerView.setLayoutParams(layoutParams);
            if (showToolbar() && getToolbarLayoutId() != 0) {
                View contentView = View.inflate(this, getLayoutResId(), null);
                mContainerView.addView(contentView);
                View toolBarView = View.inflate(this, getToolbarLayoutId(), null);
                mContainerView.addView(toolBarView);
                if (getToolBarId() != 0) {
                    mToolbar = (Toolbar) toolBarView.findViewById(getToolBarId());
                    setSupportActionBar(mToolbar);
                } else {
                    throw new NullPointerException("Toolbar id is null");
                }
            } else {
                View contentView = getLayoutInflater().inflate(getLayoutResId(), null);
                mContainerView.addView(contentView);
            }
            setContentView(mContainerView);
            mUnbinder = ButterKnife.bind(this, mContainerView);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mActivityEventBehaviorSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mActivityEventBehaviorSubject.onNext(ActivityEvent.START);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActivityEventBehaviorSubject.onNext(ActivityEvent.PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mActivityEventBehaviorSubject.onNext(ActivityEvent.STOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        if (mPresenter != null) {
            mPresenter.detachVM();
        }
        mPresenter = null;
        mActivityEventBehaviorSubject.onNext(ActivityEvent.DESTROY);
    }

    private void translucentSystemUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            // window.setNavigationBarColor(Color.TRANSPARENT);

        }
    }

    protected boolean needTranslucentSystemUi() {
        return false;
    }


    public void showLoadView(int loadingView) {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.GONE);
        }
        if (mContainerView != null) {
            if (mLoadingView == null) {
                mLoadingView = View.inflate(this, loadingView, null);
                if (!clickableLoadingState()) {
                    mLoadingView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                }
                mContainerView.addView(mLoadingView);
            }
            mContainerView.bringChildToFront(mLoadingView);
            mLoadingView.setVisibility(View.VISIBLE);
        }
    }

    public View getLoadingView() {
        return mLoadingView;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public boolean clickableLoadingState() {
        return false;
    }

    public void showEmptyView(int emptyView) {
        if (mContainerView != null) {
            if (mEmptyView == null) {
                mEmptyView = View.inflate(this, emptyView, null);
                mContainerView.addView(mEmptyView);
            }
            mContainerView.bringChildToFront(mEmptyView);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    public void showContentView() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getToolBarId() {
        return 0;
    }

    @Override
    public int getToolbarLayoutId() {
        return 0;
    }

    protected abstract void initViews(Bundle savedInstanceState);

    public void loadData(Bundle savedInstanceState) {

    }

    public void bindEventListener() {

    }

    protected abstract Class<M> getModelClass();

    protected abstract Class<P> getPresenterClass();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean checkSingleClick() {
        if (System.currentTimeMillis() - mLastClickTime < 500) {
            return false;
        }
        mLastClickTime = System.currentTimeMillis();
        return true;
    }

    public void launchActivity(Class clzz, Bundle bundle) {
        if (!checkSingleClick()) {
            return;
        }
        Intent intent = new Intent(this, clzz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void launchActivity(Class clzz) {
        launchActivity(clzz, null);
    }

    public void launchActivityForResult(Class clzz, Bundle bundle, int requestCode) {
        if (!checkSingleClick()) {
            return;
        }
        Intent intent = new Intent(this, clzz);
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
    public static void showSoftInputBord(final View view) {
        if (view != null) {
            ((InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 隐藏软键盘  系統強制关闭软键盘方法 v为获得焦点的View
     */
    public static void hidesoftInputBord(View view) {
        if (view != null)
            ((InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}

