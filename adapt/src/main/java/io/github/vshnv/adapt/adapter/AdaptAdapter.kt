package io.github.vshnv.adapt.adapter

import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView

abstract class AdaptAdapter<T>: RecyclerView.Adapter<AdaptViewHolder<T>>(), Filterable {
    abstract val currentList: List<T>
    abstract fun getUnfilteredList(): List<T>

    abstract suspend fun submitDataSuspending(data: List<T>)
    abstract fun submitData(data: List<T>, callback: () -> Unit = {})
    abstract fun submitDataFromFilter(data: List<T>, callback: () -> Unit = {})
}