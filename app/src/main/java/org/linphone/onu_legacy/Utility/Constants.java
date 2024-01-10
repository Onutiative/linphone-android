package org.linphone.onu_legacy.Utility;

/**
 * Created by Android on 12/5/2016.
 */

public class Constants {

    public static final String TAG = "Callrecorder";

    public static final String FILE_DIRECTORY = "recordedCalls";
    public static final String LISTEN_ENABLED = "ListenEnabled";
    public static final String FILE_NAME_PATTERN = "^d[\\d]{14}p[_\\d]*\\.3gp$";

    public static final int MEDIA_MOUNTED = 0;
    public static final int MEDIA_MOUNTED_READ_ONLY = 1;
    //ore
    public static final int NO_MEDIA = 2;

    public static final int STATE_INCOMING_NUMBER = 1;
    public static final int STATE_CALL_START = 2;
    public static final int STATE_CALL_END = 3;
    public static final int STATE_START_RECORDING = 4;
    public static final int STATE_STOP_RECORDING = 5;
    public static final int RECORDING_ENABLED = 6;
    public static final int RECORDING_DISABLED = 7;



    //ALl SMS STATUS CODES
    public static final int PROCESSED=0;
    public static final int PENDING=-13;
    public static final int SMS_SUBMMIT=1;
    public static final int SENT_RESULT_OK=2;
    public static final int SENT_RESULT_ERROR_GENERIC_FAILURE=-2;
    public static final int SENT_RESULT_ERROR_RADIO_OFF=-3;
    public static final int SENT_RESULT_ERROR_NULL_PDU=-5;
    public static final int SENT_RESULT_ERROR_NO_SERVICE=-6;

    public static final int DELIVERY_RESULT_OK=4;
    public static final int DELIVERY_RESULT_CANCELED=-4;



}