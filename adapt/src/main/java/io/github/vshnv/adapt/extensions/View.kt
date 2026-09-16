package io.github.vshnv.adapt.extensions

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner

internal fun View.findViewTreeLifecycleOwner(): LifecycleOwner? {
    return ViewTreeLifecycleOwner.get(this)
}