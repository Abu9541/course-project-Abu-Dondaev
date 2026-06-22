package ru.ncfu.autoshow.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import ru.ncfu.autoshow.data.session.SessionManager

/** Добавляет заголовок Authorization: Bearer <token> ко всем запросам, кроме /api/auth/. */
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = sessionManager.token
        val isAuthEndpoint = request.url.encodedPath.contains("/api/auth/")

        val finalRequest = if (!token.isNullOrBlank() && !isAuthEndpoint) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        return chain.proceed(finalRequest)
    }
}
