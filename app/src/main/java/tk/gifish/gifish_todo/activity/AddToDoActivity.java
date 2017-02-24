package tk.gifish.gifish_todo.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tk.gifish.gifish_todo.R;
import tk.gifish.gifish_todo.ToDoItem;

public class AddToDoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Date lastEdited;
    private EditText toDoTextBodyEditText;
    private LinearLayout userDateSpinnerContainingLinearLayout;
    private TextView reminderTextView;
    private SwitchCompat toDoDateSwitch;

    private EditText dateEditText;
    private EditText timeEditText;
    private String defaultTimeOptions12H;
    private String defaultTimeOptions24H;

    private ToDoItem userToDoItem;
    private Button chooseDateButton;
    private Button chooseTimeButton;
    private FloatingActionButton toDoSendFloatingActionButton;
    public static final String DATE_FORMAT = "MMM d, yyyy";
    public static final String DATE_FORMAT_MONTH_DAY = "MMM d";
    public static final String DATE_FORMAT_TIME = "H:m";

    private LinearLayout containerLayout;
    private boolean setDateButtonClickedOnce = false;
    private boolean setTimeButtonClickedOnce = false;
    private String userEnteredText;
    private boolean userHasReminder;
    private Date userReminderDate;
    private int userColor;
    private Toolbar toolbar;
    private String theme;

    public static String formatDate(String formatString, Date dateToFormat){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString);
        return simpleDateFormat.format(dateToFormat);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ImageButton reminderIconImageButton;
        TextView reminderRemindMeTextView;

        theme = getSharedPreferences(MainActivity.THEME_PREFERENCES, MODE_PRIVATE).getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME);
        if(theme.equals(MainActivity.LIGHTTHEME)){
            setTheme(R.style.CustomStyle_LightTheme);
            Log.d("giglfDebugTag", "Light Theme");
        } else{
            setTheme(R.style.CustomStyle_DarkTheme);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_todo_test);

        final Drawable cross = getResources().getDrawable(R.drawable.ic_clear_white_24dp);
        if(cross!=null){
            cross.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        }

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ActionBar的设置
        if(getSupportActionBar()!=null){
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(cross);
        }

        userToDoItem = (ToDoItem)getIntent().getSerializableExtra(MainActivity.TODOITEM);

        userEnteredText = userToDoItem.getToDoText();
        userHasReminder = userToDoItem.hasReminder();
        userReminderDate = userToDoItem.getToDoDate();
        userColor = userToDoItem.getToDoColor();

        reminderIconImageButton = (ImageButton)findViewById(R.id.userToDoReminderIconImageButton);
        reminderRemindMeTextView = (TextView)findViewById(R.id.userToDoRemindMeTextView);

        if(theme.equals(MainActivity.DARKTHEME)){
            reminderIconImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_alarm_add_white_24dp));
            reminderRemindMeTextView.setTextColor(Color.WHITE);
        }

        containerLayout = (LinearLayout)findViewById(R.id.todoReminderAndDateContainerLayout);
        userDateSpinnerContainingLinearLayout = (LinearLayout)findViewById(R.id.toDoEnterDateLinearLayout);
        toDoTextBodyEditText = (EditText)findViewById(R.id.userToDoEditText);
        toDoDateSwitch = (SwitchCompat)findViewById(R.id.toDoHasDateSwitchCompat);
        toDoSendFloatingActionButton = (FloatingActionButton)findViewById(R.id.makeToDoFloatingActionButton);
        reminderTextView = (TextView)findViewById(R.id.newToDoDateTimeReminderTextView);

        containerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyBoard(toDoTextBodyEditText);
            }
        });

        if(userHasReminder && userReminderDate!=null){
            setReminderTextView();
            setEnterDateLayoutVisibleWithAnimations(true);
        }

        if(userReminderDate == null){
            toDoDateSwitch.setChecked(false);
            reminderTextView.setVisibility(View.INVISIBLE);
        }

        toDoTextBodyEditText.requestFocus();
        toDoTextBodyEditText.setText(userEnteredText);
        InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        toDoTextBodyEditText.setSelection(toDoTextBodyEditText.length());

        toDoTextBodyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { //使userEnteredText即时获得editText的输入内容
                userEnteredText = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //设置选择按钮
        setEnterDateLayoutVisible(toDoDateSwitch.isChecked());
        toDoDateSwitch.setChecked(userHasReminder && (userReminderDate != null));
        toDoDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    userReminderDate = null;
                }
                userHasReminder = isChecked;
                setDateAndTimeEditText();
                setEnterDateLayoutVisibleWithAnimations(isChecked);
                hideKeyBoard(toDoTextBodyEditText);

            }
        });

        toDoSendFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userReminderDate!=null && userReminderDate.before(new Date())){
                    makeResult(RESULT_CANCELED);
                } else{
                    makeResult(RESULT_OK);
                }
                hideKeyBoard(toDoTextBodyEditText);
                finish();
            }
        });

        dateEditText = (EditText)findViewById(R.id.newTodoDateEditText);
        timeEditText = (EditText)findViewById(R.id.newTodoTimeEditText);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date;
                hideKeyBoard(toDoTextBodyEditText);
                if(userToDoItem.getToDoDate()!=null){
                    date = userReminderDate;
                } else{
                    date = new Date();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AddToDoActivity.this, year, month, day);
                if(theme.equals(MainActivity.DARKTHEME)){
                    datePickerDialog.setThemeDark(true);
                }
                datePickerDialog.show(getFragmentManager(), "DateFragment");
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date;
                hideKeyBoard(toDoTextBodyEditText);
                if(userToDoItem.getToDoDate()!=null){
                    date = userReminderDate;
                } else{
                    date = new Date();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(AddToDoActivity.this, hour, minute, DateFormat.is24HourFormat(AddToDoActivity.this));
                if(theme.equals(MainActivity.DARKTHEME)){
                    timePickerDialog.setThemeDark(true);
                }
                timePickerDialog.show(getFragmentManager(), "TimeFragment");
            }
        });

        setDateAndTimeEditText();

    }

    @Override
    public void onBackPressed() {
        if(userReminderDate.before(new Date())){
            userToDoItem.setmToDoDate(null);
        }
        makeResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(NavUtils.getParentActivityName(this)!=null){
                    makeResult(RESULT_CANCELED);
                    NavUtils.navigateUpFromSameTask(this);
                }
                hideKeyBoard(toDoTextBodyEditText);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 确认TODO item的操作
     * @param result
     */
    public void makeResult(int result){
        Intent i = new Intent();
        if(userEnteredText.length()>0){
            String capitalizedString = Character.toUpperCase(userEnteredText.charAt(0)) + userEnteredText.substring(1);
            userToDoItem.setToDoText(capitalizedString);
        } else {
            userToDoItem.setToDoText(userEnteredText);
        }

        if(userReminderDate!=null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(userReminderDate);
            calendar.set(Calendar.SECOND, 0);
            userReminderDate = calendar.getTime();
        }
        userToDoItem.setHasReminder(userHasReminder);
        userToDoItem.setmToDoDate(userReminderDate);
        userToDoItem.setToDoColor(userColor);
        i.putExtra(MainActivity.TODOITEM, userToDoItem);
        setResult(result, i);
    }

    public void hideKeyBoard(EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void setReminderTextView(){
        if(userReminderDate!=null){
            reminderTextView.setVisibility(View.VISIBLE);
            if(userReminderDate.before(new Date())){
                Log.d("giglfDebugTag", "DATE is " + userReminderDate);
                reminderTextView.setText(getString(R.string.date_error_check_again));
                reminderTextView.setTextColor(Color.RED);
                return;
            }
            Date date = userReminderDate;
            String dateString = formatDate("d MMM, yyyy", date);
            String timeString;
            String amPmString = "";

            if(DateFormat.is24HourFormat(this)){
                timeString = formatDate("k:mm", date);
            } else{
                timeString = formatDate("h:mm", date);
                amPmString = formatDate("a", date);
            }
            String finalString = String.format(getResources().getString(R.string.remind_date_and_time), dateString, timeString, amPmString);
            reminderTextView.setTextColor(getResources().getColor(R.color.secondary_text));
            reminderTextView.setText(finalString);
        } else{
            reminderTextView.setVisibility(View.INVISIBLE);
        }
    }

    public void setEnterDateLayoutVisible(boolean checked){
        if(checked){
            userDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
        } else{
            userDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置日期显示的动画特效，淡入淡出
     * @param checked
     */
    public void setEnterDateLayoutVisibleWithAnimations(boolean checked){
        if(checked){
            setReminderTextView();
            userDateSpinnerContainingLinearLayout.animate().alpha(1.0f).setDuration(500).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    userDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {}

                @Override
                public void onAnimationCancel(Animator animator) {}

                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
        } else{
            userDateSpinnerContainingLinearLayout.animate().alpha(0.0f).setDuration(500).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {}

                @Override
                public void onAnimationEnd(Animator animator) {
                    userDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {}

                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
        }
    }

    public void setDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        int hour, minute;

        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.set(year, month, day);

        if(reminderCalendar.before(calendar)){
            Toast.makeText(this, "My time-machine is a bit rusty", Toast.LENGTH_SHORT).show();
            return;
        }

        if(userReminderDate!=null){
            calendar.setTime(userReminderDate);
        }

        if(DateFormat.is24HourFormat(this)){
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        } else{
            hour = calendar.get(Calendar.HOUR);
        }
        minute = calendar.get(Calendar.MINUTE);

        calendar.set(year, month, day, hour, minute);
        userReminderDate = calendar.getTime();
        setReminderTextView();
        setDateEditText();
    }

    public void setDateEditText(){
        dateEditText.setText(formatDate("d MMM, yyyy", userReminderDate));
    }

    public void setTimeEditText(){
        String dateFormat;
        if(DateFormat.is24HourFormat(this)){
            dateFormat = "k:mm";
        } else{
            dateFormat = "h:mm a";
        }
        timeEditText.setText(formatDate(dateFormat, userReminderDate));
    }


    public void setTime(int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        if(userReminderDate!=null){
            calendar.setTime(userReminderDate);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("giglfDebugTag", "Time set: " + hour);
        calendar.set(year, month, day, hour, minute, 0);
        userReminderDate = calendar.getTime();

        setReminderTextView();
        setTimeEditText();
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        setDate(year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        setTime(hourOfDay, minute);
    }

    private void setDateAndTimeEditText(){

        if(userToDoItem.hasReminder() && userReminderDate!=null){
            String userDate = formatDate("d MMM, yyyy", userReminderDate);
            String formatToUse;
            if(DateFormat.is24HourFormat(this)){
                formatToUse = "k:mm";
            } else{
                formatToUse = "h:mm a";
            }
            String userTime = formatDate(formatToUse, userReminderDate);
            timeEditText.setText(userTime);
            dateEditText.setText(userDate);
        } else{
            dateEditText.setText(getString(R.string.date_reminder_default));

            boolean time24 = DateFormat.is24HourFormat(this);
            //默认设置定时时间为下一个整点
            Calendar calendar = Calendar.getInstance();
            if(time24){
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)+1);
            } else{
                calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR)+1);
            }
            calendar.set(Calendar.MINUTE, 0);
            userReminderDate = calendar.getTime();

            Log.d("giglfDebugTag", "Imagined Date: " + userReminderDate);
            String timeString;
            if(time24){
                timeString = formatDate("k:mm", userReminderDate);
            } else {
                timeString = formatDate("h:mm a", userReminderDate);
            }
            timeEditText.setText(timeString);
        }
    }


}
