package ru.ncfu.autoshow.data.remote

import retrofit2.http.*
import ru.ncfu.autoshow.data.remote.dto.*

/**
 * Контракт REST API серверной части (api_client слой клиента в адаптации PCMEF).
 * Все методы — suspend; ошибки HTTP бросаются как HttpException.
 */
interface ApiService {

    // ---------------- Аутентификация ----------------
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto

    // ---------------- Профиль ----------------
    @GET("api/users/me")
    suspend fun me(): UserDto

    @PUT("api/users/me")
    suspend fun updateProfile(@Body body: UpdateProfileRequestDto): UserDto

    // ---------------- Марки ----------------
    @GET("api/brands")
    suspend fun brands(): List<BrandDto>

    @POST("api/brands")
    suspend fun createBrand(@Body body: BrandRequestDto): BrandDto

    // ---------------- Автомобили ----------------
    @GET("api/vehicles")
    suspend fun vehicles(
        @Query("q") q: String?,
        @Query("brandId") brandId: Long?,
        @Query("bodyType") bodyType: String?,
        @Query("status") status: String?,
        @Query("minPrice") minPrice: Double?,
        @Query("maxPrice") maxPrice: Double?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
        @Query("direction") direction: String
    ): PageDto<VehicleSummaryDto>

    @GET("api/vehicles/sold")
    suspend fun soldVehicles(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PageDto<VehicleSummaryDto>

    @GET("api/vehicles/{id}")
    suspend fun vehicle(@Path("id") id: Long): VehicleDto

    @POST("api/vehicles")
    suspend fun createVehicle(@Body body: VehicleRequestDto): VehicleDto

    @PUT("api/vehicles/{id}")
    suspend fun updateVehicle(@Path("id") id: Long, @Body body: VehicleRequestDto): VehicleDto

    @DELETE("api/vehicles/{id}")
    suspend fun deleteVehicle(@Path("id") id: Long): MessageDto

    @GET("api/vehicles/{id}/reviews")
    suspend fun reviews(@Path("id") id: Long): List<ReviewDto>

    @POST("api/vehicles/{id}/reviews")
    suspend fun addReview(@Path("id") id: Long, @Body body: ReviewRequestDto): ReviewDto

    // ---------------- Тест-драйвы ----------------
    @POST("api/test-drives")
    suspend fun bookTestDrive(@Body body: TestDriveRequestDto): TestDriveDto

    @GET("api/test-drives/my")
    suspend fun myTestDrives(): List<TestDriveDto>

    @GET("api/test-drives")
    suspend fun allTestDrives(): List<TestDriveDto>

    @POST("api/test-drives/{id}/confirm")
    suspend fun confirmTestDrive(@Path("id") id: Long): TestDriveDto

    @POST("api/test-drives/{id}/reject")
    suspend fun rejectTestDrive(@Path("id") id: Long): TestDriveDto

    @POST("api/test-drives/{id}/complete")
    suspend fun completeTestDrive(@Path("id") id: Long): TestDriveDto

    @POST("api/test-drives/{id}/cancel")
    suspend fun cancelTestDrive(@Path("id") id: Long): TestDriveDto

    // ---------------- Заказы ----------------
    @POST("api/orders/calculate")
    suspend fun calculateInstallment(@Body body: InstallmentCalcRequestDto): InstallmentPlanDto

    @POST("api/orders")
    suspend fun buy(@Body body: OrderRequestDto): OrderDto

    @GET("api/orders/my")
    suspend fun myOrders(): List<OrderDto>

    @GET("api/orders")
    suspend fun allOrders(): List<OrderDto>

    @POST("api/orders/{id}/confirm")
    suspend fun confirmOrder(@Path("id") id: Long): OrderDto

    @POST("api/orders/{id}/pay")
    suspend fun payOrder(@Path("id") id: Long): OrderDto

    @POST("api/orders/{id}/complete")
    suspend fun completeOrder(@Path("id") id: Long): OrderDto

    @POST("api/orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: Long): OrderDto

    // ---------------- Оплата ----------------
    @POST("api/payments/order/{orderId}")
    suspend fun createPayment(@Path("orderId") orderId: Long): PaymentDto

    @POST("api/payments/{providerPaymentId}/confirm")
    suspend fun confirmPayment(
        @Path("providerPaymentId") providerPaymentId: String,
        @Body body: ConfirmPaymentRequestDto
    ): PaymentDto

    // ---------------- Избранное ----------------
    @GET("api/favorites")
    suspend fun favorites(): List<VehicleSummaryDto>

    @POST("api/favorites/{vehicleId}")
    suspend fun addFavorite(@Path("vehicleId") vehicleId: Long): MessageDto

    @DELETE("api/favorites/{vehicleId}")
    suspend fun removeFavorite(@Path("vehicleId") vehicleId: Long): MessageDto

    // ---------------- Уведомления ----------------
    @GET("api/notifications")
    suspend fun notifications(): List<NotificationDto>

    @GET("api/notifications/unread-count")
    suspend fun unreadCount(): Map<String, Long>

    @POST("api/notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: Long): MessageDto

    @POST("api/notifications/read-all")
    suspend fun markAllNotificationsRead(): MessageDto

    // ---------------- Аналитика ----------------
    @GET("api/dashboard")
    suspend fun dashboard(): DashboardDto

    // ---------------- Администрирование ----------------
    @GET("api/admin/users")
    suspend fun users(): List<UserDto>

    @PUT("api/admin/users/{id}/role")
    suspend fun updateUserRole(@Path("id") id: Long, @Body body: UpdateRoleRequestDto): UserDto

    @PUT("api/admin/users/{id}/status")
    suspend fun setUserStatus(@Path("id") id: Long, @Query("active") active: Boolean): UserDto
}
