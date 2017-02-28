package tk.gifish.gifish_todo.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import tk.gifish.gifish_todo.CustomRecyclerScrollViewListener;
import tk.gifish.gifish_todo.ItemTouchHelperClass;
import tk.gifish.gifish_todo.R;
import tk.gifish.gifish_todo.StoreRetrieveData;
import tk.gifish.gifish_todo.ToDoItem;
import tk.gifish.gifish_todo.layoutwidget.RecyclerViewEmptySupport;
import tk.gifish.gifish_todo.service.TodoNotificationService;

public class MainActivity extends AppCompatActivity {

    public static final String FILENAME = "todoitems.json";
    public static final String THEME_PREFERENCES = "tk.gifish.themepref";
    public static final String THEME_SAVED = "tk.gifish.savedtheme";
    public static final String LIGHTTHEME = "tk.gifish.lighttheme";
    public static final String DARKTHEME = "tk.gifish.darktheme";

    public static final String SHARED_PREF_DATA_SET_CHANGED = "tk.gifish.datasetchanged";
    public static final String CHANGE_OCCURED = "tk.gifish.changeoccured";
    public static final String RECREATE_ACTIVITY = "tk.gifish.recreateactivity";

    public static final String TODOITEM = "tk.gifish.tk.gifish.gifish_todo.activity.MainActivity";
    public static final int REQUEST_ID_TODO_ITEM = 100;
    public static final String DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy h:mm a";
    public static final String DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy k:mm";

    public ItemTouchHelper itemTouchHelper;

    private int mTheme = -1;
    private String theme;       //name_of_the_theme

    private BasicListAdapter adapter;
    private StoreRetrieveData storeRetrieveData;
    private ArrayList<ToDoItem> toDoItemArrayList;
    private ToDoItem justDeletedToDoItem;
    private int indexOfDeletedToDoItem;

    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton addToDoItemFAB;
    private RecyclerViewEmptySupport recyclerView;
    private CustomRecyclerScrollViewListener customRecyclerScrollViewListener;


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
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if(sharedPreferences.getBoolean(ReminderActivity.EXIT, false)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ReminderActivity.EXIT, false);
            editor.apply();
            finish();
        }

        if(getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getBoolean(RECREATE_ACTIVITY, false)){
            SharedPreferences.Editor editor = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).edit();
            editor.putBoolean(RECREATE_ACTIVITY, false);
            editor.apply();
            recreate();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if(sharedPreferences.getBoolean(CHANGE_OCCURED, false)){
            toDoItemArrayList = getLocallyStoredData(storeRetrieveData);
            adapter = new BasicListAdapter(toDoItemArrayList);
            recyclerView.setAdapter(adapter);
            setAlarms();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CHANGE_OCCURED, false);
            editor.apply();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //从文件获取主题偏好设置
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

        //从储存文件获取已添加的TODO项目
        storeRetrieveData = new StoreRetrieveData(this, FILENAME);
        toDoItemArrayList = getLocallyStoredData(storeRetrieveData);
        adapter = new BasicListAdapter(toDoItemArrayList);
        setAlarms();

        //获取一系列控件
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.myCoordinatorLayout);
        addToDoItemFAB = (FloatingActionButton)findViewById(R.id.addToDoItemFAB);

        //浮动按钮点击事件，添加新的TODO
        addToDoItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newToDo = new Intent(MainActivity.this, AddToDoActivity.class);
                ToDoItem item = new ToDoItem("", false, null);
                int color = ColorGenerator.MATERIAL.getRandomColor();
                item.setToDoColor(color);
                newToDo.putExtra(TODOITEM, item);
                startActivityForResult(newToDo, REQUEST_ID_TODO_ITEM);
            }
        });

        //RecycleView设置
        recyclerView = (RecyclerViewEmptySupport)findViewById(R.id.toDoRecyclerView);
        if(theme.equals(LIGHTTHEME)){
            recyclerView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightest));
        }
        recyclerView.setEmptyView(findViewById(R.id.toDoEmptyView));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customRecyclerScrollViewListener = new CustomRecyclerScrollViewListener() {
            @Override
            public void show() {
                addToDoItemFAB.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)addToDoItemFAB.getLayoutParams();
                int fabMargin = layoutParams.bottomMargin;
                addToDoItemFAB.animate().translationY(addToDoItemFAB.getHeight() + fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }
        };

        recyclerView.addOnScrollListener(customRecyclerScrollViewListener);
        ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
    }


    /**
     * 设置新的Alarms
     */
    private void setAlarms(){
        if(toDoItemArrayList != null){
            for(ToDoItem item : toDoItemArrayList){
                if(item.hasReminder() && item.getToDoDate()!=null){
                    if(item.getToDoDate().before(new Date())){
                        item.setmToDoDate(null);
                        continue;
                    }
                    Intent i = new Intent(this, TodoNotificationService.class);
                    i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
                    i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
                    createAlarm(i, item.getIdentifier().hashCode(), item.getToDoDate().getTime());
                }
            }
        }
    }

    public void addThemeToSharedPreferences(String theme){
        SharedPreferences sharedPreferences = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(THEME_SAVED, theme);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.aboutMeMenuItem:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
            case R.id.preferences:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * AddToDoActivity完成返回后调用，判断是否新增了TODO Item，并设置闹钟，添加到列表RecycleView中
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED && requestCode == REQUEST_ID_TODO_ITEM){
            ToDoItem item = (ToDoItem)data.getSerializableExtra(TODOITEM);
            if(item.getToDoText().length()<=0){
                return;
            }
            boolean existed = false;

            if(item.hasReminder() && item.getToDoDate() != null){
                Intent i = new Intent(this, TodoNotificationService.class);
                i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
                i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
                createAlarm(i, item.getIdentifier().hashCode(), item.getToDoDate().getTime());
            }

            for(int i=0;i<toDoItemArrayList.size();i++){
                if(item.getIdentifier().equals(toDoItemArrayList.get(i))){
                    toDoItemArrayList.set(i, item);
                    existed = true;
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
            if(!existed){
                addToDataStore(item);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            storeRetrieveData.saveToFile(toDoItemArrayList);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerView.removeOnScrollListener(customRecyclerScrollViewListener);
    }

    private AlarmManager getAlarmManager(){
        return (AlarmManager)getSystemService(ALARM_SERVICE);
    }

    private void createAlarm(Intent i, int requestCode, long timeInMillis){
        AlarmManager alarmManager = getAlarmManager();
        PendingIntent pendingIntent = PendingIntent.getService(this, requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
    }

    private void deleteAlarm(Intent i, int requestCode){
        if(doesPendingIntentExist(i, requestCode)){
            PendingIntent pendingIntent = PendingIntent.getService(this, requestCode, i, PendingIntent.FLAG_NO_CREATE);
            pendingIntent.cancel();
            getAlarmManager().cancel(pendingIntent);
            Log.d("giglfDebugTag", "PI Cancelled" + doesPendingIntentExist(i,requestCode));
        }
    }

    private boolean doesPendingIntentExist(Intent i, int requestCode){
        PendingIntent pendingIntent = PendingIntent.getService(this, requestCode, i, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    private void addToDataStore(ToDoItem item){
        toDoItemArrayList.add(item);
        adapter.notifyItemInserted(toDoItemArrayList.size()-1);
    }

    private void saveDate(){
        try{
            storeRetrieveData.saveToFile(toDoItemArrayList);
        } catch (Exception e){
            e.printStackTrace();
        }
    }



//--------------------------------------An inner class-----------------------
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
                    timeToShow = AddToDoActivity.formatDate(MainActivity.DATE_TIME_FORMAT_24_HOUR, item.getToDoDate());
                } else{
                    timeToShow = AddToDoActivity.formatDate(MainActivity.DATE_TIME_FORMAT_12_HOUR, item.getToDoDate());
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
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRemoved(int position) {

            justDeletedToDoItem = items.remove(position);
            indexOfDeletedToDoItem = position;
            Intent i = new Intent(MainActivity.this, TodoNotificationService.class);
            deleteAlarm(i, justDeletedToDoItem.getIdentifier().hashCode());
            notifyItemRemoved(position);

            String toShow = "Todo";
            Snackbar.make(coordinatorLayout, "Deleted " + toShow, Snackbar.LENGTH_SHORT)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            items.add(indexOfDeletedToDoItem, justDeletedToDoItem);
                            if (justDeletedToDoItem.getToDoDate()!=null && justDeletedToDoItem.hasReminder()){
                                Intent intent = new Intent(MainActivity.this, TodoNotificationService.class);
                                intent.putExtra(TodoNotificationService.TODOTEXT, justDeletedToDoItem.getToDoText());
                                intent.putExtra(TodoNotificationService.TODOUUID, justDeletedToDoItem.getIdentifier());
                                createAlarm(intent, justDeletedToDoItem.getIdentifier().hashCode(), justDeletedToDoItem.getToDoDate().getTime());
                            }
                            notifyItemInserted(indexOfDeletedToDoItem);
                        }
                    }).show();

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
                        Intent i = new Intent(MainActivity.this, AddToDoActivity.class);
                        i.putExtra(TODOITEM, item);
                        startActivityForResult(i, REQUEST_ID_TODO_ITEM);
                    }
                });
                toDoTextView = (TextView)v.findViewById(R.id.toDoListItemTextView);
                timeTextView = (TextView)v.findViewById(R.id.toDoListItemTimeTextView);
                colorImageView = (ImageView)v.findViewById(R.id.toDoListItemColorImageView);
                linearLayout = (LinearLayout)v.findViewById(R.id.listItemLinearLayout);


            }

        }

    }



}
