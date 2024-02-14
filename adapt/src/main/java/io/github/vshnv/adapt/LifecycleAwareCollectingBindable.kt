package io.github.vshnv.adapt

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import java.lang.RuntimeException

class LifecycleAwareCollectingBindable<T, V>(val creator: (parent: ViewGroup) -> ViewSource<V>): LifecycleAwareBindable<T, V> {
    var bindView: ((LifecycleOwner, Int, T, Any) -> Unit)? = null
        private set

    override fun bind(bindView: (T, V) -> Unit) {
        this.bindView = { _, _, a, b -> bindView(a, resolveSourceParam(b)) }
    }

    override fun bind(bindView: (Int, T, V) -> Unit) {
        this.bindView = { _, i, a, b -> bindView(i, a, resolveSourceParam(b)) }
    }

    override fun bindWithLifecycle(bindView: (LifecycleOwner, T, V) -> Unit) {
        this.bindView = { lifecycleOwner, _, a, b -> bindView(lifecycleOwner, a, resolveSourceParam(b)) }
    }

    override fun bindWithLifecycle(bindView: (LifecycleOwner, Int, T, V) -> Unit) {
        this.bindView = { lifecycleOwner, idx, a, b -> bindView(lifecycleOwner, idx, a, resolveSourceParam(b)) }
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