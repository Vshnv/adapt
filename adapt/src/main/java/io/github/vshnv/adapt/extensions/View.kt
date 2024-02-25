package io.github.vshnv.adapt.extensions

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.runtime.R

internal fun View.findViewTreeLifecycleOwner(): LifecycleOwner? {
    return generateSequence(this) { currentView ->
        currentView.parent as? View
    }.mapNotNull { viewParent ->
        viewParent.getTag(R.id.view_tree_lifecycle_owner) as? LifecycleOwner
    }.firstOrNull()
}