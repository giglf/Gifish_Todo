package tk.gifish.gifish_todo.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import tk.gifish.gifish_todo.StoreRetrieveData;
import tk.gifish.gifish_todo.ToDoItem;
import tk.gifish.gifish_todo.activity.MainActivity;

/**
 * Created by giglf on 2017/2/18.
 */

public class DeleteNotificationService extends IntentService {

    private StoreRetrieveData storeRetrieveData;
    private ArrayList<ToDoItem> mToDoItems;
    private ToDoItem mItem;

    public DeleteNotificationService(){
        super("DeleteNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        storeRetrieveData = new StoreRetrieveData(this, MainActivity.FILENAME);
        UUID todoID = (UUID) intent.getSerializableExtra(TodoNotificationService.TODOUUID);

        mToDoItems = loadData();
        if(mToDoItems!=null){
            for(ToDoItem item : mToDoItems){
                if(item.getIdentifier().equals(todoID)){
                    mItem = item;
                    break;
                }
            }
            if(mItem!=null){
                mToDoItems.remove(mItem);
                dataChanged();
                saveData();
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveData();
    }

    private void dataChanged(){
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MainActivity.CHANGE_OCCURED, true);
        editor.apply();
    }

    private void saveData(){
        try{
            storeRetrieveData.saveToFile(mToDoItems);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private ArrayList<ToDoItem> loadData(){
        try {
            return storeRetrieveData.loadFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
