package io.github.vshnv.adapt

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.lang.RuntimeException

class CollectingBindable<T, V>(val creator: (parent: ViewGroup) -> ViewSource<V>): Bindable<T, V> {
    var bindDataToView: ((viewHolder: ViewHolder, index: Int, data: T, viewSource: ViewSource<*>) -> Unit)? = null
        private set

    override fun bind(bindView: BindScope<T, V>.() -> Unit) {
        this.bindDataToView = { viewHolder, index, data, viewSource ->
            val scope = SimpleBindScope(
                index,
                data,
                resolveSourceParam(viewSource),
                viewHolder
            )
            scope.bindView()
        }
    }

    private fun resolveSourceParam(item: ViewSource<*>): V {
        return when (item) {
            is ViewSource.BindingViewSource<*> -> item.binding as V
            is ViewSource.SimpleViewSource<*> -> item.view as V
        }
    }

}