package com.dji.sdk.sample.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.common.BaseThreeBtnView;
import com.dji.sdk.sample.common.DJISampleApplication;
import com.dji.sdk.sample.common.Utils;
import com.dji.sdk.sample.utils.DJIModuleVerificationUtil;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;


import javax.net.ssl.HttpsURLConnection;

import dji.sdk.Camera.DJICameraSettingsDef;
import dji.sdk.Camera.DJIMedia;
import dji.sdk.Camera.DJIMediaManager;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIError;
//import dji.sdk.interfaces;
/**
 * Class for featching the media.
 */
public class FetchMediaView extends BaseThreeBtnView {

    private DJIMedia mMedia;
    private ArrayList<DJIMedia> mMedias;

    public Handler messageHandler;

    public FetchMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Looper looper=Looper.myLooper();
        messageHandler = new MessageHandler(looper);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (DJIModuleVerificationUtil.isCameraModuleAvailable()) {
            if (DJIModuleVerificationUtil.isMediaManagerAvailable()) {
                DJISampleApplication.getProductInstance().getCamera().setCameraMode(
                        DJICameraSettingsDef.CameraMode.MediaDownload,
                        new DJIBaseComponent.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (null == djiError)
                                    fetchMediaList();
                            }
                        }
                );
            } else {
                mTexInfo.setText(R.string.not_support_mediadownload);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (DJIModuleVerificationUtil.isCameraModuleAvailable()) {
            DJISampleApplication.getProductInstance().getCamera().setCameraMode(
                    DJICameraSettingsDef.CameraMode.ShootPhoto,
                    new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {

                        }
                    }
            );
        }
    }

    @Override
    protected int getMiddleBtnTextResourceId() {
        return R.string.fetch_media_view_fetch_thumbnail;
    }

    @Override
    protected int getLeftBtnTextResourceId() {
        return R.string.fetch_media_view_fetch_preview;
    }

    @Override
    protected int getRightBtnTextResourceId() {
        return R.string.fetch_media_view_fetch_media;
    }

    @Override
    protected int getInfoResourceId() {
        if(!DJIModuleVerificationUtil.isMediaManagerAvailable()){
            return R.string.not_support_mediadownload;
        }else {
            return R.string.support_mediadownload;
        }

    }

    @Override
    protected void getMiddleBtnMethod() {
        // Fetch Thumbnail Button
        if (DJIModuleVerificationUtil.isMediaManagerAvailable() && mMedia != null) {
            mMedia.fetchThumbnail(new DJIMediaManager.CameraDownloadListener<Bitmap>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onRateUpdate(long l, long l1, long l2) {

                }

                @Override
                public void onProgress(long l, long l1) {

                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    Utils.setResultToToast(getContext(), "Success! The bitmap's bytecount is: "
                            +bitmap.getByteCount());
                }

                @Override
                public void onFailure(DJIError djiError) {

                }

            });
        }
    }

    @Override
    protected void getLeftBtnMethod() {
        // Fetch Thumbnail Button
        if (DJIModuleVerificationUtil.isMediaManagerAvailable() && mMedia != null) {
            mMedia.fetchThumbnail(new DJIMediaManager.CameraDownloadListener<Bitmap>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onRateUpdate(long l, long l1, long l2) {

                }

                @Override
                public void onProgress(long l, long l1) {

                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    Utils.setResultToToast(getContext(), "Success! The bitmap's bytecount is: "
                            +bitmap.getByteCount());
                }

                @Override
                public void onFailure(DJIError djiError) {

                }

            });
        }
    }




    @Override
    protected void getRightBtnMethod() {
        // Fetch Media Data Button
        if (DJIModuleVerificationUtil.isCameraModuleAvailable() && mMedia != null) {

                File destDir = new File(Environment.getExternalStorageDirectory().
                        getPath() + "/Dji_Sdk_Test/");
            for (int i = 0;i<mMedias.size();i++) {


                mMedias.get(i).fetchMediaData(destDir, "testName"+i, new DJIMediaManager.CameraDownloadListener<String>() {
                    String str;

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onRateUpdate(long l, long l1, long l2) {

                    }

                    @Override
                    public void onProgress(long l, long l1) {
                        Message message = Message.obtain();
                        str = "Downlaoding file 1 progress: " + l / l1;
                        message.obj = str;
                        messageHandler.sendMessage(message);
                    }

                    @Override
                    public void onSuccess(String s) {
                        Utils.setResultToToast(getContext(), "The file has been store, its path is " + s);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            }
        }
    }

    // Initialize the view with getting a media file.
    private void fetchMediaList() {
        if (DJIModuleVerificationUtil.isMediaManagerAvailable()) {
            DJISampleApplication.getProductInstance().getCamera().getMediaManager().fetchMediaList(
                    new DJIMediaManager.CameraDownloadListener<ArrayList<DJIMedia>>() {
                        String str;
                        @Override
                        public void onStart() {
                            Message message = Message.obtain();
                            message.obj = "Start fetch media list";
                            messageHandler.sendMessage(message);
                        }

                        @Override
                        public void onRateUpdate(long total, long current, long persize) {
                            Message message = Message.obtain();
                            str = "in progress";
                            message.obj = str;
                            messageHandler.sendMessage(message);
                        }

                        @Override
                        public void onProgress(long l, long l1) {

                        }

                        @Override
                        public void onSuccess(ArrayList<DJIMedia> djiMedias) {
                            if (null != djiMedias) {
                                if (!djiMedias.isEmpty()){
                                    mMedia = djiMedias.get(0);
                                    mMedias = djiMedias;

                                    Message message = Message.obtain();
                                    str = "Total Media files:"+djiMedias.size() +"\n"+ "Media 1: "+djiMedias.get(0).getFileName();
                                    message.obj = str;
                                    messageHandler.sendMessage(message);
                                }else {
                                    Message message = Message.obtain();
                                    str = "No Media in SD Card";
                                    message.obj = str;
                                    messageHandler.sendMessage(message);
                                }

                            }
                        }

                        @Override
                        public void onFailure(DJIError djiError) {
                           Message message = Message.obtain();
                            message.obj = djiError.getDescription();
                            messageHandler.sendMessage(message);
                        }
                    }
            );
        }
    }


    class MessageHandler extends Handler {
        public MessageHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            mTexInfo.setText((String)msg.obj);
        }
    }
}
