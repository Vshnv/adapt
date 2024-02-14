package io.github.vshnv.adapt

import android.view.ViewGroup

interface LifecycleAwareAdaptScope<T: Any>: AdaptScope<T> {
    override fun <V> create(createView: (parent: ViewGroup) -> ViewSource<V>): LifecycleAwareBindable<T, V>
    override fun <V> create(viewType: Int, createView: (parent: ViewGroup) -> ViewSource<V>): LifecycleAwareBindable<T, V>
}