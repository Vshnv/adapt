package io.github.vshnv.adapt

interface AdaptScope<T: Any> {
    fun itemEquals(checkEquality: (data: T, otherData: T) -> Boolean)
    fun contentEquals(checkContentEquality: (data: T, otherData: T) -> Boolean)
    fun usingViewTypes(mapToViewType: (data: T, position: Int) -> Int)
    fun <V> create(createView: () -> ViewSource<V>): Bindable<T, V>
    fun <V> create(viewType: Int, createView: () -> ViewSource<V>): Bindable<T, V>
}