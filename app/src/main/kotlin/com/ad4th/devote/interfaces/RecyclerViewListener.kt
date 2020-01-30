package com.ad4th.devote.interfaces

import android.view.View

interface RecyclerViewListener {
    fun onItemClick(view: View, position: Int)
}