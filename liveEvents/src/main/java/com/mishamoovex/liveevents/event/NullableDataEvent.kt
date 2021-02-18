package com.mishamoovex.liveevents.event

class NullableDataEvent<out T>(private val data: T? = null) {

    private var hasBeenHandled = false

    fun hasBeenHandled(): Boolean = hasBeenHandled

    fun getData(): T? {
        hasBeenHandled = true
        return data
    }
}