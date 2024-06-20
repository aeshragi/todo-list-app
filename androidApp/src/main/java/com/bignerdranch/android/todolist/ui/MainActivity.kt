package com.bignerdranch.android.todolist.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.bignerdranch.android.shared.Greeting
import com.bignerdranch.android.todolist.ui.screens.detail.TodoDetailViewModel
import com.bignerdranch.android.todolist.ui.screens.list.TodoListViewModel
import com.bignerdranch.android.todolist.ui.theme.TodoListTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var viewModelAssistedFactory: TodoDetailViewModel.Factory
    private val todoListViewModel: TodoListViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Login Activity", "Hello from shared module: " + (Greeting().greet()))
        setContent {
            TodoListTheme {
                // A surface container using the 'background' color from the theme
                App(viewModelAssistedFactory, todoListViewModel)
            }
        }
    }
}
