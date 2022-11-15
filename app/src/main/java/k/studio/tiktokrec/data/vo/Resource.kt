package k.studio.tiktokrec.data.vo

import k.studio.tiktokrec.utils.ResourceDevelopException

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Resource<out S, T : Enum<T>>(
    val status: Status,
    val data: S?,
    private val errorCode: T?
) {
    companion object {
        fun <S, T : Enum<T>> success(data: S?): Resource<S, T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <S, T : Enum<T>> error(msg: T?): Resource<S, T> {
            return Resource(Status.ERROR, null, msg)
        }

        fun <S, T : Enum<T>> loading(): Resource<S, T> {
            return Resource(Status.LOADING, null, null)
        }
    }

    /**
     * @return NonNull errorCode
     */
    fun getError(): T {
        if (status == Status.ERROR)
            return errorCode!!
        else
            throw ResourceDevelopException("Resource. Fail use errorCode in Resource")
    }
}