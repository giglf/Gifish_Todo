package tk.gifish.gifish_todo.layoutwidget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by giglf on 2017/2/10.
 */

public class RecyclerViewEmptySupport extends RecyclerView {

    private View emptyView;

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            showEmptyView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            showEmptyView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            showEmptyView();
        }
    };

    public RecyclerViewEmptySupport(Context context){
        super(context);
    }

    public RecyclerViewEmptySupport(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public RecyclerViewEmptySupport(Context context, AttributeSet attributeSet, int defStyle){
        super(context, attributeSet, defStyle);
    }

    public void showEmptyView(){
        RecyclerView.Adapter<?> adapter = getAdapter();
        if(adapter!=null && emptyView !=null) {
            if (adapter.getItemCount() == 0) {
                emptyView.setVisibility(VISIBLE);
                RecyclerViewEmptySupport.this.setVisibility(GONE);
            } else {
                emptyView.setVisibility(GONE);
                RecyclerViewEmptySupport.this.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter){
        super.setAdapter(adapter);
        if(adapter != null){
            adapter.registerAdapterDataObserver(observer);
            observer.onChanged();
        }
    }

    public void setEmptyView(View view){
        emptyView = view;
    }


}
