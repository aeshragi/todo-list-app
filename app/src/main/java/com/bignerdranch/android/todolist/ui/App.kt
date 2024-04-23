package com.bignerdranch.android.todolist.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.UUID

@Composable
fun App() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "todolist") {
        composable("todolist") {

            TodoListScreen(addTodoClicked = {
                navController.navigate("tododetail")
            },
                onEditTodoClicked = { id ->
                    navController.navigate("tododetail/$id")
                })
        }
        composable("tododetail/{id}") { backStackEntry ->
            val uuid: String? = backStackEntry.arguments?.getString("id")
            TodoDetailScreen(
                id = uuid?.let { UUID.fromString(it) },
                onSubmitEditAdd = {
                    navController.navigate("todolist")
                })
        }
        composable("tododetail") { TodoDetailScreen(id = null, onSubmitEditAdd = {
            navController.navigate("todolist")
        }) }
    }
}