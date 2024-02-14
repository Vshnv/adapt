package io.github.vshnv.adapt

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.coroutines.suspendCoroutine

class LifecycleAwareAdaptAdapter<T : Any>(private val lifecycleOwner: LifecycleOwner, private val viewTypeMapper: ((T, Int) -> Int)?, private val defaultBinder: LifecycleAwareCollectingBindable<T, *>?, private val viewBinders: MutableMap<Int, LifecycleAwareCollectingBindable<T, *>>, private val itemEquals: (T, T) -> Boolean, private val itemContentEquals: (T, T) -> Boolean): AdaptAdapter<T>() {
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
        val binderItem: LifecycleAwareCollectingBindable<T, *> = viewBinders[viewType] ?: defaultBinder
        ?: throw AssertionError("Adapt found ViewType with no bound view creator or any default view creator, Cannot proceed!")
        val viewSource = binderItem.creator(parent)
        return LifecycleAwareAdaptViewHolder<T>(lifecycleOwner, viewSource.view) { lifecycleOwner, position, data ->
            binderItem.bindView?.let { bind ->
                bind(lifecycleOwner, position, data, viewSource)
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
    override fun onViewAttachedToWindow(holder: AdaptViewHolder<T>) {
        super.onViewAttachedToWindow(holder)
        val registry = (holder as LifecycleAwareAdaptViewHolder<T>).lifecycleRegistry
        registry.highestState = Lifecycle.State.RESUMED
    }
    override fun onViewDetachedFromWindow(holder: AdaptViewHolder<T>) {
        val registry = (holder as LifecycleAwareAdaptViewHolder<T>).lifecycleRegistry
        registry.highestState = Lifecycle.State.CREATED
        super.onViewDetachedFromWindow(holder)
    }

    override fun onViewRecycled(holder: AdaptViewHolder<T>) {
        super.onViewRecycled(holder)
        (holder as LifecycleAwareAdaptViewHolder<T>).renewLifecycle()
    }
    class LifecycleAwareAdaptViewHolder<T>(private val parentLifecycleOwner: LifecycleOwner, view: View, private val bindRaw: (LifecycleOwner, Int, T) -> Unit): AdaptViewHolder<T>(view), LifecycleOwner {
        var lifecycleRegistry = AdapterLifecycleRegistry(parentLifecycleOwner, parentLifecycleOwner.lifecycle)
            private set
        override fun getLifecycle(): Lifecycle = lifecycleRegistry

        override fun bind(idx: Int, data: T) {
            bindRaw(this, idx, data)
        }

        fun renewLifecycle() {
            lifecycleRegistry.highestState = Lifecycle.State.DESTROYED
            lifecycleRegistry = AdapterLifecycleRegistry(parentLifecycleOwner, parentLifecycleOwner.lifecycle)
        }
    }

}