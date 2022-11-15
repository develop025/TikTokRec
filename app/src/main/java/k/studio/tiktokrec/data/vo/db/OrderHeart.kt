package k.studio.tiktokrec.data.vo.db

import androidx.room.Entity
import androidx.room.Index
import com.google.firebase.database.ServerValue
import java.util.*

/**
 * Videos for like
 * @param username - username for following
 * @param videoLink - id from tiktok video url
 * @param heartsNumber - order total number like for video
 * @property id - not use as parameter exist only for write to Firebase
 */
@Entity(
    tableName = "orderheart",
    indices = [Index("id", "videoLink", "username")],
    primaryKeys = ["id", "username", "videoLink"]
)
data class OrderHeart(
    var id: String = UUID.randomUUID().toString(),
    var username: String,
    var videoLink: String,
    var heartsNumber: Long,
    var orderAt: Long = 0,
    var firebaseKey: String = ""
) {

    /**
     * For firebase
     */
    constructor() : this(UUID.randomUUID().toString(), "", "", 0, 0)

    fun toMap(firebaseKey: String): Any {
        return mapOf(
            "id" to id,
            "username" to username,
            "videoLink" to videoLink,
            "heartsNumber" to heartsNumber,
            "orderAt" to ServerValue.TIMESTAMP,
            "firebaseKey" to firebaseKey
        )
    }
}