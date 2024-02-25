package io.github.vshnv.adapt

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.OnLifecycleEvent
import java.lang.ref.WeakReference

class AdapterLifecycleRegistry(owner: LifecycleOwner, private val parentLifecycle: Lifecycle): LifecycleRegistry(owner) {
    private val ownerWeakRef = WeakReference(owner)
    private val parentLifecycleObserver = object: LifecycleObserver {
        @OnLifecycleEvent(Event.ON_ANY)
        fun onAny() {
            if (ownerWeakRef.get() == null) {
                ignoreParent()
                return
            }
            currentState = parentLifecycle.currentState
        }
    }
    var highestState = State.INITIALIZED
        set(value) {
            field = value
            if (currentState > State.INITIALIZED) {
                if (parentLifecycle.currentState <= value) {
                    currentState = parentLifecycle.currentState
                } else if (currentState >= value) {
                    currentState = value
                }
            }
        }
    init {
        val currentParentState = parentLifecycle.currentState
        if (currentParentState > State.INITIALIZED) {
            highestState = parentLifecycle.currentState
            currentState = parentLifecycle.currentState
        }
        observeParent()
    }
    private fun observeParent() {
        parentLifecycle.addObserver(parentLifecycleObserver)
    }
    private fun ignoreParent() {
        parentLifecycle.removeObserver(parentLifecycleObserver)
    }
    override fun setCurrentState(nextState: State) {
        val maxNextState = if (nextState > highestState)
            highestState else nextState
        if (nextState == State.DESTROYED) {
            ignoreParent()
        }
        super.setCurrentState(maxNextState)
    }

    fun destroy() {
        ignoreParent()
        highestState = State.DESTROYED
    }
}