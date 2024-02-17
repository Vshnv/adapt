package io.github.vshnv.adapt

import android.view.ViewGroup

interface AdaptScope<T: Any> {
    fun itemEquals(checkEquality: (data: T, otherData: T) -> Boolean)
    fun contentEquals(checkContentEquality: (data: T, otherData: T) -> Boolean)
    fun defineViewTypes(mapToViewType: (data: T, position: Int) -> Int)
    fun <V: Any> create(createView: (parent: ViewGroup) -> ViewSource<V>): Bindable<T, V>
    fun <V: Any> create(viewType: Int, createView: (parent: ViewGroup) -> ViewSource<V>): Bindable<T, V>
}