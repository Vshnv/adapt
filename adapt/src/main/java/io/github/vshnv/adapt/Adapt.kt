package io.github.vshnv.adapt

import androidx.recyclerview.widget.RecyclerView

fun <T: Any> adapt(setup: AdaptScope<T>.() -> Unit): AdaptAdapter<T> {
    val adaptScope = CollectingAdaptScope<T>()
    adaptScope.setup()
    return adaptScope.buildAdapter()
}