package io.github.vshnv.adapt

import android.view.View
import java.lang.RuntimeException

class CollectingBindable<T, V>(val creator: () -> ViewSource<V>): Bindable<T, V> {
    var bindView: ((T, Any) -> Unit)? = null
        private set

    override fun bind(bindView: (T, V) -> Unit) {
        this.bindView = { a, b -> bindView(a, resolveSourceParam(b)) }
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