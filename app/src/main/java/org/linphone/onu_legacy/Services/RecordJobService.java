package org.linphone.onu_legacy.Services;
// It is just like shit. ha ha ha

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.util.Log;

import org.linphone.onu_legacy.Utility.SharedPrefManager;

import java.io.File;
import java.io.IOException;

import static android.content.Context.AUDIO_SERVICE;

public class RecordJobService {

    Context context;
    private SharedPrefManager sharedPrefManager;
    private SharedPreferences sharedPref;

    public int receiverCallCount=0;
    private static File audioFile;
    private AudioManager audiomanager;
    private String path;
    private MediaRecorder mRecorder;
    private String TAG="RecordJobService";

    public RecordJobService(Context context) {
        this.context = context;
        sharedPrefManager = new SharedPrefManager(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Log.i(TAG,"Recorder called");
        mRecorder = new MediaRecorder();
        sharedPrefManager.setIsRecordingOn(false);

        if (sharedPrefManager.getCallRecordingFlag()){
            Log.i(TAG,"Recording ON");
        }else {
            Log.i(TAG,"Recording OFF");
        }
    }

    public void startRecording(String calltype, String callID) {

        if (sharedPrefManager.getCallRecordingFlag()&& !sharedPrefManager.getIsRecordingOn()&& sharedPrefManager.getIsCallOn()){
            Log.i(TAG,"Recording Start!");
            // Extra block starts
            File sampleDir = new File(Environment.getExternalStorageDirectory(), "/onuRecords");

            path= sampleDir.getAbsolutePath();
            Log.i(TAG,"Path:"+path);
            if(!sampleDir.exists())
            {
                sampleDir.mkdirs();
                Log.i(TAG,"Directory Created!");
            }
// Audio record
//            try {
//                audioFile = File.createTempFile(callID+"abc", ".mp3", sampleDir);
//                Log.i(TAG,"Up File Path:"+audioFile.getAbsolutePath());
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//        fileLocation=Environment.getExternalStorageDirectory().getAbsolutePath();
            //extra block ended.

            try {
                audiomanager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
                audiomanager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audiomanager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audiomanager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);

                mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                mRecorder.getMaxAmplitude();
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                mRecorder.setOutputFile(audioFile.getAbsolutePath());
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mRecorder.prepare();
            } catch (IOException e) {
                Log.i(TAG, "prepare() failed: "+e.toString());
            }
            try{
                mRecorder.start();
            }catch (Exception e){
                Log.i(TAG,"Start exception: "+e.toString());
            }
            Log.i(TAG,"Recorder on called");
            sharedPrefManager.setIsRecordingOn(true);
        }
    }
    public void stopRecording(String callType, String irndID) {

        if (sharedPrefManager.getCallRecordingFlag() && sharedPrefManager.getIsRecordingOn() && !sharedPrefManager.getIsCallOn()){
            sharedPrefManager.setIsRecordingOn(false);
            Log.i(TAG,"Recording Stop!");
            try {
                mRecorder.stop();
                mRecorder.release();
                //mRecorder = null;
                mRecorder = new MediaRecorder();
            }catch (Exception e){
                Log.i(TAG,"Stop Exception: "+e.toString());
            }
            audiomanager.setMode(AudioManager.MODE_NORMAL);
            //new AudioUploader(context,irndID,callType).execute();
        }
    }
}
