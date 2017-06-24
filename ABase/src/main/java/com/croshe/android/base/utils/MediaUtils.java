package com.croshe.android.base.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Janesen on 2017/5/2.
 */

public class MediaUtils {
    private static MediaPlayer mediaPlayer;
    private static int lastVolume=-1;


    public static void playRaw(Context context, int resource) {
        playRaw(context, true, resource, null);
    }

    public static void playRaw(Context context, boolean isMaxVolume, int resource) {
        playRaw(context, isMaxVolume, resource, null);
    }

    public static void playRaw(Context context, int resource, final Runnable onComplete) {
        playRaw(context, true, resource, onComplete);
    }

    public static void playRaw(final Context context, boolean isMaxVolume, int resource, final Runnable onComplete) {
        stopMedia(context);


        final AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        lastVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if (isMaxVolume) {
            int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            //将媒体音量调到最大，便于音量大
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        }

        mediaPlayer = MediaPlayer.create(context, resource);
        if (mediaPlayer != null) {
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (onComplete != null) {
                        onComplete.run();
                    }
                    stopMedia(context);
                }
            });

        }
    }


    /**
     * 播放assets下的音频
     *
     * @param context
     * @param mp3Name
     */
    public static void playAssets(Context context, String mp3Name) {
        playAssets(context, mp3Name, true, null);

    }

    /**
     * 播放assets下的音频
     *
     * @param context
     * @param mp3Name
     * @param isMaxVolume
     */
    public static void playAssets(Context context, String mp3Name, boolean isMaxVolume) {
        playAssets(context, mp3Name, isMaxVolume, null);
    }

    /**
     * 播放assets下的音频
     *
     * @param context
     * @param mp3Name
     * @param isMaxVolume
     * @param onComplete
     */
    public static void playAssets(final Context context, String mp3Name, boolean isMaxVolume, final Runnable onComplete) {
        try {
            stopMedia(context);

            AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            lastVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            if (isMaxVolume) {
                int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                //将媒体音量调到最大，便于音量大
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
            }

            AssetManager assetManager = context.getAssets();

            AssetFileDescriptor fileDescriptor = assetManager.openFd(mp3Name);

            mediaPlayer = new MediaPlayer();
            if (mediaPlayer != null) {
                mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                mediaPlayer.prepare();
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (onComplete != null) {
                            onComplete.run();
                        }
                        stopMedia(context);
                    }
                });

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 播放网络声音文件
     *
     * @param context
     * @param url
     */
    public static void playUrl(Context context, String url) {
        playUrl(context, url, true, null);
    }


    /**
     * 播放网络声音文件
     *
     * @param context
     * @param url
     * @param isMaxVolume
     */
    public static void playUrl(Context context, String url, boolean isMaxVolume) {
        playUrl(context, url, isMaxVolume, null);
    }


    /**
     * 播放网络声音文件
     *
     * @param context
     * @param url
     * @param isMaxVolume
     * @param onComplete
     */
    public static void playUrl(final Context context, final String url, boolean isMaxVolume, final Runnable onComplete) {
        try {
            stopMedia(context);

            final AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            lastVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            if (isMaxVolume) {
                int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                //将媒体音量调到最大，便于音量大
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
            }
            mediaPlayer = new MediaPlayer();
            if (mediaPlayer != null) {
                OKHttpUtils.getInstance().downFile(context, url, new OKHttpUtils.HttpDownFileCallBack() {
                    @Override
                    public boolean onDownLoad(long countLength, long downLength, String localPath) {
                        if (countLength == downLength) {
                            try {
                                mediaPlayer.setDataSource(localPath);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (Exception e) {}
                        }
                        return true;
                    }
                    @Override
                    public void onDownFail(String message) {

                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (onComplete != null) {
                            onComplete.run();
                        }
                        stopMedia(context);
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 停止播放
     */
    public static void stopMedia(Context context) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            if (lastVolume >= 0) {
                AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, lastVolume, 0);
            }
        } catch (Exception e) {}
    }
}
