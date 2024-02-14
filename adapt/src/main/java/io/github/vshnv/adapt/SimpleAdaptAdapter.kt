package io.github.vshnv.adapt

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.coroutines.suspendCoroutine


class SimpleAdaptAdapter<T : Any>(private val viewTypeMapper: ((T, Int) -> Int)?, private val defaultBinder: CollectingBindable<T, *>?, private val viewBinders: MutableMap<Int, CollectingBindable<T, *>>, private val itemEquals: (T, T) -> Boolean, private val itemContentEquals: (T, T) -> Boolean): AdaptAdapter<T>() {
    private val diffCallback: DiffUtil.ItemCallback<T> = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return itemEquals(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return itemContentEquals(oldItem, newItem)
        }
    }
    private val mDiffer: AsyncListDiffer<T> = AsyncListDiffer(this, diffCallback)
    override val currentList: List<T>
        get() = mDiffer.currentList

    override fun getItemViewType(position: Int): Int {
        return viewTypeMapper?.let {
            it(getItem(position), position)
        } ?: super.getItemViewType(position)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptViewHolder<T> {
        val binderItem: CollectingBindable<T, *> = viewBinders[viewType] ?: defaultBinder
        ?: throw AssertionError("Adapt found ViewType with no bound view creator or any default view creator, Cannot proceed!")
        val viewSource = binderItem.creator(parent)
        return SimpleAdaptViewHolder<T>(viewSource.view) { position, data ->
            binderItem.bindView?.let { bind ->
                bind(position, data, viewSource)
            }
        }
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: AdaptViewHolder<T>, position: Int) {
        val data = getItem(position)
        holder.bind(position, data)
    }

    private fun getItem(position: Int): T {
        return mDiffer.currentList[position]
    }

    override suspend fun submitDataSuspending(data: List<T>) = suspendCoroutine<Unit> { continuation ->
        mDiffer.submitList(data) {
            continuation.resumeWith(Result.success(Unit))
        }
    }

    override fun submitData(data: List<T>, callback: () -> Unit) {
        mDiffer.submitList(data, callback)
    }

    class SimpleAdaptViewHolder<T>(view: View, private val bindRaw: (Int, T) -> Unit): AdaptViewHolder<T>(view) {
        override fun bind(idx: Int, data: T) {
            bindRaw(idx, data)
        }
    }

}