package com.dji.sdk.sample.common;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dji.sdk.sample.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import dji.sdk.Products.DJIAircraft;
import dji.sdk.base.DJIBaseProduct;
import dji.thirdparty.eventbus.EventBus;

/**
 * Created by dji on 15/12/18.
 */
public class MainContent extends RelativeLayout implements DJIBaseProduct.DJIVersionCallback {

    public static final String TAG = MainContent.class.getName();

    public MainContent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private TextView mTextConnectionStatus;
    private TextView mTextProduct;
    private TextView mTextModelAvailable;
    private Button mBtnOpen;
    private Button mBtnRequest;

    private DJIBaseProduct mProduct;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initUI();
    }



    class UploadImage extends AsyncTask<URL, Void, String> {

        Context c ;
        JSONArray fileKeys = new JSONArray();


        public UploadImage(Context c){
            super();
            this.c = c;
        }
        @Override
        protected String doInBackground(URL... url) {
            try {
                uploadImage(this.c);
            }catch(Exception e){
                Toast.makeText(this.c, e.getMessage(), Toast.LENGTH_SHORT).show();
            }finally{
                Toast.makeText(this.c, "upload success", Toast.LENGTH_SHORT).show();
                File destDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Dji_Sdk_Test/");
                for(File file: destDir.listFiles()){
                  file.delete();
                }
            }
            return "";
        }

        public void uploadImage(Context c) throws Exception{

            File destDir = new File(Environment.getExternalStorageDirectory().
                    getPath() + "/Dji_Sdk_Test/");
            File[] files = destDir.listFiles();
            String uri = "your domain";
            String base64_pass = "your base 64 password";
            for (int i = 0;i<files.length;i++) {
                try {
                    sendMultipart(uri+"/k/v1/file.json", base64_pass, files[i]);
                } catch (Exception e) {
//                    Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            postNewImagesToApp("3916",uri+"/k/v1/record.json",base64_pass,this.fileKeys);
//            linkImageToApp("271",uri,base64_pass,this.fileKeys);
        }

        private void linkImageToApp(String appId,String uri,String password,JSONArray filekeys) throws Exception{

            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("PUT");

            // jsonデータの作成
            JSONObject requestData = new JSONObject();
            JSONObject recordJson = new JSONObject();
            JSONObject tempJson = new JSONObject();

            requestData.put("app",appId);
            requestData.put("id","2");
            tempJson.put("type", "FILE");
            tempJson.put("value", filekeys);
            recordJson.put("drone_image",tempJson);
            requestData.put("record",recordJson);

            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("X-Cybozu-Authorization", password);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(requestData.toString());

            if( connection.getResponseCode() == HttpURLConnection.HTTP_OK ){
                StringBuffer responseJSON = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    responseJSON.append(inputLine);
                }
//                this.fileKeys.put(new JSONObject(new String(responseJSON)));
                Log.i("OSA030", "doPost success");
            }
            else{
                StringBuffer responseJSON = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    responseJSON.append(inputLine);
                }
                Log.i("OSA030", "doPost success");

            }

            outputStream.close();
            connection.disconnect();

        }

        private void postNewImagesToApp(String appId,String uri,String password,JSONArray filekeys) throws Exception{

            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");

            // jsonデータの作成
            JSONObject requestData = new JSONObject();
            JSONObject recordJson = new JSONObject();
            JSONObject tempJson = new JSONObject();

            requestData.put("app",appId);
            tempJson.put("type", "FILE");
            tempJson.put("value", filekeys);
            recordJson.put("drone_image",tempJson);
            requestData.put("record",recordJson);

            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("X-Cybozu-Authorization", password);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(requestData.toString());

            if( connection.getResponseCode() == HttpURLConnection.HTTP_OK ){
                StringBuffer responseJSON = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    responseJSON.append(inputLine);
                }
//                this.fileKeys.put(new JSONObject(new String(responseJSON)));
                Log.i("OSA030", "doPost success");
            }
            else{
                StringBuffer responseJSON = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    responseJSON.append(inputLine);
                }
                Log.i("OSA030", "doPost success");

            }

            outputStream.close();
            connection.disconnect();

        }

        // this function is implemented based on http://www.androidsnippets.com/multipart-http-requests
        private void sendMultipart(String uri,String password, File file) throws Exception {

            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            final String twoHyphens = "--";
            final String boundary =  "--"+
                    UUID.randomUUID().toString()
//                    "eagbijhfiharosfjgirjasiu23fnae"
                    +"";
            final String lineEnd = "\r\n";
            final int maxBufferSize = 1024*1024*3;

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-Cybozu-Authorization", password);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" +
                    file.getName()
                    + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream" + lineEnd);
            outputStream.writeBytes(lineEnd);

            FileInputStream fileInputStream = new FileInputStream(file);
            int bytesAvailable = fileInputStream.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while(bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(lineEnd);

            outputStream.writeBytes(twoHyphens+boundary + twoHyphens + lineEnd);

            if( connection.getResponseCode() == HttpURLConnection.HTTP_OK ){
                StringBuffer responseJSON = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    responseJSON.append(inputLine);
                }
                this.fileKeys.put(new JSONObject(new String(responseJSON)));
                Log.i("OSA030", "doPost success");
            }
            else{
                StringBuffer responseJSON = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    responseJSON.append(inputLine);
                }
                Log.i("OSA030", "doPost success");

            }
            Log.d("Logging",readStream(connection.getInputStream()));
            outputStream.close();
        }

        private String readStream(InputStream is) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
            for (String line = r.readLine(); line != null; line =r.readLine()){
                sb.append(line);
            }
            is.close();
            return sb.toString();
        }

    }

    private void initUI() {
        Log.v(TAG, "initUI");

        mTextConnectionStatus = (TextView) findViewById(R.id.text_connection_status);
        mTextModelAvailable = (TextView) findViewById(R.id.text_model_available);
        mTextProduct = (TextView) findViewById(R.id.text_product_info);
        mBtnOpen = (Button) findViewById(R.id.btn_open);
        mBtnRequest = (Button) findViewById(R.id.btn_request);

        mBtnRequest.setOnClickListener(new OnClickListener() {
            @Override

            public void onClick(final View v) {



                if (Utils.isFastDoubleClick()) return;

                new AlertDialog.Builder(v.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("画像アップロードを開始します。")
                        .setMessage("ドローンからダウンロードした画像は全て削除されますがよろしいですか？")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new UploadImage( v.getContext()).execute();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

//                sendMultipart();
        //      Toast.makeText(v.getContext(), "テスト", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Logging", "click run");
                if (Utils.isFastDoubleClick()) return;
                EventBus.getDefault().post(new SetViewWrapper(R.layout.content_component_list, R.string.activity_component_list, getContext()));
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        Log.v(TAG, "onAttachedToWindow");

        refreshSDKRelativeUI();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DJISampleApplication.FLAG_CONNECTION_CHANGE);
        getContext().registerReceiver(mReceiver, filter);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        getContext().unregisterReceiver(mReceiver);
        super.onDetachedFromWindow();
    }

    private void updateVersion() {
        String version = null;
        if(mProduct != null) {
            version = mProduct.getFirmwarePackageVersion();
        }

        if(version == null) {
            mTextModelAvailable.setText("N/A"); //Firmware version: 
        } else {
            mTextModelAvailable.setText(version); //"Firmware version: " +
        }


    }

    @Override
    public void onProductVersionChange(String oldVersion, String newVersion) {
        updateVersion();
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshSDKRelativeUI();
        }

    };

    private void refreshSDKRelativeUI() {
        mProduct = DJISampleApplication.getProductInstance();

        if (null != mProduct && mProduct.isConnected()) {
            Log.v(TAG, "refreshSDK: True");
            mBtnOpen.setEnabled(true);

            String str = mProduct instanceof DJIAircraft ? "DJIAircraft" : "DJIHandHeld";
            mTextConnectionStatus.setText("Status: " + str + " connected");
            mProduct.setDJIVersionCallback(this);
            updateVersion();

            if (null != mProduct.getModel()) {
                mTextProduct.setText("" + mProduct.getModel().getDisplayName());
            } else {
                mTextProduct.setText(R.string.product_information);
            }
        } else {
            Log.v(TAG, "refreshSDK: False");
            mBtnOpen.setEnabled(false);

            mTextProduct.setText(R.string.product_information);
            mTextConnectionStatus.setText(R.string.connection_loose);
        }
    }
}