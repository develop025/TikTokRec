package k.studio.tiktokrec.data.vo.datasource

import k.studio.tiktokrec.data.source.AppRepository
import k.studio.tiktokrec.data.vo.db.OrderHeart
import k.studio.tiktokrec.data.vo.db.UniqueAction
import k.studio.tiktokrec.utils.logD
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UniqueActionDataSource @Inject constructor(private val appRepository: AppRepository) {

    fun saveUniqueAction(orderHeart: OrderHeart) {
        val uniqueAction = UniqueAction.fromOrderHeart(orderHeart)
        "UniqueActionDataSource.saveUniqueAction $uniqueAction".logD()
        appRepository.save(uniqueAction)
    }
}