package k.studio.tiktokrec.data.vo

import k.studio.tiktokrec.utils.ResourceDevelopException

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class ActionResource<out S, T : Enum<T>>(
    val status: ActionStatus,
    val data: S?,
    private val errorCode: T?
) {
    companion object {
        fun <S, T : Enum<T>> success(data: S?): ActionResource<S, T> {
            return ActionResource(ActionStatus.SUCCESS, data, null)
        }

        fun <S, T : Enum<T>> error(msg: T?): ActionResource<S, T> {
            return ActionResource(ActionStatus.ERROR, null, msg)
        }

        fun <S, T : Enum<T>> processing(): ActionResource<S, T> {
            return ActionResource(ActionStatus.PROCESSING, null, null)
        }
    }

    /**
     * @return NonNull errorCode
     */
    fun getError(): T {
        if (status == ActionStatus.ERROR)
            return errorCode!!
        else
            throw ResourceDevelopException("ActionResource. Fail use errorCode in Resource")
    }
}