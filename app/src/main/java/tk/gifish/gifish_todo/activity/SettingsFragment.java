package tk.gifish.gifish_todo.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;

import tk.gifish.gifish_todo.PreferenceKeys;
import tk.gifish.gifish_todo.R;

/**
 * Created by giglf on 2017/2/18.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_layout);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PreferenceKeys preferenceKeys = new PreferenceKeys(getResources());
        if(key.equals(preferenceKeys.getNight_mode_pref_key())){
            SharedPreferences themePreferences = getActivity().getSharedPreferences(MainActivity.THEME_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor themeEditor = themePreferences.edit();

            themeEditor.putBoolean(MainActivity.RECREATE_ACTIVITY, true);
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference)findPreference(preferenceKeys.getNight_mode_pref_key());
            if(checkBoxPreference.isChecked()){
                themeEditor.putString(MainActivity.THEME_SAVED, MainActivity.DARKTHEME);
            } else{
                themeEditor.putString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME);
            }
            themeEditor.apply();

            getActivity().recreate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
