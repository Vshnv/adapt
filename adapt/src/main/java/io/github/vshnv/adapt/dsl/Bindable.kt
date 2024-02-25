package io.github.vshnv.adapt.dsl

interface Bindable<T, V> {
    fun bind(bindView: BindScope<T, V>.() -> Unit): LifecycleRenewAttachable<T, V>
}

