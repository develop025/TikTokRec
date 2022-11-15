package k.studio.tiktokrec.utils

import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import k.studio.tiktokrec.R


object FirebaseException {
    fun getMessageRes(exception: Exception?): Int {
        "class:${exception?.javaClass?.name}".logD()
        "cause:${exception?.cause}".logD()
        "message:${exception?.message}".logD()

        return when (exception) {
            is ApiException -> {
                ("FirebaseExceptionMessages. Exception:" +
                        "\nclass:${exception.javaClass.name}" +
                        "\ncause:${exception.cause}" +
                        "\nmessage:${exception.message}" +
                        "\nstatus:${exception.status}" +
                        "\nstatusCode:${exception.statusCode}").logW()
                when(exception.statusCode){
                    13->R.string.account_authorization_fail
                    else->R.string.no_internet_connection_firebase
                }
            }
            is FirebaseApiNotAvailableException -> R.string.firebase_api_not_available
            is FirebaseTooManyRequestsException -> R.string.firebase_block_request
            is FirebaseNetworkException -> R.string.no_internet_connection_firebase
            is FirebaseAuthException -> {
                "errorCode:${exception.errorCode}".logD()
                when (exception.errorCode) {
                    //FirebaseAuthInvalidCredentialsException
                    "ERROR_INVALID_EMAIL" -> R.string.error_invalid_email
                    "ERROR_WRONG_PASSWORD" -> R.string.error_wrong_password
                    //FirebaseAuthUserCollisionException
                    "ERROR_EMAIL_ALREADY_IN_USE" -> R.string.error_email_already_in_use
                    "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> R.string.error_email_already_in_use
                    "ERROR_CREDENTIAL_ALREADY_IN_USE" -> R.string.error_email_already_in_use
                    "ERROR_WEAK_PASSWORD" -> R.string.error_weak_password
                    "ERROR_USER_NOT_FOUND" -> R.string.error_user_not_found
                    else -> {
                        ("FirebaseExceptionMessages. Exception:" +
                                "\nclass:${exception.javaClass.name}" +
                                "\ncause:${exception.cause}" +
                                "\nmessage:${exception.message}" +
                                "\nerrorCode:${exception.errorCode}").logW()
                        R.string.firebase_unknown_error
                    }
                }
            }
            else -> {
                ("FirebaseExceptionMessages. Exception:" +
                        "\nclass:${exception?.javaClass?.name}" +
                        "\ncause:${exception?.cause}" +
                        "\nmessage:${exception?.message}").logW()
                R.string.firebase_unknown_error
            }
        }
    }
}

