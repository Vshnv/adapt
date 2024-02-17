package io.github.vshnv.adapt

import androidx.recyclerview.widget.RecyclerView

interface BindScope<T, V> {
    val index: Int
    val data: T
    val binding: V
    val viewHolder: RecyclerView.ViewHolder
}