package io.github.vshnv.adapt.dsl

import androidx.lifecycle.LifecycleOwner

interface LifecycleRenewAttachable<T, V> {
    fun withLifecycle(attach: BindScope<T, V>.(LifecycleOwner) -> Unit)
}