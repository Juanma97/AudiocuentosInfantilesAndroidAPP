package com.alpha.audiocuentosinfantiles.recyclerview

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewWrapper {

    companion object{
        fun setUpRecyclerView(containerView: RecyclerView, context: Context):RecyclerView {
            containerView.setHasFixedSize(false)
            val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 2)
            containerView.layoutManager = layoutManager

            return containerView
        }
    }
}
