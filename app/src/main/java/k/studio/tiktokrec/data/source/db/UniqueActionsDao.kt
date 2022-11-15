package k.studio.tiktokrec.data.source.db

import android.database.sqlite.SQLiteException
import androidx.room.*
import k.studio.tiktokrec.data.vo.db.UniqueAction

@Dao
interface UniqueActionsDao {

    @Query("SELECT COUNT(*) FROM uniqueaction WHERE username = :username AND videoLink=:videoLink")
    @Throws(SQLiteException::class)
    fun isExist(username: String, videoLink: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Throws(SQLiteException::class)
    fun insert(uniqueAction: UniqueAction): Long

    @Delete
    @Throws(SQLiteException::class)
    fun delete(vararg uniqueAction: UniqueAction)

    @Query("DELETE FROM uniqueaction")
    @Throws(SQLiteException::class)
    fun clear()
}
