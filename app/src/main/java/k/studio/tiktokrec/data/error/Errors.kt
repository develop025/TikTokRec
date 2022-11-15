package k.studio.tiktokrec.data.error

enum class ErrorCodeIsTikTokUserExist(val code: Int) {
    USERNAME_NOT_FOUND(1000),
    CANNOT_CONNECT(1001),
    NO_INTERNET_CONNECTION(1002)
}

enum class ErrorFirebaseUpdateChildren(val code: Int) {
    ON_FAILURE(1003),
    ON_CANCEL(1004)
}

enum class ErrorFirebaseEventListener(val code: Int) {
    EMPTY_LIST(0),

    /** Internal use */
    DATA_STALE(-1),

    /** The server indicated that this operation failed */
    OPERATION_FAILED(-2),

    /** This client does not have permission to perform this operation */
    PERMISSION_DENIED(-3),

    /** The operation had to be aborted due to a network disconnect */
    DISCONNECTED(-4),

    // Preempted was removed, this is for here for completeness and history
    // PREEMPTED(-5),

    /** The supplied auth token has expired */
    EXPIRED_TOKEN(-6),

    /**
     * The specified authentication token is invalid. This can occur when the token is malformed,
     * expired, or the secret that was used to generate it has been revoked.
     */
    INVALID_TOKEN(-7),

    /** The transaction had too many retries */
    MAX_RETRIES(-8),

    /** The transaction was overridden by a subsequent set */
    OVERRIDDEN_BY_SET(-9),

    /** The service is unavailable */
    UNAVAILABLE(-10),

    /** An exception occurred in user code */
    USER_CODE_EXCEPTION(-11),

    // client codes
    /** The operation could not be performed due to a network error. */
    NETWORK_ERROR(-24),

    /** The write was canceled locally */
    WRITE_CANCELED(-25),

    /**
     * An unknown error occurred. Please refer to the error message and error details for more
     * information.
     */
    UNKNOWN_ERROR(-999);

    companion object {
        fun getByCode(code: Int): ErrorFirebaseEventListener {
            return values().filter { it.code == code }.firstOrNull() ?: UNKNOWN_ERROR
        }
    }
}