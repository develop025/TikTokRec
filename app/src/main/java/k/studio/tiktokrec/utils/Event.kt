package k.studio.tiktokrec.utils

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun getContent(result: (content: T?, hasBeenHandled: Boolean) -> Unit) {
        return if (hasBeenHandled) {
            result(null, hasBeenHandled)
        } else {
            hasBeenHandled = true
            result(content, false)
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}