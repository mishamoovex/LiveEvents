package com.mishamoovex.liveevents.observer

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.mishamoovex.liveevents.event.Event

/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been handled.
 *
 * [block] is *only* called if the [Event]'s contents has not been handled.
 */
class EventObserver(private val block: () -> Unit) : Observer<Event> {

    override fun onChanged(event: Event) {
        if (event.getEventIfNoHandled()) {
            block()
        }
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
inline fun LiveData<Event>.observeEvent(
    owner: LifecycleOwner,
    crossinline onChanged: () -> Unit
): EventObserver {
    val wrappedObserver = EventObserver { onChanged.invoke() }
    observe(owner, wrappedObserver as Observer<Event>)
    return wrappedObserver
}