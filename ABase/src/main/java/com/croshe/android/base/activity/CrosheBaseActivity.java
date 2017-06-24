package com.croshe.android.base.activity;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.croshe.android.base.R;
import com.croshe.android.base.utils.StatusBarUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import permissions.dispatcher.PermissionUtils;


/**
 * Created by Janesen on 2017/6/24.
 */
public class CrosheBaseActivity extends AppCompatActivity {
    private static final int REQUEST_INITPERMISSION = 0;

    private static final String[] defaultPermission = new String[]{
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.READ_CONTACTS",
            "android.permission.READ_PHONE_STATE",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.CAMERA"};


    protected Set<String> permissions = new HashSet<>();

    protected Handler handler;
    protected Context context;
    protected Toolbar toolbar = null;
    protected String title;
    protected String subTitle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        context = this;
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        initToolBar();
    }

    public boolean fullScreen() {
        return fullScreen(true);
    }

    public boolean fullScreen(boolean windowUILight) {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE ;
        if (windowUILight) {
            option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }else{
            StatusBarUtil.setTranslucent(this);
        }
        return true;
    }

    public void onClickByBase(View v){
        if (v.getId() == R.id.llBack) {
            finish();
        }
    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            if (StringUtils.isNotEmpty(title)) {
                toolbar.setTitle(title);
            }
            toolbar.setSubtitle(subTitle);
        }
    }



    /**
     * 取消全屏
     * @return
     */
    public boolean cancelFullScreen(int color) {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        StatusBarUtil.setColor(this, color);
        setStatusBarHidden(false);
        return true;
    }


    /**
     * 设置状态栏显示或隐藏
     * @param hidden
     */
    public void setStatusBarHidden(boolean hidden) {
        if (hidden) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(lp);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }




    /**
     * 添加权限
     * @param permission
     */
    public void addPermission(String permission) {
        permissions.add(permission);
    }


    /**
     * 检测权限
     */
    public void checkPermission() {
        if (PermissionUtils.hasSelfPermissions(this, getPermissions())) {
            hasPermission();
        } else {
            ActivityCompat.requestPermissions(this,
                    getPermissions(), REQUEST_INITPERMISSION);
        }
    }



    /**
     * 权限检测成功回调
     */
    public void hasPermission() {

    }


    /**
     * 根据ID获得控件
     * @param id
     * @param <T>
     * @return
     */
    public <T> T getView(int id) {
        return (T) findViewById(id);
    }


    public void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private String[] getPermissions() {
        for (String s : defaultPermission) {
            if (!permissions.contains(s)) {
                permissions.add(s);
            }
        }
        return permissions.toArray(new String[]{});
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionsResult(this, requestCode, grantResults);
    }



    public void onRequestPermissionsResult(CrosheBaseActivity target, int requestCode, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_INITPERMISSION:
                if (PermissionUtils.getTargetSdkVersion(target) < 23
                        && !PermissionUtils.hasSelfPermissions(target, getPermissions())) {
                    return;
                }
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    target.hasPermission();
                }
                break;
            default:
                break;
        }
    }
}
