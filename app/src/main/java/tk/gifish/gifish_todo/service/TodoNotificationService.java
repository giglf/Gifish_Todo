package tk.gifish.gifish_todo.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.UUID;

import tk.gifish.gifish_todo.R;
import tk.gifish.gifish_todo.activity.ReminderActivity;


public class TodoNotificationService extends IntentService {

    public static final String TODOTEXT = "tk.gifish.todonotificationservicetext";
    public static final String TODOUUID = "tk.gifish.todonotificationserviceuuid";

    private String mTodoText;
    private UUID mTodoUUID;
    private Context mContext;

    public TodoNotificationService() {
        super("TodoNotificationService");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(Intent intent) {
        mTodoText = intent.getStringExtra(TODOTEXT);
        mTodoUUID = (UUID) intent.getSerializableExtra(TODOUUID);

        Log.d("giglfGebugTag", "onHandleIntent called");
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent i = new Intent(this, ReminderActivity.class);
        i.putExtra(TodoNotificationService.TODOUUID, mTodoUUID);
        Intent deleteIntent = new Intent(this, DeleteNotificationService.class);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(mTodoText)
                .setSmallIcon(R.drawable.ic_done_white_24dp)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDeleteIntent(PendingIntent.getService(this, mTodoUUID.hashCode(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentIntent(PendingIntent.getActivity(this, mTodoUUID.hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT))
                .build();

        manager.notify(100, notification);
    }
}
