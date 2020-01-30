package com.ad4th.devote.interfaces

import android.support.v7.widget.RecyclerView
import android.view.View


interface INoDataChanger {
    fun setNoDataView(view: View)
    fun showNoDataView(isNoData: Boolean)
    fun setRecyclerView(recyclerView: RecyclerView)
}