package io.github.vshnv.adapt

import android.view.ViewGroup

interface LifecycleAwareAdaptScope<T: Any>: AdaptScope<T> {
    override fun <V: Any> create(createView: (parent: ViewGroup) -> ViewSource<V>): LifecycleAwareBindable<T, V>
    override fun <V: Any> create(viewType: Int, createView: (parent: ViewGroup) -> ViewSource<V>): LifecycleAwareBindable<T, V>
}