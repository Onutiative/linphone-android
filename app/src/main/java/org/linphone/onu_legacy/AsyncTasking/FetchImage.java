package org.linphone.onu_legacy.AsyncTasking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Utility.Info;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 3/29/2016.
 * Edited by Sabit on 8/19/2023
 */
public class FetchImage extends AsyncTask<Void, Void, String> {
    private static final int TIMEOUT_MILLISEC = 5000;
    private Context context;
    private Info info;
    private String url;
    private String TAG = "FetchImage";

    public FetchImage(Context context) {
        this.context = context;
        info = new Info(context);
        url = info.getUrl() + "/fetchSliderImage";
        Log.i(TAG, "Image URL: " + url);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Log.i("Jhoro", "fetchImageo-3");
            String username = info.getUsername();
            String password = info.getPassword();

            Log.i("Jhoro", "fetchImageo-3.1:" + username);
            Log.i("Jhoro", "fetchImageo-4:" + password);

            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            connection.setConnectTimeout(TIMEOUT_MILLISEC);
            connection.setReadTimeout(TIMEOUT_MILLISEC);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Basic " + getBase64Credentials(username, password));
            connection.setDoOutput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.accumulate("trnxID", info.getDate("ddMMyyyyhhmmss"));
            jsonParam.accumulate("trnxTime", info.getDate("dd-MM-yyyy  hh:mm:ss"));

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonParam.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    String res = response.toString();
                    Log.i("Jhoro", "response:" + res);

                    JSONArray cast = new JSONArray(res);
                    Database db = new Database(context);
                    Log.i("Jhoro", "fetchImageo-8:");
                    db.deleteAdmin("img", "jhorotek");
                    for (int i = 0; i < cast.length(); i++) {
                        JSONObject actor = cast.getJSONObject(i);
                        db.addAdminNumber(new Contact("img", actor.getString("imageUrl"), "jhorotek"));
                        Log.i("Jhoro", "fetchImageCount-" + i);
                    }
                    db.deleteAdmin("imgcount", "jhorotek");
                    db.addAdminNumber(new Contact("imgcount", Integer.toString(cast.length()), "jhorotek"));
                }
            } else {
                Log.e("Jhoro", "HTTP Response Code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception ex) {
            Log.i("Jhoro", "fetchImageo-10 (Exception)" + ex);
        }

        return null;
    }

    private String getBase64Credentials(String username, String password) {
        String credentials = username + ":" + password;
        return Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT);
    }
}
