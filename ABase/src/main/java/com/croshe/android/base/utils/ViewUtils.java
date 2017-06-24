package com.croshe.android.base.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


public class ViewUtils {

    /**
     * 设置控件的margin
     *
     * @param view
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public static void setMargins(View view, int l, int t, int r, int b) {
        if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams mLayoutPrams = (FrameLayout.LayoutParams) view.getLayoutParams();
            mLayoutPrams.setMargins(l, t, r, b);
            view.setLayoutParams(mLayoutPrams);
        } else if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams mLayoutPrams = (LinearLayout.LayoutParams) view.getLayoutParams();
            mLayoutPrams.setMargins(l, t, r, b);
            view.setLayoutParams(mLayoutPrams);
        } else if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams mLayoutPrams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            mLayoutPrams.setMargins(l, t, r, b);
            view.setLayoutParams(mLayoutPrams);
        }
    }

    /**
     * 设置控件的margin
     *
     * @param view
     */
    public static void setMargins(View view, int width, int height) {
        if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams mLayoutPrams = (FrameLayout.LayoutParams) view.getLayoutParams();
            if (width >= 0) {
                mLayoutPrams.width = width;
            }
            if (height >= 0) {
                mLayoutPrams.height = height;
            }
            view.setLayoutParams(mLayoutPrams);
        } else if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams mLayoutPrams = (LinearLayout.LayoutParams) view.getLayoutParams();
            if (width >= 0) {
                mLayoutPrams.width = width;
            }
            if (height >= 0) {
                mLayoutPrams.height = height;
            }
            view.setLayoutParams(mLayoutPrams);
        } else if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams mLayoutPrams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            if (width >= 0) {
                mLayoutPrams.width = width;
            }
            if (height >= 0) {
                mLayoutPrams.height = height;
            }
            view.setLayoutParams(mLayoutPrams);
        }
    }

    /**
     * 设置控件的margin
     *
     * @param view
     * @param l
     * @param t
     * @param r
     * @param b
     * @param width
     * @param height
     */
    public static void setMargins(View view, int l, int t, int r, int b, int width, int height) {
        if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams mLayoutPrams = (FrameLayout.LayoutParams) view.getLayoutParams();
            if (width >= 0) {
                mLayoutPrams.width = width;
            }
            if (height >= 0) {
                mLayoutPrams.height = height;
            }
            mLayoutPrams.setMargins(l, t, r, b);
            view.setLayoutParams(mLayoutPrams);
        } else if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams mLayoutPrams = (LinearLayout.LayoutParams) view.getLayoutParams();
            if (width >= 0) {
                mLayoutPrams.width = width;
            }
            if (height >= 0) {
                mLayoutPrams.height = height;
            }
            mLayoutPrams.setMargins(l, t, r, b);
            view.setLayoutParams(mLayoutPrams);
        } else if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams mLayoutPrams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            if (width >= 0) {
                mLayoutPrams.width = width;
            }
            if (height >= 0) {
                mLayoutPrams.height = height;
            }
            mLayoutPrams.setMargins(l, t, r, b);
            view.setLayoutParams(mLayoutPrams);
        }
    }


    /**
     * 获得控件的margin
     *
     * @param view
     * @return
     */
    public static Rect getMargins(View view) {
        Rect rect = new Rect();
        if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams mLayoutPrams = (FrameLayout.LayoutParams) view.getLayoutParams();
            rect.set(mLayoutPrams.leftMargin, mLayoutPrams.topMargin, mLayoutPrams.rightMargin, mLayoutPrams.bottomMargin);
        } else if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams mLayoutPrams = (LinearLayout.LayoutParams) view.getLayoutParams();
            rect.set(mLayoutPrams.leftMargin, mLayoutPrams.topMargin, mLayoutPrams.rightMargin, mLayoutPrams.bottomMargin);
        }
        return rect;
    }


    /**
     * 释放imageview
     *
     * @param imageView
     */
    public static void releasImageView(ImageView imageView) {
        try {
            ((BitmapDrawable) imageView.getBackground()).getBitmap().recycle();
            imageView.setImageBitmap(null);
        } catch (Exception e) {
            imageView.setImageBitmap(null);
        }
    }


    /**
     * 获得控件的宽高
     *
     * @return int[0] 宽  int[1] 高
     */
    public static int[] getViewSize(View view) {
        int childEndWidth = 0;
        int childEndHeight = 0;
        if (view.getLayoutParams() != null) {
            LayoutParams rlp = view.getLayoutParams();
            childEndWidth = rlp.width;
            childEndHeight = rlp.height;
            if (childEndWidth <= 0 || childEndHeight <= 0) {

                int w = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                int h = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                view.measure(w, h);
                childEndWidth = view.getMeasuredWidth();
                childEndHeight = view.getMeasuredHeight();
            }
        } else {
            int w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            view.measure(w, h);
            childEndWidth = view.getMeasuredWidth();
            childEndHeight = view.getMeasuredHeight();
        }
        return new int[]{childEndWidth, childEndHeight};
    }


    /**
     * 获得控件的宽高
     *
     * @return int[0] 宽  int[1] 高
     */
    public static int[] getViewSize2(View view) {
        int childEndWidth = 0;
        int childEndHeight = 0;
        if (view.getLayoutParams() != null) {
            LayoutParams rlp = view.getLayoutParams();
            childEndWidth = rlp.width;
            childEndHeight = rlp.height;
            if (childEndWidth <= 0 || childEndHeight <= 0) {

                int w = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.EXACTLY);
                int h = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.EXACTLY);
                view.measure(w, h);
                childEndWidth = view.getMeasuredWidth();
                childEndHeight = view.getMeasuredHeight();
            }
        } else {
            int w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.EXACTLY);
            int h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.EXACTLY);
            view.measure(w, h);
            childEndWidth = view.getMeasuredWidth();
            childEndHeight = view.getMeasuredHeight();
        }
        return new int[]{childEndWidth, childEndHeight};
    }


    /**
     * 关闭软键盘
     */
    public static void closeKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            view.clearFocus();
        }
    }


    /**
     * 打开软键盘
     *
     * @param view
     */
    public static void openKeyboard(View view) {
        if (view != null) {
            view.requestFocus();
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }




    private static View getViewInstance(Handler handler, ViewGroup viewGroup, Class<? extends View> childViewClass) {
        try {
            View childView = viewGroup.findViewById(childViewClass.hashCode());
            if (childView == null) {
                Constructor<? extends View> constructor = childViewClass.getConstructor(Context.class, Handler.class);
                childView = constructor.newInstance(viewGroup.getContext(), handler);
                childView.setId(childViewClass.hashCode());
                viewGroup.addView(childView);
            }
            childView.setVisibility(View.VISIBLE);
            childView.bringToFront();
            return childView;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }




    /**
     * 判断点是否在view中
     *
     * @param view
     * @param x
     * @param y
     * @return
     */
    public static boolean isPointInView(View view, float x, float y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();

        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }


    public static void turnDegrees(View view, float fromDegrees, float toDegrees) {
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(200);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(rotateAnimation);
    }


    public static <T> T getView(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }


    private static Map<Object, String> viewTexts = new HashMap<>();


    /**
     * 设置控件状态
     *
     * @param targetView
     * @param enable
     * @param tip
     */
    public static void setState(View targetView, boolean enable, String... tip) {
        if (!enable) {
            if (targetView.isEnabled()) {
                if (targetView instanceof TextView) {
                    TextView textView = (TextView) targetView;
                    viewTexts.put(targetView, textView.getText().toString());
                    if (tip.length > 0) {
                        textView.setText(tip[0]);
                    }
                }else if (targetView instanceof Button) {
                    Button button = (Button) targetView;
                    viewTexts.put(targetView, button.getText().toString());
                    if (tip.length > 0) {
                        button.setText(tip[0]);
                    }
                }
            }
        }else{
            if (viewTexts.containsKey(targetView)) {
                if (targetView instanceof TextView) {
                    TextView textView = (TextView) targetView;
                    textView.setText(viewTexts.get(targetView));
                }else if (targetView instanceof Button) {
                    Button button = (Button) targetView;
                    button.setText(viewTexts.get(targetView));
                }
            }
        }
        targetView.setEnabled(enable);

    }

}
