package com.mishamoovex.liveevents.event

class DataEvent<out T: Any>(private val data: T) {

    private var hasBeenHandled = false

    fun getDataIfNotHandled(): T? =
        if (hasBeenHandled){
         null
    } else{
            hasBeenHandled = true
            data
        }
}