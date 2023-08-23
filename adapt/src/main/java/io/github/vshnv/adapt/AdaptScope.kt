package io.github.vshnv.adapt

interface AdaptScope<T: Any> {
    fun itemEquals(checkEquality: (T, T) -> Boolean)
    fun contentEquals(checkContentEquality: (T, T) -> Boolean)
    fun usingViewTypes(mapToViewType: (T) -> Int)
    fun <V> create(createView: () -> ViewSource<V>): Bindable<T, V>
    fun <V> create(viewType: Int, createView: () -> ViewSource<V>): Bindable<T, V>
}