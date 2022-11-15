package k.studio.tiktokrec.data.vo.db

import androidx.room.Entity
import androidx.room.Index
import java.util.*

@Entity(
    tableName = "error",
    indices = [Index("id")],
    primaryKeys = ["id"]
)
data class BotError(
    var id: String = UUID.randomUUID().toString(),
    var errorCode: Int,
    var errorRes: Int
)