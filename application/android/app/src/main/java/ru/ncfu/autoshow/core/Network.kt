package ru.ncfu.autoshow.core

import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/** Исключение API с человеко-читаемым сообщением и HTTP-кодом. */
class ApiException(message: String, val code: Int = -1) : Exception(message)

/**
 * Единая безопасная обёртка сетевых вызовов: переводит исключения Retrofit/IO
 * в [Result] с понятным сообщением (обработка состояний ошибки и оффлайна).
 */
suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> = withContext(Dispatchers.IO) {
    try {
        Result.success(call())
    } catch (e: HttpException) {
        Result.failure(ApiException(parseHttpError(e), e.code()))
    } catch (e: IOException) {
        Result.failure(ApiException("Нет соединения с сервером. Проверьте подключение к сети.", 0))
    } catch (e: Exception) {
        Result.failure(ApiException(e.message ?: "Неизвестная ошибка", -1))
    }
}

private fun parseHttpError(e: HttpException): String = try {
    val body = e.response()?.errorBody()?.string()
    if (body.isNullOrBlank()) httpMessage(e.code())
    else JsonParser.parseString(body).asJsonObject
        .takeIf { it.has("message") }?.get("message")?.asString ?: httpMessage(e.code())
} catch (ex: Exception) {
    httpMessage(e.code())
}

private fun httpMessage(code: Int): String = when (code) {
    401 -> "Требуется вход в систему"
    403 -> "Недостаточно прав для выполнения операции"
    404 -> "Ресурс не найден"
    409 -> "Конфликт данных"
    422 -> "Невозможно выполнить операцию"
    in 500..599 -> "Ошибка сервера. Попробуйте позже."
    else -> "Ошибка запроса (код $code)"
}
