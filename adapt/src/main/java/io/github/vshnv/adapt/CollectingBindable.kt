package io.github.vshnv.adapt

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class CollectingBindable<T, V>(val creator: (parent: ViewGroup) -> ViewSource<V>): Bindable<T, V> {

    var lifecycleRenewAttachable: CollectingLifecycleRenewAttachable<T, V>? = null
    var bindDataToView: ((viewHolder: ViewHolder, data: T, viewSource: ViewSource<*>) -> Unit)? = null
        private set


    override fun bind(bindView: BindScope<T, V>.() -> Unit): LifecycleRenewAttachable<T, V> {
        val createBindScope = { viewHolder: ViewHolder, data: T, viewSource: ViewSource<*> ->
            SimpleBindScope(
                data,
                resolveSourceParam(viewSource),
                viewHolder
            )
        }
        this.bindDataToView = { viewHolder: ViewHolder, data: T, viewSource: ViewSource<*> ->
            createBindScope(viewHolder, data, viewSource).bindView()
        }
        return CollectingLifecycleRenewAttachable<T, V>(createBindScope).also {
            lifecycleRenewAttachable = it
        }
    }

    private fun resolveSourceParam(item: ViewSource<*>): V {
        return when (item) {
            is ViewSource.BindingViewSource<*> -> item.binding as V
            is ViewSource.SimpleViewSource<*> -> item.view as V
        }
    }

}