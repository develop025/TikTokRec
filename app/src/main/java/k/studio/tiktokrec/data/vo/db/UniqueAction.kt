package k.studio.tiktokrec.data.vo.db

import androidx.room.Entity
import androidx.room.Index

/**
 * Videos for like
 * @param username - username for following
 * @param videoLink - id from tiktok video url
 */
@Entity(
    tableName = "uniqueaction",
    indices = [Index("username", "videoLink")],
    primaryKeys = ["username", "videoLink"]
)
data class UniqueAction(
    val username: String,
    val videoLink: String,
) {
    companion object {
        fun fromOrderHeart(orderHeart: OrderHeart): UniqueAction {
            return UniqueAction(orderHeart.username, orderHeart.videoLink)
        }
    }

    override fun toString(): String {
        return "username:$username, videoLink:$videoLink"
    }
}