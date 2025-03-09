package pl.cube

import pl.cube.shared.model.Priority
import pl.cube.shared.model.Task

class TaskRepositoryImpl : TaskRepository {

    private val tasks = mutableListOf(
        Task("cleaning", "Clean the house", Priority.Low),
        Task("gardening", "Mow the lawn", Priority.Medium),
        Task("shopping", "Buy the groceries", Priority.High),
        Task("painting", "Paint the fence", Priority.Medium)
    )

    override fun allTasks(): List<Task> = tasks

    override fun tasksByPriority(priority: Priority) =
        tasks.filter { it.priority == priority }

    override fun taskByName(name: String) =
        tasks.find { it.name.equals(name, ignoreCase = true) }

    override fun add(task: Task) {
        tasks.add(task)
    }

    override fun removeTask(name: String) =
        tasks.removeIf { it.name == name }

}