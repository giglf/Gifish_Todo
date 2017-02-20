package tk.gifish.gifish_todo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by giglf on 2017/2/18.
 */

public abstract class CustomRecyclerScrollViewListener extends RecyclerView.OnScrollListener{

    int scrollDist = 0;
    boolean isVisible = true;
    static final float MINIMUM = 20;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if(isVisible && scrollDist > MINIMUM){
            Log.d("giglfDebugTag", "Hide " + scrollDist);
            hide();
            scrollDist = 0;
            isVisible = false;
        } else if(!isVisible && scrollDist < -MINIMUM){
            Log.d("giglfDebugTag", "Show " + scrollDist);
            show();
            scrollDist = 0;
            isVisible = true;
        }

        if((isVisible && dy > 0) || (!isVisible && dy < 0)){
            Log.d("giglfDebugTag", "Add Up" + scrollDist);
            scrollDist += dy;
        }
    }

    public abstract void show();
    public abstract void hide();
}
