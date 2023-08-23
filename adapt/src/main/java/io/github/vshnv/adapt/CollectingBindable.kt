package io.github.vshnv.adapt

class CollectingBindable<T, V>(val creator: () -> ViewSource<V>): Bindable<T, V> {
    var bindView: ((T, Any) -> Unit)? = null
        private set

    override fun bind(bindView: (T, V) -> Unit) {
        this.bindView = { a, b -> bindView(a, b as V) }
    }
}