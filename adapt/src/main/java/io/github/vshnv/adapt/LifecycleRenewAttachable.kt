package io.github.vshnv.adapt

import androidx.lifecycle.LifecycleOwner

interface LifecycleRenewAttachable<T, V> {
    fun withLifecycle(attach: BindScope<T, V>.(LifecycleOwner) -> Unit)
}