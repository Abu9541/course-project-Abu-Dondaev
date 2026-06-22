package ru.ncfu.autoshow.di

import android.content.Context
import androidx.room.Room
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ncfu.autoshow.BuildConfig
import ru.ncfu.autoshow.data.local.AppDatabase
import ru.ncfu.autoshow.data.remote.ApiService
import ru.ncfu.autoshow.data.remote.AuthInterceptor
import ru.ncfu.autoshow.data.repository.*
import ru.ncfu.autoshow.data.session.SessionManager
import ru.ncfu.autoshow.data.settings.SettingsManager
import java.util.concurrent.TimeUnit

/**
 * Ручной контейнер зависимостей (Service Locator). Создаётся один раз в Application
 * и предоставляет репозитории слою представления.
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val sessionManager = SessionManager(appContext)
    val settingsManager = SettingsManager(appContext)

    private val gson = GsonBuilder().setLenient().create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
        else HttpLoggingInterceptor.Level.NONE
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(sessionManager))
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val api: ApiService = retrofit.create(ApiService::class.java)

    private val database = Room.databaseBuilder(
        appContext, AppDatabase::class.java, "autoshow.db"
    ).fallbackToDestructiveMigration().build()

    // ----------------------------- репозитории -----------------------------
    val authRepository = AuthRepository(api, sessionManager)
    val catalogRepository = CatalogRepository(api, database.vehicleDao())
    val testDriveRepository = TestDriveRepository(api)
    val orderRepository = OrderRepository(api)
    val paymentRepository = PaymentRepository(api)
    val favoriteRepository = FavoriteRepository(api)
    val reviewRepository = ReviewRepository(api)
    val notificationRepository = NotificationRepository(api)
    val profileRepository = ProfileRepository(api, sessionManager)
    val dashboardRepository = DashboardRepository(api)
    val adminRepository = AdminRepository(api)

    init {
        // Восстановление сохранённой сессии и настроек при старте
        runBlocking {
            sessionManager.load()
            settingsManager.load()
        }
    }
}
