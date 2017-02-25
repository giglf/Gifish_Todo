package tk.gifish.gifish_todo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;
import tk.gifish.gifish_todo.R;
import tk.gifish.gifish_todo.StoreRetrieveData;
import tk.gifish.gifish_todo.ToDoItem;
import tk.gifish.gifish_todo.service.TodoNotificationService;

/**
 * Created by giglf on 2017/2/18.
 */

public class ReminderActivity extends AppCompatActivity{

    private ToDoItem item;
    private ArrayList<ToDoItem> toDoItems;
    private StoreRetrieveData storeRetrieveData;

    private String[] snoozeOptionsArray;
    private TextView toDoTextTextView;
    private Button removeToDoButton;
    private MaterialSpinner snoozeSpinner;
    private TextView snoozeTextView;

    String theme;

    public static final String EXIT = "tk.gifish.exit";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        theme = getSharedPreferences(MainActivity.THEME_PREFERENCES, MODE_PRIVATE).getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME);

        if(theme.equals(MainActivity.LIGHTTHEME)){
            setTheme(R.style.CustomStyle_LightTheme);
        } else{
            setTheme(R.style.CustomStyle_DarkTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_layout);

        storeRetrieveData = new StoreRetrieveData(this, MainActivity.FILENAME);
        toDoItems = MainActivity.getLocallyStoredData(storeRetrieveData);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        Intent i = getIntent();
        UUID id = (UUID)i.getSerializableExtra(TodoNotificationService.TODOUUID);
        item = null;
        for(ToDoItem toDoItem : toDoItems){
            if(toDoItem.getIdentifier().equals(id)){
                item = toDoItem;
                break;
            }
        }

        snoozeOptionsArray = getResources().getStringArray(R.array.snooze_options);
        removeToDoButton = (Button)findViewById(R.id.toDoReminderRemoveButton);
        toDoTextTextView = (TextView)findViewById(R.id.toDoReminderTextViewBody);
        snoozeTextView = (TextView)findViewById(R.id.reminderViewSnoozeTextView);
        snoozeSpinner = (MaterialSpinner)findViewById(R.id.todoReminderSnoozeSpinner);

        toDoTextTextView.setText(item.getToDoText());

        if(theme.equals(MainActivity.LIGHTTHEME)){
            snoozeTextView.setTextColor(getResources().getColor(R.color.secondary_text));
        } else{
            snoozeTextView.setTextColor(Color.WHITE);
            snoozeTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_snooze_white_24dp, 0, 0, 0);
        }

        removeToDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDoItems.remove(item);
                changeOccurred();
                saveData();
                closeApp();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text_view, snoozeOptionsArray);

        adapter.setDropDownViewResource(R.layout.spinner_dropdowm_item);
        snoozeSpinner.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toDoReminderDoneMenuItem:
                Date date = addTimeToDate(valueFromSpinner());
                this.item.setmToDoDate(date);
                this.item.setHasReminder(true);
                Log.d("giglfDebugTag", "Date Changed to: " + date);
                changeOccurred();
                saveData();
                closeApp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Date addTimeToDate(int mins){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, mins);
        return calendar.getTime();
    }

    private int valueFromSpinner(){
        switch (snoozeSpinner.getSelectedItemPosition()){
            case 0:
                return 10;
            case 1:
                return 30;
            case 2:
                return 60;
            default:
                return 0;
        }
    }

    private void changeOccurred(){
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MainActivity.CHANGE_OCCURED, true);
        editor.apply();
    }

    private void saveData(){
        try {
            storeRetrieveData.saveToFile(toDoItems);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void closeApp(){
        Intent i = new Intent(ReminderActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(EXIT, true);
        editor.apply();
        startActivity(i);
    }

}
