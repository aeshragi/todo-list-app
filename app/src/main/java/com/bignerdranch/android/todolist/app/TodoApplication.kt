package com.bignerdranch.android.todolist.app

import android.app.Application
import com.bignerdranch.android.todolist.data.TodoRepository

class TodoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TodoRepository.initialize(this)
    }
}