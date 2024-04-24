package com.bignerdranch.android.todolist.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bignerdranch.android.todolist.ui.screens.detail.TODO_DETAIL_SCREEN_NAV
import com.bignerdranch.android.todolist.ui.screens.detail.TodoDetailScreen
import com.bignerdranch.android.todolist.ui.screens.detail.TodoDetailViewModel
import com.bignerdranch.android.todolist.ui.screens.list.LIST_SCREEN_NAV
import com.bignerdranch.android.todolist.ui.screens.list.TodoListScreen
import com.bignerdranch.android.todolist.ui.screens.list.TodoListViewModel
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(factory: TodoDetailViewModel.Factory, todoListViewModel: TodoListViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = LIST_SCREEN_NAV) {
        composable(LIST_SCREEN_NAV) {

            TodoListScreen(addTodoClicked = {
                navController.navigate(TODO_DETAIL_SCREEN_NAV)
            },
                onEditTodoClicked = { id ->
                    navController.navigate("$TODO_DETAIL_SCREEN_NAV/$id")
                }, todoListViewModel)
        }
        composable("$TODO_DETAIL_SCREEN_NAV/{id}") { backStackEntry ->
            val uuid: String? = backStackEntry.arguments?.getString("id")
            TodoDetailScreen(
                id = uuid?.let { UUID.fromString(it) },
                todoDetailViewModelFactory = factory,
                onSubmitEditAdd = {
                    navController.navigate(LIST_SCREEN_NAV)
                })
        }
        composable(TODO_DETAIL_SCREEN_NAV) { TodoDetailScreen(id = null,
            todoDetailViewModelFactory = factory,
            onSubmitEditAdd = {
            navController.navigate(LIST_SCREEN_NAV)
        }) }
    }
}