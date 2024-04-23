package com.bignerdranch.android.todolist.data

import android.content.Context
import androidx.room.Room
import com.bignerdranch.android.todolist.data.database.Todo
import com.bignerdranch.android.todolist.data.database.TodoDatabase
import com.bignerdranch.android.todolist.data.database.migration_1_2
import kotlinx.coroutines.flow.Flow
import java.lang.IllegalStateException
import java.util.UUID

private const val DATABASE_NAME = "todo-database"

class TodoRepository private constructor(context: Context) {
    private val database: TodoDatabase = Room.databaseBuilder(
        context.applicationContext,
        TodoDatabase::class.java,
        DATABASE_NAME
    ).addMigrations(migration_1_2)
        .build()

    fun getTodos(): Flow<List<Todo>> = database.todoDao().getTodos()

    fun getTodo(id: UUID): Flow<Todo> = database.todoDao().getTodo(id)

    suspend fun addTodo(todo: Todo) = database.todoDao().addTodo(todo)

    suspend fun updateTodo(todo: Todo) = database.todoDao().updateTodo(todo)

    companion object {
        private var INSTANCE: TodoRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TodoRepository(context)
            }
        }

        fun get(): TodoRepository {
            return INSTANCE ?: throw IllegalStateException("TodoRepository must be initialized")
        }
    }
}