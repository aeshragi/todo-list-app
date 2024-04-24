package com.bignerdranch.android.todolist.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(factory: TodoDetailViewModel.Factory, todoListViewModel: TodoListViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "todolist") {
        composable("todolist") {

            TodoListScreen(addTodoClicked = {
                navController.navigate("tododetail")
            },
                onEditTodoClicked = { id ->
                    navController.navigate("tododetail/$id")
                }, todoListViewModel)
        }
        composable("tododetail/{id}") { backStackEntry ->
            val uuid: String? = backStackEntry.arguments?.getString("id")
            TodoDetailScreen(
                id = uuid?.let { UUID.fromString(it) },
                todoDetailViewModelFactory = factory,
                onSubmitEditAdd = {
                    navController.navigate("todolist")
                })
        }
        composable("tododetail") { TodoDetailScreen(id = null,
            todoDetailViewModelFactory = factory,
            onSubmitEditAdd = {
            navController.navigate("todolist")
        }) }
    }
}