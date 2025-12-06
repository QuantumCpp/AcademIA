package com.example.academia.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.example.academia.model.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.min

object TaskDateUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun parseTaskDate(dateString: String): LocalDate? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun calculateHeaderColorAndPulse(
        tasks: List<Task>,
        today: LocalDate
    ): Pair<Color, Boolean> {
        if (tasks.isEmpty()) {
            return Pair(Color.White, false)
        }

        var urgencyScore = 0f
        var hasUrgentTasks = false

        tasks.forEach { task ->
            val taskDate = parseTaskDate(task.dueDate)
            if (taskDate != null) {
                val daysUntilDue = ChronoUnit.DAYS.between(today, taskDate)

                urgencyScore += when {
                    daysUntilDue < 0 -> 10f.also { hasUrgentTasks = true }
                    daysUntilDue == 0L -> 8f.also { hasUrgentTasks = true }
                    daysUntilDue == 1L -> 6f.also { hasUrgentTasks = true }
                    daysUntilDue <= 3 -> 4f
                    daysUntilDue <= 7 -> 2f
                    else -> 1f
                }
            }
        }

        val normalizedScore = urgencyScore / tasks.size
        val intensity = min(1f, normalizedScore / 8f)

        return Pair(
            Color(
                red = intensity,
                green = 1f - intensity,
                blue = 1f - intensity,
                alpha = 1f
            ),
            hasUrgentTasks
        )
    }
}