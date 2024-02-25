package io.github.vshnv.adapt.dsl.collector

import androidx.recyclerview.widget.RecyclerView
import io.github.vshnv.adapt.dsl.BindScope

data class SimpleBindScope<T, V>(
    override val data: T,
    override val binding: V,
    override val viewHolder: RecyclerView.ViewHolder
) : BindScope<T, V> {
    override val index: Int
        get() = viewHolder.adapterPosition
}