package io.github.vshnv.adapt

interface Bindable<T, V> {
    fun bind(bindView: (T, V) -> Unit)
    fun bind(bindView: (Int, T, V) -> Unit)
}