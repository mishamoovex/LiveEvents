package com.mishamoovex.liveevents.observer

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.mishamoovex.liveevents.event.DataEvent

/**
 * An [Observer] for [DataEvent]s, simplifying the pattern of checking if the [DataEvent]'s content has
 * already been handled.
 *
 * [block] is *only* called if the [DataEvent]'s contents has not been handled.
 */
class DataEventObserver<T : Any>(private val block: (T) -> Unit) : Observer<DataEvent<T>> {

    override fun onChanged(event: DataEvent<T>) {
        event.getDataIfNotHandled()?.let { value -> block(value) }
    }

}

/**
 * Adds the given [onChanged] lambda as an observer within the lifespan of the given
 * [owner] and returns a reference to observer.
 *
 * The events are dispatched on the main thread. If LiveData already has data
 * set, it will be delivered to the onChanged but if data set was handled before nothing happens
 * and dispatcher will wait to the new data set.
 *
 * The observer will only receive events if the owner is in [Lifecycle.State.STARTED]
 * or [Lifecycle.State.RESUMED] state (active).
 *
 * If the owner moves to the [Lifecycle.State.DESTROYED] state, the observer will
 * automatically be removed.
 *
 * When data changes while the [owner] is not active, it will not receive any updates.
 * If it becomes active again, it will receive the last available data automatically.
 *
 * LiveData keeps a strong reference to the observer and the owner as long as the
 * given LifecycleOwner is not destroyed. When it is destroyed, LiveData removes references to
 * the observer and the owner.
 *
 * If the given owner is already in [Lifecycle.State.DESTROYED] state, LiveData
 * ignores the call.
 */
@MainThread
inline fun <T : Any> LiveData<DataEvent<T>>.observeDataEvent(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
): DataEventObserver<T> {
    val wrappedObserver = DataEventObserver<T> { t -> onChanged.invoke(t) }
    observe(owner, wrappedObserver as Observer<DataEvent<T>>)
    return wrappedObserver
}