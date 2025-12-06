package com.example.academia.components.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekGrid(
    today: LocalDate,
    modifier: Modifier = Modifier
) {
    val weekDays = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    val dayOfMonthMap = (0..6).map {
        today.minusDays((today.dayOfWeek.value - 1 - it).toLong()).dayOfMonth
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        weekDays.forEachIndexed { index, day ->
            val dayNumber = dayOfMonthMap[index]
            val isToday = today.dayOfWeek.ordinal == index

            DayCell(
                dayNumber = dayNumber,
                dayName = day,
                isToday = isToday
            )
        }
    }
}

@Composable
private fun DayCell(
    dayNumber: Int,
    dayName: String,
    isToday: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (isToday) Color.Gray else Color.LightGray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayNumber.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isToday) Color.White else Color.Black.copy(alpha = 0.3f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = dayName,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isToday) Color.White else Color.White.copy(alpha = 0.5f)
        )
    }
}
