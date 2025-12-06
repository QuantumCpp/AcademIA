package com.example.academia.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.academia.model.Subtask
import com.example.academia.model.Task

class TaskViewModel : ViewModel() {
    private val _tasks = mutableStateListOf<Task>()
    val tasks: SnapshotStateList<Task> = _tasks

    private val _categoryTags = mutableStateListOf<String>()
    val categoryTags: SnapshotStateList<String> = _categoryTags

    private val _subjectTags = mutableStateListOf<String>()
    val subjectTags: SnapshotStateList<String> = _subjectTags

    // =============================== Funciones para tareas
    fun addTask(task: Task) = _tasks.add(task)

    fun updateTask(updatedTask: Task) {
        val index = _tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) _tasks[index] = updatedTask
    }

    fun removeTask(task: Task) = _tasks.remove(task)

    fun toggleTaskCompletion(taskId: String) {
        val index = _tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            _tasks[index] = _tasks[index].copy(isCompleted = !_tasks[index].isCompleted)
        }
    }

    fun toggleTaskCompletionByTask(task: Task) {
        val index = _tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            _tasks[index] = _tasks[index].copy(isCompleted = !_tasks[index].isCompleted)
        }
    }

    // =============================== Funciones para subtareas
    fun addSubtask(taskId: String, subtaskName: String) {
        val taskIndex = _tasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1 && subtaskName.isNotBlank()) {
            _tasks[taskIndex].subtasks.add(Subtask(name = subtaskName.trim()))
        }
    }

    fun removeSubtask(taskId: String, subtaskId: String) {
        val taskIndex = _tasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            _tasks[taskIndex].subtasks.removeAll { it.id == subtaskId }
        }
    }

    fun toggleSubtaskCompletion(taskId: String, subtaskId: String) {
        val taskIndex = _tasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            val subtaskIndex = _tasks[taskIndex].subtasks.indexOfFirst { it.id == subtaskId }
            if (subtaskIndex != -1) {
                val subtask = _tasks[taskIndex].subtasks[subtaskIndex]
                _tasks[taskIndex].subtasks[subtaskIndex] =
                    subtask.copy(isCompleted = !subtask.isCompleted)

                updateTaskCompletionBasedOnSubtasks(taskIndex)
            }
        }
    }

    fun updateSubtaskName(taskId: String, subtaskId: String, newName: String) {
        val taskIndex = _tasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1 && newName.isNotBlank()) {
            val subtaskIndex = _tasks[taskIndex].subtasks.indexOfFirst { it.id == subtaskId }
            if (subtaskIndex != -1) {
                val subtask = _tasks[taskIndex].subtasks[subtaskIndex]
                _tasks[taskIndex].subtasks[subtaskIndex] =
                    subtask.copy(name = newName.trim())
            }
        }
    }

    private fun updateTaskCompletionBasedOnSubtasks(taskIndex: Int) {
        val allCompleted = _tasks[taskIndex].subtasks.all { it.isCompleted }
        val anyIncomplete = _tasks[taskIndex].subtasks.any { !it.isCompleted }

        _tasks[taskIndex] = _tasks[taskIndex].copy(
            isCompleted = when {
                allCompleted -> true
                anyIncomplete -> false
                else -> _tasks[taskIndex].isCompleted
            }
        )
    }

    // =============================== Funciones para etiquetas
    fun addCategoryTag(tag: String) {
        if (tag.isNotBlank() && !_categoryTags.contains(tag.trim())) {
            _categoryTags.add(tag.trim())
        }
    }

    fun removeCategoryTag(tag: String) = _categoryTags.remove(tag)

    fun addSubjectTag(tag: String) {
        if (tag.isNotBlank() && !_subjectTags.contains(tag.trim())) {
            _subjectTags.add(tag.trim())
        }
    }

    fun removeSubjectTag(tag: String) = _subjectTags.remove(tag)

    // =============================== Funciones de utilidad
    fun getMutableTasks(): MutableList<Task> = _tasks
    fun getMutableCategoryTags(): MutableList<String> = _categoryTags
    fun getMutableSubjectTags(): MutableList<String> = _subjectTags

    fun getTaskById(id: String): Task? = _tasks.find { it.id == id }
    fun getCompletedTasks(): List<Task> = _tasks.filter { it.isCompleted }
    fun getPendingTasks(): List<Task> = _tasks.filter { !it.isCompleted }
    fun getTasksCount(): Int = _tasks.size
    fun getCompletedTasksCount(): Int = _tasks.count { it.isCompleted }

    fun getCompletionPercentage(): Int {
        val total = _tasks.size
        return if (total > 0) (_tasks.count { it.isCompleted } * 100) / total else 0
    }
}