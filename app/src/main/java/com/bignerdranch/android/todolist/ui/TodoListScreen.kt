package com.bignerdranch.android.todolist.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bignerdranch.android.todolist.app.DateUtils
import com.bignerdranch.android.todolist.data.database.Todo
import com.bignerdranch.android.todolist.data.database.TodoPriority
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID

@Composable
fun TodoListScreen(
    addTodoClicked: () -> Unit,
    onEditTodoClicked: (UUID) -> Unit,
    viewModel: TodoListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    Column {
        Box {
            OutlinedButton(onClick = { viewModel.toggleDropDown() }) {
                Text(text = "Sort by: ${uiState.todoSort}")
            }
            DropdownMenu(expanded = uiState.sortExpanded,
                onDismissRequest = { viewModel.toggleDropDown() }) {
                TodoSort.entries.forEach {
                    DropdownMenuItem(text = { Text(text = it.name) }, onClick = { viewModel.sortBy(it) })
                }
            }
        }
        LazyColumn {
            items(uiState.todos) {
                TodoItem(todo = it, onEditTodoClicked)
            }
        }
        OutlinedButton(onClick = { addTodoClicked() }) {
            Text(text = "add a todo")
        }
    }
}

@Composable
fun TodoItem(todo: Todo, onEditTodoClicked: (UUID) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
        onEditTodoClicked(todo.id)
    }) {
        Checkbox(checked = todo.isDone, onCheckedChange = { })
        Text(text = todo.priority.toString())
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = todo.title)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewTodoItem() {
    TodoItem(
        Todo(
            title = "foo",
            content = "this is a foo with some bar",
            isDone = false,
            id = UUID.randomUUID(),
            priority = TodoPriority.P0,
            dateCreated = DateUtils.asDate(LocalDateTime.now()),
            dateDue = DateUtils.asDate(LocalDateTime.now())
        ),
        onEditTodoClicked = {}
    )
}