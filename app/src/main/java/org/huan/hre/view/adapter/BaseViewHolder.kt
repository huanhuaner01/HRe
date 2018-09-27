package org.huan.hre.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * 通用item ViewHolder
 */
abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind()
}