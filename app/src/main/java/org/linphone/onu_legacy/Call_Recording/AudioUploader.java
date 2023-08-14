package org.linphone.onu_legacy.Call_Recording;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.MVP.Implementation.model.AudioDataClass.AudioUploadResponse;
import org.linphone.onu_legacy.Utility.Helper;
import org.linphone.onu_legacy.Utility.Info;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by android on 3/29/2016.
 */
public class AudioUploader extends AsyncTask<Void, Void, String> {
    int TIMEOUT_MILLISEC = 30000;
    URL connectURL;
    Context context;
    ProgressDialog dialog;
    Activity activity;
    String url=null,title=null,description=null;
    private Info info;
    private byte[ ] dataToServer;
    private FileInputStream fileInputStream = null;
    private int serverResponseCode;
    private String filename;
    private String c_type;
    private String TAG="AudioUploader";
    private String filePath;
    private Helper helper;
    private Gson gson=new Gson();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }
    public AudioUploader(Context context,String name,String type) {
        this.context = context;
        this.filename=name;
        this.c_type=type;
        Log.i(TAG,"Con_Call Type: "+type);
        Log.i(TAG,"Con_Call Type: "+c_type);
        info=new Info(context);
        File sampleDir = new File(Environment.getExternalStorageDirectory(), "/onuRecords");
        filePath= sampleDir.getAbsolutePath();
        Log.i(TAG,"Path:"+filePath);
        helper=new Helper(context);
    }
    @Override
    protected void onPostExecute(String result) {
       // Log.i(TAG,"onPost: "+result);
    }
    private String getMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(Void... params){
        String type = c_type;
        Log.i(TAG,"Call Type: "+c_type+" File: "+filename);

        String sourceFileUri = filePath+"/"+filename+".mp3";
        String sourceFileDirectory = filePath+"/";

        Log.i(TAG, "uri :"+sourceFileUri);
        File sourceFile = new File(sourceFileUri);
        Database db=new Database(context);
            //********get the file *********
        try {
            File sourceDirectory = new File(sourceFileDirectory);
            for (File ff : sourceDirectory.listFiles()) {
                if (ff.isFile())
                    Log.i(TAG, "File Name:" + ff.getName());
                if (ff.getName().contains(filename)) {
//                    sourceFileUri = "/mnt/sdcard/onuRecords/" + ff.getName();
                    sourceFileUri = filePath+"/" + ff.getName();
                    sourceFile = new File(sourceFileUri);
                }
            }
        }catch (Exception e)
        {
            Log.i(TAG, "Exception :" + e.toString());
        }
            //******** file found ***********

        Log.i(TAG, "Src URI :" + sourceFileUri);
            if (sourceFile.isFile()) {
                try {
                    //File f  = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                    String content_type = getMimeType(sourceFile.getPath());
                    Log.i(TAG, "Content Type:" + content_type);

                    String file_path = sourceFile.getAbsolutePath();
                    OkHttpClient client;
                    client = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build();

                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type), sourceFile);
                    Log.i(TAG, "Filename:" + filename);
                    RequestBody request_body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("type", content_type)
                            .addFormDataPart("calltype", c_type)
                            .addFormDataPart("trxid", filename)
                            .addFormDataPart("username", info.getUsername())
                            .addFormDataPart("password", info.getPassword())
                            .addFormDataPart("device_id", info.getImei())
                            .addFormDataPart("uploaded_file", file_path.substring(file_path.lastIndexOf("/") + 1), file_body)
                            .build();


//                    Request request = new Request.Builder()
////                           //.url("http://tester.onuserver.com/callRecorder/")
////                            .url(info.getUrl()+"/callRecordSave?audio="+filename)
////                            .post(request_body)
////                            .build();
                    //Log.i(TAG,"Request body: "+request_body.toString());
                    //new test
                    try{
                        String response = post(info.getUrl()+"/callRecordSave?audio="+filename, request_body);
                        //String response = post(info.getUrl()+"/callRecordDemo?audio="+filename, request_body);
                        if (response!=null){
                            Log.i(TAG, "Successful: "+response);
                            Gson gson=new Gson();
                            AudioUploadResponse uploadResponse=gson.fromJson(response,AudioUploadResponse.class);
                            Log.i(TAG,"Uploaded File Size: "+uploadResponse.getFileSize());
                            if (uploadResponse.getFileSize()>50 && uploadResponse.getStatus()==4000 && uploadResponse.getFileExists()){
                                Log.i(TAG,"Successfully Updated!!!");
                                db.updateCallQueueForAudioUp(filename,"1");
                                sourceFile.delete();
                            }
                        }else {
                            Log.i(TAG,"Not successful!!!");
                        }
                    }catch (Exception e){

                    }
                    //new
                    //new test end
//                    Response response = client.newCall(request).execute();
//                    //Log.i(TAG,"Request: "+"type: "+ content_type+"calltype: "+ CallType+"trxid: "+ filename+"device_id"+ info.getImei());
//                    try{
//                        //Response response = client.newCall(request).execute();
//                        //Log.i(TAG, "Error : " + response);
//                        if (!response.isSuccessful()) {
//                            Log.i(TAG, "Error Response: " + response.body().string());
//                            throw new IOException("Error : " + response);
//                        }
//                        else {
//                            Log.i(TAG, "Successful Response :" + response.body().string());
//                            String res="";
//                            db.updateCallQueueForAudioUp(filename,"1");
//                            sourceFile.delete();
//                            //return response.body().string();
//                        }
//                    } catch (IOException e) {
//                        Log.i(TAG, "Exception: "+e.toString());
//                        e.printStackTrace();
//                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            } else
            {
                Log.i(TAG, "no file found");
                //db.updateCallQueueForAudioUp(filename,"1");
                //db.deleteCall(filename);
            }
        return null;
    }

    public String post(String url, RequestBody body) throws IOException {
        OkHttpClient client = new OkHttpClient();
        //RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()){
            return response.body().string();
        }else {
            return null;
        }

    }
}