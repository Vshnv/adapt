package io.github.vshnv.adapt

import androidx.lifecycle.LifecycleOwner
interface LifecycleAwareBindable<T, V>: Bindable<T, V> {
    fun bindWithLifecycle(bindView: LifecycleAwareBindScope<T,V>.() -> Unit)
}