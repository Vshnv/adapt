package io.github.vshnv.adapt

import androidx.lifecycle.LifecycleOwner

interface LifecycleAwareBindable<T, V>: Bindable<T, V> {
    fun bind(bindView: (LifecycleOwner, T, V) -> Unit)
    fun bind(bindView: (LifecycleOwner, Int, T, V) -> Unit)
}