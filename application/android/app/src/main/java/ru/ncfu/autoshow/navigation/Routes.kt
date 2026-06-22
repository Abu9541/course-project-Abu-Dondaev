package ru.ncfu.autoshow.navigation

/** Маршруты навигации приложения. */
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val CATALOG = "catalog"
    const val FAVORITES = "favorites"
    const val ACTIVITY = "activity"
    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val USER_AGREEMENT = "user-agreement"

    const val REQUESTS = "requests"
    const val DASHBOARD = "dashboard"
    const val USERS = "users"
    const val SOLD = "sold-vehicles"

    const val VEHICLE_DETAIL = "vehicle/{id}"
    const val BOOK_TEST_DRIVE = "book-test-drive/{id}"
    const val BUY = "buy/{id}"
    const val PAYMENT = "payment/{orderId}"
    const val EDIT_VEHICLE = "edit-vehicle?id={id}"

    fun vehicleDetail(id: Long) = "vehicle/$id"
    fun bookTestDrive(id: Long) = "book-test-drive/$id"
    fun buy(id: Long) = "buy/$id"
    fun payment(orderId: Long) = "payment/$orderId"
    fun editVehicle(id: Long? = null) = if (id != null) "edit-vehicle?id=$id" else "edit-vehicle?id=-1"
}
