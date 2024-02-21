package io.github.vshnv.adapt

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

class CollectingLifecycleRenewAttachable<T, V>(private val createBindScope: (RecyclerView.ViewHolder, T, ViewSource<*>) -> SimpleBindScope<T, V>) : LifecycleRenewAttachable<T, V> {
    var attach: ((viewHolder: RecyclerView.ViewHolder, data: T, viewSource: ViewSource<*>, LifecycleOwner) -> Unit)? = null
    override fun withLifecycle(attach: BindScope<T, V>.(LifecycleOwner) -> Unit) {
        this.attach = { viewHolder, data, viewSource, lifecycleOwner ->
            createBindScope(viewHolder, data, viewSource).attach(lifecycleOwner)
        }
    }

}