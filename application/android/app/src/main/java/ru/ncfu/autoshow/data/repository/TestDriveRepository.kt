package ru.ncfu.autoshow.data.repository

import ru.ncfu.autoshow.core.safeApiCall
import ru.ncfu.autoshow.data.remote.ApiService
import ru.ncfu.autoshow.data.remote.dto.TestDriveDto
import ru.ncfu.autoshow.data.remote.dto.TestDriveRequestDto

/** Репозиторий записей на тест-драйв. */
class TestDriveRepository(private val api: ApiService) {

    suspend fun book(body: TestDriveRequestDto): Result<TestDriveDto> =
        safeApiCall { api.bookTestDrive(body) }

    suspend fun getMine(): Result<List<TestDriveDto>> = safeApiCall { api.myTestDrives() }

    suspend fun getAll(): Result<List<TestDriveDto>> = safeApiCall { api.allTestDrives() }

    suspend fun confirm(id: Long): Result<TestDriveDto> = safeApiCall { api.confirmTestDrive(id) }

    suspend fun reject(id: Long): Result<TestDriveDto> = safeApiCall { api.rejectTestDrive(id) }

    suspend fun complete(id: Long): Result<TestDriveDto> = safeApiCall { api.completeTestDrive(id) }

    suspend fun cancel(id: Long): Result<TestDriveDto> = safeApiCall { api.cancelTestDrive(id) }
}
