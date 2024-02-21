package io.github.vshnv.adapt

import androidx.recyclerview.widget.RecyclerView

data class SimpleBindScope<T, V>(
    override val data: T,
    override val binding: V,
    override val viewHolder: RecyclerView.ViewHolder
) : BindScope<T, V> {
    override val index: Int
        get() = viewHolder.adapterPosition
}