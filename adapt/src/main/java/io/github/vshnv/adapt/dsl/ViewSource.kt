package io.github.vshnv.adapt.dsl

import android.view.View

sealed interface ViewSource<V> {
    val view: View

    data class SimpleViewSource<V: View>(override val view: V): ViewSource<V>
    data class BindingViewSource<V>(val binding: V, val fetchViewRoot: (V) -> View): ViewSource<V> {
        override val view: View = fetchViewRoot(binding)
    }
}
