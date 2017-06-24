package com.croshe.android.base.activity.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.croshe.android.base.R;
import com.croshe.android.base.activity.CrosheBaseSlidingActivity;
import com.croshe.android.base.extend.listener.CrosheOnPageChangeListener;
import com.croshe.android.base.utils.OKHttpUtils;
import com.croshe.android.base.views.control.CrosheVideoView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by Janesen on 16/5/28.
 */
public class CrosheShowImageActivity extends CrosheBaseSlidingActivity {

    public final static String EXTRA_IMAGES_PATH = "images_path";
    public final static String EXTRA_IMAGES_DETAIL = "images_detail";
    public final static String EXTRA_THUMB_IMAGES_PATH = "thumb_images_path";
    public final static String EXTRA_FIRST_PATH = "first_path";
    public final static String EXTRA_FIRST_INDEX = "first_index";
    public final static String EXTRA_SCHEME = "scheme";

    private ViewPager viewPager;
    private String[] paths;
    private String[] thumbPaths;
    private String[] details;
    private String firstPath;
    private String scheme = "";

    private TextView tvNumber;
    private Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_base_activity_show_images);
        context = this;
        if (fullScreen(false)) {
            findViewById(R.id.viewHolder).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.viewHolder).setVisibility(View.GONE);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            paths = bundle.getStringArray(EXTRA_IMAGES_PATH);
            if (bundle.containsKey(EXTRA_FIRST_PATH)) {
                firstPath = bundle.getString(EXTRA_FIRST_PATH);
            }
            if (bundle.containsKey(EXTRA_THUMB_IMAGES_PATH)) {
                thumbPaths = bundle.getStringArray(EXTRA_THUMB_IMAGES_PATH);
            }
            if (bundle.containsKey(EXTRA_IMAGES_DETAIL)) {
                details = bundle.getStringArray(EXTRA_IMAGES_DETAIL);
            }
            if (bundle.containsKey(EXTRA_SCHEME)) {
                scheme = bundle.getString(EXTRA_SCHEME);
            }
        }

        initView();
    }

    public void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tvNumber = (TextView) findViewById(R.id.tvNumber);
        ShowImageAdapter showImageAdapter = new ShowImageAdapter();
        viewPager.setAdapter(showImageAdapter);
        viewPager.addOnPageChangeListener(new CrosheOnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {
                // TODO Auto-generated method stub
                super.onPageSelected(index);
                tvNumber.setText((index + 1) + "/" + paths.length);
            }
        });
        tvNumber.setText("1/" + paths.length);
        viewPager.setCurrentItem(getIntent().getExtras().getInt(EXTRA_FIRST_INDEX, 0));


        if (firstPath != null) {
            for (int i = 0; i < paths.length; i++) {
                if (paths[i].equals(firstPath)) {
                    viewPager.setCurrentItem(i);
                }
            }
        }

    }


    class ShowImageAdapter extends PagerAdapter {

        DisplayImageOptions image_display_options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.android_base_default_img)
                .showImageForEmptyUri(R.drawable.android_base_default_img)
                .showImageOnFail(R.drawable.android_base_default_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return paths.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }


        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            View view = (View) object;
            try {
                if (view.findViewById(R.id.photoView) != null) {
                    PhotoView photoView = (PhotoView) view.findViewById(R.id.photoView);
                    photoView.setImageBitmap(null);
                } else if (view.findViewById(R.id.videoView) != null) {
                    VideoView videoView = (VideoView) view.findViewById(R.id.videoView);
                    videoView.stopPlayback();
                }
            } catch (Exception e) {
            } finally {
                System.gc();
                container.removeView(view);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // TODO Auto-generated method stub
            String path = paths[position];
            View contentView;
            if (isVideo(path)) {
                contentView = getVideoView(position);
            } else if (isGIF(path)) {
                contentView = getGifView(position);
            } else {
                contentView = getPhotoView(position);
            }
            container.addView(contentView);
            return contentView;
        }


        @NonNull
        private View getPhotoView(final int position) {
            final View contentView = LayoutInflater.from(context).inflate(R.layout.android_base_item_show_imageview, null);
            final TextView tvProgress = (TextView) contentView.findViewById(R.id.tvProgress);
            tvProgress.setVisibility(View.GONE);
            final PhotoView photoView = (PhotoView) contentView.findViewById(R.id.photoView);
            if (thumbPaths != null) {
                ImageLoader.getInstance().displayImage(scheme + thumbPaths[position],
                        photoView, image_display_options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view,
                                                          Bitmap loadedImage) {
                                // TODO Auto-generated method stub
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                DoLoadImage(position, contentView, tvProgress, photoView);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view,
                                                        FailReason failReason) {
                                // TODO Auto-generated method stub
                                DoLoadImage(position, contentView, tvProgress, photoView);
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {
                                DoLoadImage(position, contentView, tvProgress, photoView);
                            }
                        });
            } else {
                photoView.setImageResource(R.drawable.android_base_default_img);
                DoLoadImage(position, contentView, tvProgress, photoView);
            }
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    CrosheShowImageActivity.this.finish();
                }
            });
            return contentView;
        }

        private void DoLoadImage(int position, final View contentView,
                                 final TextView tvProgress, final PhotoView photoView) {

            String path = paths[position];

            ImageLoader.getInstance().loadImage(scheme + path, null,
                    image_display_options, new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri, View view,
                                                      Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            photoView.setImageBitmap(loadedImage);
                            contentView.findViewById(R.id.flLoadingImg).setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {
                            // TODO Auto-generated method stub
                            contentView.findViewById(R.id.flLoadingImg).setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                            // TODO Auto-generated method stub
                            contentView.findViewById(R.id.flLoadingImg).setVisibility(View.GONE);
                        }

                    }, new ImageLoadingProgressListener() {

                        @Override
                        public void onProgressUpdate(String arg0, View arg1, int arg2, int arg3) {
                            // TODO Auto-generated method stub
                            int p = (int) Math.ceil((arg2 * 100 / arg3));
                            tvProgress.setText(p + "%");
                            tvProgress.setVisibility(View.VISIBLE);
                        }
                    });
        }


        @NonNull
        private View getVideoView(final int position) {
            final View contentView = LayoutInflater.from(context).inflate(R.layout.android_base_item_show_videoview, null);

            final String path = paths[position];
            String thumbPath;
            if (path.toLowerCase().startsWith("http://") || path.toLowerCase().startsWith("https://")) {
                thumbPath = thumbPaths[position];
            } else {
                thumbPath = "file://" + CrosheAlbumActivity.getVideoThumbPath(context, path);
            }
            final PhotoView imgThumb = (PhotoView) contentView.findViewById(R.id.imgThumb);
            ImageLoader.getInstance().displayImage(thumbPath, imgThumb, image_display_options);

            final CrosheVideoView videoView = (CrosheVideoView) contentView.findViewById(R.id.videoView);
            videoView.setBackgroundColor(Color.BLACK);

            videoView.setOnPlayStateListener(new CrosheVideoView.OnPlayStateListener() {
                @Override
                public void onPlayState(CrosheVideoView selfVideoView, int state) {
                    Log.d("STAG", "state:" + state);
                    contentView.findViewById(R.id.tvProgress).setVisibility(View.GONE);
                    switch (state) {
                        case CrosheVideoView.PLAY_STATE_START:
                            contentView.findViewById(R.id.imgPlay).setVisibility(View.GONE);
                            imgThumb.setVisibility(View.GONE);
                            contentView.findViewById(R.id.tvProgress).setVisibility(View.GONE);
                            videoView.setBackgroundColor(Color.TRANSPARENT);
                            break;
                        case CrosheVideoView.PLAY_STATE_PAUSE:
                            contentView.findViewById(R.id.imgPlay).setVisibility(View.VISIBLE);
                            contentView.findViewById(R.id.imgPlay).bringToFront();
                            break;
                        case CrosheVideoView.PLAY_STATE_BUFFERING:
                            contentView.findViewById(R.id.imgPlay).setVisibility(View.GONE);
                            contentView.findViewById(R.id.tvProgress).setVisibility(View.VISIBLE);
                            break;
                        case CrosheVideoView.PLAY_STATE_STOP:
                        case CrosheVideoView.PLAY_STATE_COMPLETE:
                            videoView.setBackgroundColor(Color.BLACK);
                            contentView.findViewById(R.id.imgPlay).setVisibility(View.VISIBLE);
                            imgThumb.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });

            contentView.findViewById(R.id.flVideoPlay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    contentView.findViewById(R.id.imgPlay).setVisibility(View.GONE);
                    contentView.findViewById(R.id.tvProgress).setVisibility(View.VISIBLE);

                    videoView.setVisibility(View.VISIBLE);
                    boolean isLocal = true;
                    if (!videoView.isInitPath()) {
                        if (path.toLowerCase().startsWith("http://") || path.toLowerCase().startsWith("https://")) {
                            isLocal = false;
                            v.setClickable(false);

                            OKHttpUtils.getInstance().downFile(context, path, new OKHttpUtils.HttpDownFileCallBack() {
                                @Override
                                public boolean onDownLoad(long countLength, long downLength, final String localPath) {
                                    if (countLength == downLength) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                v.setClickable(true);
                                                videoView.setVideoPath(localPath);
                                                videoView.start();
                                            }
                                        });
                                    }
                                    return true;
                                }
                                @Override
                                public void onDownFail(String message) {

                                }
                            });
                        } else {
                            isLocal = true;
                            videoView.setVideoPath(path);
                        }
                    }

                    if (isLocal) {
                        if (videoView.isPlaying()) {
                            videoView.pause();
                        } else {
                            videoView.start();
                        }
                    }
                }
            });


            return contentView;
        }

        @NonNull
        private View getGifView(final int position) {
            final View contentView = LayoutInflater.from(context).inflate(R.layout.android_base_item_show_imageview, null);

            final TextView tvProgress = (TextView) contentView.findViewById(R.id.tvProgress);
            tvProgress.setVisibility(View.GONE);
            final PhotoView photoView = (PhotoView) contentView.findViewById(R.id.photoView);


            if (thumbPaths != null) {
                ImageLoader.getInstance().displayImage(scheme + thumbPaths[position],
                        photoView, image_display_options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view,
                                                          Bitmap loadedImage) {
                                // TODO Auto-generated method stub
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                doLoadGIF(position, contentView, tvProgress, photoView);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view,
                                                        FailReason failReason) {
                                // TODO Auto-generated method stub
                                doLoadGIF(position, contentView, tvProgress, photoView);
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {
                                doLoadGIF(position, contentView, tvProgress, photoView);
                            }
                        });
            } else {
                photoView.setImageResource(R.drawable.android_base_default_img);
                doLoadGIF(position, contentView, tvProgress, photoView);
            }

            return contentView;
        }


        private void doLoadGIF(int position, final View contentView,
                               final TextView tvProgress, final PhotoView photoView) {
            String path = scheme + paths[position];


            if (path.toLowerCase().startsWith("http://") || path.toLowerCase().startsWith("https://")) {

                OKHttpUtils.getInstance().downFile(context, path, new OKHttpUtils.HttpDownFileCallBack() {
                    @Override
                    public boolean onDownLoad(final long countLength, final long downLength, final String localPath) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (countLength == downLength) {
                                        GifDrawable gifFromPath = new GifDrawable(localPath);
                                        photoView.setImageDrawable(gifFromPath);
                                        tvProgress.setVisibility(View.GONE);
                                        return;
                                    }
                                    tvProgress.setText((int) ((Double.valueOf(downLength) / countLength) * 100) + "%");
                                    tvProgress.setVisibility(View.VISIBLE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        return true;
                    }

                    @Override
                    public void onDownFail(String message) {

                    }
                });

            } else {
                try {
                    GifDrawable gifFromPath = new GifDrawable(path.replace("file://", ""));
                    photoView.setImageDrawable(gifFromPath);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public boolean isVideo(String path) {
        return path.toLowerCase().endsWith(".mp4");
    }


    public boolean isGIF(String path) {
        return path.toLowerCase().endsWith(".gif");
    }

}
