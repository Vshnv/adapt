package io.github.vshnv.adapt

interface Bindable<T, V> {
    fun bind(bindView: BindScope<T, V>.() -> Unit): LifecycleRenewAttachable<T, V>
}

