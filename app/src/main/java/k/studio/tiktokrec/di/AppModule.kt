package k.studio.tiktokrec.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import k.studio.tiktokrec.data.source.db.OrdersDao
import k.studio.tiktokrec.data.source.db.TikTokRecDatabase
import k.studio.tiktokrec.data.source.db.UniqueActionsDao
import k.studio.tiktokrec.data.source.db.UserStateDao
import k.studio.tiktokrec.data.source.remote.TikTokProfilePageAdapter
import k.studio.tiktokrec.data.source.remote.TikTokProfileService
import k.studio.tiktokrec.data.source.remote.firebase.realtimedb.RDBService
import k.studio.tiktokrec.data.source.remote.firebase.realtimedb.RemoteSource
import k.studio.tiktokrec.utils.logD
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Module to tell Hilt how to provide instances of types that cannot be constructor-injected.
 *
 * As these types are scoped to the application lifecycle using @Singleton, they're installed
 * in Hilt's ApplicationComponent.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideRemoteDBService(rdbService: RDBService): RemoteSource {
        return rdbService
    }

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TikTokProfileRequest

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class DefaultRequest

    @Provides
    fun provideTikTokUrl(): String = "https://www.tiktok.com/"

    @Provides
    @Singleton
    @TikTokProfileRequest
    fun provideProfileRetrofit(tikTokUrl: String): Retrofit = Retrofit.Builder()
        .client(OkHttpClient.Builder().addInterceptor { chain ->
            val request: Request = chain.request()

            val t1 = System.nanoTime()
            java.lang.String.format(
                "Sending request %s on %s %n %s",
                request.url(), chain.connection(), request.headers()
            ).logD("Retrofit")

            val response = chain.proceed(request)

            val t2 = System.nanoTime()
            String.format(
                "Received response for %s in %.1fms %n %s",
                response.request().url(), (t2 - t1) / 1e6, response.headers()
            ).logD("Retrofit")

            response
        }.build())
        .addConverterFactory(TikTokProfilePageAdapter.FACTORY)
        .baseUrl(tikTokUrl)
        .build()

    @Provides
    @DefaultRequest
    @Singleton
    fun provideRetrofit(tikTokUrl: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(tikTokUrl)
        .build()

    @Provides
    @Singleton
    fun provideTikTokService(@TikTokProfileRequest retrofit: Retrofit): TikTokProfileService =
        retrofit.create(TikTokProfileService::class.java)

    @Singleton
    @Provides
    fun provideUserStateDao(tikTokRecDatabase: TikTokRecDatabase): UserStateDao {
        return tikTokRecDatabase.appStoreDao()
    }

    @Singleton
    @Provides
    fun provideTikTokProfileDao(tikTokRecDatabase: TikTokRecDatabase): OrdersDao {
        return tikTokRecDatabase.tikTokProfileDao()
    }

    @Singleton
    @Provides
    fun provideUniqueActionsDao(tikTokRecDatabase: TikTokRecDatabase): UniqueActionsDao {
        return tikTokRecDatabase.uniqueActionsDao()
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): TikTokRecDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TikTokRecDatabase::class.java,
            "TikTokRec_3.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Singleton
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}