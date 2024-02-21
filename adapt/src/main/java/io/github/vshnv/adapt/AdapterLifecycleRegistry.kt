package io.github.vshnv.adapt

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.OnLifecycleEvent

class AdapterLifecycleRegistry(private val owner: LifecycleOwner): LifecycleRegistry(owner) {
    private val parentLifecycleObserver = object: LifecycleObserver {
        @OnLifecycleEvent(Event.ON_ANY)
        fun onAny() {
            currentState = owner.lifecycle.currentState
        }
    }
    var highestState = State.INITIALIZED
        set(value) {
            field = value
            if (owner.lifecycle.currentState > State.INITIALIZED && owner.lifecycle.currentState >= value) {
                currentState = value
            }
        }
    init {
        observeParent()
    }
    private fun observeParent() {
        owner.lifecycle.addObserver(parentLifecycleObserver)
    }
    private fun ignoreParent() {
        owner.lifecycle.removeObserver(parentLifecycleObserver)
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