package io.github.vshnv.adapt

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlin.coroutines.suspendCoroutine

abstract class AdaptAdapter<T>: RecyclerView.Adapter<AdaptViewHolder<T>>() {
    abstract val currentList: List<T>

    abstract suspend fun submitDataSuspending(data: List<T>): Unit

    abstract fun submitData(data: List<T>, callback: () -> Unit = {}): Unit
}