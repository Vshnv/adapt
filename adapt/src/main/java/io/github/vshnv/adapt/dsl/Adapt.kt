package io.github.vshnv.adapt.dsl

import io.github.vshnv.adapt.dsl.collector.CollectingAdaptScope
import io.github.vshnv.adapt.adapter.AdaptAdapter

fun <T: Any> adapt(setup: AdaptScope<T>.() -> Unit): AdaptAdapter<T> {
    val adaptScope = CollectingAdaptScope<T>()
    adaptScope.setup()
    return adaptScope.buildAdapter()
}