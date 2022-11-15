package k.studio.tiktokrec.data.source.db

import androidx.room.Database
import androidx.room.RoomDatabase
import k.studio.tiktokrec.data.vo.db.OrderHeart
import k.studio.tiktokrec.data.vo.db.UniqueAction
import k.studio.tiktokrec.data.vo.db.UserState

/**
 * The Room Database.
 *
 * Note that exportSchema should be true in production databases.
 */
@Database(
    entities = [UserState::class, OrderHeart::class, UniqueAction::class],
    version = 1,
    exportSchema = false
)
abstract class TikTokRecDatabase : RoomDatabase() {

    abstract fun appStoreDao(): UserStateDao
    abstract fun tikTokProfileDao(): OrdersDao
    abstract fun uniqueActionsDao(): UniqueActionsDao
}