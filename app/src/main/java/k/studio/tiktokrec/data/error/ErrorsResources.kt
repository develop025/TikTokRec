package k.studio.tiktokrec.data.error

import com.google.firebase.database.DatabaseError
import k.studio.tiktokrec.R

object ErrorsResources {

    fun getResource(errorCodeIsTikTokUserExist: ErrorCodeIsTikTokUserExist): Int {
        return when (errorCodeIsTikTokUserExist) {
            ErrorCodeIsTikTokUserExist.USERNAME_NOT_FOUND -> R.string.error_check_tik_tok_username_spelling
            ErrorCodeIsTikTokUserExist.CANNOT_CONNECT -> R.string.no_internet_connection_tiktok
            ErrorCodeIsTikTokUserExist.NO_INTERNET_CONNECTION -> R.string.no_internet_connection_tiktok
        }
    }

    fun getResource(errorFirebaseUpdateChildren: ErrorFirebaseUpdateChildren): Int {
        return when (errorFirebaseUpdateChildren) {
            ErrorFirebaseUpdateChildren.ON_FAILURE -> R.string.unknown_error
            ErrorFirebaseUpdateChildren.ON_CANCEL -> R.string.unknown_error
        }
    }

    enum class FirebaseObjectType {
        ORDER_HEART
    }

    /**
     * Error message from com.google.firebase.database.DatabaseError
     */
    fun getResource(
        errorFirebaseEventListener: ErrorFirebaseEventListener,
        firebaseObjectType: FirebaseObjectType
    ): Int {
        return when (errorFirebaseEventListener) {
            ErrorFirebaseEventListener.EMPTY_LIST -> when (firebaseObjectType) {
                FirebaseObjectType.ORDER_HEART -> R.string.new_orders_missing
            }
            ErrorFirebaseEventListener.DATA_STALE -> R.string.data_stale
            ErrorFirebaseEventListener.OPERATION_FAILED -> R.string.operation_failed
            ErrorFirebaseEventListener.PERMISSION_DENIED -> R.string.permission_denied
            ErrorFirebaseEventListener.DISCONNECTED -> R.string.disconnected
            ErrorFirebaseEventListener.EXPIRED_TOKEN -> R.string.expired_token
            ErrorFirebaseEventListener.INVALID_TOKEN -> R.string.invalid_token
            ErrorFirebaseEventListener.MAX_RETRIES -> R.string.max_retries
            ErrorFirebaseEventListener.OVERRIDDEN_BY_SET -> R.string.overridden_by_set
            ErrorFirebaseEventListener.UNAVAILABLE -> R.string.unavailable
            ErrorFirebaseEventListener.USER_CODE_EXCEPTION -> R.string.user_code_exception
            ErrorFirebaseEventListener.NETWORK_ERROR -> R.string.network_error
            ErrorFirebaseEventListener.WRITE_CANCELED -> R.string.write_canceled
            ErrorFirebaseEventListener.UNKNOWN_ERROR -> R.string.unknown_error
        }
    }
}
