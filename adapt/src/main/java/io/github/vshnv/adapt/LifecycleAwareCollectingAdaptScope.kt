package io.github.vshnv.adapt

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner

class LifecycleAwareCollectingAdaptScope<T: Any>(private val lifecycleOwner: LifecycleOwner): LifecycleAwareAdaptScope<T> {
    private var itemEquals: (T, T) -> Boolean = {a, b -> a == b}
    private var itemContentEquals: (T, T) -> Boolean = {a, b -> a == b}
    private var viewTypeMapper: ((T, Int) -> Int)? = null
    private var defaultBinder:  LifecycleAwareCollectingBindable<T, *>? = null
    private val viewBinders: MutableMap<Int, LifecycleAwareCollectingBindable<T, *>> = mutableMapOf()

    override fun defineViewTypes(mapToViewType: (T, Int) -> Int) {
        viewTypeMapper = mapToViewType
    }

    override fun itemEquals(checkEquality: (T, T) -> Boolean) {
        itemEquals = checkEquality
    }

    override fun contentEquals(checkContentEquality: (T, T) -> Boolean) {
        itemContentEquals = checkContentEquality
    }

    internal fun buildAdapter(): LifecycleAwareAdaptAdapter<T> {
        return LifecycleAwareAdaptAdapter<T>(
            lifecycleOwner,
            viewTypeMapper,
            defaultBinder,
            viewBinders,
            itemEquals,
            itemContentEquals
        )
    }

    override fun <V> create(createView: (parent: ViewGroup) -> ViewSource<V>): LifecycleAwareBindable<T, V> {
        return LifecycleAwareCollectingBindable<T, V>(createView).apply {
            defaultBinder = this
        }
    }

    override fun <V> create(
        viewType: Int,
        createView: (parent: ViewGroup) -> ViewSource<V>
    ): LifecycleAwareBindable<T, V> {
        return LifecycleAwareCollectingBindable<T, V>(createView).apply {
            viewBinders[viewType] = this
        }
    }
}