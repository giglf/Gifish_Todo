package tk.gifish.gifish_todo.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.UUID;


public class TodoNotificationService extends IntentService {

    public static final String TODOTEXT = "tk.gifish.gifish_todo.todonotificationservicetext";
    public static final String TODOUUID = "tk.gifish.gifish_todo.todonotificationserviceuuid";

    private String mTodoText;
    private UUID mTodoUUID;
    private Context mContext;

    public TodoNotificationService() {
        super("TodoNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mTodoText = intent.getStringExtra(TODOTEXT);
        mTodoUUID = (UUID) intent.getSerializableExtra(TODOUUID);

        Log.d("giglfGebugTag", "onHandleIntent called");
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //Intent i = new Intent(this)
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
