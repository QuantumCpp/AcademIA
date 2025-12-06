package com.example.academia.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.util.UUID

data class Subtask(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isCompleted: Boolean = false
)

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val dueDate: String,
    val category: String,
    val subject: String,
    val expectedHours: Int,
    val trackedSeconds: Int = 0,
    val isCompleted: Boolean = false,
    val scoreObtained: String = "",
    val totalScore: String = "",
    val subtasks: SnapshotStateList<Subtask> = mutableStateListOf()
) {
    fun getCompletionPercentage(): Int {
        return if (subtasks.isEmpty()) {
            if (isCompleted) 100 else 0
        } else {
            val completedSubtasks = subtasks.count { it.isCompleted }
            (completedSubtasks * 100) / subtasks.size
        }
    }

    fun shouldBeCompleted(): Boolean {
        return if (subtasks.isEmpty()) {
            isCompleted
        } else {
            subtasks.all { it.isCompleted }
        }
    }
}