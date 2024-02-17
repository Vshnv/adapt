package io.github.vshnv.adapt

import android.view.ViewGroup

internal class CollectingAdaptScope<T: Any>: AdaptScope<T> {
    private var itemEquals: (T, T) -> Boolean = {a, b -> a == b}
    private var itemContentEquals: (T, T) -> Boolean = {a, b -> a == b}
    private var viewTypeMapper: ((T, Int) -> Int)? = null
    private var defaultBinder:  CollectingBindable<T, *>? = null
    private val viewBinders: MutableMap<Int, CollectingBindable<T, *>> = mutableMapOf()

    override fun defineViewTypes(mapToViewType: (T, Int) -> Int) {
        viewTypeMapper = mapToViewType
    }

    override fun itemEquals(checkEquality: (T, T) -> Boolean) {
        itemEquals = checkEquality
    }

    override fun contentEquals(checkContentEquality: (T, T) -> Boolean) {
        itemContentEquals = checkContentEquality
    }

    internal fun buildAdapter(): SimpleAdaptAdapter<T> {
        return SimpleAdaptAdapter<T>(
            viewTypeMapper,
            defaultBinder,
            viewBinders,
            itemEquals,
            itemContentEquals
        )
    }

    override fun <V: Any> create(createView: (parent: ViewGroup) -> ViewSource<V>): Bindable<T, V> {
        return CollectingBindable<T, V>(createView).apply {
            defaultBinder = this
        }
    }

    override fun <V: Any> create(
        viewType: Int,
        createView: (parent: ViewGroup) -> ViewSource<V>
    ): Bindable<T, V> {
        return CollectingBindable<T, V>(createView).apply {
            viewBinders[viewType] = this
        }
    }
}