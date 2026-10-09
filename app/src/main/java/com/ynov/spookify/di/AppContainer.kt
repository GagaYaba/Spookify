package com.ynov.spookify.di

import android.content.Context
import androidx.room.Room
import com.ynov.spookify.BuildConfig
import com.ynov.spookify.data.local.AppDatabase
import com.ynov.spookify.data.local.settingsDataStore
import com.ynov.spookify.data.remote.AudiusApi
import com.ynov.spookify.data.remote.apiJson
import com.ynov.spookify.data.repository.ArtistRepository
import com.ynov.spookify.data.repository.PlaylistRepository
import com.ynov.spookify.data.repository.SettingsRepository
import com.ynov.spookify.data.repository.TrackRepository
import com.ynov.spookify.player.PlayerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/** Injection manuelle : une seule instance de chaque dépendance, construite à la demande. */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                val url = request.url.newBuilder().addQueryParameter("app_name", BuildConfig.APP_NAME).build()
                chain.proceed(request.newBuilder().url(url).build())
            }
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
                }
            }
            .build()
    }

    private val api: AudiusApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(apiJson.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AudiusApi::class.java)
    }

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(appContext, AppDatabase::class.java, "spookify.db")
            // Le cache se reconstruit depuis le réseau : pas de migration à maintenir pendant le développement.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    val trackRepository: TrackRepository by lazy { TrackRepository(api, database.trackDao()) }
    val playlistRepository: PlaylistRepository by lazy {
        PlaylistRepository(api, database.playlistDao(), database.trackDao())
    }
    val artistRepository: ArtistRepository by lazy {
        ArtistRepository(api, database.artistDao(), database.trackDao())
    }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(appContext.settingsDataStore) }

    // Le contrôleur vit autant que le process : il doit survivre aux activités.
    val playerController: PlayerController by lazy {
        PlayerController(
            context = appContext,
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
            trackRepository = trackRepository,
            settingsRepository = settingsRepository,
        )
    }
}
