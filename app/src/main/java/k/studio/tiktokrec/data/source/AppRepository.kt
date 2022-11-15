package k.studio.tiktokrec.data.source

import androidx.annotation.WorkerThread
import androidx.paging.PagingSource
import com.google.firebase.database.DatabaseError
import k.studio.realtime_ktx.vo.DataResponse
import k.studio.tiktokrec.data.error.ErrorCodeIsTikTokUserExist
import k.studio.tiktokrec.data.error.ErrorFirebaseUpdateChildren
import k.studio.tiktokrec.data.source.db.OrdersDao
import k.studio.tiktokrec.data.source.db.UniqueActionsDao
import k.studio.tiktokrec.data.source.db.UserStateDao
import k.studio.tiktokrec.data.source.remote.TikTokProfileService
import k.studio.tiktokrec.data.source.remote.firebase.realtimedb.RDBService
import k.studio.tiktokrec.data.source.remote.firebase.realtimedb.RemoteSource
import k.studio.tiktokrec.data.vo.Resource
import k.studio.tiktokrec.data.vo.db.OrderHeart
import k.studio.tiktokrec.data.vo.db.UniqueAction
import k.studio.tiktokrec.data.vo.db.UserState
import k.studio.tiktokrec.utils.logD
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val userStateDao: UserStateDao,
    private val ordersDao: OrdersDao,
    private val uniqueActionsDao: UniqueActionsDao,
    private val tikTokProfileService: TikTokProfileService,
    private val rdbService: RemoteSource
) {

    private var ordersHeartsChildEventListener: RDBService.CustomChildEventListener? = null

    companion object {
        const val HEART_PRICE_BUY = 5
    }

    //region UserState
    suspend fun getUserState(): UserState {
        var userState = userStateDao.getUserState()
        if (userState == null) {
            userState = UserState.getEmptyInstance()
            userStateDao.insertUserState(userState)
        }
        return userState
    }

    suspend fun getTikTokUsername(): String? {
        return userStateDao.getTikTokUsername()
    }

    suspend fun saveTikTokUsername(username: String) {
        val userState = userStateDao.getUserState() ?: run {
            UserState.getEmptyInstance()
        }

        userState.tikTokUsername = username

        userStateDao.insertUserState(userState)
    }

    fun isTikTokUserExist(username: String): Flow<Resource<Boolean, ErrorCodeIsTikTokUserExist>> {
        return flow {
            emit(Resource.loading())
            try {
                val requestResult = tikTokProfileService.getUserPage(username)
                val result: Resource<Boolean, ErrorCodeIsTikTokUserExist> =
                    if (requestResult.isSuccessful)
                        Resource.success(requestResult.body()?.profileExist == true)
                    else if (requestResult.code() == 404)
                        Resource.error(ErrorCodeIsTikTokUserExist.NO_INTERNET_CONNECTION)
                    else
                        Resource.error(ErrorCodeIsTikTokUserExist.USERNAME_NOT_FOUND)
                emit(result)
            } catch (e: Exception) {
                emit(Resource.error(ErrorCodeIsTikTokUserExist.CANNOT_CONNECT))
            }
        }
    }
    //endregion UserState

    fun getRequiredAmountOfStars(heartsNumber: Int): Int {
        "AppRepository.getRequiredAmountOfStars".logD()
        return heartsNumber * HEART_PRICE_BUY
    }

    //TODO: impl
    fun enoughStarNumber(heartsNumber: Int): Boolean {
        "AppRepository.enoughStarNumber heartsNumber:$heartsNumber".logD()
        return true
    }

    //region OrderHeart
    //region DB
    fun save(vararg orderHeart: OrderHeart) {
        "AppRepository.save orderHeart".logD()
        ordersDao.insert(*orderHeart)
    }

    fun getOrderHeartForAction(): OrderHeart? {
        "AppRepository.getOrderHeartForAction".logD()
        return ordersDao.getOrderHeartsForAction()
    }

    fun getOrdersHeartsByCurrentUserPagingSource(): PagingSource<Int, OrderHeart> {
        return ordersDao.getOrdersHeartsByCurrentUserPagingSource()
    }

    fun getOrderHeartAt(): Long {
        return userStateDao.getOrderHeartAt()
    }

    fun setOrderHeartAt(orderAt: Long) {
        userStateDao.setOrderHeartAt(orderAt)
    }
    //endregion DB

    //region Remote
    fun createWindUpTask(
        orderHeart: OrderHeart,
        stars: Long,
        onSuccess: () -> Unit,
        onFailure: (errorCode: ErrorFirebaseUpdateChildren) -> Unit
    ) {
        rdbService.addOrderHearts(
            orderHeart,
            stars,
            onSuccess,
            onFailure
        )
    }

    suspend fun subscribeOnceOrderHeart(
        orderAt: Long
    ): DataResponse<List<OrderHeart>> {
        return rdbService.subscribeOnceOrderHeart(orderAt)
    }

    fun getOrderHeartsByUsername(
        username: String,
        onAdded: (orderHeart: OrderHeart) -> Unit,
        onRemoved: (orderHeart: OrderHeart) -> Unit,
        onError: (errorCode: DatabaseError) -> Unit
    ) {
        rdbService.subscribeOrderHeartsByUsername(username, onAdded, onRemoved, onError)
    }

    @WorkerThread
    fun unsubscribeOrdersHearts() {
        "AppRepository.unsubscribeOrdersHearts".logD()
        ordersHeartsChildEventListener?.let { childEventListener ->
            rdbService.unsubscribeOrdersHearts(childEventListener)
        }
    }
    //endregion Remote
    //endregion OrderHeart

    //region UniqueAction
    fun save(uniqueAction: UniqueAction): Long {
        "AppRepository.save uniqueAction".logD()
        return uniqueActionsDao.insert(uniqueAction)
    }

    fun clearUniqueActions() {
        "AppRepository.clearUniqueActions".logD()
        uniqueActionsDao.clear()
    }

    fun clearOrders() {
        "AppRepository.clearOrders".logD()
        ordersDao.clear()
    }

    fun delete(orderHeart: OrderHeart) {
        ordersDao.delete(orderHeart)
    }

    fun clearOrdersHeartsByUsername(tikTokUsername: String?) {
        ordersDao.clearByUsername(tikTokUsername)
    }

    fun resetOrderAt() {
        "AppRepository.resetOrderAt".logD()
        userStateDao.setOrderHeartAt(0)
    }
    //endregion UniqueAction
}