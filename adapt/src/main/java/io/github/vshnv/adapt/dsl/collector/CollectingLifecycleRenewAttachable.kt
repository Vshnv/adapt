package io.github.vshnv.adapt.dsl.collector

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import io.github.vshnv.adapt.dsl.BindScope
import io.github.vshnv.adapt.dsl.LifecycleRenewAttachable
import io.github.vshnv.adapt.dsl.ViewSource

class CollectingLifecycleRenewAttachable<T, V>(private val createBindScope: (RecyclerView.ViewHolder, T, ViewSource<*>) -> SimpleBindScope<T, V>) :
    LifecycleRenewAttachable<T, V> {
    var attach: ((viewHolder: RecyclerView.ViewHolder, data: T, viewSource: ViewSource<*>, LifecycleOwner) -> Unit)? = null
    override fun withLifecycle(attach: BindScope<T, V>.(LifecycleOwner) -> Unit) {
        this.attach = { viewHolder, data, viewSource, lifecycleOwner ->
            createBindScope(viewHolder, data, viewSource).attach(lifecycleOwner)
        }
    }

}