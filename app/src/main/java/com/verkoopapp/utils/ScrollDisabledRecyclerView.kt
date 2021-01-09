package com.verkoopapp.utils

import android.content.Context
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import javax.annotation.Nullable


/**
 * Created by intel on 18-03-2019.
 */
class ScrollDisabledRecyclerView : RecyclerView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, @Nullable attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return false
    }
}