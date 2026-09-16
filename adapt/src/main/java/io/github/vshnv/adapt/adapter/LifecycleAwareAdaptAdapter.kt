package io.github.vshnv.adapt.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.github.vshnv.adapt.dsl.collector.CollectingBindable
import java.util.Collections
import java.util.WeakHashMap
import kotlin.coroutines.suspendCoroutine

class LifecycleAwareAdaptAdapter<T : Any>(
    private val viewTypeMapper: ((T, Int) -> Int)?,
    private val defaultBinder: CollectingBindable<T, *>?,
    private val viewBinders: MutableMap<Int, CollectingBindable<T, *>>,
    private val itemEquals: (T, T) -> Boolean,
    private val itemContentEquals: (T, T) -> Boolean,
    private var searchFilter: Filter?,
) : AdaptAdapter<T>() {
    private val knownAffectedViewHolders = Collections.newSetFromMap(WeakHashMap<LifecycleAwareAdaptViewHolder<T>, Boolean>())
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
    private var unFilteredList: MutableList<T> = mutableListOf()

    override fun getFilter(): Filter = requireNotNull(searchFilter) {
        "Filterable.Filter of $this accessed before assigning"
    }

    override fun getUnfilteredList(): List<T> = unFilteredList

    override fun getItemViewType(position: Int): Int {
        return viewTypeMapper?.let {
            it(getItem(position), position)
        } ?: super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptViewHolder<T> {
        val binderItem: CollectingBindable<T, *> = viewBinders[viewType] ?: defaultBinder
        ?: throw AssertionError("Adapt found ViewType with no bound view creator or any default view creator, Cannot proceed!")
        val viewSource = binderItem.creator(parent)
        return LifecycleAwareAdaptViewHolder<T>(viewSource.view,
            { viewHolder, lifecycleOwner ->
                binderItem.lifecycleRenewAttachable?.attach?.invoke(viewHolder,
                    currentList[viewHolder.adapterPosition], viewSource, lifecycleOwner)
            }) { viewHolder, _, data ->
            val bindDataToView = binderItem.bindDataToView ?: return@LifecycleAwareAdaptViewHolder
            bindDataToView(viewHolder, data, viewSource)
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

    override suspend fun submitDataSuspending(data: List<T>) {
        unFilteredList = data.toMutableList()
        suspendCoroutine<Unit> { continuation ->
            mDiffer.submitList(data) {
                continuation.resumeWith(Result.success(Unit))
            }
        }
    }

    override fun submitData(data: List<T>, callback: () -> Unit) {
        unFilteredList = data.toMutableList()
        mDiffer.submitList(data, callback)
    }

    override fun submitDataFromFilter(data: List<T>, callback: () -> Unit) {
        mDiffer.submitList(data, callback)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        knownAffectedViewHolders.filterNotNull().forEach { viewHolder ->
            viewHolder.notifyDetached(recyclerView)
        }
    }

    override fun onViewAttachedToWindow(holder: AdaptViewHolder<T>) {
        super.onViewAttachedToWindow(holder)
        val holder = (holder as LifecycleAwareAdaptViewHolder<T>)
        val lifecycleOwner = ViewTreeLifecycleOwner.get(holder.itemView) ?: return
        holder.handleLifecycleSetup(lifecycleOwner)
        val registry = holder.lifecycleRegistry
        registry?.highestState = Lifecycle.State.RESUMED
        knownAffectedViewHolders.add(holder)
    }

    override fun onViewDetachedFromWindow(holder: AdaptViewHolder<T>) {
        val registry = (holder as LifecycleAwareAdaptViewHolder<T>).lifecycleRegistry
        registry?.highestState = Lifecycle.State.CREATED
        super.onViewDetachedFromWindow(holder)
    }

    class LifecycleAwareAdaptViewHolder<T>(view: View, private val attachLifecycle: (ViewHolder, LifecycleOwner) -> Unit, private val bindRaw: (LifecycleAwareAdaptViewHolder<T>, Int, T) -> Unit): AdaptViewHolder<T>(view), LifecycleOwner {
        private var lastData: T? = null
        private var lastAttachedRecyclerView: RecyclerView? = null
        private var lastLifecycleOwner: LifecycleOwner? = null
        var lifecycleRegistry: AdapterLifecycleRegistry? = null
            private set
        override fun getLifecycle(): Lifecycle = requireNotNull(lifecycleRegistry) {"LifeCycle of $this accessed before attempting bind"}

        override fun bind(idx: Int, data: T) {
            if (lastData != data && lastLifecycleOwner != null) {
                renewLifecycleRegistry(lastLifecycleOwner!!)
            }
            bindRaw(this, idx, data)
            lastData = data
        }

        fun handleLifecycleSetup(lifecycleOwner: LifecycleOwner) {
            if (lastLifecycleOwner == lifecycleOwner) {
                return
            }
            lastLifecycleOwner = lifecycleOwner
            lastAttachedRecyclerView = itemView.findClosestRecyclerView()
            renewLifecycleRegistry(lifecycleOwner)
        }

        fun notifyDetached(recyclerView: RecyclerView) {
            if (lastAttachedRecyclerView != recyclerView) {
                return
            }
            lifecycleRegistry?.destroy()
            lifecycleRegistry = null
            lastLifecycleOwner = null
            lastAttachedRecyclerView = null
        }

        private fun renewLifecycleRegistry(lifecycleOwner: LifecycleOwner) {
            lifecycleRegistry?.destroy()
            lifecycleRegistry = AdapterLifecycleRegistry(this, lifecycleOwner.lifecycle)
            attachLifecycle(this, this)
        }
        private fun View.findClosestRecyclerView(): RecyclerView? {
            return when (this) {
                is RecyclerView -> this
                else -> when (val parent = parent) {
                    is View -> parent.findClosestRecyclerView()
                    else -> null
                }
            }
        }
    }

}