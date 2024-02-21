package io.github.vshnv.adapt

import androidx.lifecycle.LifecycleOwner

fun <T: Any> adapt(setup: AdaptScope<T>.() -> Unit): AdaptAdapter<T> {
    val adaptScope = CollectingAdaptScope<T>()
    adaptScope.setup()
    return adaptScope.buildAdapter()
}