package com.bignerdranch.android.todolist.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TodoDao {
    @Query("SELECT * FROM Todo")
    fun getTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM Todo WHERE id=(:id)")
    fun getTodo(id: UUID): Flow<Todo>

    @Insert
    suspend fun addTodo(todo: Todo)

    @Update
    suspend fun updateTodo(todo: Todo)
}