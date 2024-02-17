package io.github.vshnv.adapt

import androidx.lifecycle.LifecycleOwner

interface LifecycleAwareBindScope<T, V>: BindScope<T, V> {
    val lifecycleOwner: LifecycleOwner
}