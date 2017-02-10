package tk.gifish.gifish_todo;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Created by giglf on 2017/2/7.
 */

public class Utils {

    public static int getToolbarHeight(Context context){
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        int toobarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toobarHeight;
    }

}
