package io.github.vshnv.adapt

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.coroutines.suspendCoroutine


class AdaptAdapter<T : Any>(private val viewTypeMapper: ((T) -> Int)?, private val defaultBinder: CollectingBindable<T, *>?, private val viewBinders: MutableMap<Int, CollectingBindable<T, *>>, private val itemEquals: (T, T) -> Boolean, private val itemContentEquals: (T, T) -> Boolean): RecyclerView.Adapter<AdaptAdapter.AdaptViewHolder<T>>() {
    private val diffCallback: DiffUtil.ItemCallback<T> = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return itemEquals(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return itemContentEquals(oldItem, newItem)
        }
    }
    private val mDiffer: AsyncListDiffer<T> = AsyncListDiffer(this, diffCallback)
    val currentList: List<T>
        get() = mDiffer.currentList

    override fun getItemViewType(position: Int): Int {
        return viewTypeMapper?.let {
            it(getItem(position))
        } ?: super.getItemViewType(position)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptViewHolder<T> {
        val binderItem: CollectingBindable<T, *> = viewBinders[viewType] ?: defaultBinder
        ?: throw AssertionError("Adapt found ViewType with no bound view creator or any default view creator, Cannot proceed!")
        val viewSource = binderItem.creator()
        return AdaptViewHolder<T>(viewSource.view) { data ->
            binderItem.bindView?.let { bind ->
                bind(data, viewSource)
            }
        }
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: AdaptViewHolder<T>, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    private fun getItem(position: Int): T {
        return mDiffer.currentList[position]
    }

    suspend fun submitDataSuspending(data: List<T>) = suspendCoroutine<Unit> { continuation ->
        mDiffer.submitList(data) {
            continuation.resumeWith(Result.success(Unit))
        }
    }

    @JvmOverloads
    fun submitData(data: List<T>, callback: () -> Unit = {}) {
        mDiffer.submitList(data, callback)
    }

    class AdaptViewHolder<T>(view: View, val bind: (T) -> Unit): RecyclerView.ViewHolder(view)

}