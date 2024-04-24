package com.bignerdranch.android.todolist.data

import android.content.Context
import androidx.room.Room
import com.bignerdranch.android.todolist.data.database.TodoDatabase
import com.bignerdranch.android.todolist.data.database.migration_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "todo-database"

@Module
@InstallIn(SingletonComponent::class)
class TodoRepositoryModule {
    @Provides
    @Singleton
    fun bindsDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TodoDatabase::class.java,
            DATABASE_NAME
        ).addMigrations(migration_1_2)
            .build()
    }
}