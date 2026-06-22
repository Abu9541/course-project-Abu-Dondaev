package ru.ncfu.autoshow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.ncfu.autoshow.core.Format
import ru.ncfu.autoshow.core.Labels
import ru.ncfu.autoshow.data.remote.dto.VehicleSummaryDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleCard(
    vehicle: VehicleSummaryDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (!vehicle.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = vehicle.imageUrl,
                        contentDescription = vehicle.model,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Outlined.DirectionsCar, null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(56.dp).align(Alignment.Center)
                    )
                }
                StatusBadge(
                    text = Labels.vehicleStatus(vehicle.status),
                    color = statusColor(vehicle.status),
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                )
            }
            Column(Modifier.padding(14.dp)) {
                Text(
                    "${vehicle.brandName.orEmpty()} ${vehicle.model}".trim(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${vehicle.year} · ${Labels.bodyType(vehicle.bodyType)} · ${vehicle.powerHp} л.с. · ${Labels.drive(vehicle.driveType)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    Format.price(vehicle.price),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SpecChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
