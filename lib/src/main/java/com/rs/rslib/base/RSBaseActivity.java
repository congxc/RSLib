package com.rs.rslib.base;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.annotations.Nullable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * author: xiecong
 * create time: 2018/1/17 11:28
 * lastUpdate time: 2018/1/17 11:28
 */

public abstract class RSBaseActivity extends AppCompatActivity implements ActivityLifecycleable,IActivity{
    private Unbinder mUnbinder;
    protected Toolbar mToolbar;
    private FrameLayout mContainerView;
    private View mLoadingView;
    private View mEmptyView;
    private long mLastClickTime = 0;
    private HashMap<Integer,View> mCustomViewContainer = new HashMap<>();

    private BehaviorSubject<ActivityEvent> mActivityEventBehaviorSubject = BehaviorSubject.create();

    @Override
    public Subject<ActivityEvent> provideLifecycleSubject() {
        return mActivityEventBehaviorSubject;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityEventBehaviorSubject.onNext(ActivityEvent.CREATE);
        if (needTranslucentSystemUi()) {
            translucentSystemUi();
        }
        init(savedInstanceState);
        initContentView();
        initWidget(savedInstanceState);
        bindEventListener();
        loadData(savedInstanceState);
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

        }
    }

    protected boolean needTranslucentSystemUi() {
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
                View toolBarView = View.inflate(this,getToolbarLayoutId(), null);
                mToolbar = (Toolbar) toolBarView.findViewById(getToolBarId());
                mContainerView.addView(toolBarView);
                setSupportActionBar(mToolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                }
            } else {
                View contentView = getLayoutInflater().inflate(getLayoutResId(), null);
                mContainerView.addView(contentView);
            }
            setContentView(mContainerView);
            mUnbinder = ButterKnife.bind(this,mContainerView);
        }
    }

    public void showLoadView(int loadingView) {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.GONE);
        }
        if (mContainerView != null) {
            if (mLoadingView == null) {
                mLoadingView = View.inflate(this, loadingView, null);
                if (!clickableLoadingState()){
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

    public void bringChildToFront(View child){
        if (mContainerView != null) {
            mContainerView.bringChildToFront(child);
        }
    }

    public boolean clickableLoadingState(){
        return false;
    }

    public void showCustomView(int view){
        if (mContainerView != null) {
            View custom;
            if (!mCustomViewContainer.containsKey(view)) {
                custom = View.inflate(this, view, null);
                mContainerView.addView(custom);
                mCustomViewContainer.put(view,custom);
            }else{
                custom = mCustomViewContainer.get(view);
                custom.setVisibility(View.VISIBLE);
            }
            mContainerView.bringChildToFront(custom);
        }
    }

    public void hideAllCustomView(){
        if (mCustomViewContainer != null) {
            Set<Map.Entry<Integer, View>> entries = mCustomViewContainer.entrySet();
            for (Map.Entry<Integer, View> entry : entries) {
                View value = entry.getValue();
                value.setVisibility(View.GONE);
            }
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
    protected void onResume() {
        super.onResume();
        mActivityEventBehaviorSubject.onNext(ActivityEvent.RESUME);
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
        }
        if (mCustomViewContainer != null) {
            mCustomViewContainer.clear();
        }
        mActivityEventBehaviorSubject.onNext(ActivityEvent.DESTROY);
    }

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

    @Override
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

    @Override
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
        View view = getCurrentFocus();
        if (view != null){
            ((InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    /**
     * 隐藏软键盘  系統強制关闭软键盘方法 v为获得焦点的View
     */
    public  void hidesoftInputBord() {
        View view = getCurrentFocus();
        if (view != null)
            ((InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    @Override
    public void init(Bundle savedInstanceState) {

    }
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public void loadData(Bundle savedInstanceState) {

    }

    @Override
    public int getToolBarId(){
        return 0;
    }


    @Override
    public int getToolbarLayoutId() {
        return 0;
    }

    @Override
    public void bindEventListener() {

    }
}
