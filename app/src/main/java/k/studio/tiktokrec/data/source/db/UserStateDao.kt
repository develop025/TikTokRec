package k.studio.tiktokrec.data.source.db

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import k.studio.tiktokrec.data.vo.db.UserState

@Dao
interface UserStateDao {

    @Query("SELECT * FROM userstate WHERE id = 0")
    @Throws(SQLiteException::class)
    suspend fun getUserState(): UserState?

    @Query("SELECT userstate.tikTokUsername FROM userstate WHERE id = 0")
    @Throws(SQLiteException::class)
    suspend fun getTikTokUsername(): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Throws(SQLiteException::class)
    suspend fun insertUserState(userState: UserState)

    @Query("SELECT userstate.orderHeartAt FROM userstate WHERE id = 0")
    @Throws(SQLiteException::class)
    fun getOrderHeartAt(): Long

    @Query("UPDATE userstate SET orderHeartAt =:orderHeartAt WHERE id = 0")
    @Throws(SQLiteException::class)
    fun setOrderHeartAt(orderHeartAt: Long)
}