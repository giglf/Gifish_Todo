package tk.gifish.gifish_todo;

import android.content.res.Resources;

/**
 * Created by giglf on 2017/2/18.
 */

public class PreferenceKeys {

    private final String night_mode_pref_key;

    public PreferenceKeys(Resources resources){
        night_mode_pref_key = resources.getString(R.string.night_mode_pref_key);
    }

    public String getNight_mode_pref_key() {
        return night_mode_pref_key;
    }
}
