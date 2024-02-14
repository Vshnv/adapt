package io.github.vshnv.adapt

import androidx.lifecycle.LifecycleOwner

interface LifecycleAwareBindable<T, V>: Bindable<T, V> {
    fun bindWithLifecycle(bindView: (LifecycleOwner, T, V) -> Unit)
    fun bindWithLifecycle(bindView: (LifecycleOwner, Int, T, V) -> Unit)
}