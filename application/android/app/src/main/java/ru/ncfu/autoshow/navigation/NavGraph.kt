package ru.ncfu.autoshow.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import ru.ncfu.autoshow.core.vmFactory
import ru.ncfu.autoshow.data.session.Session
import ru.ncfu.autoshow.di.AppContainer
import ru.ncfu.autoshow.presentation.activity.ActivityScreen
import ru.ncfu.autoshow.presentation.activity.ActivityViewModel
import ru.ncfu.autoshow.presentation.admin.*
import ru.ncfu.autoshow.presentation.auth.AuthViewModel
import ru.ncfu.autoshow.presentation.auth.LoginScreen
import ru.ncfu.autoshow.presentation.auth.RegisterScreen
import ru.ncfu.autoshow.presentation.catalog.CatalogScreen
import ru.ncfu.autoshow.presentation.catalog.CatalogViewModel
import ru.ncfu.autoshow.presentation.dashboard.DashboardScreen
import ru.ncfu.autoshow.presentation.dashboard.DashboardViewModel
import ru.ncfu.autoshow.presentation.detail.VehicleDetailScreen
import ru.ncfu.autoshow.presentation.detail.VehicleDetailViewModel
import ru.ncfu.autoshow.presentation.favorites.FavoritesScreen
import ru.ncfu.autoshow.presentation.favorites.FavoritesViewModel
import ru.ncfu.autoshow.presentation.notifications.NotificationsScreen
import ru.ncfu.autoshow.presentation.notifications.NotificationsViewModel
import ru.ncfu.autoshow.presentation.payment.PaymentScreen
import ru.ncfu.autoshow.presentation.payment.PaymentViewModel
import ru.ncfu.autoshow.presentation.profile.ProfileScreen
import ru.ncfu.autoshow.presentation.profile.ProfileViewModel
import ru.ncfu.autoshow.presentation.settings.SettingsScreen
import ru.ncfu.autoshow.presentation.settings.SettingsViewModel
import ru.ncfu.autoshow.presentation.settings.UserAgreementScreen
import ru.ncfu.autoshow.presentation.purchase.PurchaseScreen
import ru.ncfu.autoshow.presentation.purchase.PurchaseViewModel
import ru.ncfu.autoshow.presentation.requests.RequestsScreen
import ru.ncfu.autoshow.presentation.requests.RequestsViewModel
import ru.ncfu.autoshow.presentation.testdrive.TestDriveBookingScreen
import ru.ncfu.autoshow.presentation.testdrive.TestDriveBookingViewModel

private data class BottomDest(val route: String, val label: String, val icon: ImageVector)

@Composable
fun AutoshowNavGraph(container: AppContainer) {
    val navController = rememberNavController()
    val session by container.sessionManager.session.collectAsStateWithLifecycle()
    val startDestination = if (session.isLoggedIn) Routes.CATALOG else Routes.LOGIN

    val bottomDestinations = remember(session.role) { destinationsFor(session) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomDestinations.any { it.route == currentRoute }

    // Счётчик непрочитанных уведомлений для бейджа в нижней навигации.
    // Работает одинаково для всех ролей (клиент, менеджер, админ). Обновляется сразу при
    // смене экрана И периодически — чтобы менеджер видел новые заявки, не переключая вкладки.
    var unreadCount by remember { mutableStateOf(0L) }
    LaunchedEffect(session.isLoggedIn, currentRoute) {
        if (!session.isLoggedIn) {
            unreadCount = 0L
            return@LaunchedEffect
        }
        while (true) {
            unreadCount = container.notificationRepository.unreadCount().getOrDefault(0L)
            delay(15_000)
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomDestinations.forEach { dest ->
                        NavigationBarItem(
                            selected = currentRoute == dest.route,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                if (dest.route == Routes.NOTIFICATIONS && unreadCount > 0) {
                                    BadgedBox(badge = {
                                        Badge { Text(if (unreadCount > 99) "99+" else unreadCount.toString()) }
                                    }) { Icon(dest.icon, dest.label) }
                                } else {
                                    Icon(dest.icon, dest.label)
                                }
                            },
                            label = { Text(dest.label, maxLines = 1) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            // ---------------- Аутентификация ----------------
            composable(Routes.LOGIN) {
                val vm: AuthViewModel = viewModel(factory = vmFactory { AuthViewModel(container.authRepository) })
                LoginScreen(
                    vm = vm,
                    onAuthenticated = {
                        navController.navigate(Routes.CATALOG) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
                )
            }
            composable(Routes.REGISTER) {
                val vm: AuthViewModel = viewModel(factory = vmFactory { AuthViewModel(container.authRepository) })
                RegisterScreen(
                    vm = vm,
                    onAuthenticated = {
                        navController.navigate(Routes.CATALOG) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // ---------------- Каталог и карточка ----------------
            composable(Routes.CATALOG) {
                val vm: CatalogViewModel = viewModel(factory = vmFactory { CatalogViewModel(container.catalogRepository) })
                CatalogScreen(
                    vm = vm,
                    isStaff = session.isStaff,
                    onVehicleClick = { navController.navigate(Routes.vehicleDetail(it)) },
                    onAddVehicle = { navController.navigate(Routes.editVehicle(null)) },
                    onShowSold = if (session.isAdmin) {
                        { navController.navigate(Routes.SOLD) }
                    } else null
                )
            }
            composable(
                Routes.VEHICLE_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: return@composable
                val vm: VehicleDetailViewModel = viewModel(factory = vmFactory {
                    VehicleDetailViewModel(id, container.catalogRepository, container.favoriteRepository, container.reviewRepository)
                })
                VehicleDetailScreen(
                    vm = vm,
                    isStaff = session.isStaff,
                    isAdmin = session.isAdmin,
                    onBack = { navController.popBackStack() },
                    onBookTestDrive = { navController.navigate(Routes.bookTestDrive(it)) },
                    onBuy = { navController.navigate(Routes.buy(it)) },
                    onEdit = { navController.navigate(Routes.editVehicle(it)) }
                )
            }

            // ---------------- Тест-драйв и покупка ----------------
            composable(
                Routes.BOOK_TEST_DRIVE,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: return@composable
                val vm: TestDriveBookingViewModel = viewModel(factory = vmFactory {
                    TestDriveBookingViewModel(id, container.catalogRepository, container.testDriveRepository)
                })
                TestDriveBookingScreen(vm, onBack = { navController.popBackStack() }, onBooked = { navController.popBackStack() })
            }
            composable(
                Routes.BUY,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: return@composable
                val vm: PurchaseViewModel = viewModel(factory = vmFactory {
                    PurchaseViewModel(id, container.catalogRepository, container.orderRepository)
                })
                PurchaseScreen(
                    vm,
                    onBack = { navController.popBackStack() },
                    onPurchased = { orderId ->
                        // Сразу открываем оплату заказа; форму покупки убираем из стека.
                        navController.navigate(Routes.payment(orderId)) {
                            popUpTo(Routes.BUY) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                Routes.PAYMENT,
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
            ) { entry ->
                val orderId = entry.arguments?.getLong("orderId") ?: return@composable
                val vm: PaymentViewModel = viewModel(factory = vmFactory {
                    PaymentViewModel(orderId, container.paymentRepository)
                })
                PaymentScreen(vm, onBack = { navController.popBackStack() }, onPaid = { navController.popBackStack() })
            }

            // ---------------- Клиентские вкладки ----------------
            composable(Routes.FAVORITES) {
                val vm: FavoritesViewModel = viewModel(factory = vmFactory { FavoritesViewModel(container.favoriteRepository) })
                FavoritesScreen(vm, onVehicleClick = { navController.navigate(Routes.vehicleDetail(it)) })
            }
            composable(Routes.ACTIVITY) {
                val vm: ActivityViewModel = viewModel(factory = vmFactory {
                    ActivityViewModel(container.testDriveRepository, container.orderRepository)
                })
                ActivityScreen(vm, onPay = { navController.navigate(Routes.payment(it)) })
            }
            composable(Routes.NOTIFICATIONS) {
                val vm: NotificationsViewModel = viewModel(factory = vmFactory { NotificationsViewModel(container.notificationRepository) })
                NotificationsScreen(vm)
            }
            composable(Routes.PROFILE) {
                val vm: ProfileViewModel = viewModel(factory = vmFactory {
                    ProfileViewModel(container.profileRepository, container.authRepository)
                })
                ProfileScreen(
                    vm,
                    onLoggedOut = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) }
                )
            }
            composable(Routes.SETTINGS) {
                val vm: SettingsViewModel = viewModel(factory = vmFactory { SettingsViewModel(container.settingsManager) })
                SettingsScreen(
                    vm = vm,
                    onBack = { navController.popBackStack() },
                    onOpenUserAgreement = { navController.navigate(Routes.USER_AGREEMENT) }
                )
            }
            composable(Routes.USER_AGREEMENT) {
                UserAgreementScreen(onBack = { navController.popBackStack() })
            }

            // ---------------- Вкладки персонала ----------------
            composable(Routes.REQUESTS) {
                val vm: RequestsViewModel = viewModel(factory = vmFactory {
                    RequestsViewModel(container.testDriveRepository, container.orderRepository)
                })
                RequestsScreen(vm)
            }
            composable(Routes.DASHBOARD) {
                val vm: DashboardViewModel = viewModel(factory = vmFactory { DashboardViewModel(container.dashboardRepository) })
                DashboardScreen(vm)
            }
            composable(Routes.USERS) {
                val vm: UsersViewModel = viewModel(factory = vmFactory { UsersViewModel(container.adminRepository) })
                UsersScreen(vm)
            }
            composable(Routes.SOLD) {
                val vm: SoldVehiclesViewModel = viewModel(factory = vmFactory { SoldVehiclesViewModel(container.catalogRepository) })
                SoldVehiclesScreen(
                    vm = vm,
                    onBack = { navController.popBackStack() },
                    onVehicleClick = { navController.navigate(Routes.vehicleDetail(it)) }
                )
            }
            composable(
                Routes.EDIT_VEHICLE,
                arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
            ) { entry ->
                val idArg = entry.arguments?.getLong("id") ?: -1L
                val id = if (idArg > 0) idArg else null
                val vm: EditVehicleViewModel = viewModel(factory = vmFactory { EditVehicleViewModel(id, container.catalogRepository) })
                EditVehicleScreen(vm, onBack = { navController.popBackStack() }, onSaved = { navController.popBackStack() })
            }
        }
    }
}

private fun destinationsFor(session: Session): List<BottomDest> = when {
    session.isAdmin -> listOf(
        BottomDest(Routes.CATALOG, "Каталог", Icons.Outlined.DirectionsCar),
        BottomDest(Routes.REQUESTS, "Заявки", Icons.Outlined.Assignment),
        BottomDest(Routes.DASHBOARD, "Аналитика", Icons.Outlined.BarChart),
        BottomDest(Routes.USERS, "Люди", Icons.Outlined.Group),
        BottomDest(Routes.PROFILE, "Профиль", Icons.Outlined.Person)
    )
    session.isManager -> listOf(
        BottomDest(Routes.CATALOG, "Каталог", Icons.Outlined.DirectionsCar),
        BottomDest(Routes.REQUESTS, "Заявки", Icons.Outlined.Assignment),
        BottomDest(Routes.DASHBOARD, "Аналитика", Icons.Outlined.BarChart),
        BottomDest(Routes.NOTIFICATIONS, "Уведомл.", Icons.Outlined.Notifications),
        BottomDest(Routes.PROFILE, "Профиль", Icons.Outlined.Person)
    )
    else -> listOf(
        BottomDest(Routes.CATALOG, "Каталог", Icons.Outlined.DirectionsCar),
        BottomDest(Routes.FAVORITES, "Избранное", Icons.Outlined.FavoriteBorder),
        BottomDest(Routes.ACTIVITY, "Заявки", Icons.Outlined.ReceiptLong),
        BottomDest(Routes.NOTIFICATIONS, "Уведомл.", Icons.Outlined.Notifications),
        BottomDest(Routes.PROFILE, "Профиль", Icons.Outlined.Person)
    )
}
