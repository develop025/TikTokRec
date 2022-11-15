package k.studio.tiktokrec.data.vo.db

import androidx.room.Entity
import androidx.room.Index

/**
 * Users for following
 * @param username - username for following
 * @param number - order total number following
 */
@Entity(
    tableName = "orderfollowing",
    indices = [Index("username")],
    primaryKeys = ["username"]
)
data class OrderFollowing(
    val username: String,
    val number: Long
)