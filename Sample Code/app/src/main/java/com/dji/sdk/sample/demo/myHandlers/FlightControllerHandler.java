package com.dji.sdk.sample.demo.myHandlers;

import android.util.Log;

import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.thirdparty.afinal.core.AsyncTask;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;


public class FlightControllerHandler {
    private static BaseProduct product;

    private static FlightControllerHandler SINGLE_INSTANCE = null;

    private FlightControllerHandler() {
    }

    public static FlightControllerHandler getInstance() {
        if (SINGLE_INSTANCE == null) {
            synchronized (FlightControllerHandler.class) {
                SINGLE_INSTANCE = new FlightControllerHandler();
            }
        }
        return SINGLE_INSTANCE;
    }

    public static synchronized BaseProduct getProductInstance() {
        product = DJISDKManager.getInstance().getProduct();
        return product;
    }

    public static synchronized Aircraft getAircraftInstance() {
        return (Aircraft) getProductInstance();
    }

    public void startTakeoff() {
        if (DJISampleApplication.getProductInstance() == null) {
            ToastUtils.setResultToToast("Disconnected");
        } else {
            Aircraft aircraft = (Aircraft) DJISampleApplication.getProductInstance();
            if (null != aircraft.getFlightController()) {
                FlightController flightController = getAircraftInstance().getFlightController();
                flightController.startTakeoff(null);
            }
            else{
                ToastUtils.setResultToToast("NO HAY FLIGHT CONTROLLER");
            }
        }
    }

    public void startLanding() {
        if (DJISampleApplication.getProductInstance() == null) {
            ToastUtils.setResultToToast("Disconnected");
        } else {
            Aircraft aircraft = (Aircraft) DJISampleApplication.getProductInstance();
            if (null != aircraft.getFlightController()) {
                FlightController flightController = getAircraftInstance().getFlightController();
                flightController.startLanding(null);
            }
            else{
                ToastUtils.setResultToToast("NO HAY FLIGHT CONTROLLER");
            }
        }
    }

    public void startGoHome() {
        if (DJISampleApplication.getProductInstance() == null) {
            ToastUtils.setResultToToast("Disconnected");
        } else {
            Aircraft aircraft = (Aircraft) DJISampleApplication.getProductInstance();
            if (null != aircraft.getFlightController()) {
                FlightController flightController = getAircraftInstance().getFlightController();
                flightController.startGoHome(null);
            }
            else{
                ToastUtils.setResultToToast("NO HAY FLIGHT CONTROLLER");
            }
        }
    }

    public void cancelGoHome() {
        if (DJISampleApplication.getProductInstance() == null) {
            ToastUtils.setResultToToast("Disconnected");
        } else {
            Aircraft aircraft = (Aircraft) DJISampleApplication.getProductInstance();
            if (null != aircraft.getFlightController()) {
                FlightController flightController = DJISampleApplication.getAircraftInstance().getFlightController();
                flightController.cancelGoHome(null);
            } else {
                ToastUtils.setResultToToast("NO HAY FLIGHT CONTROLLER");
            }
        }
    }

    public void setHomeLocation(Float longitude, Float latitude) {
        if (DJISampleApplication.getProductInstance() == null) {
            ToastUtils.setResultToToast("Disconnected");
        } else {
            Aircraft aircraft = (Aircraft) DJISampleApplication.getProductInstance();
            if (null != aircraft.getFlightController()) {
                LocationCoordinate2D homeLocation = new LocationCoordinate2D(longitude,latitude);
                FlightController flightController = DJISampleApplication.getAircraftInstance().getFlightController();
                flightController.setHomeLocation(homeLocation, null);
            } else {
                ToastUtils.setResultToToast("NO HAY FLIGHT CONTROLLER");
            }
        }
    }

    public static CommonCallbacks.CompletionCallbackWith<LocationCoordinate2D> clientStreamToCompletionCallbackWithLocationCoordinate2D() {
        return new CommonCallbacks.CompletionCallbackWith<LocationCoordinate2D>() {
            @Override
            public void onSuccess(LocationCoordinate2D locationCoordinate2D) {
                JSONObject data = null;
                try {
                    data = new JSONObject();
                    data.put("locationCoordinate2D", locationCoordinate2D);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(DJIError djiError) {
            }

        };
    }

    public  void getHomeLocation() {
        if (DJISampleApplication.getProductInstance() == null) {
            ToastUtils.setResultToToast("Disconnected");
        } else {
            Aircraft aircraft = (Aircraft) DJISampleApplication.getProductInstance();
            if (null != aircraft.getFlightController()) {
                FlightController flightController = DJISampleApplication.getAircraftInstance().getFlightController();
                flightController.getHomeLocation(clientStreamToCompletionCallbackWithLocationCoordinate2D());
            } else {
                ToastUtils.setResultToToast("NO HAY FLIGHT CONTROLLER");
            }
        }
    }

    public String flightControllerStateResponse(Boolean stop) {
        FlightControllerState flightControllerState;
        if (DJISampleApplication.getProductInstance() == null) {
            ToastUtils.setResultToToast("Disconnected");
            return null;
        } else {
            Aircraft aircraft = (Aircraft) DJISampleApplication.getProductInstance();
            if (null != aircraft.getFlightController()) {
                FlightController flightController = getAircraftInstance().getFlightController();
                flightControllerState = flightController.getState();
            }
            else{
                return "NO HAY FLIGHT CONTROLLER";
            }
        }

        JSONObject jsonData = new JSONObject();
        JSONObject locationJSON = new JSONObject();

        LocationCoordinate3D location = flightControllerState.getAircraftLocation();

        try {
            if(stop) {
                locationJSON.put("getAltitude", 0);
                locationJSON.put("getLatitude", 0);
                locationJSON.put("getLongitude", 0);
            }else{
                locationJSON.put("getAltitude", location.getAltitude());
                locationJSON.put("getLatitude", location.getLatitude());
                locationJSON.put("getLongitude", location.getLongitude());
            }
                jsonData.put("getAircraftLocation", locationJSON);

                AsyncTask<String, Void, String> data = new SendDetails().execute("http://34.82.64.231:5000/send_coordinates/ATLAS", jsonData.toString());

                return data.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (Exception e) {
            Log.d("uploadListener", e.getMessage());
            return e.getMessage();
        }
    }
}
