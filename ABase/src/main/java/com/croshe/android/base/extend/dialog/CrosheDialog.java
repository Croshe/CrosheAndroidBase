package com.croshe.android.base.extend.dialog;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import com.croshe.android.base.extend.listener.CrosheAnimationListener;

public class CrosheDialog extends Dialog {

    private int animViewId;
    private Animation onCloseAnim;
    private View animView;
	private boolean showed;
    private View contentView;
	public boolean isShowed() {
		return showed;
	}

	public void setShowed(boolean showed) {
		this.showed = showed;
	}

	public CrosheDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public CrosheDialog(Context context, int theme, int animViewId, Animation closeAnim) {
		super(context, theme);
		this.animViewId=animViewId;
		this.onCloseAnim =closeAnim;
	}


    public CrosheDialog(Context context, int theme, View animView, Animation closeAnim) {
        super(context, theme);
        this.animView=animView;
        this.onCloseAnim =closeAnim;
    }

	
	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		if(onCloseAnim !=null){
			onCloseAnim.setAnimationListener(new CrosheAnimationListener() {
                @Override
                public void onAnimationEnd(Animation arg0) {
                    CrosheDialog.super.cancel();
                    super.onAnimationEnd(arg0);
                }
            });
            if (animView != null) {
                animView.clearAnimation();
                animView.startAnimation(onCloseAnim);
            }else {
                if (findViewById(animViewId) != null) {
                    findViewById(animViewId).clearAnimation();
                    findViewById(animViewId).startAnimation(onCloseAnim);
                }
            }
            onCloseAnim =null;
		}else{
			super.cancel();
		}
	}

    @Override
    public void dismiss() {
        if(onCloseAnim !=null){
            onCloseAnim.setAnimationListener(new CrosheAnimationListener() {
                @Override
                public void onAnimationEnd(Animation arg0) {
                    CrosheDialog.super.dismiss();
                    super.onAnimationEnd(arg0);
                }
            });
            if (animView != null) {
                animView.clearAnimation();
                animView.startAnimation(onCloseAnim);
            }else {
                if (findViewById(animViewId) != null) {
                    findViewById(animViewId).clearAnimation();
                    findViewById(animViewId).startAnimation(onCloseAnim);
                }
            }
            onCloseAnim =null;
        }else{
            super.dismiss();
        }
    }


    @Override
    public void setContentView(View contentView) {
        super.setContentView(contentView);
        this.contentView = contentView;
    }

    public Animation getOnCloseAnim() {
        return onCloseAnim;
    }

    public void setOnCloseAnim(Animation onCloseAnim) {
        this.onCloseAnim = onCloseAnim;
    }

    public int getAnimViewId() {
        return animViewId;
    }

    public void setAnimViewId(int animViewId) {
        this.animViewId = animViewId;
    }

    public View getAnimView() {
        return animView;
    }

    public void setAnimView(View animView) {
        this.animView = animView;
    }

    public View getContentView() {
        return contentView;
    }
}
