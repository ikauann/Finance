package com.personal.financeapp.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.*

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    variation: String,
    currencyFormatter: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = currencyFormatter.format(amount),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = variation,
                style = MaterialTheme.typography.bodySmall,
                color = if (variation.startsWith("↑")) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
    }
}

@Composable
fun IncomeExpenseRow(
    income: Double,
    expense: Double,
    currencyFormatter: NumberFormat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SmallSummaryCard(
            title = "Receitas",
            amount = income,
            icon = Icons.Default.TrendingUp,
            containerColor = Color(0xFFE8F5E9),
            contentColor = Color(0xFF2E7D32),
            modifier = Modifier.weight(1f),
            currencyFormatter = currencyFormatter
        )
        SmallSummaryCard(
            title = "Despesas",
            amount = expense,
            icon = Icons.Default.TrendingDown,
            containerColor = Color(0xFFFFEBEE),
            contentColor = Color(0xFFC62828),
            modifier = Modifier.weight(1f),
            currencyFormatter = currencyFormatter
        )
    }
}

@Composable
fun SmallSummaryCard(
    title: String,
    amount: Double,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    currencyFormatter: NumberFormat
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = title, style = MaterialTheme.typography.labelMedium, color = contentColor)
            }
            Text(
                text = currencyFormatter.format(amount),
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
        }
    }
}
