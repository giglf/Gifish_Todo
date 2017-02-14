package tk.gifish.gifish_todo;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by giglf on 2017/2/12.
 */

public class StoreRetrieveData {

    private Context mContext;
    private String mFileName;

    public StoreRetrieveData(Context context, String filename){
        mContext = context;
        mFileName = filename;
    }

    public static JSONArray toJSONArry(ArrayList<ToDoItem> items) throws JSONException{
        JSONArray jsonArray = new JSONArray();
        for(ToDoItem item : items){
            JSONObject jsonObject = item.toJSON();
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }


    public void saveToFile(ArrayList<ToDoItem> items) throws IOException, JSONException {
        FileOutputStream fileOutputStream = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        outputStreamWriter.write(toJSONArry(items).toString());
        outputStreamWriter.close();
        fileOutputStream.close();
    }

    public ArrayList<ToDoItem> loadFromFile() throws IOException, JSONException {
        ArrayList<ToDoItem> items = new ArrayList<>();
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream = null;
        try{
            fileInputStream = mContext.openFileInput(mFileName);
            StringBuilder builder = new StringBuilder();
            String line;
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            while((line = bufferedReader.readLine())!=null){
                builder.append(line);
            }

            JSONArray jsonArray = (JSONArray) new JSONTokener(builder.toString()).nextValue();
            for(int i=0;i<jsonArray.length();i++){
                ToDoItem item = new ToDoItem(jsonArray.getJSONObject(i));
                items.add(item);
            }

        } catch (FileNotFoundException e){

        } finally {
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if (fileInputStream!=null){
                fileInputStream.close();
            }
        }
        return items;
    }




}
