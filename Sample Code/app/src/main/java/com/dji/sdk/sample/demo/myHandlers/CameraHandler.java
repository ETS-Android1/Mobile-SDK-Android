package com.dji.sdk.sample.demo.myHandlers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.ToastUtils;

import java.util.List;

import dji.common.camera.CameraVideoStreamSource;
import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.keysdk.CameraKey;
import dji.keysdk.callback.SetCallback;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.keysdk.KeyManager;
import dji.sdk.camera.Lens;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class CameraHandler {
    private static CameraHandler SINGLE_INSTANCE = null;
    private final int VISUAL_CAMERA_INDEX = 0;
    private final int THERMAL_CAMERA_INDEX = 2;

    private CameraHandler() {

    }

    public static CameraHandler getInstance() {
        if (SINGLE_INSTANCE == null) {
            synchronized (CameraHandler.class) {
                SINGLE_INSTANCE = new CameraHandler();
            }
        }
        return SINGLE_INSTANCE;
    }

    public void visualMode(){
        if (DJISampleApplication.getProductInstance() == null) {
            ToastUtils.setResultToToast("Disconnected");
        } else {
            if (null != DJISampleApplication.getProductInstance().getCamera()) {
                Aircraft aircraft = (Aircraft) DJISampleApplication.getProductInstance();

                List cameras = aircraft.getCameras();

                Camera thermal = (Camera) cameras.get(1);

                thermal.setDisplayMode(SettingsDefinitions.DisplayMode.VISUAL_ONLY, null);

                ToastUtils.setResultToToast(thermal.getDisplayName());

            } else {
                ToastUtils.setResultToToast("NO HAY CAMARA");
            }
        }
    }

    public void thermalMode(){
        if (DJISampleApplication.getProductInstance() == null) {
            ToastUtils.setResultToToast("Disconnected");
        } else {
            if (null != DJISampleApplication.getProductInstance().getCamera()) {
                Aircraft aircraft = (Aircraft) DJISampleApplication.getProductInstance();

                List cameras = aircraft.getCameras();

                Camera thermal = (Camera) cameras.get(1);

                thermal.setDisplayMode(SettingsDefinitions.DisplayMode.THERMAL_ONLY, null);

                ToastUtils.setResultToToast(thermal.getDisplayName());

            } else {
                ToastUtils.setResultToToast("NO HAY CAMARA");
            }
        }
    }
}
