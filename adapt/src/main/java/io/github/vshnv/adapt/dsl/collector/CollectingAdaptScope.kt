package io.github.vshnv.adapt.dsl.collector

import android.view.ViewGroup
import android.widget.Filter
import io.github.vshnv.adapt.adapter.AdaptAdapter
import io.github.vshnv.adapt.adapter.LifecycleAwareAdaptAdapter
import io.github.vshnv.adapt.dsl.AdaptScope
import io.github.vshnv.adapt.dsl.Bindable
import io.github.vshnv.adapt.dsl.ViewSource

internal class CollectingAdaptScope<T : Any> : AdaptScope<T> {
    private var itemEquals: (T, T) -> Boolean = { a, b -> a == b }
    private var itemContentEquals: (T, T) -> Boolean = { a, b -> a == b }
    private var viewTypeMapper: ((T, Int) -> Int)? = null
    private var defaultBinder: CollectingBindable<T, *>? = null
    private val viewBinders: MutableMap<Int, CollectingBindable<T, *>> = mutableMapOf()
    private var searchFilterable: Filter? = null

    override fun defineViewTypes(mapToViewType: (T, Int) -> Int) {
        viewTypeMapper = mapToViewType
    }

    override fun itemEquals(checkEquality: (T, T) -> Boolean) {
        itemEquals = checkEquality
    }

    override fun contentEquals(checkContentEquality: (T, T) -> Boolean) {
        itemContentEquals = checkContentEquality
    }

    override fun filter(searchFilter: Filter?) {
        searchFilterable = searchFilter
    }

    internal fun buildAdapter(): AdaptAdapter<T> {
        return LifecycleAwareAdaptAdapter<T>(
            viewTypeMapper,
            defaultBinder,
            viewBinders,
            itemEquals,
            itemContentEquals,
            searchFilterable
        )
    }

    override fun <V : Any> create(createView: (parent: ViewGroup) -> ViewSource<V>): Bindable<T, V> {
        return CollectingBindable<T, V>(createView).apply {
            defaultBinder = this
        }
    }

    override fun <V : Any> create(
        viewType: Int,
        createView: (parent: ViewGroup) -> ViewSource<V>,
    ): Bindable<T, V> {
        return CollectingBindable<T, V>(createView).apply {
            viewBinders[viewType] = this
        }
    }
}