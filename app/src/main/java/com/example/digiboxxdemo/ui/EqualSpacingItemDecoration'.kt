package com.example.digiboxxdemo.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EqualSpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = spacing
        outRect.right = spacing
//        outRect.bottom = spacing
//        outRect.top = spacing
//        if (parent.getChildAdapterPosition(view) == 0) {
//            outRect.top = spacing
//        }
    }
}