package ltd.royalgreen.pacecloud.util

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class ResponseStatus<out T>(val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): ResponseStatus<T> {
            return ResponseStatus(data, null)
        }

        fun <T> error(data: T? = null, msg: String): ResponseStatus<T> {
            return ResponseStatus(data, msg)
        }
    }
}