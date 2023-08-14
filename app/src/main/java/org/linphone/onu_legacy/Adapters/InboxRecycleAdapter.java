package org.linphone.onu_legacy.Adapters;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.TaskConversion;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.R;
import org.linphone.onu_legacy.Utility.AnimationUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by mamba on 3/14/2016.
 */
public class InboxRecycleAdapter extends RecyclerView.Adapter <InboxRecycleAdapter.Holder>{
    //ArrayList<Fruit> arrayList=new ArrayList<>();
    Cursor cursor;
    Context context;
    private int mPreviousPosition = 0;
    private Long timestamp;
    private TaskConversion taskConversion;

    private String TAG = "InboxRecycleAdapte";

    public InboxRecycleAdapter(Cursor cur, Context con)
    {
        this.cursor=cur;
        this.context=con;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_recent_inbox2,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        cursor.moveToPosition(position);
        int columnIndex = cursor.getColumnIndex("address");
        if (columnIndex != -1) {
            String address = cursor.getString(columnIndex);
            holder.index.setText(address);
        }

        columnIndex = cursor.getColumnIndex("body");
        if (columnIndex != -1) {
            String body = cursor.getString(columnIndex);
            holder.surah.setText(body);
        }

        columnIndex = cursor.getColumnIndex("date");
        if (columnIndex != -1) {
            String date = cursor.getString(columnIndex);
            timestamp = Long.parseLong(date);
            Log.i(TAG,"Time: "+timestamp);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");


        String smsTime=dateFormat.format(calendar.getTime());

        holder.lines.setText(smsTime);


        if (position > mPreviousPosition) {
            AnimationUtils.animateSunblind(holder, true);
        } else {
            AnimationUtils.animateSunblind(holder, false);
        }
        mPreviousPosition = position;

    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener

    {
        TextView index,surah,lines;
        TextView checkText;
        ImageView imageViewDot;
        public Holder(View itemView)
        {
            super(itemView);
            index= (TextView) itemView.findViewById(R.id.t1);
            surah= (TextView) itemView.findViewById(R.id.t2);
            lines= (TextView) itemView.findViewById(R.id.t3);

            imageViewDot=(ImageView)itemView.findViewById(R.id.imageview_dot);

            imageViewDot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog dialog = new AlertDialog.Builder(v.getContext()).create();
                    dialog.setTitle(index.getText().toString());
                    dialog.setIcon(R.drawable.onukit_logo2);
                    dialog.setMessage(surah.getText().toString());

                    final Context context=v.getContext();

                    dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "SET AS TASK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            taskConversion=new TaskConversion(context);
                            Log.i(TAG,"clicked");
                            taskConversion.taskMaking(index.getText().toString(),surah.getText().toString(),getDate(timestamp),"","inbox");
                            dialog.dismiss();
                            return;
                        }
                    });

                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "POST",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "Post Clicked");
                                    Database db = new Database(context);
                                    db.addsms(new Contact(surah.getText().toString(), index.getText().toString(),String.valueOf(timestamp)));
                                    Log.i(TAG,surah.getText().toString()+"; "+ index.getText().toString()+ "; "+lines.getText().toString());
                                    dialog.dismiss();
                                    return;
                                }
                            });
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Log.i(TAG,"Cancel clicked");
                                    dialog.dismiss();
                                    return;
                                }
                            });
                    dialog.show();

                }
            });

//            checkText=(TextView)itemView.findViewById(R.id.check_text);
//            surah.setOnClickListener(this);

            surah.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(surah.getText());
                // Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        }

        // This onClick function is not working because  - "surah.setOnClickListener(this);" is commented out.

        @Override
        public void onClick(View v) {
            //not working
            // Show The Dialog with Selected SMS
            AlertDialog dialog = new AlertDialog.Builder(v.getContext()).create();
            dialog.setTitle(index.getText().toString());
            dialog.setIcon(R.drawable.onukit_logo2);
            dialog.setMessage(surah.getText().toString());

             final Context context=v.getContext();
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "POST",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Database db = new Database(context);
                            db.addsms(new Contact(surah.getText().toString(), index.getText().toString(), lines.getText().toString()));
                            //Log.i(TAG, "Post Clicked");
                            //Log.i(TAG,surah.getText().toString()+"; "+ index.getText().toString()+ "; "+lines.getText().toString());
                            dialog.dismiss();
                            return;
                        }
                    });
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Log.i(TAG,"Cancel clicked");
                            dialog.dismiss();
                            return;
                        }
                    });
            dialog.show();

        }
    }

    private String getDate(long time){
        long timeInMillis = time;
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(timeInMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(cal1.getTime());
    }
}
