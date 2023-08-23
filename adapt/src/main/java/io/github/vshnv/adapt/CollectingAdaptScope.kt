package io.github.vshnv.adapt

import androidx.recyclerview.widget.RecyclerView

internal class CollectingAdaptScope<T: Any>: AdaptScope<T> {
    private var itemEquals: (T, T) -> Boolean = {a, b -> a == b}
    private var itemContentEquals: (T, T) -> Boolean = {a, b -> a == b}
    private var viewTypeMapper: ((T) -> Int)? = null
    private var defaultBinder: CollectingBindable<T, *>? = null
    private val viewBinders: MutableMap<Int, CollectingBindable<T, *>> = mutableMapOf()

    override fun usingViewTypes(mapToViewType: (T) -> Int) {
        viewTypeMapper = mapToViewType
    }

    override fun itemEquals(checkEquality: (T, T) -> Boolean) {
        itemEquals = checkEquality
    }

    override fun contentEquals(checkContentEquality: (T, T) -> Boolean) {
        itemContentEquals = checkContentEquality
    }

    override fun <V> create(createView: () -> ViewSource<V>): Bindable<T, V> {
        return CollectingBindable<T, V> { createView() }.apply {
            defaultBinder = this
        }
    }

    override fun <V> create(viewType: Int, createView: () -> ViewSource<V>): Bindable<T, V> {
        return CollectingBindable<T, V> { createView() }.apply {
            viewBinders[viewType] = this
        }
    }

    internal fun buildAdapter(): AdaptAdapter<T> {
        return AdaptAdapter<T>(
            viewTypeMapper,
            defaultBinder,
            viewBinders,
            itemEquals,
            itemContentEquals
        )
    }
}