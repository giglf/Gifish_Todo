package tk.gifish.gifish_todo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by giglf on 2017/2/7.
 */

public class ToDoItem implements Serializable{

    private String mToDoText;
    private boolean mHasReminder;
    private int mToDoColor;
    private Date mToDoDate;
    private UUID mToDoIdentifier;

    private static final String TODOTEXT = "todotext";
    private static final String TODOREMINDER = "todoreminder";
    private static final String TODOCOLOR = "todocolor";
    private static final String TODODATE = "tododate";
    private static final String TODOIDENTIFIER = "todoidentifier";

    public ToDoItem(String todoBody, boolean hasReminder, Date toDoDate) {
        mToDoText = todoBody;
        mHasReminder = hasReminder;
        mToDoDate = toDoDate;
        mToDoColor = 0x19999D;
        mToDoIdentifier = UUID.randomUUID();
    }

    public ToDoItem(JSONObject jsonObject) throws JSONException {
        mToDoText = jsonObject.getString(TODOTEXT);
        mHasReminder = jsonObject.getBoolean(TODOREMINDER);
        mToDoColor = jsonObject.getInt(TODOCOLOR);
        mToDoIdentifier = UUID.fromString(jsonObject.getString(TODOIDENTIFIER));

        if(jsonObject.has(TODODATE)){
            mToDoDate = new Date(jsonObject.getLong(TODODATE));
        }
    }

    public ToDoItem(){
        this("Clean my room", true, new Date());
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TODOTEXT, mToDoText);
        jsonObject.put(TODOREMINDER, mHasReminder);
        if(mToDoDate!=null){
            jsonObject.put(TODODATE, mToDoDate.getTime());
        }
        jsonObject.put(TODOCOLOR, mToDoColor);
        jsonObject.put(TODOIDENTIFIER, mToDoIdentifier);
        return jsonObject;
    }

    public String getToDoText() {
        return mToDoText;
    }

    public void setToDoText(String mToDoText) {
        this.mToDoText = mToDoText;
    }

    public boolean hasReminder() {
        return mHasReminder;
    }

    public void setHasReminder(boolean mHasReminder) {
        this.mHasReminder = mHasReminder;
    }

    public int getToDoColor() {
        return mToDoColor;
    }

    public void setToDoColor(int mToDoColor) {
        this.mToDoColor = mToDoColor;
    }

    public UUID getIdentifier() {
        return mToDoIdentifier;
    }

    public Date getToDoDate() {
        return mToDoDate;
    }

    public void setmToDoDate(Date mToDoDate) {
        this.mToDoDate = mToDoDate;
    }
}
