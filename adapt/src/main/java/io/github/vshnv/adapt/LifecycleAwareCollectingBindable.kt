package io.github.vshnv.adapt

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.lang.RuntimeException

class LifecycleAwareCollectingBindable<T: Any, V: Any>(val creator: (parent: ViewGroup) -> ViewSource<V>): LifecycleAwareBindable<T, V> {
    var bindDataToView: ((viewHolder: ViewHolder, lifecycleOwner: LifecycleOwner, index: Int, data: T, viewSource: ViewSource<*>) -> Unit)? = null
        private set



    private fun resolveSourceParam(item: ViewSource<*>): V {
        return when (item) {
            is ViewSource.BindingViewSource<*> -> item.binding as V
            is ViewSource.SimpleViewSource<*> -> item.view as V
        }
    }

    override fun bind(bindView: BindScope<T, V>.() -> Unit) {
        this.bindDataToView = { viewHolder, lifecycleOwner, index, data, viewSource ->
            val scope = SimpleBindScope(
                index,
                 data,
                resolveSourceParam(viewSource),
                viewHolder
            )
            scope.bindView()
        }
    }

    override fun bindWithLifecycle(bindView: LifecycleAwareBindScope<T, V>.() -> Unit) {
        this.bindDataToView = { viewHolder, lifecycleOwner, index, data, viewSource ->
            val scope = SimpleLifecycleAwareBindScope(
                index,
                data,
                resolveSourceParam(viewSource),
                viewHolder,
                lifecycleOwner
            )
            scope.bindView()
        }
    }

}