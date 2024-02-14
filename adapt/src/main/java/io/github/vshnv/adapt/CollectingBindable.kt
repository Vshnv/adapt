package io.github.vshnv.adapt

import android.view.View
import android.view.ViewGroup
import java.lang.RuntimeException

class CollectingBindable<T, V>(val creator: (parent: ViewGroup) -> ViewSource<V>): Bindable<T, V> {
    var bindView: ((Int, T, Any) -> Unit)? = null
        private set

    override fun bind(bindView: (T, V) -> Unit) {
        this.bindView = { _, a, b -> bindView(a, resolveSourceParam(b)) }
    }

    override fun bind(bindView: (Int, T, V) -> Unit) {
        this.bindView = { i, a, b -> bindView(i, a, resolveSourceParam(b)) }
    }

    private fun resolveSourceParam(item: Any): V {
        return when (item) {
            is ViewSource.BindingViewSource<*> -> item.binding as V
            is ViewSource.SimpleViewSource<*> -> item.view as V
            else -> {
                throw RuntimeException("Invalid ViewSource found!")
            }
        }
    }

}