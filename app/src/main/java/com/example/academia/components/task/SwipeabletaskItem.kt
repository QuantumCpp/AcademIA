package com.example.academia.components.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.academia.model.Task

@Composable
fun SwipeableTaskItem(
    task: Task,
    onTaskClick: (Task) -> Unit,
    onToggleComplete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val maxSwipeDistance = with(density) { 100.dp.toPx() }

    val completionPercentage = task.getCompletionPercentage()
    val isCompleted = task.shouldBeCompleted()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        // Fondo rojo para completar tarea
        if (offsetX > 0) {
            CompleteTaskBackground(
                isCompleted = isCompleted,
                onToggleComplete = { onToggleComplete(task) }
            )
        }

        // Tarjeta de tarea
        TaskCard(
            task = task,
            offsetX = offsetX,
            maxSwipeDistance = maxSwipeDistance,
            completionPercentage = completionPercentage,
            isCompleted = isCompleted,
            onOffsetChange = { newOffset ->
                offsetX = newOffset.coerceIn(0f, maxSwipeDistance)
            },
            onDragEnd = {
                offsetX = if (offsetX > maxSwipeDistance / 3) maxSwipeDistance else 0f
            },
            onTaskClick = {
                if (offsetX == 0f) onTaskClick(task)
            }
        )
    }
}

@Composable
private fun CompleteTaskBackground(
    isCompleted: Boolean,
    onToggleComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red.copy(alpha = 0.9f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            onClick = onToggleComplete,
            shape = RoundedCornerShape(8.dp),
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Complete",
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isCompleted) "Undo" else "Done",
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    offsetX: Float,
    maxSwipeDistance: Float,
    completionPercentage: Int,
    isCompleted: Boolean,
    onOffsetChange: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onTaskClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { translationX = offsetX }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = { onDragEnd() }
                ) { _, dragAmount ->
                    onOffsetChange(offsetX + dragAmount)
                }
            }
            .clickable(onClick = onTaskClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .graphicsLayer {
                    val blurAmount = (offsetX / maxSwipeDistance).coerceIn(0f, 0.6f)
                    alpha = 1f - blurAmount * 0.4f
                }
        ) {
            TaskHeader(task, isCompleted)

            if (task.subtasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                SubtasksProgressBar(completionPercentage)
            }
        }
    }
}

@Composable
private fun TaskHeader(task: Task, isCompleted: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )

            if (task.subtasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${task.subtasks.count { it.isCompleted }}/${task.subtasks.size} parts completed",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = task.dueDate,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = task.subject,
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SubtasksProgressBar(completionPercentage: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = { completionPercentage / 100f },
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = when {
                completionPercentage == 100 -> Color(0xFF22C55E)
                completionPercentage >= 50 -> Color(0xFF667eea)
                else -> Color(0xFFF59E0B)
            },
            trackColor = Color.Gray.copy(alpha = 0.2f),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$completionPercentage%",
            fontSize = 12.sp,
            color = when {
                completionPercentage == 100 -> Color(0xFF22C55E)
                completionPercentage >= 50 -> Color(0xFF667eea)
                else -> Color(0xFFF59E0B)
            },
            fontWeight = FontWeight.Bold
        )
    }
}