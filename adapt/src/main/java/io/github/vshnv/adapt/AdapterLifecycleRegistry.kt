package io.github.vshnv.adapt

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.OnLifecycleEvent

class AdapterLifecycleRegistry(owner: LifecycleOwner, private val parent: Lifecycle): LifecycleRegistry(owner) {
    private val parentLifecycleObserver = object: LifecycleObserver {
        @OnLifecycleEvent(Event.ON_ANY)
        fun onAny() {
            currentState = parent.currentState
        }
    }
    var highestState = State.INITIALIZED
        set(value) {
            field = value
            if (parent.currentState > State.INITIALIZED && parent.currentState >= value) {
                currentState = value
            }
        }
    init {
        observeParent()
    }
    private fun observeParent() {
        parent.addObserver(parentLifecycleObserver)
    }
    private fun ignoreParent() {
        parent.removeObserver(parentLifecycleObserver)
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