package k.studio.tiktokrec.data.vo.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Immutable model class for a User state. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * Only single item
 *
 * @param tikTokUsername TikTok username for current user
 * @param isUserAuth true - if user is auth
 * @param isEmailConfirmed true - if email is confirmed
 * @param id id of the user state
 */
@Entity(tableName = "userstate")
data class UserState @JvmOverloads constructor(
    var tikTokUsername: String?,
    var isUserAuth: Boolean,
    var isEmailConfirmed: Boolean,
    var isPermissionAccessibilityGranted: Boolean,
    var isPermissionDrawOverAnotherAppsGranted: Boolean,
    var orderHeartAt: Long,
    @PrimaryKey var id: Int = 0
) {
    companion object {
        fun getEmptyInstance(): UserState {
            return UserState(
                tikTokUsername = null,
                isUserAuth = false,
                isEmailConfirmed = false,
                isPermissionAccessibilityGranted = false,
                isPermissionDrawOverAnotherAppsGranted = false,
                orderHeartAt = 0,
                id = 0
            )
        }
    }
}