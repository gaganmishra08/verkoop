package com.verkoopapp.utils;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import javax.annotation.Nullable;

/**
 * Created by intel on 09-04-2018.
 */

public class NonscrollRecylerview extends RecyclerView {

    public NonscrollRecylerview(Context context) {
        super(context);
        this.setNestedScrollingEnabled(false);
    }

    public NonscrollRecylerview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setNestedScrollingEnabled(false);
    }

    public NonscrollRecylerview(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
