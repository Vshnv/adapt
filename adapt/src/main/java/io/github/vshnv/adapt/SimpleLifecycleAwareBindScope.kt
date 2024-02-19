package io.github.vshnv.adapt

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

data class SimpleLifecycleAwareBindScope<T, V>(
    override val index: Int,
    override val data: T,
    override val binding: V,
    override val viewHolder: RecyclerView.ViewHolder,
    override val lifecycleOwner: LifecycleOwner,
) : LifecycleAwareBindScope<T, V>