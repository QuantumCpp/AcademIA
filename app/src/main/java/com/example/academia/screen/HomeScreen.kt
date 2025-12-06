package com.example.academia.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academia.components.task.SwipeableTaskItem
import com.example.academia.components.ui.WeekGrid
import com.example.academia.model.Task
import com.example.academia.utils.TaskDateUtils
import com.example.academia.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

enum class SortOrder { DATE, SUBJECT, CATEGORY, COMPLETED }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    dayFontWeight: FontWeight = FontWeight.Bold,
    monthDayFontWeight: FontWeight = FontWeight.Bold,
    yearFontWeight: FontWeight = FontWeight.Bold,
    scrollAreaWidth: Dp = 380.dp,
    scrollAreaHeight: Dp = 480.dp,
    scrollAreaPaddingHorizontal: Dp = 16.dp,
    scrollAreaPaddingVertical: Dp = 16.dp,
    scrollAreaBackgroundColor: Color = Color.White.copy(alpha = 0.1f),
    scrollAreaCornerRadius: Dp = 12.dp
) {
    val taskViewModel: TaskViewModel = viewModel()
    val today = LocalDate.now()
    val showAddTaskDialog = remember { mutableStateOf(false) }
    var sortOrder by remember { mutableStateOf(SortOrder.DATE) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    val tasks = taskViewModel.tasks
    val currentDay = today.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase()
    val monthDay = today.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " +
            today.dayOfMonth.toString().padStart(2, '0')
    val year = today.year.toString()

    val (headerColor, shouldPulse) = TaskDateUtils.calculateHeaderColorAndPulse(
        tasks.filter { !it.shouldBeCompleted() },
        today
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (shouldPulse) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val sortedTasks = remember(tasks, sortOrder) {
        when (sortOrder) {
            SortOrder.DATE -> tasks
                .filter { !it.shouldBeCompleted() }
                .sortedBy { task -> TaskDateUtils.parseTaskDate(task.dueDate) }
            SortOrder.SUBJECT -> tasks
                .filter { !it.shouldBeCompleted() }
                .sortedBy { it.subject.lowercase() }
            SortOrder.CATEGORY -> tasks
                .filter { !it.shouldBeCompleted() }
                .sortedBy { it.category.lowercase() }
            SortOrder.COMPLETED -> tasks
                .filter { it.shouldBeCompleted() }
                .sortedBy { it.name.lowercase() }
        }
    }

    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.shouldBeCompleted() }
    val completionPercentage = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Contenido superior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                HomeHeader(
                    currentDay = currentDay,
                    monthDay = monthDay,
                    year = year,
                    headerColor = headerColor,
                    pulseScale = pulseScale,
                    dayFontWeight = dayFontWeight,
                    monthDayFontWeight = monthDayFontWeight,
                    yearFontWeight = yearFontWeight
                )

                Spacer(modifier = Modifier.height(12.dp))
                WeekGrid(today = today)
                Spacer(modifier = Modifier.height(16.dp))

                TaskStatsAndSort(
                    completionPercentage = completionPercentage,
                    sortOrder = sortOrder,
                    onSortChange = { sortOrder = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                TaskList(
                    sortedTasks = sortedTasks,
                    scrollAreaWidth = scrollAreaWidth,
                    scrollAreaHeight = scrollAreaHeight,
                    scrollAreaPaddingHorizontal = scrollAreaPaddingHorizontal,
                    scrollAreaPaddingVertical = scrollAreaPaddingVertical,
                    scrollAreaBackgroundColor = scrollAreaBackgroundColor,
                    scrollAreaCornerRadius = scrollAreaCornerRadius,
                    onTaskClick = { selectedTask = it },
                    onToggleComplete = { taskViewModel.toggleTaskCompletionByTask(it) }
                )
            }
        }

        // BOTÓN FLOTANTE "+" - AJUSTADO PARA ESTAR CERCA DEL BOTTOM BAR
        FloatingAddButton(
            onClick = { showAddTaskDialog.value = true }
        )
    }
}

@Composable
private fun HomeHeader(
    currentDay: String,
    monthDay: String,
    year: String,
    headerColor: Color,
    pulseScale: Float,
    dayFontWeight: FontWeight,
    monthDayFontWeight: FontWeight,
    yearFontWeight: FontWeight
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() - 20.dp,
                start = 20.dp,
                end = 20.dp,
                bottom = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.graphicsLayer(
                scaleX = pulseScale,
                scaleY = pulseScale
            )
        ) {
            Text(
                text = currentDay,
                fontSize = 30.sp,
                fontWeight = dayFontWeight,
                color = headerColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color = headerColor, shape = CircleShape)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = monthDay,
                fontSize = 20.sp,
                fontWeight = monthDayFontWeight
            )
            Text(
                text = year,
                fontSize = 18.sp,
                fontWeight = yearFontWeight,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun TaskStatsAndSort(
    completionPercentage: Int,
    sortOrder: SortOrder,
    onSortChange: (SortOrder) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompletionIndicator(completionPercentage)

        SortButton(
            currentOrder = sortOrder,
            onClick = {
                onSortChange(
                    when (sortOrder) {
                        SortOrder.DATE -> SortOrder.SUBJECT
                        SortOrder.SUBJECT -> SortOrder.CATEGORY
                        SortOrder.CATEGORY -> SortOrder.COMPLETED
                        SortOrder.COMPLETED -> SortOrder.DATE
                    }
                )
            }
        )
    }
}

@Composable
private fun CompletionIndicator(percentage: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.White.copy(alpha = 0.3f), CircleShape)
            )
            if (percentage > 0) {
                Box(
                    modifier = Modifier
                        .size((20 * percentage / 100).dp.coerceAtLeast(0.dp))
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                )
            }
        }
        Text(
            text = "$percentage% Task Complete",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun SortButton(
    currentOrder: SortOrder,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
        onClick = onClick
    ) {
        Text(
            text = when (currentOrder) {
                SortOrder.DATE -> "By Date"
                SortOrder.SUBJECT -> "By Subject"
                SortOrder.CATEGORY -> "By Type"
                SortOrder.COMPLETED -> "Completed"
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun TaskList(
    sortedTasks: List<Task>,
    scrollAreaWidth: Dp,
    scrollAreaHeight: Dp,
    scrollAreaPaddingHorizontal: Dp,
    scrollAreaPaddingVertical: Dp,
    scrollAreaBackgroundColor: Color,
    scrollAreaCornerRadius: Dp,
    onTaskClick: (Task) -> Unit,
    onToggleComplete: (Task) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .width(scrollAreaWidth)
                .fillMaxHeight(),
            shape = RoundedCornerShape(scrollAreaCornerRadius),
            color = scrollAreaBackgroundColor
        ) {
            if (sortedTasks.isEmpty()) {
                EmptyTasksMessage()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = scrollAreaPaddingHorizontal,
                            vertical = scrollAreaPaddingVertical
                        ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedTasks) { task ->
                        SwipeableTaskItem(
                            task = task,
                            onTaskClick = onTaskClick,
                            onToggleComplete = onToggleComplete
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingAddButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Surface(
            modifier = Modifier
                .padding(end = 20.dp, bottom = 8.dp) // AJUSTA ESTE VALOR: 70dp es para que esté justo encima del BottomBar
                .size(66.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color.Black),
            shadowElevation = 3.dp,
            onClick = onClick
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "+",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EmptyTasksMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No assignments currently",
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Press the + button to add a new task",
                fontSize = 14.sp,
                color = Color.Gray.copy(alpha = 0.7f)
            )
        }
    }
}