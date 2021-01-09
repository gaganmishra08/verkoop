package com.verkoopapp.utils

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager


class CustomGridLayoutManager(context: Context) : GridLayoutManager(context,2) {
    private var isScrollEnabled = true

    fun setScrollEnabled(flag: Boolean) {
        this.isScrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically()
    }
}