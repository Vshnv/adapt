package io.github.vshnv.adapt.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class AdaptAdapter<T>: RecyclerView.Adapter<AdaptViewHolder<T>>() {
    abstract val currentList: List<T>

    abstract suspend fun submitDataSuspending(data: List<T>): Unit

    abstract fun submitData(data: List<T>, callback: () -> Unit = {}): Unit
}