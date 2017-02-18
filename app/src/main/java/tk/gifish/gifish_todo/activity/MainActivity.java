package tk.gifish.gifish_todo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import tk.gifish.gifish_todo.ItemTouchHelperClass;
import tk.gifish.gifish_todo.R;
import tk.gifish.gifish_todo.StoreRetrieveData;
import tk.gifish.gifish_todo.ToDoItem;
import tk.gifish.gifish_todo.service.TodoNotificationService;

public class MainActivity extends AppCompatActivity {

    public static final String FILENAME = "todoitems.json";
    public static final String THEME_PREFERENCES = "tk.gifish.gifish_todo.themepref";
    public static final String THEME_SAVED = "tk.gifish.gifish_todo.savedtheme";
    public static final String LIGHTTHEME = "tk.gifish.gifish_todo.lighttheme";
    public static final String DARKTHEME = "tk.gifish.gifish_todo.darktheme";

    public static final String SHARED_PREF_DATA_SET_CHANGED = "tk.gifish.gifish_todo.datasetchanged";
    public static final String CHANGE_OCCURED = "tk.gifish.gifish_todo.changeoccured";

    public static final String TODOITEM = "tk.gifish.gifish_todo.tk.gifish.gifish_todo.activity.MainActivity";
    public static final int REQUEST_ID_TODO_ITEM = 100;

    private int mTheme = -1;
    private String theme;       //name_of_the_theme

    private StoreRetrieveData storeRetrieveData;
    private ArrayList<ToDoItem> toDoItemArrayList;
    private ToDoItem justDeletedToDoItem;
    private int indexOfDeletedToDoItem;


    public static ArrayList<ToDoItem> getLocallyStoredData(StoreRetrieveData storeRetrieveData){
        ArrayList<ToDoItem> items = null;
        try {
            items = storeRetrieveData.loadFromFile();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return items==null? new ArrayList<ToDoItem>() : items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        theme = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME);

        if(theme.equals(LIGHTTHEME)){
            mTheme = R.style.CustomStyle_LightTheme;
        } else{
            mTheme = R.style.CustomStyle_DarkTheme;
        }
        this.setTheme(mTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CHANGE_OCCURED, false);
        editor.apply();

        storeRetrieveData = new StoreRetrieveData(this, FILENAME);
        toDoItemArrayList = getLocallyStoredData(storeRetrieveData);

    }


    public class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter{

        private ArrayList<ToDoItem> items;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_circle_try, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ToDoItem item = items.get(position);

            SharedPreferences sharedPreferences = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE);
            int bgColor;
            int todoTextColor;

            if(sharedPreferences.getString(THEME_SAVED, LIGHTTHEME).equals(LIGHTTHEME)){
                bgColor = Color.WHITE;
                todoTextColor = getResources().getColor(R.color.secondary_text);
            } else{
                bgColor = Color.DKGRAY;
                todoTextColor = Color.WHITE;
            }
            holder.linearLayout.setBackgroundColor(bgColor);

            if (item.hasReminder() && item.getToDoDate()!=null){
                holder.toDoTextView.setMaxLines(1);
                holder.timeTextView.setVisibility(View.VISIBLE);
            } else{
                holder.timeTextView.setVisibility(View.GONE);
                holder.toDoTextView.setMaxLines(2);
            }
            holder.toDoTextView.setText(item.getToDoText());
            holder.toDoTextView.setTextColor(todoTextColor);

            TextDrawable myDrawable = TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(item.getToDoText().substring(0,1), item.getToDoColor());

            holder.colorImageView.setImageDrawable(myDrawable);
            if(item.getToDoDate()!=null){
                String timeToShow;
                if(android.text.format.DateFormat.is24HourFormat(MainActivity.this)){

                } else{

                }
                holder.timeTextView.setText(timeToShow);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        BasicListAdapter(ArrayList<ToDoItem> items){
            this.items = items;
        }

        @Override
        public void onItemMoved(int fromPosition, int toPosition) {
            //I think here can be optimized the algorithm
            if(fromPosition<toPosition){
                for(int i=fromPosition;i<toPosition;i++){
                    Collections.swap(items, i, i+1);
                }
            } else{
                for(int i=toPosition;i<fromPosition;i++){
                    Collections.swap(items, i, i+1);
                }
            }
        }

        @Override
        public void onItemRemoved(int position) {

            justDeletedToDoItem = items.remove(position);
            Intent i = new Intent(MainActivity.this, TodoNotificationService.class);

        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            View view;
            LinearLayout linearLayout;
            TextView toDoTextView;
            ImageView colorImageView;
            TextView timeTextView;

            public ViewHolder(View v){
                super(v);
                view = v;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToDoItem item = items.get(ViewHolder.this.getAdapterPosition());
                        Intent i = new Intent(MainActivity.this, AddToDoActivity.class);  //AddToDoActivity need to be defined
                        i.putExtra(TODOITEM, item);
                        startActivityForResult(i, REQUEST_ID_TODO_ITEM);
                    }
                });
                toDoTextView = (TextView)v.findViewById(R.id.toDoListItemTextView);
                timeTextView = (TextView)v.findViewById(R.id.todoListItemTimeTextView);
                colorImageView = (ImageView)v.findViewById(R.id.toDoListItemColorImageView);
                linearLayout = (LinearLayout)v.findViewById(R.id.listItemLinearLayout);


            }

        }

    }



}
