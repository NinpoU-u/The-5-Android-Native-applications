package com.example.musio.other

// Wrapper class for Resource class to get content ( data )
open class Event<out T>(private val data: T) {

    // emit single time
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            data
        }
    }

    fun peekContent() = data
}