package ru.ncfu.autoshow.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.ncfu.autoshow.core.Format
import ru.ncfu.autoshow.core.Labels
import ru.ncfu.autoshow.core.UiState
import ru.ncfu.autoshow.data.remote.dto.ReviewDto
import ru.ncfu.autoshow.data.remote.dto.VehicleDto
import ru.ncfu.autoshow.ui.components.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VehicleDetailScreen(
    vm: VehicleDetailViewModel,
    isStaff: Boolean,
    isAdmin: Boolean,
    onBack: () -> Unit,
    onBookTestDrive: (Long) -> Unit,
    onBuy: (Long) -> Unit,
    onEdit: (Long) -> Unit
) {
    val snackbar = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(vm.message) {
        vm.message?.let { snackbar.showSnackbar(it); vm.consumeMessage() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Карточка авто") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") }
                },
                actions = {
                    IconButton(onClick = { vm.toggleFavorite() }) {
                        Icon(
                            if (vm.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            "Избранное",
                            tint = if (vm.isFavorite) MaterialTheme.colorScheme.error else LocalContentColor.current
                        )
                    }
                    if (isStaff) {
                        IconButton(onClick = {
                            (vm.state as? UiState.Success)?.let { onEdit(it.data.id) }
                        }) { Icon(Icons.Filled.Edit, "Редактировать") }
                    }
                    if (isAdmin) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, "Удалить")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when (val s = vm.state) {
            is UiState.Loading -> FullScreenLoading(Modifier.padding(padding))
            is UiState.Error -> FullScreenError(s.message, Modifier.padding(padding), onRetry = vm::load)
            is UiState.Success -> VehicleDetailContent(
                vehicle = s.data,
                reviews = vm.reviews,
                isStaff = isStaff,
                reviewSubmitting = vm.reviewSubmitting,
                onBookTestDrive = onBookTestDrive,
                onBuy = onBuy,
                onSubmitReview = { rating, comment -> vm.submitReview(rating, comment) {} },
                modifier = Modifier.padding(padding)
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить автомобиль?") },
            text = { Text("Действие необратимо. Автомобиль будет удалён из каталога.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    vm.deleteVehicle(onDeleted = onBack)
                }) { Text("Удалить", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") } }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VehicleDetailContent(
    vehicle: VehicleDto,
    reviews: List<ReviewDto>,
    isStaff: Boolean,
    reviewSubmitting: Boolean,
    onBookTestDrive: (Long) -> Unit,
    onBuy: (Long) -> Unit,
    onSubmitReview: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val canBuy = vehicle.status == "IN_STOCK"
    val canTestDrive = vehicle.status == "IN_STOCK" || vehicle.status == "RESERVED"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // Главное изображение
        Box(
            Modifier.fillMaxWidth().height(240.dp).background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (!vehicle.imageUrl.isNullOrBlank()) {
                AsyncImage(vehicle.imageUrl, vehicle.model, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Icon(
                    Icons.Filled.DirectionsCar, null,
                    Modifier.size(72.dp).align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            StatusBadge(
                Labels.vehicleStatus(vehicle.status), statusColor(vehicle.status),
                Modifier.align(Alignment.TopEnd).padding(12.dp)
            )
        }

        // Доп. изображения
        if (!vehicle.images.isNullOrEmpty()) {
            LazyRow(
                Modifier.padding(top = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(vehicle.images) { url ->
                    AsyncImage(
                        url, null,
                        Modifier.size(96.dp).clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Column(Modifier.padding(16.dp)) {
            Text(
                "${vehicle.brand?.name.orEmpty()} ${vehicle.model}".trim(),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                "${vehicle.year} год · ${vehicle.equipmentLevel.orEmpty()}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                Format.price(vehicle.price),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (vehicle.reviewCount > 0 && vehicle.averageRating != null) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = androidx.compose.ui.graphics.Color(0xFFF79009), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${vehicle.averageRating} · ${vehicle.reviewCount} отзыв(ов)", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Характеристики
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SpecChip("Кузов", Labels.bodyType(vehicle.bodyType))
                SpecChip("Двигатель", Labels.engine(vehicle.engineType))
                SpecChip("КПП", Labels.transmission(vehicle.transmission))
                SpecChip("Привод", Labels.drive(vehicle.driveType))
                SpecChip("Мощность", "${vehicle.powerHp} л.с.")
                vehicle.engineVolume?.let { SpecChip("Объём", "$it л") }
                SpecChip("Пробег", "${Format.number(vehicle.mileage.toDouble())} км")
                SpecChip("Цвет", vehicle.color)
                vehicle.fuelConsumption?.let { SpecChip("Расход", "$it л/100км") }
            }

            if (!vehicle.description.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Text("Описание", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(vehicle.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(16.dp))
            InfoRow("VIN", vehicle.vin)

            // Действия клиента
            if (!isStaff) {
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { onBuy(vehicle.id) },
                    enabled = canBuy,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Filled.ShoppingCart, null); Spacer(Modifier.width(8.dp))
                    Text(if (canBuy) "Купить" else "Недоступен для покупки")
                }
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { onBookTestDrive(vehicle.id) },
                    enabled = canTestDrive,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Filled.DirectionsCar, null); Spacer(Modifier.width(8.dp))
                    Text("Записаться на тест-драйв")
                }
            }

            // Отзывы
            Spacer(Modifier.height(24.dp))
            Text("Отзывы", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            if (!isStaff) {
                AddReviewCard(submitting = reviewSubmitting, onSubmit = onSubmitReview)
                Spacer(Modifier.height(12.dp))
            }
            if (reviews.isEmpty()) {
                Text("Пока нет отзывов", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            } else {
                reviews.forEach { ReviewItem(it); Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun ReviewItem(review: ReviewDto) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.userName ?: "Пользователь", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.weight(1f))
                repeat(review.rating) {
                    Icon(Icons.Filled.Star, null, tint = androidx.compose.ui.graphics.Color(0xFFF79009), modifier = Modifier.size(16.dp))
                }
            }
            if (!review.comment.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(review.comment, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(4.dp))
            Text(Format.date(review.createdAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AddReviewCard(submitting: Boolean, onSubmit: (Int, String) -> Unit) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }
    Card {
        Column(Modifier.padding(14.dp)) {
            Text("Оставить отзыв", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row {
                (1..5).forEach { i ->
                    IconButton(onClick = { rating = i }) {
                        Icon(
                            if (i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                            null,
                            tint = androidx.compose.ui.graphics.Color(0xFFF79009)
                        )
                    }
                }
            }
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Комментарий (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onSubmit(rating, comment) },
                enabled = !submitting,
                modifier = Modifier.align(Alignment.End)
            ) {
                if (submitting) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                else Text("Отправить")
            }
        }
    }
}
