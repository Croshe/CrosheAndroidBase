package com.croshe.android.base.activity.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.croshe.android.base.R;
import com.croshe.android.base.activity.CrosheBaseSlidingActivity;
import com.croshe.android.base.utils.DensityUtils;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;


/**
 * Created by Janesen on 16/5/28.
 */
public class CrosheCropImageActivity extends CrosheBaseSlidingActivity {


    public final static String EXTRA_IMAGE_PATH = "image_path";
    public final static String EXTRA_IMAGE_WIDTH = "image_width";
    public final static String EXTRA_IMAGE_HEIGHT = "image_height";
    public final static String EXTRA_IMAGE_QUALITY = "image_quality";
    public final static String EXTRA_CROP_FREE = "image_crop_free";

    public final static String RESULT_IMAGE_PATH = "return_image_path";

    private CropImageView cropImageView;
    private String imagePath;

    private Bundle bundle;


    DisplayImageOptions image_display_options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.android_base_default_img)
            .showImageForEmptyUri(R.drawable.android_base_default_img)
            .showImageOnFail(R.drawable.android_base_default_img)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.android_base_activity_crop_image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("剪裁图片");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bundle = getIntent().getExtras();
        imagePath = bundle.getString(EXTRA_IMAGE_PATH);
        initView();
    }

    public void initView() {
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setMinFrameSizeInDp(150);

        if (bundle.getBoolean(EXTRA_CROP_FREE, true)) {
            cropImageView.setCropMode(CropImageView.CropMode.FREE);
        }else{
            cropImageView.setCropMode(CropImageView.CropMode.CUSTOM);
        }
        cropImageView.setCompressFormat(Bitmap.CompressFormat.JPEG);
        cropImageView.setCompressQuality(bundle.getInt(EXTRA_IMAGE_QUALITY, 100));

        cropImageView.setOutputMaxSize(DensityUtils.dip2px(bundle.getInt(EXTRA_IMAGE_WIDTH, 120)), DensityUtils.dip2px(bundle.getInt(EXTRA_IMAGE_HEIGHT, 120)));


        String path = imagePath;

        if (!imagePath.startsWith("file") && !imagePath.startsWith("http")) {
            path = "file://" + imagePath;
        }
        ImageLoader.getInstance().loadImage(path, image_display_options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);

                cropImageView.setImageBitmap(loadedImage);
            }
        });


        findViewById(R.id.sllConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final File file = new File(context.getFilesDir() + "/Croshe/Crop/" + System.currentTimeMillis() + ".jpg");
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                cropImageView.startCrop(Uri.fromFile(file), null, new SaveCallback() {
                    @Override
                    public void onSuccess(Uri outputUri) {
                        Intent data = new Intent();
                        data.putExtra(EXTRA_IMAGE_PATH, file.getAbsolutePath());
                        data.putExtra(RESULT_IMAGE_PATH, file.getAbsolutePath());
                        setResult(RESULT_OK, data);
                        finish();
                    }
                    @Override
                    public void onError() {

                    }
                });
            }
        });

    }


}
