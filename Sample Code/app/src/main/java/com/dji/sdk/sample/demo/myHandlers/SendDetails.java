package com.dji.sdk.sample.demo.myHandlers;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import dji.thirdparty.afinal.core.AsyncTask;

public class SendDetails extends AsyncTask<String, Void, String> {

    public String ServerResponse;

    @Override
    protected String doInBackground(String... params) {

        String data = "";

        HttpURLConnection httpURLConnection = null;
        try {

            httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            //I tried this line, but it breaks everything.

            httpURLConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            JSONObject postJson = new JSONObject();
            postJson.put("name", params[1]);
            wr.write(postJson.toString().getBytes());
            wr.flush();
            wr.close();

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        ServerResponse = result;
        super.onPostExecute(result);
        Log.e("TAG", result.toString()); // this is expecting a response code to be sent from your server upon receiving the POST data
    }
}