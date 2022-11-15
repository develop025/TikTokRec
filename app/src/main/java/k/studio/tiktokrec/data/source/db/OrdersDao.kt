package k.studio.tiktokrec.data.source.db

import android.database.sqlite.SQLiteException
import androidx.paging.PagingSource
import androidx.room.*
import k.studio.tiktokrec.data.vo.db.OrderHeart

@Dao
interface OrdersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Throws(SQLiteException::class)
    fun insert(vararg video: OrderHeart)

    @Delete
    @Throws(SQLiteException::class)
    fun delete(vararg video: OrderHeart)

    @Query("SELECT * FROM orderheart WHERE username = :username")
    @Throws(SQLiteException::class)
    fun get(username: String): List<OrderHeart>

    @Query(
        "SELECT orderheart.* FROM orderheart " +
                "LEFT JOIN userstate ON orderheart.username != userstate.tikTokUsername " +
                "LEFT JOIN uniqueaction ON orderheart.username = uniqueaction.username AND orderheart.videoLink != uniqueaction.videoLink " +
                "ORDER BY orderheart.orderAt ASC "
    )
    @Throws(SQLiteException::class)
    fun getForAction(): List<OrderHeart>

    @Query("SELECT orderheart.* FROM orderheart LEFT JOIN userstate ON orderheart.username = userstate.tikTokUsername ORDER BY orderheart.orderAt ASC")
    fun getOrdersHeartsByCurrentUserPagingSource(): PagingSource<Int, OrderHeart>

    @Query(
        "SELECT orderheart.*" +
                "FROM orderheart " +
                "LEFT JOIN uniqueaction " +
                "ON orderheart.username = uniqueaction.username " +
                "   AND orderheart.videoLink == uniqueaction.videoLink " +
                "WHERE  " +
                "(orderheart.username != (SELECT tikTokUsername FROM userstate WHERE id = 0)) " +
                "AND uniqueaction.videoLink IS NULL " +
                "ORDER BY orderheart.orderAt ASC " +
                "LIMIT 1"
    )
    @Throws(SQLiteException::class)
    fun getOrderHeartsForAction(): OrderHeart?

    @Query("DELETE FROM orderheart")
    @Throws(SQLiteException::class)
    fun clear()

    @Query("DELETE FROM orderheart WHERE username=:tikTokUsername")
    @Throws(SQLiteException::class)
    fun clearByUsername(tikTokUsername: String?)
}
