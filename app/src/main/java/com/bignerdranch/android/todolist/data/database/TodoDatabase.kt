package com.bignerdranch.android.todolist.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ Todo::class ], version = 2)
@TypeConverters(TodoTypeConverter::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}

val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Todo ADD COLUMN dateCreated INTEGER")
        db.execSQL("ALTER TABLE Todo ADD COLUMN dateDue INTEGER")
        db.execSQL("ALTER TABLE Todo ADD COLUMN priority INTEGER NOT NULL DEFAULT")
    }
}