package com.croshe.android.base.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.croshe.android.base.R;
import com.croshe.android.base.views.layout.CrosheSlidingPaneLayout;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class CrosheBaseSlidingActivity extends CrosheBaseActivity implements SlidingPaneLayout.PanelSlideListener {
    private static final int DEFAULT_TRANSLATION_X = 300;
    private static final int DEFAULT_SHADOW_WIDTH = 30;
    private View mPreDecorView;
    private static View sDecorView;
    private static Map<CrosheBaseSlidingActivity, View> mViewMap = new HashMap<>();
    private CrosheSlidingPaneLayout mSlidingPaneLayout;
    private FrameLayout mContentView;
    private View mShadowView;
    /**
     * Flag of whether SlidingPaneLayout initialize success.
     * If success, use SlidingPaneLayout as contentView,
     * otherwise use the contentView which set in Activity#setContentView().
     */
    protected boolean mInitSlidingSuccess;
    protected boolean isCheckBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            mSlidingPaneLayout = new CrosheSlidingPaneLayout(this);
            Field mOverhangSize = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
            mOverhangSize.setAccessible(true);
            mOverhangSize.set(mSlidingPaneLayout, 0);
            mSlidingPaneLayout.setPanelSlideListener(this);
            mSlidingPaneLayout.setSliderFadeColor(getResources().getColor(android.R.color.transparent));
            mInitSlidingSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            mInitSlidingSuccess = false;
        }
        super.onCreate(savedInstanceState);

        mInitSlidingSuccess = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

        if (!mInitSlidingSuccess) {
            return;
        }

        // frontContainer
        LinearLayout frontContainer = new LinearLayout(this);
        frontContainer.setOrientation(LinearLayout.HORIZONTAL);
        frontContainer.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        frontContainer.setLayoutParams(new ViewGroup.LayoutParams(getWindowManager().getDefaultDisplay().getWidth() + DEFAULT_SHADOW_WIDTH, ViewGroup.LayoutParams.MATCH_PARENT));
        // contentView
        mContentView = new FrameLayout(this);
        mContentView.setBackgroundColor(getResources().getColor(android.R.color.white));
        mContentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        // shadowView
        mShadowView = new ImageView(this);
        mShadowView.setBackgroundResource(R.drawable.android_base_shadow_left);

        mShadowView.setLayoutParams(new LinearLayout.LayoutParams(DEFAULT_SHADOW_WIDTH, LinearLayout.LayoutParams.MATCH_PARENT));
        // add views to frontContainer
        frontContainer.addView(mShadowView);
        frontContainer.addView(mContentView);
        frontContainer.setTranslationX(-DEFAULT_SHADOW_WIDTH);
        // add views to SlidingPaneLayout
        mSlidingPaneLayout.addView(new View(this), 0);
        mSlidingPaneLayout.addView(frontContainer, 1);
        mViewMap.put(this, sDecorView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreDecorView = mViewMap.get(this);
    }

    @Override
    protected void onDestroy() {
        if (mInitSlidingSuccess) {
            if (mPreDecorView != null) {
                mPreDecorView.setTranslationX(0);
            }
            mViewMap.remove(this);
        }
        super.onDestroy();
    }

    @Override
    public void setContentView(int id) {
        setContentView(getLayoutInflater().inflate(id, null));
    }

    @Override
    public void setContentView(View v) {
        setContentView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setContentView(View v, ViewGroup.LayoutParams params) {
        if (mInitSlidingSuccess) {
            super.setContentView(mSlidingPaneLayout, params);
            mContentView.removeAllViews();
            mContentView.addView(v, params);
        } else {
            super.setContentView(v, params);
        }
    }


    @Override
    public void onPanelClosed(View panel) {
        if (mPreDecorView != null) {
            mPreDecorView.setTranslationX(0);
        }
    }

    @Override
    public void onPanelOpened(View panel) {
        isCheckBack=false;
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        // Add any effect here
        if (mPreDecorView != null) {
            mPreDecorView.setTranslationX(slideOffset * (float) DEFAULT_TRANSLATION_X - (float) DEFAULT_TRANSLATION_X);
        }
        mShadowView.setAlpha(1 - slideOffset);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        sDecorView = this.getWindow().getDecorView();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        sDecorView = this.getWindow().getDecorView();
    }


    public static void startSlidingFinishActivity(Activity activity, Intent intent) {
        sDecorView = activity.getWindow().getDecorView();
        activity.startActivity(intent);
    }

    public static void startSlidingFinishActivityForResult(Activity activity, Intent intent, int requestCode) {
        sDecorView = activity.getWindow().getDecorView();
        activity.startActivityForResult(intent, requestCode);
    }


    public void setSlideEnable(boolean slideEnable) {
        mSlidingPaneLayout.setSlidEnable(slideEnable);
    }

    public boolean isSlideEnable() {
        return mSlidingPaneLayout.isSlidEnable();
    }

}
