package pl.cube

import pl.cube.shared.model.Priority
import pl.cube.shared.model.Task

interface TaskRepository {
    fun allTasks(): List<Task>
    fun tasksByPriority(priority: Priority): List<Task>
    fun taskByName(name: String): Task?
    fun add(task: Task)
    fun removeTask(name: String): Boolean
}