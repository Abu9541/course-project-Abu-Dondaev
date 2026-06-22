package ru.ncfu.autoshow.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import ru.ncfu.autoshow.data.remote.dto.AuthResponseDto
import ru.ncfu.autoshow.data.remote.dto.UserDto

private val Context.dataStore by preferencesDataStore(name = "autoshow_session")

/** Текущая сессия пользователя. */
data class Session(
    val token: String? = null,
    val userId: Long = 0,
    val role: String = "",
    val fullName: String = "",
    val email: String = ""
) {
    val isLoggedIn: Boolean get() = !token.isNullOrBlank()
    val isAdmin: Boolean get() = role == "ADMIN"
    val isManager: Boolean get() = role == "MANAGER"
    val isStaff: Boolean get() = isAdmin || isManager
    val isClient: Boolean get() = role == "CLIENT"
}

/**
 * Безопасное хранение JWT-токена и данных сессии (DataStore) с зеркалом в памяти
 * для синхронного доступа из сетевого перехватчика.
 */
class SessionManager(private val context: Context) {

    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val USER_ID = longPreferencesKey("user_id")
        val ROLE = stringPreferencesKey("role")
        val NAME = stringPreferencesKey("name")
        val EMAIL = stringPreferencesKey("email")
    }

    private val _session = MutableStateFlow(Session())
    val session: StateFlow<Session> = _session.asStateFlow()

    val token: String? get() = _session.value.token

    /** Загрузка сохранённой сессии при старте приложения. */
    suspend fun load() {
        val p = context.dataStore.data.first()
        _session.value = Session(
            token = p[Keys.TOKEN],
            userId = p[Keys.USER_ID] ?: 0,
            role = p[Keys.ROLE] ?: "",
            fullName = p[Keys.NAME] ?: "",
            email = p[Keys.EMAIL] ?: ""
        )
    }

    suspend fun save(auth: AuthResponseDto) {
        context.dataStore.edit { p ->
            p[Keys.TOKEN] = auth.token
            p[Keys.USER_ID] = auth.user.id
            p[Keys.ROLE] = auth.user.role
            p[Keys.NAME] = auth.user.fullName
            p[Keys.EMAIL] = auth.user.email
        }
        _session.value = Session(
            auth.token, auth.user.id, auth.user.role, auth.user.fullName, auth.user.email
        )
    }

    /** Обновление данных профиля без смены токена. */
    suspend fun updateUser(user: UserDto) {
        context.dataStore.edit { p ->
            p[Keys.ROLE] = user.role
            p[Keys.NAME] = user.fullName
            p[Keys.EMAIL] = user.email
        }
        _session.value = _session.value.copy(
            role = user.role, fullName = user.fullName, email = user.email
        )
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
        _session.value = Session()
    }
}
