package io.github.vshnv.adapt.dsl

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method

interface AdaptScope<T: Any> {
    fun itemEquals(checkEquality: (data: T, otherData: T) -> Boolean)
    fun contentEquals(checkContentEquality: (data: T, otherData: T) -> Boolean)
    fun defineViewTypes(mapToViewType: (data: T, position: Int) -> Int)
    fun <V: Any> create(createView: (parent: ViewGroup) -> ViewSource<V>): Bindable<T, V>
    fun <V: Any> create(viewType: Int, createView: (parent: ViewGroup) -> ViewSource<V>): Bindable<T, V>

}

inline fun <T : Any, reified V : ViewBinding> AdaptScope<T>.create(): Bindable<T, V> = create {
    ViewSource.BindingViewSource(inflateViewBinding(V::class.java, it), ViewBinding::getRoot)
}

private val inflateMethodCache = mutableMapOf<Class<out ViewBinding>, Method>()
fun <T : ViewBinding> inflateViewBinding(bindingClass: Class<T>, parent: ViewGroup): T {
    val layoutInflater = LayoutInflater.from(parent.context)
    return inflateMethodCache.getOrPut(bindingClass) {
        bindingClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.javaPrimitiveType
        )
    }.invoke(null, layoutInflater, parent, false) as T
}