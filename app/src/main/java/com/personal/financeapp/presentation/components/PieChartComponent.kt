package com.personal.financeapp.presentation.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

@Composable
fun PieChartComponent(data: Map<String, Double>) {
    val categoryColors = listOf(
        Color.parseColor("#1B5E20"), // Mobilidade
        Color.parseColor("#FF6F00"), // Alimentação
        Color.parseColor("#0277BD"), // Saúde
        Color.parseColor("#E91E63"), // Relacionamento
        Color.parseColor("#9C27B0")  // Moradia
    )

    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                holeRadius = 50f
                setHoleColor(Color.TRANSPARENT)
                setTransparentCircleAlpha(0)
                setEntryLabelColor(Color.BLACK)
                setEntryLabelTextSize(12f)
                
                legend.apply {
                    isEnabled = true
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(false)
                }
                
                animateY(1000)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        update = { chart ->
            val entries = data.map { PieEntry(it.value.toFloat(), it.key) }
            val dataSet = PieDataSet(entries, "").apply {
                colors = categoryColors
                valueTextSize = 12f
                valueTextColor = Color.WHITE
            }
            
            chart.data = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter(chart))
                setValueTextColor(Color.BLACK)
            }
            chart.setUsePercentValues(true)
            chart.invalidate()
        }
    )
}
