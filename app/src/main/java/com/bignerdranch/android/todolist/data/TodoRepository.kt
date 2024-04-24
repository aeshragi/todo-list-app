package com.bignerdranch.android.todolist.data

import com.bignerdranch.android.todolist.data.database.Todo
import com.bignerdranch.android.todolist.data.database.TodoDatabase
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(private val database: TodoDatabase) {

    fun getTodos(): Flow<List<Todo>> = database.todoDao().getTodos()

    fun getTodo(id: UUID): Flow<Todo> = database.todoDao().getTodo(id)

    suspend fun addTodo(todo: Todo) = database.todoDao().addTodo(todo)

    suspend fun updateTodo(todo: Todo) = database.todoDao().updateTodo(todo)
}
