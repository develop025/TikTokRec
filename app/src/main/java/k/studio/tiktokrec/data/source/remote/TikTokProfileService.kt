package k.studio.tiktokrec.data.source.remote

import k.studio.tiktokrec.data.vo.retrofit.TikTokProfilePage
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface TikTokProfileService {

    @GET("/@{username}")
    suspend fun getUserPage(@Path("username") username: String): Response<TikTokProfilePage>
}