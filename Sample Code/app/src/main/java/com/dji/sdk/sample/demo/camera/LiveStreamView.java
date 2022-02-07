package com.dji.sdk.sample.demo.camera;

import android.app.Service;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.demo.myHandlers.FlightControllerHandler;
import com.dji.sdk.sample.demo.myHandlers.CameraHandler;
import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.Helper;
import com.dji.sdk.sample.internal.utils.ToastUtils;
import com.dji.sdk.sample.internal.utils.VideoFeedView;
import com.dji.sdk.sample.internal.view.PresentableView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.sdkmanager.LiveStreamManager;

/**
 * Class for live stream demo.
 *
 * @author Hoker
 * @date 2019/1/28
 * <p>
 * Copyright (c) 2019, DJI All Rights Reserved.
 */
public class LiveStreamView extends LinearLayout implements PresentableView, View.OnClickListener {

    private String liveShowUrl = "rtmp://34.82.64.231:1935/live/ATLAS";
    static boolean exit = false;

    private VideoFeedView primaryVideoFeedView;
    private VideoFeedView fpvVideoFeedView;
    private EditText showUrlInputEdit;

    private Button startLiveShowBtn;
    private Button stopLiveShowBtn;
    private Button startTakeOffBtn;
    private Button startLandingBtn;
    private Button startGoHomeBtn;
    private Button visualModeBtn;
    private Button thermalModeBtn;

    private LiveStreamManager.OnLiveChangeListener listener;
    private LiveStreamManager.LiveStreamVideoSource currentVideoSource = LiveStreamManager.LiveStreamVideoSource.Primary;

    public LiveStreamView(Context context) {
        super(context);
        initUI(context);
        initListener();
    }

    private void initUI(Context context) {
        setClickable(true);
        setOrientation(VERTICAL);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_live_stream, this, true);

        primaryVideoFeedView = (VideoFeedView) findViewById(R.id.video_view_primary_video_feed);
        primaryVideoFeedView.registerLiveVideo(VideoFeeder.getInstance().getPrimaryVideoFeed(), true);

        fpvVideoFeedView = (VideoFeedView) findViewById(R.id.video_view_fpv_video_feed);
        fpvVideoFeedView.registerLiveVideo(VideoFeeder.getInstance().getSecondaryVideoFeed(), false);
        if (Helper.isMultiStreamPlatform()){
            fpvVideoFeedView.setVisibility(VISIBLE);
        }

        showUrlInputEdit = (EditText) findViewById(R.id.edit_live_show_url_input);
        showUrlInputEdit.setText(liveShowUrl);

        startLiveShowBtn = (Button) findViewById(R.id.btn_start_live_show);
        stopLiveShowBtn = (Button) findViewById(R.id.btn_stop_live_show);
        startTakeOffBtn = (Button) findViewById(R.id.btn_start_take_off);
        startLandingBtn = (Button) findViewById(R.id.btn_start_landing);
        startGoHomeBtn = (Button) findViewById(R.id.btn_start_go_home);
        visualModeBtn = (Button) findViewById(R.id.btn_visual_mode);
        thermalModeBtn = (Button) findViewById(R.id.btn_thermal_mode);

        startLiveShowBtn.setOnClickListener(this);
        stopLiveShowBtn.setOnClickListener(this);
        startTakeOffBtn.setOnClickListener(this);
        startLandingBtn.setOnClickListener(this);
        startGoHomeBtn.setOnClickListener(this);
        visualModeBtn.setOnClickListener(this);
        thermalModeBtn.setOnClickListener(this);
    }

    private void initListener() {
        showUrlInputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                liveShowUrl = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listener = new LiveStreamManager.OnLiveChangeListener() {
            @Override
            public void onStatusChanged(int i) {
                ToastUtils.setResultToToast("status changed : " + i);
            }
        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BaseProduct product = DJISampleApplication.getProductInstance();
        if (product == null || !product.isConnected()) {
            ToastUtils.setResultToToast("Disconnect");
            return;
        }
        if (isLiveStreamManagerOn()){
            DJISDKManager.getInstance().getLiveStreamManager().registerListener(listener);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isLiveStreamManagerOn()){
            DJISDKManager.getInstance().getLiveStreamManager().unregisterListener(listener);
        }
    }

    @Override
    public int getDescription() {
        return R.string.component_listview_live_stream;
    }

    @NonNull
    @Override
    public String getHint() {
        return this.getClass().getSimpleName() + ".java";
    }

    void startLiveShow() {
        exit = false;
        ToastUtils.setResultToToast("Start Live Show");
        if (!isLiveStreamManagerOn()) {
            return;
        }
        if (DJISDKManager.getInstance().getLiveStreamManager().isStreaming()) {
            ToastUtils.setResultToToast("already started!");
            return;
        }
        new Thread() {
            @Override
            public void run() {

                DJISDKManager.getInstance().getLiveStreamManager().setLiveUrl(liveShowUrl);
                int result = DJISDKManager.getInstance().getLiveStreamManager().startStream();
                DJISDKManager.getInstance().getLiveStreamManager().setStartTime();
                ToastUtils.setResultToToast("startLive:" + result +
                        "\n isVideoStreamSpeedConfigurable:" + DJISDKManager.getInstance().getLiveStreamManager().isVideoStreamSpeedConfigurable() +
                        "\n isLiveAudioEnabled:" + DJISDKManager.getInstance().getLiveStreamManager().isLiveAudioEnabled());

                while (!exit) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    FlightControllerHandler.getInstance().flightControllerStateResponse(false);
                }
            }
        }.start();
    }

    private void stopLiveShow() {
        if (!isLiveStreamManagerOn()) {
            return;
        }
        DJISDKManager.getInstance().getLiveStreamManager().stopStream();
        exit = true;
        FlightControllerHandler.getInstance().flightControllerStateResponse(true);
        ToastUtils.setResultToToast("Stop Live Show");
    }

    private boolean isLiveStreamManagerOn() {
        if (DJISDKManager.getInstance().getLiveStreamManager() == null) {
            ToastUtils.setResultToToast("No live stream manager!");
            return false;
        }
        return true;
    }

    private boolean isSupportSecondaryVideo(){
        if (!Helper.isMultiStreamPlatform()) {
            ToastUtils.setResultToToast("No secondary video!");
            return false;
        }
        return true;
    }

    private void startTakeoff() {
        FlightControllerHandler.getInstance().startTakeoff();
        ToastUtils.setResultToToast("Start Take Off");
    }

    private void startLanding() {
        FlightControllerHandler.getInstance().startLanding();
        ToastUtils.setResultToToast("Start Landing");
    }

    private void startGoHome() {
        FlightControllerHandler.getInstance().startGoHome();
        ToastUtils.setResultToToast("Start Go Home");
    }

    private void visualMode(){
        CameraHandler.getInstance().visualMode();
        ToastUtils.setResultToToast("Successful Switch");
    }

    private void thermalMode(){
        CameraHandler.getInstance().thermalMode();
        ToastUtils.setResultToToast("Successful Switch");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_live_show:
                startLiveShow();
                break;
            case R.id.btn_stop_live_show:
                stopLiveShow();
                break;
            case R.id.btn_start_take_off:
                startTakeoff();
                break;
            case R.id.btn_start_landing:
                startLanding();
                break;
            case R.id.btn_start_go_home:
                startGoHome();
                break;
            case R.id.btn_visual_mode:
                visualMode();
                break;
            case R.id.btn_thermal_mode:
                thermalMode();
                break;
            default:
                break;
        }
    }
}
